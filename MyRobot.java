package first;

import javax.vecmath.Vector3d;
import simbad.sim.Agent;
import simbad.sim.LampActuator;
import simbad.sim.RobotFactory;

public class MyRobot extends Agent {
    public MyRobot (Vector3d position, String name) {
        super(position,name);
    }
    public void initBehavior() {
        //setTranslationalVelocity(0.5);
        //setRotationalVelocity(0.3);
    }
    public void performBehavior()
    {
        setTranslationalVelocity(0.5);
        setRotationalVelocity(Math.PI/2*(0.5-Math.random()));

        /*
        getRotationalVelocity() //radians/second
        getTranslationalVelocity() //meters/second

        collisionDetected() //true αν το ρομπότ έχει συγκρουστεί με κάποιο αντικείμενο
        getLifeTime() //ο συνολικός χρόνος ζωής του ρομπότ
     */
    }
}
