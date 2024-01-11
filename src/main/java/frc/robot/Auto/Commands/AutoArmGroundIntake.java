package frc.robot.auto.Commands;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.*;
import frc.robot.subsystems.Arm.*;

public class AutoArmGroundIntake extends Command {
    private Arm arm;
    private boolean ended = false;

    public AutoArmGroundIntake() {
    }

    @Override
    public void initialize() {
        arm = Arm.getInstance();
    }

    @Override
    public void execute() {
        arm.setState(ArmState.GROUND_INTAKE);

        if (arm.hasReachedTargetPose(1.5)) {
            ended = true;
        }
    }

    @Override
    public void end(boolean interrupted) {
    }

    @Override
    public boolean isFinished() {
        return ended;
    }
}