/**
 * 
 */
package de.zintel.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import de.zintel.math.MathUtils;

/**
 * @author Friedemann
 *
 */
public class Test {

	private static class Item {

		private final String id;
		private final String node;

		public Item(String id, String node) {
			this.id = id;
			this.node = node;
		}

		public String getId() {
			return id;
		}

		public String getNode() {
			return node;
		}

		public boolean isNeighbour(final Item item) {
			return node.equals(item.getNode());
		}
	}

	private static class Node {

		private final Item item;
		private final Collection<Node> neighbours = new LinkedList<>();

		public Node(Item item) {
			this.item = item;
		}

		public Item getItem() {
			return item;
		}

		public Collection<Node> getNeighbours() {
			return neighbours;
		}

	}

	/**
	 * 
	 */
	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		connect();
	}

	private static void realIp() {

		double start = 0.5;
		double end = 1.5;
		int maxIt = 10;

		for (int i = 1; i <= maxIt; i++) {
			System.out.println(MathUtils.interpolateLinearReal(start, end, i, maxIt));
		}

	}

	private static void connect() {

		int size = 6000;

		Collection<Item> items = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			items.add(new Item(String.valueOf(i), "node"));
		}

		Collection<Node> nodes = new ArrayList<>();
		long startTS = System.currentTimeMillis();
		for (Item item : items) {

			final Node currentNode = new Node(item);

			for (Node node : nodes) {
				if (item.isNeighbour(node.getItem())) {

					currentNode.getNeighbours().add(node);
					node.getNeighbours().add(currentNode);

				}
			}

			nodes.add(currentNode);

		}
		System.out.println("time: " + (System.currentTimeMillis() - startTS) / 1000);

	}

}
