package frc.robot.Auto.Sequences;
import  frc.robot.Auto.Commands.*;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import com.pathplanner.lib.commands.PathPlannerAuto;

public class TestAuto extends SequentialCommandGroup {
     public TestAuto(){
        addCommands(
            new PathPlannerAuto("TestAuto"),
            new WaitCommand(2)
            // new AutoArmExtend(),
            // new WaitCommand(2)
            // new AutoOuttake(0.25)
            //new WaitCommand(2),
            //new AutoArmRetract(),
           // new WaitCommand(2),
        //    new PathPlannerAuto("Testv2Auto")
            

        );
     }
}
