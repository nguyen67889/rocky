package solution.krazysolution;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import problem.ProblemSpec;
import solution.Util;

import java.math.BigDecimal;
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
        ROBOT, BOXES, OBSTACLES, ALL
    }

    private StateNode start;
    private StateNode goal;
    private GraphType type;
    private int index; //the box we are moving

    public StateGraph(StateNode start, StateNode goal, GraphType type, int boxIndex) {
        this.start = start;
        this.goal = goal;
        this.type = type;
        this.index = boxIndex;

        System.out.println(start.state + " to " + goal.state);
    }

    public StateGraph(StateNode start, StateNode goal) {
        this(start, goal, GraphType.ALL, -1);
    }

    private int cost(StateNode start, StateNode end) {
        //TODO modify to be better
        int cost = 0;
        for (int i = 0; i < start.state.mBoxes.size(); i++) {
            Box.MBox startBox = start.state.mBoxes.get(i);
            Box.MBox endBox = end.state.mBoxes.get(i);
            cost += Math.abs(startBox.getX() - endBox.getX()) +
                    Math.abs(startBox.getY() - endBox.getY());
            if (startBox.getY() != endBox.getY() && startBox.getX() != endBox.getX()) {
                cost += 100;
            }
        }
        if (!start.state.robot.getAngle().equals(goal.state.robot.getAngle())) {
            cost += 100;
        }
        return cost;
    }

    private int nextCost(StateNode start, StateNode end) {
        int result = 1;
        if (start.state.dir != end.state.dir) {
            result += 1000;
        }
        if (start.state.current != end.state.current) {
            result += 10000;
        }
        return result;
    }

    private Set<State> getRotationStates(State state) {
        Set<State> states = new HashSet<>();
        State s1 = state.saveState();
        s1.robot.rotateClockwise();
        State s2 = state.saveState();
        s2.robot.rotateAntiClockwise();
        if (!s1.isRobotCollision()) {
            states.add(s1);
        }
        if (!s2.isRobotCollision()) {
            states.add(s2);
        }
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

    private Set<State> getAllMovementStates(State state) {
        Set<State> states = new HashSet<>();
        State s1 = state.saveState();
        s1.robot.moveDown();
        State s2 = state.saveState();
        s2.robot.moveUp();
        State s3 = state.saveState();
        s3.robot.moveLeft();
        State s4 = state.saveState();
        s4.robot.moveRight();

        if (!s1.isRobotOutOfBounds()) {
            if (s1.isRobotCollision()) {
                Box.MBox aligned = s1.getRobotAlignment();
                if (aligned != null) {
                    aligned.setY(s1.robot.getY());
                    states.add(s1);
                }
            } else {
                states.add(s1);
            }
        }

        if (!s2.isRobotOutOfBounds()) {
            if (s2.isRobotCollision()) {
                Box.MBox aligned = s2.getRobotAlignment();
                if (aligned != null) {
                    aligned.setY(s2.robot.getY());
                    states.add(s2);
                }
            } else {
                states.add(s2);
            }
        }

        if (!s3.isRobotOutOfBounds()) {
            if (s3.isRobotCollision()) {
                Box.MBox aligned = s3.getRobotAlignment();
                if (aligned != null) {
                    aligned.setY(s3.robot.getX());
                    states.add(s3);
                }
            } else {
                states.add(s3);
            }
        }

        if (!s4.isRobotOutOfBounds()) {
            if (s4.isRobotCollision()) {
                Box.MBox aligned = s4.getRobotAlignment();
                if (aligned != null) {
                    aligned.setY(s4.robot.getX());
                    states.add(s4);
                }
            } else {
                states.add(s4);
            }
        }

        return states;
    }

    private Set<State> getObsMovementStates(State state) {
        Set<State> states = new HashSet<>();

        int i = index;
        State s1 = state.saveState();
        s1.mObstacles.get(i).moveDown();
        s1.dir = Util.Side.TOP;
        State s2 = state.saveState();
        s2.mObstacles.get(i).moveUp();
        s2.dir = Util.Side.BOTTOM;
        State s3 = state.saveState();
        s3.mObstacles.get(i).moveLeft();
        s3.dir = Util.Side.RIGHT;
        State s4 = state.saveState();
        s4.mObstacles.get(i).moveRight();
        s4.dir = Util.Side.LEFT;
        s1.current = s2.current = s3.current = s4.current = -(i + 1);
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

        return states;
    }

    private Set<State> getBoxMovementStates(State state) {
        Set<State> states = new HashSet<>();
        int i = index;

            State s1 = state.saveState();
            s1.mBoxes.get(i).moveDown();
            s1.dir = Util.Side.TOP;
            State s2 = state.saveState();
            s2.dir = Util.Side.BOTTOM;
            s2.mBoxes.get(i).moveUp();
            State s3 = state.saveState();
            s3.dir = Util.Side.RIGHT;
            s3.mBoxes.get(i).moveLeft();
            State s4 = state.saveState();
            s4.dir = Util.Side.LEFT;
            s4.mBoxes.get(i).moveRight();
            s1.current = s2.current = s3.current = s4.current = i + 1;
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

        return states;
    }

    private Set<StateNode> getNeighbours(StateNode node) {
        Set<State> states = new HashSet<>();
        Set<StateNode> result = new HashSet<>();
        switch(type) {
            case ROBOT:
                states.addAll(getRotationStates(node.state));
                states.addAll(getMovementStates(node.state));
                break;
            case BOXES:
                states.addAll(getBoxMovementStates(node.state));
                break;
            case OBSTACLES:
                states.addAll(getObsMovementStates(node.state));
                break;
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

            boolean isClose = false;
            switch (type) {
                case BOXES:
                    isClose = current.state.isCloseBox(goal.state);
                    break;
                case OBSTACLES:
                    isClose=  current.state.isCloseObs(goal.state);
                    break;
                case ROBOT:
                    isClose = current.state.isCloseRobot(goal.state);
                    break;
            }

            if (isClose) {
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
                int nextG = current.g + nextCost(current, neighbour);
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
        State gState1 = startState.saveState();
        Box.MBox box = gState1.mBoxes.get(0);
        box.setX(box.getXGoal());
        box.setY(box.getYGoal());
        List<StateGraph.StateNode> nodes = new StateGraph(new StateNode(startState), new StateNode(gState1),
                GraphType.BOXES, 0).aStar();
        List<State> path = new ArrayList<>();
        for (StateGraph.StateNode node : nodes) {
            path.add(node.state);
        }
        System.out.println(State.outputString(path));

        /*State startState = new State(spec);
        startState.robot.setX(2000);
        startState.robot.setY(0);
        startState.robot.setAngle(BigDecimal.ZERO);
        State endState = startState.saveState();
        endState.robot.setX(2500);
        endState.robot.setY(5000);
        endState.robot.setAngle(BigDecimal.ZERO);
        System.out.println(startState);
        System.out.println(endState);

        StateGraph graph = new StateGraph(new StateNode(startState), new StateNode(endState), GraphType.ROBOT);

        List<StateNode> allStates = graph.aStar();
        List<State> interimStates = new ArrayList<>();
        System.out.println("gotAllStates");
        for(int i = 0; i < allStates.size() - 1; i++) {
            interimStates.addAll(State.interimStates(allStates.get(i).state, allStates.get(i + 1).state));
            //interimStates.add(allStates.get(i).state);
        }
        interimStates.addAll(State.interimStates(interimStates.get(interimStates.size() - 1), endState));
        System.out.println(State.outputString(interimStates));

       /* State startState = new State(spec);
        State endState = startState.saveState();
        for(int i = 0; i < startState.mBoxes.size(); i++) {
            Box.MBox startBox = startState.mBoxes.get(i);
            endState.mBoxes.get(i).setX(startBox.getXGoal());
            endState.mBoxes.get(i).setY(startBox.getYGoal());
        }

        StateNode startNode = new StateNode(startState);
        StateNode endNode = new StateNode(endState);
        StateGraph graph = new StateGraph(startNode, endNode, GraphType.BOXES);

        List<StateNode> allStates = graph.aStar();
        List<State> interimStates = new ArrayList<>();
        System.out.println("gotAllStates");
        for(int i = 0; i < allStates.size() - 1; i++) {
            interimStates.addAll(State.interimBoxStates(allStates.get(i).state, allStates.get(i + 1).state));
            //interimStates.add(allStates.get(i).state);
        }
        interimStates.addAll(State.interimBoxStates(interimStates.get(interimStates.size() - 1), endState));
        System.out.println(State.outputString(interimStates));*/
    }
}
