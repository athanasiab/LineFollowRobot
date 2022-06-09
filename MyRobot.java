import javax.vecmath.Vector3d;

import simbad.sim.*;

public class MyRobot extends Agent {
    RangeSensorBelt sonars;
    RangeSensorBelt bumpers;
    LightSensor lightL;
    LightSensor lightR;
    RangeSensorBelt touch1;
    LineSensor lineSens;

    public MyRobot (Vector3d position, String name) {
        super(position, name);
        lightL= RobotFactory.addLightSensorLeft(this);
        lightR= RobotFactory.addLightSensorRight(this);
        touch1= RobotFactory.addBumperBeltSensor(this, 8);
        lineSens = RobotFactory.addLineSensor(this,11);
        sonars = RobotFactory.addSonarBeltSensor(this,12);
        bumpers = RobotFactory.addBumperBeltSensor(this,8);

    }
    public void initBehavior() {
        setTranslationalVelocity(0.5);
        //setRotationalVelocity(0.3);
    }
    public void performBehavior()
    {
        int left=0, right=0;
        float k=0;
        if (collisionDetected()) {
            // stop the robot
            setTranslationalVelocity(0.0);
            setRotationalVelocity(0);
        } else {
            // progress at 0.5 m/s
            setTranslationalVelocity(0.5);
            // frequently change orientation
            if ((getCounter() % 100)==0)
                setRotationalVelocity(Math.PI/2 * (0.5 - Math.random()));
        }
        //every 20 frames
        if (getCounter()%20==0){
            // print each sonars measurement
            for (int i=0;i< sonars.getNumSensors();i++) {
                double range = sonars.getMeasurement(i);
                double angle = sonars.getSensorAngle(i);
                boolean hit = sonars.hasHit(i);
                System.out.println("Sonar at angle "+ angle +
                        "measured range ="+range+ " has hit something:"+hit);
            }
            // print each bumper state
            for (int i=0;i< bumpers.getNumSensors();i++) {
                double angle = bumpers.getSensorAngle(i);
                boolean hit = bumpers.hasHit(i);
                System.out.println("Bumpers at angle "+ angle
                        + " has hit something:"+hit);
            }
        }
        for (int i=0;i<lineSens.getNumSensors()/2;i++)
        {
            left+=lineSens.hasHit(i)?1:0;
            right+=lineSens.hasHit(lineSens.getNumSensors()-i-1)?1:0;
            k++;
        }
        if (left==0 && right==0)
        {
            setRotationalVelocity(0);
            setTranslationalVelocity(0);
        }
        else
            this.setRotationalVelocity((left-right)/k*5);

    }
}
