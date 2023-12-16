package frc.robot;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
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

  // private static TorqueLogiPro driver;
  private static PS4Controller driver;
  private static XboxController operator;

  private boolean outtake;
  private boolean cycle;
  private boolean manual;

  private Command driveCommand;

  private static final String kDefaultAuto = "DriveCommand";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  @Override
  public void robotInit() {
    drivebase = Drivebase.getInstance();
    intake = Intake.getInstance();
    arm = Arm.getInstance();
    visionTables = VisionTablesListener.getInstance();

    // driver = new TorqueLogiPro(0);
    driver = new PS4Controller(0);
    operator = new XboxController(1);

    m_chooser.addOption("Drive Command", kDefaultAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  @Override
  public void robotPeriodic() {
    drivebase.periodic();
    // arm.update(0, 0);

    SmartDashboard.putBoolean("Arm Manual:", manual);
    visionTables.putInfoOnDashboard();
  }

  @Override
  public void autonomousInit() {

    driveCommand = drivebase.getCommand("Ansh");

    driveCommand.initialize();

    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    visionTables.putInfoOnDashboard();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
        driveCommand.execute();

    // switch (m_autoSelected) {
    // case kDefaultAuto:
    // default:
    // driveCommand.execute();
    // break;
    // }
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {

    /* Drive Controls */
    // double ySpeed = -driver.getRoll();
    // double xSpeed = -driver.getPitch();

    double ySpeed = -driver.getLeftY();
    double xSpeed = -driver.getLeftX();
    double rot = driver.getRightX();

    SmartDashboard.putNumber("Xspeed", xSpeed);
    SmartDashboard.putNumber("Yspeed", ySpeed);
    SmartDashboard.putNumber("rot", rot);

    // if (driver.getTrigger()) {
    //   rot = driver.getYaw();
    // }

    // if (driver.getButtonByIndex(7)) {
    //   drivebase.lockWheels();
    // } else {
    //   drivebase.drive(xSpeed, ySpeed, rot, true);
    // }

    // /* Arm Controls */

    // // finer control when holding L1
    // if (operator.getRawButton(Controller.XBOX_LB)) {
    //   arm.setControlSpeed(ArmControlSpeed.FINE);
    // } else {
    //   arm.setControlSpeed(ArmControlSpeed.FULL);
    // }

    // // manage arm control states
    // if (driver.getRawButton(9)) {
    //   if (arm.controlState == ArmControlState.MANUAL) {
    //     arm.setControlState(ArmControlState.PID);
    //     manual = false;
    //   } else {
    //     arm.setControlState(ArmControlState.MANUAL);
    //     manual = true;
    //   }
    // }

    // // manage arm PID states & update

    // if (operator.getRawButton(Controller.XBOX_X)) {
    //   arm.setState(ArmState.EXTENDED);
    // } else if (operator.getRawButton(Controller.XBOX_Y)) {
    //   arm.setState(ArmState.GROUND_INTAKE);
    // } else if (operator.getRawButton(Controller.XBOX_LB)) {
    //   arm.setState(ArmState.RETRACTED);
    // } else if (operator.getRawButtonPressed(Controller.XBOX_RB)) {
    //   if (cycle) {
    //     arm.setState(ArmState.MID);
    //     cycle = false;
    //   } else {
    //     arm.setState(ArmState.LOW);
    //     cycle = true;
    //   }
    // }

    // arm.update(operator.getRawAxis(Controller.PS_AXIS_RIGHT_Y) * .5,
    //     operator.getRawAxis(Controller.PS_AXIS_LEFT_Y) * .5);

    // if (arm.state != ArmState.RETRACTED) {
    //   outtake = operator.getRawButton(Controller.XBOX_A);
    // } else {
    //   outtake = false;
    // }
    // boolean intakeButton = operator.getRawButton(Controller.XBOX_B);
    // intake.update(outtake, intakeButton);

    // /* Drive Controls */

    // // slow driving while holding left bumper, fast while holding right bumper
    // arm.update(0, 0);
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
