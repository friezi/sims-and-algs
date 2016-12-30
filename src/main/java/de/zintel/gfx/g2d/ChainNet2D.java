/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.zintel.math.Utils;

/**
 * @author friedemann.zintel
 *
 */
public class ChainNet2D implements IEdgeContainer2D {

	private IRenderer<ChainNet2D> renderer;

	private final Collection<Edge2D> edges = new LinkedList<>();

	/**
	 * 
	 */
	public ChainNet2D(Vertex2D topleft, Vertex2D topright, int height, int chainLinks, int chainsHorizontal, int chainsVertical,
			IRenderer<ChainNet2D> renderer, IRenderer<Chain2D> chainRenderer, IRenderer<Edge2D> edgeRenderer) {

		this.renderer = renderer;

		List<Vertex2D> nodesH = new ArrayList<>();
		List<Vertex2D> nodesV = new ArrayList<>();
		Vertex2D previousH = null;
		for (int v = 1; v <= chainsHorizontal; v++) {

			if (v == 1) {
				previousH = topleft;
			} else if (v == chainsHorizontal) {
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
		for (int h = 1; h <= chainsVertical; h++) {

			if (h > 1) {
				nodesV = new ArrayList<>();
				for (Vertex2D node : nodesH) {

					final Vertex2D currentV = new Vertex2D(new Vector2D(node.getCurrent().x, node.getCurrent().y + height));
					final Chain2D chainV = new Chain2D(node, currentV, chainLinks, chainRenderer, edgeRenderer);
					edges.addAll(chainV.getEdges());
					nodesV.add(currentV);

				}
			}

			previousH = null;
			for (int v = 0; v < chainsHorizontal; v++) {

				if (v == 0) {
					previousH = nodesV.get(v);
				} else {

					final Vertex2D currentH = nodesV.get(v);
					final Chain2D chain = new Chain2D(previousH, currentH, chainLinks, chainRenderer, edgeRenderer);
					edges.addAll(chain.getEdges());

					previousH = currentH;
				}
			}
			nodesH = nodesV;
		}

		final AdjustingColorModifier colorModifier = new AdjustingColorModifier();
		for (Edge2D edge : getEdges()) {
			edge.setColorModifier(colorModifier);
		}

	}

	public ChainNet2D setColor(Color color) {
		for (Edge2D edge : getEdges()) {
			edge.setColor(color);
		}
		return this;
	}

	@Override
	public void render() {
		renderer.render(this);
	}

	@Override
	public Collection<Edge2D> getEdges() {
		return edges;
	}

}
