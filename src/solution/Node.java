package solution;

import java.util.HashSet;
import java.util.Set;

/**
 * A generic Node.
 *
 * @param <T> The type of object stored in the node.
 */
public class Node<T> {

    // The object stored in the node.
    private T item;

    // Information about the point needed for A* algorithm
    public int g;
    public int h;
    public Set<Node<T>> connected;
    public Node<T> parent;

    /**
     * Construct a new Node.
     *
     * @param item The item stored in this node
     */
    public Node(T item) {
        this.item = item;
        connected = new HashSet<>();
    }

    /**
     * Find the box stored at this point.
     *
     * @return The box at this point or null if point is empty.
     */
    public T getItem() {
        return item;
    }

    public int f() {
        return g + h;
    }

    @Override
    public String toString() {
        return item.toString();
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        return item.equals(((Node) obj).item);
    }
}
