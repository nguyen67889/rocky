package solution;

import problem.Box;
import problem.MovingBox;
import problem.ProblemSpec;
import problem.RobotConfig;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HenrysSolution {
    public static void main(String[] args) throws java.io.IOException {
        System.out.println("go!");
        ProblemSpec spec = new ProblemSpec();
        spec.loadProblem("input3.txt");

        new HenrysSolution().solve(spec);

        /*Box b = spec.getMovingBoxes().get(0);
        double cx = b.getRect().getCenterX();
        double cy = b.getRect().getCenterY();
        double w = b.getWidth();

        List<RobotConfig> configs = new ArrayList<>();
        RobotConfig init = new RobotConfig(new Point2D.Double(cx, cy + w/2), 0);
        configs.add(init);
        configs.addAll(drawBack(b, init, Util.Side.TOP));
        configs.addAll(rotateAround(b, configs.get(configs.size() - 1), BigDecimal.valueOf(180)));
        configs.addAll(pushForward(b, configs.get(configs.size() - 1), Util.Side.LEFT));

        StringBuilder sb = new StringBuilder();
        sb.append(configs.size() + "\n");
        for(RobotConfig c : configs) {
            sb.append(Util.round(c.getPos().getX(), 4) + " ");
            sb.append(Util.round(c.getPos().getY(), 4) + " ");
            sb.append(Util.round(c.getOrientation(), 4) + " ");
            sb.append(Util.round(b.getPos().getX() + w/2, 4) + " ");
            sb.append(Util.round(b.getPos().getY() + w/2, 4) + " ");
            sb.append("0 0 0 0");
            sb.append("\n");
        }

        System.out.println(sb.toString());*/
    }

    private void sbAppendLine(StringBuilder sb, double robotX, double robotY, double robotAngle,
                          double b1X, double b1Y, double b1w, double b2X, double b2Y, double b2w,
                          double moX, double moY, double mow) {
        sb.append(Util.round(robotX, 4) + " ");
        sb.append(Util.round(robotY, 4) + " ");
        sb.append(Util.round(robotAngle, 4) + " ");
        sb.append(Util.round(b1X + b1w/2, 4) + " ");
        sb.append(Util.round(b1Y + b1w/2, 4) + " ");
        sb.append(Util.round(b2X + b2w/2, 4) + " ");
        sb.append(Util.round(b2Y + b2w/2, 4) + " ");
        sb.append(Util.round(moX + mow/2, 4) + " ");
        sb.append(Util.round(moY + mow/2, 4) + " ");
        sb.append("\n");
        //TODO: work for more boxes
    }

    private void solve(ProblemSpec spec) {
        //STEP 1: generate path for robot to box 1
        //STEP 2: generate path for box 1 to goal
        // 2a: set robot dir
        // 2b: generate path from one node to next

        RobotConfig init = spec.getInitialRobotConfig();
        StringBuilder sb = new StringBuilder();

        Box box1 = spec.getMovingBoxes().get(0);
        Box box2 = spec.getMovingBoxes().get(1);
        Box mo = spec.getMovingObstacles().get(0);
        Point2D goal1 = spec.getMovingBoxEndPositions().get(0);
        Point2D goal2 = spec.getMovingBoxEndPositions().get(1);

        GridGraph2 graph = new GridGraph2(spec);
        List<GridGraph2.Node> nodes = graph.aStar(box1, goal1.getX(), goal1.getY());

        robot = new RobotConfig(new Point2D.Double(box1.getRect().getCenterX(),
                box1.getRect().getCenterY() - box1.getWidth()/2), 0);
        robotSide = Util.Side.BOTTOM;
        movingBox = box1;

        boxPts = new ArrayList<>();
        robotPts = new ArrayList<>();
        boxPts.add(movingBox);
        robotPts.add(robot);

        for(GridGraph2.Node node : nodes) {
            pointsToNext(movingBox.getPos(),
                    new Point2D.Double(node.getX().doubleValue(), node.getY().doubleValue()));
        }
        pointsToNext(movingBox.getPos(), goal1);

        for(int i = 0; i < boxPts.size(); i++) {
            sbAppendLine(sb, robotPts.get(i).getPos().getX(), robotPts.get(i).getPos().getY(),
                    robotPts.get(i).getOrientation(), boxPts.get(i).getPos().getX(),
                    boxPts.get(i).getPos().getY(), box1.getWidth(),
                    box2.getPos().getX(), box2.getPos().getY(), box2.getWidth(),
                    mo.getPos().getX(), mo.getPos().getY(), mo.getWidth());
        }

        sb.insert(0, boxPts.size() + "\n");

        try {
            write(sb);
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("done");
    }

    private static void write(StringBuilder sb) throws IOException {
        FileWriter fileWriter = new FileWriter("output3-test.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(sb.toString());
        printWriter.close();
    }

    public void drawBack() {
        BigDecimal radius = Util.round((Math.sqrt(2)/2)*movingBox.getWidth() + 0.001, 4);

        switch(robotSide) {
            case LEFT:
                while(robot.getPos().getX() > movingBox.getRect().getCenterX() - radius.doubleValue()) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX() - 0.001,
                            robot.getPos().getY()), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
            case RIGHT:
                while(robot.getPos().getX() < movingBox.getRect().getCenterX() + radius.doubleValue()) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX() + 0.001,
                            robot.getPos().getY()), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
            case TOP:
                while(robot.getPos().getY() < movingBox.getRect().getCenterY() + radius.doubleValue()) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX(),
                            robot.getPos().getY() + 0.001), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
            case BOTTOM:
                while(robot.getPos().getY() > movingBox.getRect().getCenterY() - radius.doubleValue()) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX(),
                            robot.getPos().getY() - 0.001), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
        }

    }

    private void pushForward() {

        switch(robotSide) {
            case LEFT:
                while(robot.getPos().getX() < movingBox.getRect().getCenterX() - movingBox.getWidth()/2) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX() + 0.001,
                            robot.getPos().getY()), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
            case RIGHT:
                while(robot.getPos().getX() > movingBox.getRect().getCenterX() + movingBox.getWidth()/2) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX() - 0.001,
                            robot.getPos().getY()), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
            case TOP:
                while(robot.getPos().getY() > movingBox.getRect().getCenterY() + movingBox.getWidth()/2) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX(),
                            robot.getPos().getY() - 0.001), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
            case BOTTOM:
                while(robot.getPos().getY() < movingBox.getRect().getCenterY() - movingBox.getWidth()/2) {
                    robot = new RobotConfig(new Point2D.Double(robot.getPos().getX(),
                            robot.getPos().getY() + 0.001), robot.getOrientation());
                    robotPts.add(robot);
                    boxPts.add(movingBox);
                }
                break;
        }

    }

    private static BigDecimal getAngle(Util.Side side) {
        switch(side) {
            case RIGHT:
                return BigDecimal.valueOf(0);
            case TOP:
                return BigDecimal.valueOf(90);
            case LEFT:
                return BigDecimal.valueOf(180);
            case BOTTOM:
                return BigDecimal.valueOf(270);
        }
        return BigDecimal.valueOf(0);
    }

    private void rotateAround(Util.Side goal) {
        BigDecimal radius = Util.round((Math.sqrt(2)/2)*movingBox.getWidth() + 0.001, 4);

        BigDecimal angle = getAngle(robotSide);
        BigDecimal goalAngle = getAngle(goal);
        int dir = 1;
        List<Util.Side> sideValues = Arrays.asList(Util.Side.values());
        if(sideValues.indexOf(goal) == sideValues.indexOf(robotSide) - 1 % sideValues.size()) {
            dir = -1;
        }

        Point2D center = new Point2D.Double(movingBox.getRect().getCenterX(), movingBox.getRect().getCenterY());
        while(goalAngle.compareTo(angle) != 0) {
            double ptx = center.getX() + radius.doubleValue() * Math.cos(Math.toRadians(angle.doubleValue()));
            double pty = center.getY() + radius.doubleValue() * Math.sin(Math.toRadians(angle.doubleValue()));
            robot = new RobotConfig(new Point2D.Double(Util.round(ptx, 4).doubleValue(),
                    Util.round(pty, 4).doubleValue()),
                    Util.round(Math.toRadians(angle.doubleValue() - 90), 4).doubleValue());
            robotPts.add(robot);
            boxPts.add(movingBox);

            angle = angle.add(BigDecimal.valueOf(0.5 * dir));
            if(angle.compareTo(BigDecimal.ZERO) < 0) {
                angle = angle.add(BigDecimal.valueOf(360));
            }
            if(angle.compareTo(BigDecimal.valueOf(360)) >= 0) {
                angle = angle.subtract(BigDecimal.valueOf(360));
            }
        }

        robotSide = goal;
    }

    private Util.Side robotSide;
    private RobotConfig robot;
    private Box movingBox;
    private List<Box> boxPts;
    private List<RobotConfig> robotPts;

    private void pointsToNext(Point2D start, Point2D goal) {
        BigDecimal startX = Util.round(start.getX(), 4);
        BigDecimal startY = Util.round(start.getY(), 4);
        BigDecimal goalX = Util.round(goal.getX(), 4);
        BigDecimal goalY = Util.round(goal.getY(), 4);

        if(startX.compareTo(goalX) < 0) {
            preparePush(Util.Side.LEFT);
            while(startX.compareTo(goalX) < 0) {
                startX = startX.add(BigDecimal.valueOf(0.001));
                movingBox = new MovingBox(new Point2D.Double(startX.doubleValue(),
                        startY.doubleValue()), movingBox.getWidth());
                robot = new RobotConfig(new Point2D.Double(startX.doubleValue(),
                        startY.doubleValue() + movingBox.getWidth()/2), Math.PI/2);
                boxPts.add(movingBox);
                robotPts.add(robot);
            }
        }
        if(startX.compareTo(goalX) > 0) {
            preparePush(Util.Side.RIGHT);
            while(startX.compareTo(goalX) > 0) {
                startX = startX.subtract(BigDecimal.valueOf(0.001));
                movingBox = new MovingBox(new Point2D.Double(startX.doubleValue(),
                        startY.doubleValue()), movingBox.getWidth());
                robot = new RobotConfig(new Point2D.Double(startX.doubleValue() + movingBox.getWidth(),
                        startY.doubleValue() + movingBox.getWidth()/2), Math.PI/2);
                boxPts.add(movingBox);
                robotPts.add(robot);
            }
        }
        if(startY.compareTo(goalY) < 0) {
            preparePush(Util.Side.BOTTOM);
            while(startY.compareTo(goalY) < 0) {
                startY = startY.add(BigDecimal.valueOf(0.001));
                movingBox = new MovingBox(new Point2D.Double(startX.doubleValue(),
                        startY.doubleValue()), movingBox.getWidth());
                robot = new RobotConfig(new Point2D.Double(startX.doubleValue() + movingBox.getWidth()/2,
                        startY.doubleValue()), 0);
                boxPts.add(movingBox);
                robotPts.add(robot);
            }
        }
        if(startY.compareTo(goalY) > 0) {
            preparePush(Util.Side.TOP);
            while(startY.compareTo(goalY) > 0) {
                startY = startY.subtract(BigDecimal.valueOf(0.001));
                movingBox = new MovingBox(new Point2D.Double(startX.doubleValue(),
                        startY.doubleValue()), movingBox.getWidth());
                robot = new RobotConfig(new Point2D.Double(startX.doubleValue() + movingBox.getWidth()/2,
                        startY.doubleValue() + movingBox.getWidth()), 0);
                boxPts.add(movingBox);
                robotPts.add(robot);
            }
        }
    }

    private void preparePush(Util.Side newSide) {
        if(robotSide == newSide) {
            return;
        }
        drawBack();
        rotateAround(newSide);
        pushForward();
    }
}
