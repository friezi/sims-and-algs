/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.zintel.gfx.IRenderer;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class VLChainNet2D implements IVLEdgeContainer2D {

	private IRenderer<VLChainNet2D> renderer;

	private List<VLEdge2D> edges = new ArrayList<>();

	// for each vertical position all horizontal chains
	private List<List<List<VLEdge2D>>> edgesH = new ArrayList<>();
	// for each horizontal position all vertical chains
	private List<List<List<VLEdge2D>>> edgesV = new ArrayList<>();

	/**
	 * 
	 */
	public VLChainNet2D(VLVertex2D topleft, VLVertex2D topright, int height, int chainLinks, int dimHorizontal, int dimVertical,
			IRenderer<VLChainNet2D> renderer, IRenderer<VLEdge2D> edgeRenderer) {

		this.renderer = renderer;

		List<VLVertex2D> verticesV = new ArrayList<>();
		List<VLVertex2D> verticesH = new ArrayList<>();
		VLVertex2D current = null;
		for (int h = 0; h < dimHorizontal; h++) {
			// generate the initial horizontal node-vertices.

			if (h == 0) {
				current = topleft;
			} else if (h == dimHorizontal - 1) {
				current = topright;
			} else {

				current = new VLVertex2D(
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

			List<List<VLEdge2D>> currentChainsH = new ArrayList<>();
			edgesH.add(currentChainsH);

			if (v > 0) {

				verticesH = new ArrayList<>();
				for (int h = 0; h < dimHorizontal; h++) {

					List<List<VLEdge2D>> currentChainsV = edgesV.get(h);
					final VLVertex2D top = verticesV.get(h);
					VLVertex2D bottom = new VLVertex2D(new Vector2D(top.getCurrent().x, top.getCurrent().y + height));
					final VLChain2D chainV = new VLChain2D(top, bottom, chainLinks, null, edgeRenderer);
					edges.addAll(chainV.getEdges());
					currentChainsV.add(chainV.getEdges());
					verticesH.add(bottom);

				}
			}

			VLVertex2D left = null;
			for (int h = 0; h < dimHorizontal; h++) {

				VLVertex2D right = verticesH.get(h);
				if (h > 0) {

					final VLChain2D chain = new VLChain2D(left, right, chainLinks, null, edgeRenderer);
					edges.addAll(chain.getEdges());
					currentChainsH.add(chain.getEdges());

				}
				left = right;
			}
			verticesV = verticesH;
		}

	}

	private VLChainNet2D(IRenderer<VLChainNet2D> renderer, List<VLEdge2D> edges, List<List<List<VLEdge2D>>> edgesH,
			List<List<List<VLEdge2D>>> edgesV) {
		this.renderer = renderer;
		this.edges = edges;
		this.edgesH = edgesH;
		this.edgesV = edgesV;
	}

	public VLChainNet2D setColor(Color color) {
		for (VLEdge2D edge : getEdges()) {
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
	public List<VLEdge2D> getEdges() {
		return edges;
	}

	public List<List<List<VLEdge2D>>> getEdgesH() {
		return edgesH;
	}

	public List<List<List<VLEdge2D>>> getEdgesV() {
		return edgesV;
	}

	public IRenderer<VLChainNet2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(IRenderer<VLChainNet2D> renderer) {
		this.renderer = renderer;
	}

	private List<List<List<VLEdge2D>>> dcopyChainsList(List<List<List<VLEdge2D>>> chains) {

		List<List<List<VLEdge2D>>> newChains = new ArrayList<>(chains.size());
		for (List<List<VLEdge2D>> chain : chains) {

			List<List<VLEdge2D>> newChain = new ArrayList<>(chain.size());
			for (List<VLEdge2D> edges : chain) {

				List<VLEdge2D> newEdges = new ArrayList<>(chain.size());
				for (VLEdge2D edge : edges) {
					newEdges.add(edge.dcopy());
				}

				newChain.add(newEdges);
			}

			newChains.add(newChain);
		}

		return newChains;

	}

	private void addAllEdges(List<VLEdge2D> allEdges, List<List<List<VLEdge2D>>> chains) {

		for (List<List<VLEdge2D>> chain : chains) {
			for (List<VLEdge2D> edges : chain) {
				allEdges.addAll(edges);
			}
		}
	}

	@Override
	public IVLEdgeContainer2D dcopy() {
		// FIXME linked vertices are not maintained by dcopy()!!!!

		List<List<List<VLEdge2D>>> newEdgesH = dcopyChainsList(edgesH);
		List<List<List<VLEdge2D>>> newEdgesV = dcopyChainsList(edgesV);
		List<VLEdge2D> allEdges = new ArrayList<>();
		addAllEdges(allEdges, newEdgesH);
		addAllEdges(allEdges, newEdgesV);

		return new VLChainNet2D(renderer, allEdges, newEdgesH, newEdgesV);
	}

}