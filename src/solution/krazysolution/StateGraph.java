package solution.krazysolution;

import problem.ProblemSpec;

import java.util.*;

public class StateGraph {

    public static class StateNode {
        public Set<StateNode> connected;
        public State state;

        public StateNode(State state) {
            this.state = state;
            connected = new HashSet<>();
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StateNode && state.equals(((StateNode) obj).state);
        }

        @Override
        public String toString() {
            return state.toString();
        }

        //FOLLOWING CODE USED FOR A*
        public int g;
        public int h;

        public int f() {
            return g + h;
        }

        public StateNode parent;
    }

    public enum GraphType {
        ROBOT, BOXES, ALL
    }

    private StateNode start;
    private StateNode goal;
    private GraphType type;

    public StateGraph(StateNode start, StateNode goal, GraphType type) {
        this.start = start;
        this.goal = goal;
        this.type = type;
    }

    public StateGraph(StateNode start, StateNode goal) {
        this(start, goal, GraphType.ALL);
    }

    private int cost(StateNode start, StateNode end) {
        //TODO modify to be better
        int revCost = 0;
        for(int i = 0; i < start.state.mBoxes.size(); i++) {
            Box.MBox startBox = start.state.mBoxes.get(i);
            Box.MBox endBox = end.state.mBoxes.get(i);
            int distance = Math.abs(startBox.getX() - endBox.getX()) +
                    Math.abs(startBox.getY() - endBox.getY());
            int pow = (int)(Math.pow(0.999, distance) * 1000);
            revCost += pow;
            if(distance == 0) {
                revCost += 1000;
            }
        }
        return Integer.MAX_VALUE - revCost;
    }

    private Set<State> getRotationStates(State state) {
        Set<State> states = new HashSet<>();
        State s1 = state.saveState();
        s1.robot.rotateClockwise();
        State s2 = state.saveState();
        s2.robot.rotateAntiClockwise();
        states.add(s1);
        states.add(s2);
        return states;
    }

    private Set<State> getMovementStates(State state) {
        Set<State> states = new HashSet<>();
        State s1 = state.saveState();
        s1.robot.moveDown();
        State s2 = state.saveState();
        s2.robot.moveUp();
        State s3 = state.saveState();
        s3.robot.moveLeft();
        State s4 = state.saveState();
        s4.robot.moveRight();
        if (!s1.isRobotCollision()) {
            states.add(s1);
        }
        if (!s2.isRobotCollision()) {
            states.add(s2);
        }
        if (!s3.isRobotCollision()) {
            states.add(s3);
        }
        if (!s4.isRobotCollision()) {
            states.add(s4);
        }
        return states;
    }

    private Set<State> getBoxMovementStates(State state) {
        Set<State> states = new HashSet<>();
        for(int i = 0; i < state.mBoxes.size(); i++) {
            State s1 = state.saveState();
            s1.mBoxes.get(i).moveDown();
            State s2 = state.saveState();
            s2.mBoxes.get(i).moveUp();
            State s3 = state.saveState();
            s3.mBoxes.get(i).moveLeft();
            State s4 = state.saveState();
            s4.mBoxes.get(i).moveRight();
            if (!s1.isBoxCollision(s1.mBoxes.get(i))) {
                states.add(s1);
            }
            if (!s2.isBoxCollision(s2.mBoxes.get(i))) {
                states.add(s2);
            }
            if (!s3.isBoxCollision(s3.mBoxes.get(i))) {
                states.add(s3);
            }
            if (!s4.isBoxCollision(s4.mBoxes.get(i))) {
                states.add(s4);
            }
        }
        for(int i = 0; i < state.mObstacles.size(); i++) {
            State s1 = state.saveState();
            s1.mObstacles.get(i).moveDown();
            State s2 = state.saveState();
            s2.mObstacles.get(i).moveUp();
            State s3 = state.saveState();
            s3.mObstacles.get(i).moveLeft();
            State s4 = state.saveState();
            s4.mObstacles.get(i).moveRight();
            if (!s1.isBoxCollision(s1.mObstacles.get(i))) {
                states.add(s1);
            }
            if (!s2.isBoxCollision(s2.mObstacles.get(i))) {
                states.add(s2);
            }
            if (!s3.isBoxCollision(s3.mObstacles.get(i))) {
                states.add(s3);
            }
            if (!s4.isBoxCollision(s4.mObstacles.get(i))) {
                states.add(s4);
            }
        }
        return states;
    }

    private Set<StateNode> getNeighbours(StateNode node) {
        Set<State> states = new HashSet<>();
        Set<StateNode> result = new HashSet<>();
        if(type == GraphType.ROBOT || type == GraphType.ALL) {
            states.addAll(getRotationStates(node.state));
            states.addAll(getMovementStates(node.state));
        }
        if(type == GraphType.BOXES || type == GraphType.ALL) {
            states.addAll(getBoxMovementStates(node.state));
        }
        for (State state : states) {
            result.add(new StateNode(state));
        }
        return result;
    }

    public List<StateNode> aStar() {
        Set<StateNode> open = new HashSet<>();
        Set<StateNode> closed = new HashSet<>();

        start.g = 0;
        start.h = cost(start, goal);

        open.add(start);

        while (open.size() > 0) {
            StateNode current = null;

            for (StateNode node : open) {
                if (current == null || node.f() < current.f()) {
                    current = node;
                }
            }

            if (current.equals(goal)) {
                List<StateNode> path = new ArrayList<>();
                while (current.parent != null) {
                    path.add(0, current);
                    current = current.parent;
                }
                path.add(0, start);

                return path;
            }

            open.remove(current);
            closed.add(current);

            for (StateNode neighbour : getNeighbours(current)) {
                int nextG = current.g + 1;
                if (nextG < neighbour.g) {
                    open.remove(neighbour);
                    closed.remove(neighbour);
                }

                if (!open.contains(neighbour) && !closed.contains(neighbour)) {
                    neighbour.g = nextG;
                    neighbour.h = cost(neighbour, goal);
                    neighbour.parent = current;
                    open.add(neighbour);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws java.io.IOException {
        System.out.println("go!");
        ProblemSpec spec = new ProblemSpec();
        spec.loadProblem("problems/inputK.txt");

        State startState = new State(spec);
        State endState = startState.saveState();
        State goalState = endState.saveState(); //the end state after alignment adjustments
        for(int i = 0; i < startState.mBoxes.size(); i++) {
            Box.MBox startBox = startState.mBoxes.get(i);
            Box.MBox goalBox = startBox.gridifyGoal();
            endState.mBoxes.get(i).setX(goalBox.getX());
            endState.mBoxes.get(i).setY(goalBox.getY());
            goalState.mBoxes.get(i).setX(startBox.getXGoal());
            goalState.mBoxes.get(i).setY(startBox.getYGoal());
        }

        StateNode startNode = new StateNode(startState);
        StateNode endNode = new StateNode(endState);
        StateGraph graph = new StateGraph(startNode, endNode, GraphType.BOXES);

        List<StateNode> allStates = graph.aStar();
        List<State> interimStates = new ArrayList<>();
        System.out.println("gotAllStates");
        for(int i = 0; i < allStates.size() - 1; i++) {
            interimStates.addAll(State.interimStates(allStates.get(i).state, allStates.get(i + 1).state));
        }
        interimStates.addAll(State.interimStates(endState, goalState));
        System.out.println(State.outputString(interimStates));
    }
}
