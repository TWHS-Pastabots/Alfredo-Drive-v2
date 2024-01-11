package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Current;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.auto.Commands.CurrentSpike;
import frc.robot.auto.Sequences.TestAuto;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Arm.ArmControlSpeed;
import frc.robot.subsystems.Arm.ArmControlState;
import frc.robot.subsystems.Arm.ArmState;
import frc.robot.subsystems.Intake.IntakeState;
import frc.robot.subsystems.Swerve.Drivebase;

public class Robot extends TimedRobot {

  private Drivebase drivebase;
  private Intake intake;
  private Arm arm;
  private VisionTablesListener visionTables;
  private AutoAlign visAlign;
  

  // private static TorqueLogiPro driver;
  private static XboxController driver;
  private static XboxController operator;

  private boolean outtake;
  private boolean cycle;
  private boolean manual;

  
   private Command currentSpike;
  private Command driveCommand;

 // private static final String kDefaultAuto = "TestAuto";
  //private static final String kCustomAuto = "New Path";
  private boolean isPathExecuted = false;

  private Command m_autoSelected;
  private SendableChooser<Command> m_chooser;

  @Override
  public void robotInit() {
    drivebase = Drivebase.getInstance();
    intake = Intake.getInstance();
    arm = Arm.getInstance();
    currentSpike = new CurrentSpike();
    visionTables = VisionTablesListener.getInstance();
    visAlign = AutoAlign.getInstance();

     driver = new XboxController(0);
   // driver = new PS4Controller(0);
    operator = new XboxController(1);

    drivebase.resetOdometry(new Pose2d(0.0, 0.0, new Rotation2d(0)));

    m_chooser = AutoBuilder.buildAutoChooser();
    m_chooser.setDefaultOption("Arm+Drive Auto", new TestAuto());
    m_chooser.addOption("DriveCommand", new PathPlannerAuto("TestAutov2"));
    m_chooser.addOption("Straight Auto", new PathPlannerAuto("Straight"));
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    drivebase.periodic();

    SmartDashboard.putBoolean("Arm Manual:", manual);
    visionTables.putInfoOnDashboard();
  }

  @Override
  public void autonomousInit() {

    //m_autoSelected = m_chooser.getSelected();
    m_autoSelected = new TestAuto();
    
    if (m_autoSelected != null) {
      m_autoSelected.schedule();
    }

  
    visionTables.putInfoOnDashboard();

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    currentSpike.initialize();
    if (m_autoSelected != null) {
      m_autoSelected.cancel();
    }
  
  }

  @Override
  public void teleopPeriodic() {


       if (arm.state != ArmState.RETRACTED) {
      outtake = operator.getRawButton(Controller.XBOX_A);
        
    } else {
      outtake = false;
    }
      boolean intakeButton = operator.getRawButton(Controller.XBOX_B);
    currentSpike.execute();

    boolean fieldRelative = true;

    /* Drive Controls */
     //double ySpeed = -driver.getRoll();
    // double xSpeed = -driver.getPitch();

    double ySpeed = -driver.getLeftX();
    double xSpeed = driver.getLeftY();
    double rot = driver.getRightX();
  // double rot = 0;

    SmartDashboard.putNumber("Xspeed", xSpeed);
    SmartDashboard.putNumber("Yspeed", ySpeed);
    SmartDashboard.putNumber("Vision yPose", visAlign.getY());
   SmartDashboard.putNumber("rot", rot);
   SmartDashboard.putNumber("Intake Current", intake.getCurrent());
   

    //  if (driver.getTrigger()) {
    //    rot = driver.getYaw();
    //  }

    if(driver.getYButton()){
      fieldRelative = ! fieldRelative;
    }

    //  if (driver.getButtonByIndex(10)){
    //   fieldRelative = ! fieldRelative;
    //  }

    //  if (driver.getButtonByIndex(7)) {
    //    drivebase.lockWheels();
    //  } else if (driver.getButtonByIndex(2)){
    //     //drivebase.drive(0, 0, visAlign.getRotSpeed(), fieldRelative);
    //     drivebase.drive(visAlign.getXSpeed(), visAlign.getYSpeed(), visAlign.getRotSpeed(), fieldRelative);

    //  } else {
    //    drivebase.drive(xSpeed, ySpeed, rot, fieldRelative);
    //  }

     if (driver.getXButton()) {
       drivebase.lockWheels();
     } else if (driver.getAButton()){
        //drivebase.drive(0, 0, visAlign.getRotSpeed(), fieldRelative);
        drivebase.drive(visAlign.getXSpeed(), visAlign.getYSpeed(), visAlign.getRotSpeed(), fieldRelative);

     } else {
       drivebase.drive(xSpeed, ySpeed, rot, fieldRelative);
      
     }

    // /* Arm Controls */

    // // finer control when holding L1
     if (operator.getRawButton(Controller.XBOX_LB)) {
       arm.setControlSpeed(ArmControlSpeed.FINE);
     } else {
       arm.setControlSpeed(ArmControlSpeed.FULL);
     }

    // // manage arm control states
     if (driver.getRawButton(9)) {
       if (arm.controlState == ArmControlState.MANUAL) {
         arm.setControlState(ArmControlState.PID);
         manual = false;
       } else {
         arm.setControlState(ArmControlState.MANUAL);
         manual = true;
       }
     }

    // // manage arm PID states & update


     if (operator.getRawButton(Controller.XBOX_X)) {
       arm.setState(ArmState.EXTENDED);
     } else if (operator.getRawButton(Controller.XBOX_Y)) {
       arm.setState(ArmState.GROUND_INTAKE);
       intakeButton = true;
     } else if (operator.getRawButton(Controller.XBOX_LB)) {
       arm.setState(ArmState.RETRACTED);
     } else if (operator.getRawButtonPressed(Controller.XBOX_RB)) {
       if (cycle) {
         arm.setState(ArmState.MID);
         cycle = false;
       } else {
         arm.setState(ArmState.LOW);
        cycle = true;
       }
     } else if (!outtake){
      intake.update(false, false);
     }

    arm.update(operator.getRawAxis(Controller.PS_AXIS_RIGHT_Y) * .5,
        operator.getRawAxis(Controller.PS_AXIS_LEFT_Y) * .5);

 
    intake.update(outtake, intakeButton);

    
    arm.update(0, 0);
    visionTables.putInfoOnDashboard();
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
