package solution;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTrees {

    private final static int MIN_DIST = 1; //change these numbers
    private final static int MAX_DIST = 2;

    private final static int ITERATIONS = 100;

    private List<Node> nodes;

    RandomTrees() {
        nodes = new LinkedList<>();
    }

    public void step() {
        List<Node> newNodes = new LinkedList<>();
        for(Node node : nodes) {
            if(node.isComplete()) {
                continue;
            }
            if(node.left == null) {
                double x = node.x - ThreadLocalRandom.current().nextInt(MIN_DIST, MAX_DIST);
                double y = node.y;
                node.left = new Node(x, y);
                node.left.right = node;
                newNodes.add(node.left);
            }
            if(node.right == null) {
                double x = node.x + ThreadLocalRandom.current().nextInt(MIN_DIST, MAX_DIST);
                double y = node.y;
                node.right = new Node(x, y);
                node.right.left = node;
                newNodes.add(node.right);
            }
            if(node.above == null) {
                double x = node.x;
                double y = node.y + ThreadLocalRandom.current().nextInt(MIN_DIST, MAX_DIST);
                node.above = new Node(x, y);
                node.above.below = node;
                newNodes.add(node.above);
            }
            if(node.below == null) {
                double x = node.x;
                double y = node.y - ThreadLocalRandom.current().nextInt(MIN_DIST, MAX_DIST);
                node.below = new Node(x, y);
                node.below.above = node;
                newNodes.add(node.below);
            }
            newNodes.add(node);
        }
        nodes = newNodes;
    }

    public void run() {
        nodes.add(new Node(50, 50));
        for(int i = 0; i < ITERATIONS; i ++) {
            step();
        }
    }

    public class Node {
        Node left;
        Node right;
        Node above;
        Node below;

        double x;
        double y;

        Node(double x, double y) {
            this.x = x;
            this.y = y;

            System.out.println("new node at " + x + ", " + y);
        }

        boolean isComplete() {
            return left != null && right != null && above != null && below != null;
        }
    }

    public static void main(String[] args) {
        new RandomTrees().run();
    }
}
