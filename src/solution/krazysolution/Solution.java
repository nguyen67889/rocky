package solution.krazysolution;

import problem.ProblemSpec;
import solution.Util;
import solution.boxes.Movable;
import solution.boxes.MovingBox;
import solution.boxes.MovingObstacle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Solution {
    ProblemSpec spec;

    public Solution(String file) {
        System.out.println("go!");
        spec = new ProblemSpec();
        try {
            spec.loadProblem(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        List<State> states = generateStates(new State(spec));
        try {
            write(State.outputString(states), "problems/outputK.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("done!");
    }

    private List<State> getBoxStates(State startState, int index) {
        State goalState = startState.saveState();
        MovingBox box = goalState.mBoxes.get(index);
        box.setX(box.getXGoal());
        box.setY(box.getYGoal());
        List<StateGraph.StateNode> nodes = new StateGraph(new StateGraph.StateNode(startState),
                new StateGraph.StateNode(goalState), StateGraph.GraphType.BOXES, index).aStar();
        if(nodes == null) {
            return null;
        }

        List<State> path = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i++) {
            path.addAll(State.interimBoxStates(nodes.get(i).state, nodes.get(i + 1).state));
        }
        path.addAll(State.interimBoxStates(path.get(path.size() - 1), goalState));

        return path;
    }

    private List<State> getMovingBoxStates(State startState) {
        System.out.println("Time to move some boxes!");

        State goalState = startState.saveState();
        List<StateGraph.StateNode> nodes = null;
        while(nodes == null) {
            int index = ThreadLocalRandom.current().nextInt(goalState.mObstacles.size());
            MovingObstacle start = startState.mObstacles.get(index);
            MovingObstacle obs = goalState.mObstacles.get(index);
            obs.setX(-1);
            obs.setY(-1);
            while(goalState.isBoxCollision(obs) || startState.isCloseObs(goalState)) {
                System.out.println("iter");
                int deltaX = ThreadLocalRandom.current().nextInt(-50, 51)*10;
                int deltaY = ThreadLocalRandom.current().nextInt(-50, 51)*10;
                obs.setX(start.getX() + deltaX);
                obs.setY(start.getY() + deltaY);
            }
            System.out.println(obs.getX() + ", " + obs.getY());
            nodes = new StateGraph(new StateGraph.StateNode(startState), new StateGraph.StateNode(goalState),
                    StateGraph.GraphType.OBSTACLES, index).aStar();
            System.out.println("help");
        }
        System.out.println("path!");

        List<State> path = new ArrayList<>();
        System.out.println(path);
        for (int i = 0; i < nodes.size() - 1; i++) {
            path.addAll(State.interimBoxStates(nodes.get(i).state, nodes.get(i + 1).state));
        }
        path.addAll(State.interimBoxStates(path.get(path.size() - 1), goalState));

        return path;
    }

    private List<State> getAllBoxStates(State startState) {
        //TODO: handle moving obstacles
        State goalState = startState.saveState();
        State prevState = startState.saveState();
        List<State> states = new ArrayList<>();

        LinkedList<Integer> boxIndexes = new LinkedList<>();
        for(int i = 0; i < startState.mBoxes.size(); i++) {
            boxIndexes.add(i);

            goalState.mBoxes.get(i).setX(goalState.mBoxes.get(i).getXGoal());
            goalState.mBoxes.get(i).setY(goalState.mBoxes.get(i).getYGoal());
        }

        int loop = 0;
        while(!boxIndexes.isEmpty()) {
            if(loop >= boxIndexes.size()) {
                states.addAll(getMovingBoxStates(prevState));
                prevState = states.get(states.size() - 1);
            }

            int i = boxIndexes.removeFirst();
            List<State> boxStates = getBoxStates(prevState, i);

            if(boxStates != null) {
                loop = 0;
                states.addAll(boxStates);
                prevState = states.get(states.size() - 1);
            } else {
                loop += 1;
                boxIndexes.addLast(i);
            }
        }

        try {
            write(State.outputString(states), "problems/outputK.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return states;
    }

    private List<State> robotStateToState(State startState, int index, Util.Side alignment) {
        int x = 0, y = 0;
        BigDecimal a = null;
        Movable box;
        if(index > 0){
            box = startState.mBoxes.get(index - 1);
        } else if(index < 0) {
            box = startState.mObstacles.get(-index - 1);
        } else {
            throw new RuntimeException("Invalid box index");
        }

        System.out.println("Moving to side " + alignment + " of " + index);
        switch (alignment) {
            case BOTTOM:
                x = box.getX() + box.getWidth() / 2;
                y = box.getY();
                a = BigDecimal.ZERO;
                break;
            case TOP:
                x = box.getX() + box.getWidth() / 2;
                y = box.getY() + box.getHeight();
                a = BigDecimal.ZERO;
                break;
            case LEFT:
                x = box.getX();
                y = box.getY() + box.getHeight() / 2;
                a = BigDecimal.valueOf(90);
                break;
            case RIGHT:
                x = box.getX() + box.getWidth();
                y = box.getY() + box.getHeight() / 2;
                a = BigDecimal.valueOf(90);
                break;
        }

        System.out.println(":" + x + ", " + y);

        State goalState = startState.saveState();
        goalState.robot.setX(x);
        goalState.robot.setY(y);
        goalState.robot.setAngle(a);

        List<StateGraph.StateNode> nodes = new StateGraph(new StateGraph.StateNode(startState),
                new StateGraph.StateNode(goalState), StateGraph.GraphType.ROBOT, -1).aStar();
        List<State> path = new ArrayList<>();

        for (int i = 0; i < nodes.size() - 1; i++) {
            path.addAll(State.interimStates(nodes.get(i).state, nodes.get(i + 1).state));
        }

        path.addAll(State.interimStates(path.get(path.size() - 1), goalState));

        return path;
    }

    private List<State> generateStates(State startState) {
        List<State> path = new ArrayList<>();
        List<State> boxStates = getAllBoxStates(startState);

        Util.Side side = null;
        int index = 0;

        path.add(boxStates.get(0));
        for (int i = 1; i < boxStates.size(); i++) {
            State currentState = boxStates.get(i);
            State prevState = boxStates.get(i - 1);

            if (currentState.dir != side || currentState.current != index) {
                side = currentState.dir;
                index = currentState.current;
                path.addAll(robotStateToState(prevState, index, side));
            }

            prevState = path.get(path.size() - 1);

            Movable box;
            if(currentState.current > 0) {
                box = currentState.mBoxes.get(currentState.current - 1);
            } else if(currentState.current < 0 ) {
                box = currentState.mObstacles.get(-currentState.current - 1);
            } else {
                throw new RuntimeException("Invalid box index");
            }

            switch (currentState.dir) {
                case RIGHT:
                    currentState.robot.setX(box.getX() + box.getWidth());
                    currentState.robot.setY(prevState.robot.getY());
                    currentState.robot.setAngle(prevState.robot.getAngle());
                    break;
                case LEFT:
                    currentState.robot.setX(box.getX());
                    currentState.robot.setY(prevState.robot.getY());
                    currentState.robot.setAngle(prevState.robot.getAngle());
                    break;
                case TOP:
                    currentState.robot.setY(box.getY() + box.getHeight());
                    currentState.robot.setX(prevState.robot.getX());
                    currentState.robot.setAngle(prevState.robot.getAngle());
                    break;
                case BOTTOM:
                    currentState.robot.setY(box.getY());
                    currentState.robot.setX(prevState.robot.getX());
                    currentState.robot.setAngle(prevState.robot.getAngle());
                    break;
            }
            path.add(currentState);
        }

        return path;
    }

    private static void write(String str, String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(str);
        printWriter.close();
    }

    public static void main(String[] args) {
        new Solution("problems/inputK.txt").run();
    }
}
