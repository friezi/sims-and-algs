/**
 * 
 */
package de.zintel.sim.epicycles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.zintel.gfx.g3d.EpicyclesPointGenerator.Epicycle;

/**
 * @author friedemann.zintel
 *
 */
public class FileCyclesSet implements IECArgumentSet {

	private static final Pattern PATTERN_LINE = Pattern.compile("circles: \\d+: \\[(.*)\\]");
	private static final Pattern PATTERN_CIRCLES = Pattern
			.compile("(?:FourierCircle \\[radius=((?:\\d|-|\\.)+), angularVelocity=((?:\\d|-|\\.)+)\\],?)+?");

	private final InputStream istream;

	private final ArrayList<LinkedList<Epicycle>> epicycles = new ArrayList<>();

	private int idx = -1;

	public FileCyclesSet(InputStream istream) throws IOException {
		this.istream = istream;
		read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.epicycles.IFParameterSet#getSpeed()
	 */
	@Override
	public int getSpeed() {
		return 50;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.epicycles.IFParameterSet#getCircles()
	 */
	@Override
	public Collection<Epicycle> getCycles() {

		idx = ++idx % epicycles.size();
		return epicycles.get(idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.epicycles.IFParameterSet#getIterations()
	 */
	@Override
	public int getIterations() {
		return 10000;
	}

	private void read() throws IOException {

		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(istream))) {

			String line;
			while ((line = reader.readLine()) != null) {

				final LinkedList<Epicycle> linecycles = convertLine(line);
				if (linecycles != null) {
					epicycles.add(linecycles);
				}
			}
		}

	}

	private LinkedList<Epicycle> convertLine(final String line) {

		final LinkedList<Epicycle> list = new LinkedList<>();

		final Matcher linematcher = PATTERN_LINE.matcher(line);
		if (!linematcher.matches()) {
			System.out.println("line '" + line + "' does not match!");
			return null;
		}
		final String linecycles = linematcher.group(1);

		final Matcher cyclesmatcher = PATTERN_CIRCLES.matcher(linecycles);
		while (cyclesmatcher.find()) {

			final String radius = cyclesmatcher.group(1);
			final String angularVelocity = cyclesmatcher.group(2);
			list.add(new Epicycle(Double.valueOf(radius), Double.valueOf(angularVelocity)));
		}

		return list;
	}

}
