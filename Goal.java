
import javax.vecmath.Vector3d;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dvrakas
 */
public class Goal {
    double pose;
    Vector3d point;
    Goal(double x, double y, double z, double theta){
        point = new Vector3d(x,y,z);
        pose = theta;
    }
    double getPose(){
        return pose;
    }
    Vector3d getPoint(){
        return point;
    }
    double getX(){
        return point.x;
    }
    double getY(){
        return point.y;
    }
    double getZ(){
        return point.z;
    }
    double getPoseinDegrees(){
        return pose*180/360;
    }
}
