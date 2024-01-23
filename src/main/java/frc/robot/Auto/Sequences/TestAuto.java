package frc.robot.auto.Sequences;
import frc.robot.Constants.AutoConstants;
import  frc.robot.auto.Commands.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import java.util.GregorianCalendar;
import java.util.List;

import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

public class TestAuto extends SequentialCommandGroup {

     public TestAuto(){

      List<Translation2d> bezierPoints = PathPlannerPath.bezierFromPoses(new Pose2d(0.0, 0.0, Rotation2d.fromRadians(0)), new Pose2d(0, 0, Rotation2d.fromRotations(0)));
        
      PathPlannerPath path = new PathPlannerPath(bezierPoints, 
         new PathConstraints(AutoConstants.kMaxSpeedMetersPerSecond, AutoConstants.kMaxAccelerationMetersPerSecondSquared, AutoConstants.kMaxAngularSpeedRadiansPerSecond, AutoConstants.kMaxAngularSpeedRadiansPerSecondSquared),
         new GoalEndState(0, new Rotation2d(0, 0))
         );

        addCommands(
         new PathPlannerAuto("blub")
        );
     }

}
