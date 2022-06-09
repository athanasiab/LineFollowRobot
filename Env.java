import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import simbad.sim.*;

public class Env extends EnvironmentDescription {
    Env(){
        floorColor = white;
        backgroundColor = darkgray;
        light1SetPosition(-8, 2, 5);
        light1IsOn = true;
        light2IsOn = false;
        light2Color = black;
        Wall w1 = new Wall(new Vector3d(-6, 0, 0), 8, 1, this);
        w1.rotate90(1);
        add(w1);
        boxColor = blue;
        add(new Box(new Vector3d(-3,0,0), new Vector3f(1,1,1),this));
        add(new Box(new Vector3d(-3.5,0,5), new Vector3f(2,1,1),this));
        add(new MyRobot(new Vector3d(0, 0, 0), "robot 1"));
        Line l1 = new Line(new Vector3d(-4, 0, 0), 4, this);
        l1.rotate90(1);
        add(l1);
        add(new Line(new Vector3d(-4, 0, 0), 5, this));
        Line l2 = new Line(new Vector3d(-5, 0, 5), 1, this);
        l2.rotate90(1);
        add(l2);
        Line l3 = new Line(new Vector3d(-7, 0, 0), 7, this);
        l3.rotate90(1);
        add(l3);
    }
}

