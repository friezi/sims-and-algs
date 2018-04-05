/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class ChainNet2D implements IEdgeContainer2D {

	private IRenderer<ChainNet2D> renderer;

	private List<Edge2D> edges = new ArrayList<>();

	// for each vertical position all horizontal chains
	private List<List<List<Edge2D>>> edgesH = new ArrayList<>();
	// for each horizontal position all vertical chains
	private List<List<List<Edge2D>>> edgesV = new ArrayList<>();

	/**
	 * 
	 */
	public ChainNet2D(Vertex2D topleft, Vertex2D topright, int height, int chainLinks, int dimHorizontal, int dimVertical,
			IRenderer<ChainNet2D> renderer, IRenderer<Edge2D> edgeRenderer) {

		this.renderer = renderer;

		List<Vertex2D> verticesV = new ArrayList<>();
		List<Vertex2D> verticesH = new ArrayList<>();
		Vertex2D current = null;
		for (int h = 0; h < dimHorizontal; h++) {
			// generate the initial horizontal node-vertices.

			if (h == 0) {
				current = topleft;
			} else if (h == dimHorizontal - 1) {
				current = topright;
			} else {

				current = new Vertex2D(
						new Vector2D(MathUtils.interpolateLinearReal(topleft.getCurrent().x, topright.getCurrent().x, h + 1, dimHorizontal),
								topleft.getCurrent().y));
				current.setPinned(topleft.isPinned());
			}

			verticesH.add(current);

		}

		for (int h = 0; h < dimHorizontal; h++) {
			edgesV.add(new ArrayList<>());
		}

		for (int v = 0; v < dimVertical; v++) {

			List<List<Edge2D>> currentChainsH = new ArrayList<>();
			edgesH.add(currentChainsH);

			if (v > 0) {

				verticesH = new ArrayList<>();
				for (int h = 0; h < dimHorizontal; h++) {

					List<List<Edge2D>> currentChainsV = edgesV.get(h);
					final Vertex2D top = verticesV.get(h);
					Vertex2D bottom = new Vertex2D(new Vector2D(top.getCurrent().x, top.getCurrent().y + height));
					final Chain2D chainV = new Chain2D(top, bottom, chainLinks, null, edgeRenderer);
					edges.addAll(chainV.getEdges());
					currentChainsV.add(chainV.getEdges());
					verticesH.add(bottom);

				}
			}

			Vertex2D left = null;
			for (int h = 0; h < dimHorizontal; h++) {

				Vertex2D right = verticesH.get(h);
				if (h > 0) {

					final Chain2D chain = new Chain2D(left, right, chainLinks, null, edgeRenderer);
					edges.addAll(chain.getEdges());
					currentChainsH.add(chain.getEdges());

				}
				left = right;
			}
			verticesV = verticesH;
		}

		final AdjustingColorModifier colorModifier = new AdjustingColorModifier();
		for (Edge2D edge : getEdges()) {
			edge.setColorModifier(colorModifier);
		}

	}

	private ChainNet2D(IRenderer<ChainNet2D> renderer, List<Edge2D> edges, List<List<List<Edge2D>>> edgesH, List<List<List<Edge2D>>> edgesV) {
		this.renderer = renderer;
		this.edges = edges;
		this.edgesH = edgesH;
		this.edgesV = edgesV;
	}

	public ChainNet2D setColor(Color color) {
		for (Edge2D edge : getEdges()) {
			edge.setColor(color);
		}
		return this;
	}

	@Override
	public void render() {
		if (renderer != null) {
			renderer.render(this);
		}
	}

	@Override
	public List<Edge2D> getEdges() {
		return edges;
	}

	public List<List<List<Edge2D>>> getEdgesH() {
		return edgesH;
	}

	public List<List<List<Edge2D>>> getEdgesV() {
		return edgesV;
	}

	public IRenderer<ChainNet2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(IRenderer<ChainNet2D> renderer) {
		this.renderer = renderer;
	}

	private List<List<List<Edge2D>>> dcopyChainsList(List<List<List<Edge2D>>> chains) {

		List<List<List<Edge2D>>> newChains = new ArrayList<>(chains.size());
		for (List<List<Edge2D>> chain : chains) {

			List<List<Edge2D>> newChain = new ArrayList<>(chain.size());
			for (List<Edge2D> edges : chain) {

				List<Edge2D> newEdges = new ArrayList<>(chain.size());
				for (Edge2D edge : edges) {
					newEdges.add(edge.dcopy());
				}

				newChain.add(newEdges);
			}

			newChains.add(newChain);
		}

		return newChains;

	}

	private void addAllEdges(List<Edge2D> allEdges, List<List<List<Edge2D>>> chains) {

		for (List<List<Edge2D>> chain : chains) {
			for (List<Edge2D> edges : chain) {
				allEdges.addAll(edges);
			}
		}
	}

	@Override
	public IEdgeContainer2D dcopy() {
		// FIXME linked vertices are not maintained by dcopy()!!!!

		List<List<List<Edge2D>>> newEdgesH = dcopyChainsList(edgesH);
		List<List<List<Edge2D>>> newEdgesV = dcopyChainsList(edgesV);
		List<Edge2D> allEdges = new ArrayList<>();
		addAllEdges(allEdges, newEdgesH);
		addAllEdges(allEdges, newEdgesV);

		return new ChainNet2D(renderer, allEdges, newEdgesH, newEdgesV);
	}

}
