/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;

import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.component.FadingText;
import de.zintel.gfx.component.GfxState;
import de.zintel.gfx.component.IGfxComponent;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystemFactory;
import de.zintel.gfx.graphicsubsystem.IRendererListener;
import de.zintel.xinput.IXInputCombinedHandler;
import de.zintel.xinput.XInputController;
import de.zintel.xinput.XInputHandle;

/**
 * @author friedemann.zintel
 *
 */
public abstract class SimulationScreen
		implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener, IRendererListener, IXInputCombinedHandler {

	public static final EGraphicsSubsystem GFX_SSYSTEM = GfxUtils.EGraphicsSubsystem.GL;

	private static final int DFLT_CALCULATION_RATE = 1000 / 60;

	private static final int FADING_TEXT_ITERATIONS = (GFX_SSYSTEM == GfxUtils.EGraphicsSubsystem.GL ? 20 : 40);

	private static final long TEXT_TIMEOUT = 1500;

	private static final Point TEXT_POSITION = new Point(20, 30);

	private static final String ID_STATUS = "status";

	private boolean printButtons = false;

	private final ScreenParameters screenParameters;

	private final boolean doRecord;

	private final String recordFilename;

	private final int recordingRate;

	private final IGraphicsSubsystem graphicsSubsystem;

	private boolean logging = true;

	private boolean stopped = false;

	private long calculations = 0;

	private long rStartTs = 0;

	private long renderings = 0;

	private int calculationRate = DFLT_CALCULATION_RATE;

	private long maxFrames = 0;

	private volatile boolean paused = false;

	private volatile boolean shift = false;

	private Map<Integer, IKeyAction> keyActions = new HashMap<>();

	private Integer keyValue = null;

	private Map<String, IGfxComponent> gfxComponents = new LinkedHashMap<>();

	private final Collection<XInputController> xInputControllers = new LinkedList<>();

	public SimulationScreen(String title, EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord,
			String recordFilename, int recordingRate) {
		this.screenParameters = screenParameters;
		this.doRecord = doRecord;
		this.recordFilename = recordFilename;
		this.recordingRate = recordingRate;

		final IGraphicsSubsystemFactory graphicsSubsystemFactory = GfxUtils.graphicsSubsystemFactories.get(gfxSsystem);
		graphicsSubsystem = graphicsSubsystemFactory.newGraphicsSubsystem(title, screenParameters.WIDTH, screenParameters.HEIGHT);
	}

	public void start() throws Exception {

		graphicsSubsystem.init(doRecord, recordFilename);

		graphicsSubsystem.setFullScreen();
		graphicsSubsystem.addMouseListener(this);
		graphicsSubsystem.addMouseWheelListener(this);
		graphicsSubsystem.addMouseMotionListener(this);
		graphicsSubsystem.addKeyListener(this);
		graphicsSubsystem.addRendererListener(this);

		if (logging) {
			System.out.println("initialising ...");
		}

		init(graphicsSubsystem);
		initXInput();
		initBaseKeyActions();

		if (logging) {
			System.out.println("initialised");
		}

		graphicsSubsystem.display();

		loop();

	}

	/**
	 * @param graphicsSubsystem
	 */
	protected abstract void init(final IGraphicsSubsystem graphicsSubsystem);

	/**
	 * 
	 */
	private void initXInput() {
		addXInputHandle(new XInputHandle(0, this));
	}

	/**
	 * @throws Exception
	 */
	private void loop() throws Exception {

		long crStartTs = 0;
		long rIter = 0;
		while (!stopped && (!doRecord || maxFrames <= 0 || rIter < maxFrames)) {

			long startTs = System.currentTimeMillis();

			if (!paused) {

				calculations++;
				if (crStartTs == 0) {
					crStartTs = System.currentTimeMillis();
				}

				for (XInputController xInputController : xInputControllers) {
					xInputController.handleXInput();
				}

				calculate(graphicsSubsystem.getDimension());

				if (!doRecord || calculations % recordingRate == 0) {
					rIter++;
					graphicsSubsystem.repaint();
				}

				long crStopTs = System.currentTimeMillis();
				if (crStopTs - crStartTs >= 1000) {

					double calculationrate = calculations / ((crStopTs - crStartTs) / (double) 1000);
					if (logging) {
						System.out.println("calculationrate: " + calculationrate + " cps");
					}

					crStartTs = System.currentTimeMillis();
					calculations = 0;

				}
			}
			long diffTs = System.currentTimeMillis() - startTs;
			if (diffTs < calculationRate) {
				Thread.sleep(calculationRate - diffTs);
			}
		}

		shutdown();
		graphicsSubsystem.shutdown();
		System.exit(0);

	}

	@Override
	public final void render(IGraphicsSubsystem graphicsSubsystem) {

		renderings++;

		if (rStartTs == 0) {
			rStartTs = System.currentTimeMillis();
		}

		renderSim(graphicsSubsystem);
		renderGfxComponents();

		long rStopTs = System.currentTimeMillis();
		if (rStopTs - rStartTs >= 1000) {

			double framerate = renderings / ((rStopTs - rStartTs) / (double) 1000);
			if (logging) {
				System.out.println("framerate: " + framerate + " fps");
			}

			rStartTs = System.currentTimeMillis();
			renderings = 0;

		}

	}

	private void renderGfxComponents() {

		Iterator<IGfxComponent> gfxIterator = gfxComponents.values().iterator();
		while (gfxIterator.hasNext()) {

			IGfxComponent gfxComponent = gfxIterator.next();
			if (gfxComponent.getState() == GfxState.STOPPED) {
				gfxIterator.remove();
			} else {
				gfxComponent.draw(graphicsSubsystem);
			}
		}

	}

	protected abstract void renderSim(IGraphicsSubsystem graphicsSubsystem);

	/**
	 * @throws Exception
	 */
	protected abstract void calculate(Dimension dimension) throws Exception;

	/**
	 * @throws Exception
	 */
	protected abstract void shutdown() throws Exception;

	@Override
	public void keyPressed(KeyEvent ke) {

		int pressedKeyCode = ke.getExtendedKeyCode();
		IKeyAction keyAction = keyActions.get(pressedKeyCode);
		if (keyAction != null) {

			String result = keyAction.getValue();
			String text = keyAction.text();
			updateFadingText(keyAction.textID(), (text != null ? text + ": " : "") + result, TEXT_POSITION, Color.YELLOW, TEXT_TIMEOUT,
					keyAction.toggleComponent());

			if (keyAction.withAction()) {
				keyValue = pressedKeyCode;
			} else {
				keyValue = null;
			}

		} else if (pressedKeyCode == KeyEvent.VK_PLUS) {

			if (keyValue != null) {

				keyAction = keyActions.get(keyValue);
				if (keyAction == null) {
					return;
				}

				keyAction.plus();
				updateFadingText(keyAction.textID(), keyAction.text() + ": " + keyAction.getValue(), TEXT_POSITION, Color.YELLOW,
						TEXT_TIMEOUT, keyAction.toggleComponent());

			}

		} else if (pressedKeyCode == KeyEvent.VK_MINUS) {

			if (keyValue != null) {

				keyAction = keyActions.get(keyValue);
				if (keyAction == null) {
					return;
				}

				keyAction.minus();
				updateFadingText(keyAction.textID(), keyAction.text() + ": " + keyAction.getValue(), TEXT_POSITION, Color.YELLOW,
						TEXT_TIMEOUT, keyAction.toggleComponent());

			}
		} else if (pressedKeyCode == KeyEvent.VK_SHIFT) {
			shift = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent ke) {

		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			stopped = true;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
			paused = !paused;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_SHIFT) {
			shift = false;
		}

	}

	private void updateFadingText(String id, String text, Point position, Color color, long timeout, boolean toggleComponent) {

		FadingText fadingText = (FadingText) gfxComponents.get(id);
		if (fadingText == null) {

			for (IGfxComponent gfxComponent : gfxComponents.values()) {
				gfxComponent.stop();
			}

			if (toggleComponent) {
				fadingText = new FadingText(text, position, color).setMaxIterations(FADING_TEXT_ITERATIONS);
			} else {
				fadingText = new FadingText(text, position, color, timeout).setMaxIterations(FADING_TEXT_ITERATIONS);
			}
			gfxComponents.put(id, fadingText);

		} else {

			boolean stopping = fadingText.getState() == GfxState.STOPPING;

			for (IGfxComponent gfxComponent : gfxComponents.values()) {
				gfxComponent.stop();
			}

			if (!toggleComponent || stopping) {
				fadingText.setText(text);
			}
		}

	}

	private void initBaseKeyActions() {
		keyActions.put(KeyEvent.VK_F1, new IKeyAction() {

			@Override
			public boolean withAction() {
				return false;
			}

			@Override
			public String textID() {
				return ID_STATUS;
			}

			@Override
			public String text() {
				return null;
			}

			@Override
			public void plus() {

			}

			@Override
			public void minus() {

			}

			@Override
			public String getValue() {
				return keyActions.entrySet().stream().filter(entry -> entry.getValue() != this).map(
						entry -> KeyEvent.getKeyText(entry.getKey()) + ": " + entry.getValue().text() + ": " + entry.getValue().getValue())
						.collect(Collectors.joining("\n"));
			}

			@Override
			public boolean toggleComponent() {
				return true;
			}
		});

	}

	public void addKeyAction(final int event, final IKeyAction action) {
		keyActions.put(event, action);
	}

	public IGraphicsSubsystem getGraphicsSubsystem() {
		return graphicsSubsystem;
	}

	public int getCalculationRate() {
		return calculationRate;
	}

	/**
	 * millisecond.per calculation
	 * 
	 * @param calculationRate
	 */
	public void setCalculationRate(int calculationRate) {
		this.calculationRate = Math.max(calculationRate, 1);
	}

	public long getMaxFrames() {
		return maxFrames;
	}

	public void setMaxFrames(long maxFrames) {
		this.maxFrames = maxFrames;
	}

	public ScreenParameters getScreenParameters() {
		return screenParameters;
	}

	public boolean isShift() {
		return shift;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public boolean isLogging() {
		return logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	public void addXInputHandle(final XInputHandle xInputHandle) {

		if (XInputDevice.isAvailable()) {

			try {

				XInputDevice xInputDevice = XInputDevice.getDeviceFor(xInputHandle.getPlayerNmb());
				if (xInputHandle.getXInputCombinedHandler() != null) {
					xInputDevice.addListener(xInputHandle.getXInputCombinedHandler());
				}

				xInputControllers.add(new XInputController(xInputDevice, xInputHandle));

			} catch (XInputNotLoadedException e) {
				System.out.println("WARN: XInput not loaded for device " + xInputHandle.getPlayerNmb() + "!");
			}
		} else {

			System.out.println("WARN: XInput not available!");

		}
	}

	@Override
	public void handleXInputLeftStick(float x, float y) {
		if (printButtons && (x != 0 || y != 0)) {
			System.out.println("XBox-C: LS : " + x + ", " + y);
		}
	}

	@Override
	public void handleXInputRightStick(float x, float y) {
		if (printButtons && (x != 0 || y != 0)) {
			System.out.println("XBox-C: RS : " + x + ", " + y);
		}
	}

	@Override
	public void handleXInputLT(float value) {
		if (printButtons && value != 0) {
			System.out.println("XBox-C: LT : " + value);
		}
	}

	@Override
	public void handleXInputRT(float value) {
		if (printButtons && value != 0) {
			System.out.println("XBox-C: RT : " + value);
		}
	}

	@Override
	public void buttonChanged(XInputButton button, boolean pressed) {
		if (printButtons) {
			System.out.println("XBox-C: " + button + " : " + pressed);
		}
	}

	@Override
	public void connected() {
		if (printButtons) {
			System.out.println("XBox-C: connected");
		}
	}

	@Override
	public void disconnected() {
		if (printButtons) {
			System.out.println("XBox-C: disconnected");
		}
	}

	public boolean isPrintButtons() {
		return printButtons;
	}

	public void setPrintButtons(boolean printButtons) {
		this.printButtons = printButtons;
	}

	public void stop() {
		stopped = true;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
