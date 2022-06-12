import java.util.Arrays;
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
    int counter;
    int lap;
    Point3d check[];
    boolean on_line[];
    Random rand;
    boolean goal = false;
    boolean hasHitLine, isHasHitSonar, isHasHitLight = false;
    boolean onavoidmode;


    public MyRobot (Vector3d position, String name)
    {
        super(position,name);
        //sensors
        lightL= RobotFactory.addLightSensorLeft(this);
        lightR= RobotFactory.addLightSensorRight(this);
        touch1= RobotFactory.addBumperBeltSensor(this, 8);
        lineSens = RobotFactory.addLineSensor(this,11);
        sonars = RobotFactory.addSonarBeltSensor(this,8);

        //bumpers = RobotFactory.addBumperBeltSensor(this,8);

        on_line = new boolean[11];
        rand = new Random();
        counter =0;
        check = new Point3d[3];
        check[0] = new Point3d(0,0,0);
        check[1] = new Point3d(0,0,6);
        lap=0;
    }
    public void initBehavior() {
        rotateY(Math.PI);
    }
    public void performBehavior()
    {
        if(!goal) {
            boolean tempHasHitLine = false;
            boolean tempisHasHitSonar = false;

            for (int i = 0; i < lineSens.getNumSensors(); i++) {
                if (lineSens.hasHit(i)) {
                    tempHasHitLine = true;
                    System.out.println("HIT");
                    break;
                }
            }
            if (sonars.getFrontQuadrantHits() != 0) {
                tempisHasHitSonar = true;
            }

            if ((!onavoidmode && !tempHasHitLine) || isHasHitLight) {
                System.out.println("FIRST IF");
                isHasHitLight = true;
                isHasHitSonar = false;
                hasHitLine = false;
                followLight();
            } else if ((onavoidmode && tempHasHitLine && !hasHitLine) || (tempHasHitLine && !tempisHasHitSonar && !onavoidmode)) {
                System.out.println("SECOND IF");
                isHasHitSonar = false;
                hasHitLine = true;
                onavoidmode = false;
                followLine();
            } else {
                System.out.println("THIRD IF");
                hasHitLine = false;
                isHasHitSonar = true;
                onavoidmode = true;
                circumNavigate();
            }
        }
      /*  if (lineEnd() || isHasHitLight)
        {
            hasHitLine = false;
            isHasHitSonar = false;
            isHasHitLight = true;
            followLight();
        }
        else if (hasHitLine && tempisHasHitSonar)
        {
            circumNavigate();
            hasHitLine = false;
            isHasHitSonar = true;
            isHasHitLight = false;
        }
        else if (isHasHitSonar && tempisHasHitSonar)
        {
            circumNavigate();
            hasHitLine = false;
            isHasHitSonar = true;
            isHasHitLight = false;
        }
        else if (isHasHitSonar && tempHasHitLine)
        {
            followLine();
            hasHitLine = true;
            isHasHitSonar = false;
            isHasHitLight = false;
        }
        else if(hasHitLine && tempHasHitLine && !isHasHitLight) {
            {
                followLine();
                hasHitLine = true;
                isHasHitSonar = false;
                isHasHitLight = false;
            }
        }
        else
        {
            {
                followLine();
                hasHitLine = true;
                isHasHitSonar = false;
                isHasHitLight = false;
            }
        }


       /* if(leftLine)
        {
            lineHit();
        }
        if(!leftLine)
        {
            sonarHit();
        }



        //System.out.println("Obstacle mode:" + obstacleMode);
        //System.out.println("Line mode:" + lineMode);
        if(!lineMode && !obstacleMode && !foundline) { //is executed when the robot found ghe end of the line and has no obstacle in front of it
            followLight();
        }
        else if(obstacleMode && lineMode && leftLine)
        {
            foundline = true;
            System.out.println("First If");
            leftLine = false;
            obstacleMode = false;
            followLine();
        }
        else if(lineMode && !obstacleMode)
        {
            System.out.println("Second If");
            leftLine = false;
            followLine();
        }
        else if(obstacleMode)
        {
            System.out.println("Third If");
            foundline = false;
            leftLine = true;
            lineMode = false;
            circumNavigate();
        }*/

    }
    void followLight()
    {
        //Robot follows the light
        double l = lightL.getAverageLuminance();
        double r = lightR.getAverageLuminance();
        if(l <= 0.25 || r <= 0.22)
        {
            setRotationalVelocity(0);
            setTranslationalVelocity(0);
            goal = true;
            return;
        }
        setRotationalVelocity(l-r);
        setTranslationalVelocity(0.5);

    }
    void followLine(){
        int left=0, right=0;
        float k=0;
        for (int i=0;i<lineSens.getNumSensors()/2;i++)
        {
            left+=lineSens.hasHit(i)?1:0;
            right+=lineSens.hasHit(lineSens.getNumSensors()-i-1)?1:0;
            k++;
        }
        this.setRotationalVelocity((left-right)/k*5);
        this.setTranslationalVelocity(0.2);
    }
    public void circumNavigate(){
        int min;
        min=0;
        for (int i=1;i<sonars.getNumSensors();i++)
            if (sonars.getMeasurement(i)<sonars.getMeasurement(min))
                min=i;
        Point3d p = getSensedPoint(min);
        double d = p.distance(new Point3d(0,0,0));
        Vector3d v;

        v = CLOCKWISE? new Vector3d(-p.z,0,p.x): new Vector3d(p.z,0,-p.x);
        double phLin = Math.atan2(v.z,v.x);
        double phRot =Math.atan(K3*(d-SAFETY));
        if (CLOCKWISE)
            phRot=-phRot;
        double phRef = wrapToPi(phLin+phRot);

        setRotationalVelocity(K1*phRef);
        setTranslationalVelocity(K2*Math.cos(phRef));
    }

    public Point3d getSensedPoint(int sonar){
        double v =radius+sonars.getMeasurement(sonar);
        double x = v*Math.cos(sonars.getSensorAngle(sonar));
        double z = v*Math.sin(sonars.getSensorAngle(sonar));
        return new Point3d(x,0,z);
    }
    public double wrapToPi(double a)
    {
        if (a>Math.PI)
            return a-Math.PI*2;
        if (a<=-Math.PI)
            return a+Math.PI*2;
        return a;
    }
}
