/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.zintel.math.Utils;

/**
 * @author friedemann.zintel
 *
 */
public class ChainNet2D extends EdgeContainer2D {

	private static class ColorChooser implements Edge2D.EdgeColorChooser {

		@Override
		public Color getColor(Edge2D edge) {

			final Color origColor = edge.getOrigColor();
			double ratio = Vector2D.distance(edge.getFirst().getCurrent(), edge.getSecond().getCurrent()) / edge.getLength();
			if (ratio != 1) {

				final float[] cValues = origColor.getRGBComponents(null);
				return new Color(adjustColor(cValues[0], ratio), adjustColor(cValues[1], ratio), adjustColor(cValues[2], ratio),
						cValues[3]);

			}

			return origColor;
		}

		private float adjustColor(final float value, double ratio) {
			return (ratio > 1 ? (float) (value / (1.0 + (ratio - 1.0) / 2)) : (float) (1.0 - (1 - value) * (1.0 * ratio)));
		}

	}

	/**
	 * 
	 */
	public ChainNet2D(Vertex2D topleft, Vertex2D topright, int height, int chainLinks, int chainsVertical, int chainsHorizontal) {

		List<Vertex2D> nodesH = new ArrayList<>();
		List<Vertex2D> nodesV = new ArrayList<>();
		Vertex2D previousH = null;
		for (int v = 1; v <= chainsVertical; v++) {

			if (v == 1) {
				previousH = topleft;
			} else if (v == chainsVertical) {
				previousH = topright;
			} else {

				final Vertex2D currentH = new Vertex2D(
						new Vector2D(Utils.interpolateLinearReal(topleft.getCurrent().x, topright.getCurrent().x, v, chainsHorizontal),
								topleft.getCurrent().y));
				currentH.setPinned(topleft.isPinned());
				previousH = currentH;
			}

			nodesV.add(previousH);

		}
		for (int h = 1; h <= chainsHorizontal; h++) {

			if (h > 1) {
				nodesV = new ArrayList<>();
				for (Vertex2D node : nodesH) {

					final Vertex2D currentV = new Vertex2D(new Vector2D(node.getCurrent().x, node.getCurrent().y + height));
					final Chain2D chainV = new Chain2D(node, currentV, chainLinks);
					getEdges().addAll(chainV.getEdges());
					nodesV.add(currentV);

				}
			}

			previousH = null;
			for (int v = 0; v < chainsVertical; v++) {

				if (v == 0) {
					previousH = nodesV.get(v);
				} else {

					final Vertex2D currentH = nodesV.get(v);
					final Chain2D chain = new Chain2D(previousH, currentH, chainLinks);
					getEdges().addAll(chain.getEdges());

					previousH = currentH;
				}
			}
			nodesH = nodesV;
		}

		for (Edge2D edge : getEdges()) {
			edge.setColorChooser(new ColorChooser());
		}

	}

	public ChainNet2D setColor(Color color) {
		for (Edge2D edge : getEdges()) {
			edge.setColor(color);
		}
		return this;
	}

}
