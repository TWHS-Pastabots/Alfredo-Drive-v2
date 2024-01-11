// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.auto.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Arm.ArmState;

public class CurrentSpike extends Command {


  
  private Intake intake;
  private Arm arm;

  private double threshold = 18;

  private boolean outtake = false;
    
  public CurrentSpike() {
  }
  @Override
  public void initialize() {
    
    intake =  Intake.getInstance();
    arm =  Arm.getInstance();
    }

 

  @Override
  public void execute() {

    if (intake.getCurrent() > threshold && !outtake) {
       arm.setState(ArmState.RETRACTED);
       
    }

    if(arm.hasReachedTargetPose(1.5)){
        outtake =! outtake;
        intake.update(false, false);
    }


  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }
}