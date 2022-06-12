import java.util.Random;
import  simbad.sim.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;

public class MyRobot extends Agent {
    static boolean CLOCKWISE=true;
    static double K1 = 5;
    static double K2 = 0.9;
    static double K3 = 3;

    final double SAFETY =0.8;
    RangeSensorBelt sonars;
    LightSensor lightL;
    LightSensor lightR;
    RangeSensorBelt touch1;
    LineSensor lineSens;
    boolean goal = false;
    boolean hasHitLine, isHasHitSonar, isHasHitLight = false;
    boolean onAvoidMode;


    public MyRobot (Vector3d position, String name)
    {
        super(position,name);
        //sensors
        lightL= RobotFactory.addLightSensorLeft(this);
        lightR= RobotFactory.addLightSensorRight(this);
        touch1= RobotFactory.addBumperBeltSensor(this, 8);
        lineSens = RobotFactory.addLineSensor(this,11);
        sonars = RobotFactory.addSonarBeltSensor(this,8);
    }

    public void initBehavior() {
        //rotateY(Math.PI);
    }

    public void performBehavior()
    {
        if(!goal) {
            boolean tempHasHitLine = false;
            boolean tempisHasHitSonar = false;

            for (int i = 0; i < lineSens.getNumSensors(); i++) { //searches for the line
                if (lineSens.hasHit(i)) {
                    tempHasHitLine = true;
                    System.out.println("Has found line");
                    break;
                }
            }
            if (sonars.getFrontQuadrantHits() != 0) { //front sonars don't detect obstacle
                tempisHasHitSonar = true;
            }

            if ((!onAvoidMode && !tempHasHitLine) || isHasHitLight) {
                System.out.println("Follows light");
                //if it doesn't find line or doesn't see obstacle it follows the light
                if(tempHasHitLine)
                {
                    isHasHitLight = false;
                    isHasHitSonar = false;
                    hasHitLine = true;
                    onAvoidMode = false;
                    followLine();
                } else if (tempisHasHitSonar) {
                    isHasHitLight = false;
                    hasHitLine = false;
                    isHasHitSonar = true;
                    onAvoidMode = true;
                    circumNavigate();
                }else{
                    System.out.println("FIRST IF");
                    isHasHitLight = true;
                    isHasHitSonar = false;
                    hasHitLine = false;
                    followLight();
                }
            } else if ((onAvoidMode && tempHasHitLine && !hasHitLine) || (tempHasHitLine && !tempisHasHitSonar && !onAvoidMode)) {
                //Was on obstacle avoidance mode and has found line but hasn't followed it
                //or is following line
                System.out.println("Follows the line");
                isHasHitSonar = false;
                hasHitLine = true;
                onAvoidMode = false;
                followLine();
            } else {
                System.out.println("Avoids obstacle");
                hasHitLine = false;
                isHasHitSonar = true;
                onAvoidMode = true;
                circumNavigate();
            }
        }

    }

    void followLight()
    {
        //Robot follows the light
        double l = lightL.getAverageLuminance();
        double r = lightR.getAverageLuminance();
        if(l <= 0.22 || r <= 0.22)
        {
            setRotationalVelocity(0);
            setTranslationalVelocity(0);
            goal = true;
            return;
        }
        setRotationalVelocity(l-r);
        setTranslationalVelocity(0.2);
    }

    void followLine(){
        //Follows the line
        int left = 0, right = 0;
        float k = 0;
        for (int i = 0; i < lineSens.getNumSensors() / 2; i++)
        {
            left += lineSens.hasHit(i)?1:0;
            right += lineSens.hasHit(lineSens.getNumSensors() - i - 1)? 1: 0;
            k++;
        }
        if(left == right) {
            left += 2;
        }
        this.setRotationalVelocity((left - right) / k * 5);
        this.setTranslationalVelocity(0.2);
    }

    public void circumNavigate(){
        //Goes around the obstacle
        int min;
        min = 0;
        for (int i = 1; i<sonars.getNumSensors(); i++)
            if (sonars.getMeasurement(i) < sonars.getMeasurement(min))
                min = i;
        Point3d p = getSensedPoint(min);
        double d = p.distance(new Point3d(0, 0, 0));
        Vector3d v;

        v = CLOCKWISE? new Vector3d(-p.z, 0, p.x): new Vector3d(p.z, 0, -p.x);
        double phLin = Math.atan2(v.z, v.x);
        double phRot = Math.atan(K3 * (d - SAFETY));
        if (CLOCKWISE)
            phRot =- phRot;
        double phRef = wrapToPi(phLin + phRot);

        setRotationalVelocity(K1 * phRef);
        setTranslationalVelocity(K2 * Math.cos(phRef)/2);
    }

    public Point3d getSensedPoint(int sonar){
        double v = radius + sonars.getMeasurement(sonar);
        double x = v * Math.cos(sonars.getSensorAngle(sonar));
        double z = v * Math.sin(sonars.getSensorAngle(sonar));
        return new Point3d(x, 0, z);
    }

    public double wrapToPi(double a)
    {
        if (a > Math.PI)
            return a - Math.PI * 2;
        if (a <= -Math.PI)
            return a + Math.PI * 2;
        return a;
    }
}