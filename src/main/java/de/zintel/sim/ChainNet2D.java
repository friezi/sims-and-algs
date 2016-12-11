/**
 * 
 */
package de.zintel.sim;

import java.util.ArrayList;
import java.util.Collection;

import de.zintel.gfx.g2d.Chain2D;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.EdgeContainer2D;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.gfx.g2d.Vertex2D;
import de.zintel.math.Utils;

/**
 * @author friedemann.zintel
 *
 */
public class ChainNet2D extends EdgeContainer2D {

	/**
	 * 
	 */
	public ChainNet2D(Vertex2D topleft, Vertex2D topright, int height, int chainLinks, int chainsVertical, int chainsHorizontal) {
		// TODO Auto-generated constructor stub

		Collection<Vertex2D> nodesH = new ArrayList<>();
		Collection<Vertex2D> nodesV;
		for (int h = 1; h <= chainsHorizontal; h++) {

			final double y = (h == 1 ? topleft.getCurrent().y
					: Utils.interpolateLinearReal(topleft.getCurrent().y, topleft.getCurrent().y + height, h, chainsVertical));

			if (h > 1) {

				nodesV = new ArrayList<>();
				for (Vertex2D node : nodesH) {

					final Vertex2D currentV = new Vertex2D(new Vector2D(node.getCurrent().x, node.getCurrent().y + height));
					final Chain2D chainV = new Chain2D(node, currentV, chainLinks);
					getEdges().addAll(chainV.getEdges());
					nodesV.add(currentV);

				}
			}

			Vertex2D previousV = null;
			for (int v = 1; v <= chainsVertical; v++) {

				if (v == 1) {
					if (h == 1) {
						previousV = topleft;
					} else {
						previousV = new Vertex2D(new Vector2D(topleft.getCurrent().x, y));
					}
				} else if (v == chainsVertical) {

					final Chain2D chainH = new Chain2D(previousV, topright, chainLinks);
					getEdges().addAll(chainH.getEdges());

					previousV = topright;

				} else {

					final Vertex2D currentH = new Vertex2D(new Vector2D(
							Utils.interpolateLinearReal(topleft.getCurrent().x, topright.getCurrent().x, v, chainsHorizontal), y));
					if (h == 1) {
						currentH.setPinned(topleft.isPinned());
					}
					final Chain2D chain = new Chain2D(previousV, currentH, chainLinks);
					getEdges().addAll(chain.getEdges());

					previousV = currentH;
				}

				nodesH.add(previousV);

			}

		}

	}

}
