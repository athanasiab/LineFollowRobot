import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import simbad.sim.*;

public class Env extends EnvironmentDescription {
    Env(){
        floorColor = white;
        backgroundColor = darkgray;
        light1SetPosition(-8, 2, 5); //Changes the position of the light
        light1IsOn = true;
        light2IsOn = false; //We only want one light source, the goal
        light2Color = black; //To be sure it is closed and doesn't affect the environment
        //Adds obstacles
        Wall w1 = new Wall(new Vector3d(-6, 0, 0), 6, 1, this);
        w1.rotate90(1);
        add(w1);
        boxColor = blue;
        add(new Box(new Vector3d(0,0,0), new Vector3f(1,1,1),this));
        add(new Box(new Vector3d(-3.5,0,5), new Vector3f(2,1,1),this));
        //Creates Line
        Line l1 = new Line(new Vector3d(-7, 0, 0), 7, this);
        l1.rotate90(1);
        //1st
        add(l1);
        Line l2 = new Line(new Vector3d(6, 0, 0), 3, this);
        //2nd
        add(l2);
        Line l3 = new Line(new Vector3d(0, 0, 3), 6, this);
        l3.rotate90(1);
        add(l3);
        Line l4 = new Line(new Vector3d(0, 0, 0), 3, this);
        add(l4);
        Line l5 = new Line(new Vector3d(-4, 0, 0), 4, this);
        l5.rotate90(1);
        add(l5);
        Line l6 = new Line(new Vector3d(-4, 0, 0), 5, this);
        add(l6);
        Line l7 = new Line(new Vector3d(-7, 0, 5), 3, this);
        l7.rotate90(1);
        add(l7);
        Line l8 = new Line(new Vector3d(3, 0, 0), 3, this);
        l8.rotate90(1);
        add(l8);
        //Adds robot
        MyRobot r = new MyRobot(new Vector3d(3, 0, 0), "robot 1");
        add(r);

    }
}