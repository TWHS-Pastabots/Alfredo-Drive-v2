package frc.robot.subsystems;
import edu.wpi.first.math.controller.PIDController;

public class AutoAlign {
    VisionTablesListener vTListen;
    double x;
    private PIDController xPID = new PIDController(0.1, 0, 0);
    public static AutoAlign instance;
    public double yMaxSpeed;

    public AutoAlign() {
        vTListen = VisionTablesListener.getInstance();
        x = 0;
        yMaxSpeed = 0.25;
    }

    public static AutoAlign getInstance() {
        if (instance == null)
            instance = new AutoAlign();
        return instance;
    }
    public double getYSpeed(){
        x = vTListen.getX();

        if(x < -0.7)
            return yMaxSpeed;
        else if(x > -0.64)
            return -yMaxSpeed;
        return 0.0;
    }

}
