package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Auto.Sequences.TestAuto;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Arm.ArmControlSpeed;
import frc.robot.subsystems.Arm.ArmControlState;
import frc.robot.subsystems.Arm.ArmState;
import frc.robot.subsystems.Swerve.Drivebase;

public class Robot extends TimedRobot {

  private Drivebase drivebase;
  private Intake intake;
  private Arm arm;
  private VisionTablesListener visionTables;
  private AutoAlign visAlign;
  

  private static TorqueLogiPro driver;
//private static PS4Controller driver;
  private static XboxController operator;

  private boolean outtake;
  private boolean cycle;
  private boolean manual;

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
    visionTables = VisionTablesListener.getInstance();
    visAlign = AutoAlign.getInstance();

     driver = new TorqueLogiPro(0);
   // driver = new PS4Controller(0);
    operator = new XboxController(1);

    drivebase.resetOdometry(new Pose2d(0.0, 0.0, new Rotation2d(0)));
    
    //SmartDashboard.putData("Test Auto", new PathPlannerAuto("TestAuto"));
    //SmartDashboard.putData("Ansh", new PathPlannerAuto("Ansh"));
    //SmartDashboard.putData("First Full Auto", new TestAuto());


    m_chooser = AutoBuilder.buildAutoChooser();
   // m_chooser.setDefaultOption("Default Auto", new PathPlannerAuto("TestAuto"));
   // m_chooser.addOption("Ansh Auto", new PathPlannerAuto("Ansh"));
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

    if (m_autoSelected != null) {
      m_autoSelected.cancel();
    }
  
  }

  @Override
  public void teleopPeriodic() {

    boolean fieldRelative = true;

    /* Drive Controls */
     double 
     ySpeed = -driver.getRoll();
     double xSpeed = -driver.getPitch();

   // double ySpeed = driver.getLeftY();
    //double xSpeed = driver.getLeftX();
   // double rot = driver.getRightX();
   double rot = 0;

    SmartDashboard.putNumber("Xspeed", xSpeed);
    SmartDashboard.putNumber("Yspeed", ySpeed);
    SmartDashboard.putNumber("Vision yPose", visAlign.getY());
   SmartDashboard.putNumber("rot", rot);

     if (driver.getTrigger()) {
       rot = driver.getYaw();
     }

     if (driver.getButtonByIndex(10)){
      fieldRelative = ! fieldRelative;
     }

     if (driver.getButtonByIndex(7)) {
       drivebase.lockWheels();
     } else if (driver.getButtonByIndex(2)){
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
     }

    arm.update(operator.getRawAxis(Controller.PS_AXIS_RIGHT_Y) * .5,
        operator.getRawAxis(Controller.PS_AXIS_LEFT_Y) * .5);

    if (arm.state != ArmState.RETRACTED) {
      outtake = operator.getRawButton(Controller.XBOX_A);
    } else {
      outtake = false;
    }
    boolean intakeButton = operator.getRawButton(Controller.XBOX_B);
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
