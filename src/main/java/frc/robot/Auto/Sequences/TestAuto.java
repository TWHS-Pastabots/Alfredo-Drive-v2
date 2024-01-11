package frc.robot.auto.Sequences;
import  frc.robot.auto.Commands.*;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import com.pathplanner.lib.commands.PathPlannerAuto;

public class TestAuto extends SequentialCommandGroup {
     public TestAuto(){
        addCommands(
            new PathPlannerAuto("Straight"),
            new WaitCommand(2),
             new AutoArmExtend(),
            new WaitCommand(2),
            //  new AutoOuttake(2),
            // new WaitCommand(2)
            new AutoArmRetract(),
           new WaitCommand(2)
        //    new PathPlannerAuto("Testv2Auto")
            

        );
     }
}
