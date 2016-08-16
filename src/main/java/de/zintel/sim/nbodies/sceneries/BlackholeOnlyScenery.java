/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.awt.Color;

import de.zintel.gfx.g2d.Vector2D;
import de.zintel.physics.Body;
import de.zintel.physics.GravitationSystem;
import de.zintel.sim.nbodies.SwingBodyProperty;

/**
 * @author friedo
 *
 */
public class BlackholeOnlyScenery extends CommonScenery {

	/**
	 * @param width
	 * @param height
	 * @param sceneryConfig
	 */
	public BlackholeOnlyScenery(int width, int height, SceneryConfig sceneryConfig) {
		super(width, height, sceneryConfig);
	}

	@Override
	public GravitationSystem createGravitationSystem() {

		final GravitationSystem gravitationSystem = super.createGravitationSystem();

		final Body blackhole1 = new Body("blackhole1", GravitationSystem.calculateSize(1000000) / 10, 8000000,
				makeRandomPoint((int) spaceMin(width / 3), (int) spaceMin(height / 3), (int) spaceMax(width / 3),
						(int) spaceMax(height / 3)),
				new Vector2D(0, 0));
		final SwingBodyProperty blackhole1Property = new SwingBodyProperty();
		final Color blackhole1Color = Color.RED;
		blackhole1Property.setBodyColor(blackhole1Color);
		blackhole1Property.setCurrentBodyColor(blackhole1Color);
		blackhole1Property.setCurrentCoronaColor(blackhole1Color);
		blackhole1.setProperty(SwingBodyProperty.CLASSNAME, blackhole1Property);
		gravitationSystem.addBody(blackhole1);

		final Body blackhole2 = new Body("blackhole2", GravitationSystem.calculateSize(1000000) / 10, 8000000,
				makeRandomPoint((int) spaceMin(width / 3), (int) spaceMin(height / 3), (int) spaceMax(width / 3),
						(int) spaceMax(height / 3)),
				new Vector2D(0, 0));
		final SwingBodyProperty blackhole2Property = new SwingBodyProperty();
		final Color blackhole2Color = Color.BLUE;
		blackhole2Property.setBodyColor(blackhole2Color);
		blackhole2Property.setCurrentBodyColor(blackhole2Color);
		blackhole2Property.setCurrentCoronaColor(blackhole2Color);
		blackhole2.setProperty(SwingBodyProperty.CLASSNAME, blackhole2Property);
		gravitationSystem.addBody(blackhole2);

		final Body blackhole3 = new Body("blackhole3", GravitationSystem.calculateSize(1000000) / 10, 8000000,
				makeRandomPoint((int) spaceMin(width / 3), (int) spaceMin(height / 3), (int) spaceMax(width / 3),
						(int) spaceMax(height / 3)),
				new Vector2D(0, 0));
		final SwingBodyProperty blackhole3Property = new SwingBodyProperty();
		final Color blackhole3Color = Color.ORANGE;
		blackhole3Property.setBodyColor(blackhole3Color);
		blackhole3Property.setCurrentBodyColor(blackhole3Color);
		blackhole3Property.setCurrentCoronaColor(blackhole3Color);
		blackhole3.setProperty(SwingBodyProperty.CLASSNAME, blackhole3Property);
		gravitationSystem.addBody(blackhole3);

		return gravitationSystem;
	}

}
