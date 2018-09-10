package solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractSearch {
    protected AbstractNode start;
    protected AbstractNode goal;

    public AbstractSearch(AbstractNode start, AbstractNode goal) {
        this.start = start;
        this.goal = goal;
    }

    public static abstract class AbstractNode {
        public int g;
        public int h;
        public AbstractNode parent;

        public int f() {
            return g + h;
        }
    }

    protected abstract int heuristicCost(AbstractNode start, AbstractNode goal);
    protected abstract Set<AbstractNode> getNeighbours(AbstractNode node);

    public List<AbstractNode> run() {
        Set<AbstractNode> open = new HashSet<>();
        Set<AbstractNode> closed = new HashSet<>();

        start.g = 0;
        start.h = heuristicCost(start, goal);

        open.add(start);

        while(open.size() > 0) {
            AbstractNode current = null;

            for(AbstractNode node : open) {
                if(current == null || node.f() < current.f()) {
                    current = node;
                }
            }

            if(current == goal) {
                List<AbstractNode> path = new ArrayList<>();
                while(current.parent != null) {
                    path.add(0, current);
                    current = current.parent;
                }
                path.add(0, start);

                return path;
            }

            open.remove(current);
            closed.add(current);

            for(AbstractNode neighbour : getNeighbours(current)) {
                int nextG = current.g + 1;
                if(nextG < neighbour.g) {
                    open.remove(neighbour);
                    closed.remove(neighbour);

                }
                if(!open.contains(neighbour) && !closed.contains(neighbour)) {
                    neighbour.g = nextG;
                    neighbour.h = heuristicCost(neighbour, goal);
                    neighbour.parent = current;
                    open.add(neighbour);
                }
            }
        }
        return null;
    }
}
