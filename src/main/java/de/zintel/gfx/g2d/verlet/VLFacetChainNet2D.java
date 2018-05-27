/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class VLFacetChainNet2D implements IVLEdgeContainer2D {

	private Consumer<VLFacetChainNet2D> renderer;

	private List<VLEdge2D> edges = new ArrayList<>();

	// for each vertical position all horizontal chains
	private List<List<List<VLEdge2D>>> edgesH = new ArrayList<>();
	// for each horizontal position all vertical chains
	private List<List<List<VLEdge2D>>> edgesV = new ArrayList<>();

	private Collection<VLFacet2D> facets = new ArrayList<>();

	private Collection<Collection<VLEdge2D>> sublayerEdges = new ArrayList<>();

	/**
	 * 
	 */
	public VLFacetChainNet2D(VLVertexSkid topleft, VLVertexSkid topright, int height, int chainLinks, int dimHorizontal, int dimVertical,
			Consumer<VLFacetChainNet2D> renderer, Consumer<VLEdge2D> edgeRenderer) {

		this.renderer = renderer;

		List<VLVertexSkid> verticesV = new ArrayList<>();
		List<VLVertexSkid> verticesH = new ArrayList<>();
		VLVertexSkid current = null;
		for (int h = 0; h < dimHorizontal; h++) {
			// generate the initial horizontal node-vertices.

			if (h == 0) {
				current = topleft;
			} else if (h == dimHorizontal - 1) {
				current = topright;
			} else {

				current = new VLVertexSkid(new VLVertex2D(new Vector2D(MathUtils.interpolateLinearReal(topleft.getVertex().getCurrent().x,
						topright.getVertex().getCurrent().x, h + 1, dimHorizontal), topleft.getVertex().getCurrent().y)));
				current.setSticky(topleft.isSticky());
			}

			verticesH.add(current);

		}

		// create edgeList for all horizontal nodes;
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
					final VLVertexSkid top = verticesV.get(h);
					VLVertexSkid bottom = new VLVertexSkid(
							new VLVertex2D(new Vector2D(top.getVertex().getCurrent().x, top.getVertex().getCurrent().y + height)));
					final VLChain2D chainV = new VLChain2D(top, bottom, chainLinks, null, edgeRenderer);
					edges.addAll(chainV.getEdges());
					currentChainsV.add(chainV.getEdges());
					verticesH.add(bottom);

				}
			}

			VLVertexSkid left = null;
			for (int h = 0; h < dimHorizontal; h++) {

				VLVertexSkid right = verticesH.get(h);
				if (h > 0) {

					final VLChain2D chain = new VLChain2D(left, right, chainLinks, null, edgeRenderer);
					edges.addAll(chain.getEdges());
					currentChainsH.add(chain.getEdges());

				}
				left = right;
			}
			verticesV = verticesH;
		}

		createSublayerEdges(edgeRenderer);

	}

	public void createSublayerEdges(Consumer<VLEdge2D> edgeRenderer) {

		for (int row = 0; row < edgesH.size() - 1; row++) {

			List<List<VLEdge2D>> topChains = edgesH.get(row);
			List<List<VLEdge2D>> bottomChains = edgesH.get(row + 1);

			for (int column = 0; column < edgesV.size() - 1; column++) {

				List<List<VLEdge2D>> leftChains = edgesV.get(column);
				List<List<VLEdge2D>> rightChains = edgesV.get(column + 1);

				facets.addAll(mesh2facets(topChains.get(column), bottomChains.get(column), leftChains.get(row), rightChains.get(row),
						edgeRenderer));

			}
		}

		Collection<VLEdge2D> subedges = new ArrayList<>();
		for (VLFacet2D facet : facets) {
			subedges.addAll(facet.getEdges());
		}

		sublayerEdges.add(subedges);
	}

	private Collection<VLFacet2D> mesh2facets(List<VLEdge2D> topChain, List<VLEdge2D> bottomChain, List<VLEdge2D> leftChain,
			List<VLEdge2D> rightChain, Consumer<VLEdge2D> edgeRenderer) {

		final int rows = leftChain.size();
		final int columns = topChain.size();

		final Collection<VLFacet2D> facets = new ArrayList<>();
		final Square[][] innerSquares = new Square[rows][columns];

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {

				VLEdge2D e1;
				if (row == 0) {
					// lediglich neue VLVertexSkids und Edge erzeugen
					e1 = createInnerEdge(topChain.get(column), edgeRenderer);
				} else {
					e1 = innerSquares[row - 1][column].e3;
				}

				VLEdge2D e2;
				if (column == columns - 1) {
					e2 = createInnerEdge(rightChain.get(row), edgeRenderer);
				} else {
					if (row == rows - 1) {
						e2 = newEdge(e1.getSecond(),
								new VLVertexSkid(bottomChain.get(column).getSecond().getVertex()).setSticky(true).setDependent(true),
								edgeRenderer);
					} else {
						e2 = newEdge(e1.getSecond(), new VLVertexSkid(new VLVertex2D(new Vector2D(e1.getSecond().getVertex().getCurrent().x,
								leftChain.get(row).getSecond().getVertex().getCurrent().y))), edgeRenderer);
					}
				}

				VLEdge2D e3;
				if (row == rows - 1) {
					e3 = createInnerEdge(bottomChain.get(column), edgeRenderer);
				} else {
					if (column == 0) {
						e3 = newEdge(new VLVertexSkid(leftChain.get(row).getSecond().getVertex()).setSticky(true).setDependent(true),
								e2.getSecond(), edgeRenderer);
					} else {
						e3 = newEdge(innerSquares[row][column - 1].e2.getSecond(), e2.getSecond(), edgeRenderer);
					}
				}

				VLEdge2D e4;
				if (column == 0) {
					e4 = createInnerEdge(leftChain.get(row), edgeRenderer);
				} else {
					e4 = newEdge(innerSquares[row][column - 1].e2.getSecond(), innerSquares[row][column - 1].e2.getFirst(), edgeRenderer);
				}

				innerSquares[row][column] = new Square(e1, e2, e3, e4);

				// create facets
				VLEdge2D diagonal = newEdge(e1.getSecond(), e3.getFirst(), edgeRenderer);
				facets.add(new VLFacet2D(e1, diagonal, e4, null));
				facets.add(new VLFacet2D(diagonal, e3, e2, null));
			}
		}

		return facets;

	}

	private VLEdge2D createInnerEdge(final VLEdge2D baseEdge, Consumer<VLEdge2D> edgeRenderer) {
		return new VLEdge2D(new VLVertexSkid(baseEdge.getFirst().getVertex()).setSticky(true).setDependent(true),
				new VLVertexSkid(baseEdge.getSecond().getVertex()).setSticky(true).setDependent(true), Color.WHITE, edgeRenderer);
	}

	private VLEdge2D newEdge(VLVertexSkid first, VLVertexSkid second, Consumer<VLEdge2D> edgeRenderer) {
		return new VLEdge2D(first, second, Color.WHITE, edgeRenderer);
	}

	private VLFacetChainNet2D(Consumer<VLFacetChainNet2D> renderer, List<VLEdge2D> edges, List<List<List<VLEdge2D>>> edgesH,
			List<List<List<VLEdge2D>>> edgesV) {
		this.renderer = renderer;
		this.edges = edges;
		this.edgesH = edgesH;
		this.edgesV = edgesV;
	}

	public VLFacetChainNet2D setColor(Color color) {

		getEdges().stream().forEach(edge -> edge.setColor(color));
		sublayerEdges.stream().forEach(edges -> edges.stream().forEach(edge -> edge.setColor(color)));

		return this;
	}

	@Override
	public void render() {
		if (renderer != null) {
			renderer.accept(this);
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

	public Consumer<VLFacetChainNet2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(Consumer<VLFacetChainNet2D> renderer) {
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

		final Collection<Collection<VLEdge2D>> copySublayerEdges = new ArrayList<>();
		for (Collection<VLEdge2D> edges : sublayerEdges) {

			final Collection<VLEdge2D> copyEdges = new ArrayList<>();
			copySublayerEdges.add(copyEdges);
			edges.stream().forEach(edge -> copyEdges.add(edge.dcopy()));

		}

		Collection<VLFacet2D> facetsCopy = new ArrayList<>(facets.size());
		for (VLFacet2D facet : facets) {
			facetsCopy.add(facet.dcopy());
		}

		return new VLFacetChainNet2D(renderer, allEdges, newEdgesH, newEdgesV).setSublayerEdges(copySublayerEdges).setFacets(facetsCopy);
	}

	public Collection<Collection<VLEdge2D>> getSublayerEdges() {
		return sublayerEdges;
	}

	public VLFacetChainNet2D setSublayerEdges(Collection<Collection<VLEdge2D>> sublayerEdges) {
		this.sublayerEdges = sublayerEdges;
		return this;
	}

	private static class Square {

		public VLEdge2D e1;
		public VLEdge2D e2;
		public VLEdge2D e3;
		public VLEdge2D e4;

		public Square(VLEdge2D e1, VLEdge2D e2, VLEdge2D e3, VLEdge2D e4) {
			this.e1 = e1;
			this.e2 = e2;
			this.e3 = e3;
			this.e4 = e4;
		}
	}

	public Collection<VLFacet2D> getFacets() {
		return facets;
	}

	public VLFacetChainNet2D setFacets(Collection<VLFacet2D> facets) {
		this.facets = facets;
		return this;
	}

}
