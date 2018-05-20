/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;
import java.util.Iterator;

/**
 * @author Friedemann
 *
 */
public abstract class APointInterpolater2D implements Iterator<IterationUnit2D>, Iterable<IterationUnit2D> {

	private final Point start;

	private final Point end;

	public APointInterpolater2D(Point start, Point end) {
		this.start = start;
		this.end = end;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	@Override
	public Iterator<IterationUnit2D> iterator() {
		return this;
	}

}
