package solution.states;

import solution.Node;
import solution.Util;

import java.util.*;
import solution.boxes.MovingBox;

public class StateGraph {

    public enum GraphType {
        ROBOT, BOXES, OBSTACLES, ALL
    }

    private Node<State> start;
    private Node<State> goal;
    private GraphType type;
    private int index; //the box we are moving

    public StateGraph(Node<State> start, Node<State> goal, GraphType type,
            int boxIndex) {
        this.start = start;
        this.goal = goal;
        this.type = type;
        this.index = boxIndex;
    }

    public StateGraph(Node<State> start, Node<State> goal) {
        this(start, goal, GraphType.ALL, -1);
    }

    private int cost(Node<State> start, Node<State> end) {
        //TODO modify to be better
        int cost = 0;
        for (int i = 0; i < start.getItem().mBoxes.size(); i++) {
            MovingBox startBox = start.getItem().mBoxes.get(i);
            MovingBox endBox = end.getItem().mBoxes.get(i);
            cost += Math.abs(startBox.getX() - endBox.getX()) +
                    Math.abs(startBox.getY() - endBox.getY());
            if (startBox.getY() != endBox.getY() && startBox.getX() != endBox
                    .getX()) {
                cost += 100;
            }
        }
        if (!start.getItem().robot.getAngle().equals(goal.getItem().robot.getAngle())) {
            cost += 100;
        }
        return cost;
    }

    private int nextCost(Node<State> start, Node<State> end) {
        int result = 1;
        if (start.getItem().dir != end.getItem().dir) {
            result += 1000;
        }
        if (start.getItem().current != end.getItem().current) {
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
        if (!s1.isRobotCollision() || (type == GraphType.BOXES && s1.isCloseBox(goal.getItem())) ||
                (type == GraphType.OBSTACLES && s1.isCloseObs(goal.getItem()))) {
            states.add(s1);
        }
        if (!s2.isRobotCollision() || (type == GraphType.BOXES && s2.isCloseBox(goal.getItem())) ||
                (type == GraphType.OBSTACLES && s2.isCloseObs(goal.getItem()))) {
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
        if (!s1.isRobotCollision() ||
                (type == GraphType.BOXES && s1.isCloseBox(goal.getItem())) ||
                (type == GraphType.OBSTACLES && s1.isCloseObs(goal.getItem()))) {
            states.add(s1);
        }
        if (!s2.isRobotCollision() ||
                (type == GraphType.BOXES && s2.isCloseBox(goal.getItem())) ||
                (type == GraphType.OBSTACLES && s2.isCloseObs(goal.getItem()))) {
            states.add(s2);
        }
        if (!s3.isRobotCollision() ||
                (type == GraphType.BOXES && s3.isCloseBox(goal.getItem())) ||
                (type == GraphType.OBSTACLES && s3.isCloseObs(goal.getItem()))) {
            states.add(s3);
        }
        if (!s4.isRobotCollision() ||
                (type == GraphType.BOXES && s4.isCloseBox(goal.getItem())) ||
                (type == GraphType.OBSTACLES && s4.isCloseObs(goal.getItem()))) {
            states.add(s4);
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

    private Set<Node<State>> getNeighbours(Node<State> node) {
        Set<State> states = new HashSet<>();
        Set<Node<State>> result = new HashSet<>();
        switch (type) {
            case ROBOT:
                states.addAll(getRotationStates(node.getItem()));
                states.addAll(getMovementStates(node.getItem()));
                break;
            case BOXES:
                states.addAll(getBoxMovementStates(node.getItem()));
                break;
            case OBSTACLES:
                states.addAll(getObsMovementStates(node.getItem()));
                break;
        }
        for (State state : states) {
            result.add(new Node<>(state));
        }
        return result;
    }

    public List<Node<State>> aStar() {
        Set<Node<State>> open = new HashSet<>();
        Set<Node<State>> closed = new HashSet<>();

        start.g = 0;
        start.h = cost(start, goal);

        open.add(start);

        while (open.size() > 0) {
            Node<State> current = null;

            for (Node<State> node : open) {
                if (current == null || node.f() < current.f()) {
                    current = node;
                }
            }

            boolean isClose = false;
            switch (type) {
                case BOXES:
                    isClose = current.getItem().isCloseBox(goal.getItem());
                    break;
                case OBSTACLES:
                    isClose = current.getItem().isCloseObs(goal.getItem());
                    break;
                case ROBOT:
                    isClose = current.getItem().isCloseRobot(goal.getItem());
                    break;
            }

            if (isClose) {
                List<Node<State>> path = new ArrayList<>();
                while (current.parent != null) {
                    path.add(0, current);
                    current = current.parent;
                }
                path.add(0, start);

                return path;
            }

            open.remove(current);
            closed.add(current);

            for (Node<State> neighbour : getNeighbours(current)) {
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
}
