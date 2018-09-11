package solution.krazysolution;

import problem.ProblemSpec;
import solution.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        List<State> states = stateBuilder();
        try {
            write(State.outputString(states), "problems/outputK.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("done!");
    }

    private static void write(String str, String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(str);
        printWriter.close();
    }

    private List<State> getRobotStates(State currentState, State nextState) {
        List<State> result = new ArrayList<>();

        System.out.println("yes");
        Util.Side dir = nextState.dir;
        int index = nextState.current;
        if(dir == null) {
            return result;
        }

        Box box = null;
        if(index > 0) {
            box = currentState.mBoxes.get(index - 1);
        } else if(index < 0) {
            box = currentState.mObstacles.get(-index - 1);
        }

        int x = 0, y = 0;
        BigDecimal a = BigDecimal.ZERO;
        switch(dir) {
            case BOTTOM:
                x = box.getX() + box.getWidth()/2;
                y = box.getY();
                a = BigDecimal.ZERO;
                break;
            case TOP:
                x = box.getX() + box.getWidth()/2;
                y = box.getY() + box.getHeight();
                a = BigDecimal.ZERO;
                break;
            case LEFT:
                x = box.getX();
                y = box.getY() + box.getHeight()/2;
                a = BigDecimal.valueOf(90);
                break;
            case RIGHT:
                x = box.getX() + box.getWidth();
                y = box.getY() + box.getHeight()/2;
                a = BigDecimal.valueOf(90);
                break;
        }

        State robotGoalState = currentState.saveState();
        robotGoalState.robot.setX(x);
        robotGoalState.robot.setY(y);
        robotGoalState.robot.setAngle(a);

        System.out.println(currentState);
        System.out.println(robotGoalState);

        StateGraph robotGraph = new StateGraph(new StateGraph.StateNode(currentState),
                new StateGraph.StateNode(robotGoalState), StateGraph.GraphType.ROBOT);

        List<StateGraph.StateNode> robotStates = robotGraph.aStar();
        if(robotStates == null) {
            System.out.println(State.outputString(result));
            throw new RuntimeException("No path found to next obstacle");
        }
        List<State> interimRobotStates = new ArrayList<>();
        for(int j = 0; j < robotStates.size() - 1; j++) {
            interimRobotStates.addAll(State.interimStates(robotStates.get(j).state,
                    robotStates.get(j + 1).state));
            //interimRobotStates.add(robotStates.get(i).state);
        }
        if(interimRobotStates.size() > 0) {
            interimRobotStates.addAll(State.interimStates(interimRobotStates.get(interimRobotStates.size() - 1), robotGoalState));
        } else {
            throw new RuntimeException("No interim states");
        }
        result.addAll(interimRobotStates);

        currentState.robot.setX(robotGoalState.robot.getX());
        currentState.robot.setY(robotGoalState.robot.getY());
        currentState.robot.setAngle(robotGoalState.robot.getAngle());

        return result;
    }

    private List<State> stateBuilder() {

        List<State> result = new ArrayList<>();

        State startState = new State(spec);
        State endState = startState.saveState();

        for(int i = 0; i < startState.mBoxes.size(); i++) {
            Box.MBox startBox = startState.mBoxes.get(i);
            endState.mBoxes.get(i).setX(startBox.getXGoal());
            endState.mBoxes.get(i).setY(startBox.getYGoal());
        }

        StateGraph graph = new StateGraph(new StateGraph.StateNode(startState),
                new StateGraph.StateNode(endState), StateGraph.GraphType.BOXES);
        List<StateGraph.StateNode> allStates = graph.aStar();

        for(State state : State.interimBoxStates(allStates.get(allStates.size() - 1).state, endState)) {
            allStates.add(new StateGraph.StateNode(state));
        }

        System.out.println("All box states generated");

        Util.Side dir = null;
        int index = 0;
        for(int i = 0; i < allStates.size() - 1; i++) {
            State currentState = allStates.get(i).state;
            State nextState = allStates.get(i + 1).state;
            if(nextState.dir != dir || nextState.current != index) { //need to move robot
                dir = nextState.dir;
                index = nextState.current;
                result.addAll(getRobotStates(currentState, nextState));
            }
            List<State> interimBoxStates = State.interimBoxStates(currentState, nextState);
            result.addAll(interimBoxStates);

            Robot latestConfig = result.get(result.size() - 1).robot;
            nextState.robot.setX(latestConfig.getX());
            nextState.robot.setY(latestConfig.getY());
            nextState.robot.setAngle(latestConfig.getAngle());
        }

        /*List<State> statesToGoal = State.interimBoxStates(result.get(result.size() - 1), endState);
        for(int i = 0; i < statesToGoal.size() - 1; i++) {
            State currentState = statesToGoal.get(i);
            State nextState = statesToGoal.get(i + 1);
            if(nextState.dir != dir || nextState.current != index) { //need to move robot
                dir = nextState.dir;
                index = nextState.current;
                result.addAll(getRobotStates(currentState, nextState));
            }
            result.add(currentState);

            Robot latestConfig = result.get(result.size() - 1).robot;
            nextState.robot.setX(latestConfig.getX());
            nextState.robot.setY(latestConfig.getY());
            nextState.robot.setAngle(latestConfig.getAngle());
        }*/

        return result;
    }

    public static void main(String[] args) {
        new Solution("problems/input1.txt").run();
    }
}
