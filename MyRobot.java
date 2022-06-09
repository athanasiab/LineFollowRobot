import java.util.Random;
import  simbad.sim.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;

public class MyRobot extends Agent {
    static boolean CLOCKWISE=true;
    static double K1 = 5;
    static double K2 = 0.9;
    static double K3 = 3;
    boolean onhit=false;
    boolean look_for_line=false;
    final double SAFETY =0.8;
    RangeSensorBelt sonars;
    RangeSensorBelt bumpers;
    LightSensor lightL;
    LightSensor lightR;
    RangeSensorBelt touch1;
    LineSensor lineSens;
    int counter;
    int lap;
    Point3d check[];
    boolean on_line[];
    int number_of_sonars;
    Random rand;
    boolean lastline;
    public MyRobot (Vector3d position, String name)
    {
        super(position,name);
        //sensors
        lightL= RobotFactory.addLightSensorLeft(this);
        lightR= RobotFactory.addLightSensorRight(this);
        touch1= RobotFactory.addBumperBeltSensor(this, 8);
        lineSens = RobotFactory.addLineSensor(this,11);
        sonars = RobotFactory.addSonarBeltSensor(this,12);
        bumpers = RobotFactory.addBumperBeltSensor(this,8);

        on_line = new boolean[8];
        rand = new Random();
        counter =0;
        check = new Point3d[2];
        check[0] = new Point3d(0,0,0);
        check[1] = new Point3d(0,0,6);
        lap=0;
    }
    public void initBehavior() {

    }
    public Point3d getSensedPoint(int sonar){
        double v =radius+sonars.getMeasurement(sonar);
        double x = v*Math.cos(sonars.getSensorAngle(sonar));
        double z = v*Math.sin(sonars.getSensorAngle(sonar));
        return new Point3d(x,0,z);
    }
    void online(){
        int counter=0;
        for(int i=0;i<lineSens.getNumSensors();i++){

            if(lineSens.hasHit(i)){
                on_line[i]=true;
                counter+=(i-4)<0? -1 : 1;
            }else{
                on_line[i]=false;
            }


        }
        //this variable is true when last line sensor is on left side , false when last line sensor is on right side
        lastline=counter<=0;
        this.setRotationalVelocity(-counter*Math.PI/2);
        this.setTranslationalVelocity(0.5);
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

    @Override
    public void performBehavior()
    {
        //check light



        //check if you are on line
        boolean on=false;
        for(int i=0;i<lineSens.getNumSensors() && !on;i++){
            on=lineSens.hasHit(i);
        }

        //check for Hit in front of the Robot
        if(sonars.hasHit(0) ){
            onhit=true;

            look_for_line=false;
            // enable the flag look_for_line so the robot can follow the line after the obstacle
        }else if(!sonars.hasHit(0)  && onhit){
            look_for_line=true;
        }


        // if you are not on hit mode , just follow the line
        if(!onhit  && on){

            look_for_line=false;
            this.online();
        }
        else if(onhit){
            onhit=true;
            circumNavigate();

            if(look_for_line){


                lookForLine();

            }


        }
        else{
            // behavior when robot lose the line
            if(lastline){
                this.setRotationalVelocity(-Math.PI);
            }else{
                this.setRotationalVelocity(Math.PI);
            }
            this.setTranslationalVelocity(0.5);
        }




    }



    //this method lead the robot in correct position after it pass the obstacle
    private void lookForLine(){

        boolean stop=false;
        // check for line .
        for(int i=0;i<lineSens.getNumSensors();i++){
            if(lineSens.hasHit(i)){

                this.setRotationalVelocity(2*Math.PI);

                break;
            }
        }
        //find min sonar from obstcle
        int min=0;
        for (int i=1;i<sonars.getNumSensors();i++)
            if (sonars.getMeasurement(i)<sonars.getMeasurement(min))
                min=i;
        //look for line until the robot has behind it the obstacle that has already pass
        //if min is the sonar in the back part of the robot and the robot is on the line stop looking for linr
        if((lineSens.hasHit(lineSens.getNumSensors()/2) || lineSens.hasHit(lineSens.getNumSensors()/2-1) )&& min==number_of_sonars/2){
            onhit=false;
        }
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
