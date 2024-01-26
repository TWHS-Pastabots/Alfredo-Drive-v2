package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swerve.Drivebase;

public class Robot extends TimedRobot {

  private Drivebase drivebase;
  // private VisionTablesListener visionTables;
  // private AutoAlign visAlign;
  

  // private static TorqueLogiPro driver;
  private static XboxController driver;

  private Command m_autoSelected;
  private SendableChooser<Command> m_chooser;

  @Override
  public void robotInit() {
    drivebase = Drivebase.getInstance();
    // visionTables = VisionTablesListener.getInstance();
    // visAlign = AutoAlign.getInstance();

     driver = new XboxController(0);
   // driver = new PS4Controller(0);

    drivebase.resetOdometry(new Pose2d(0.0, 0.0, new Rotation2d(0)));

    // m_chooser = AutoBuilder.buildAutoChooser();
    // m_chooser.addOption("DriveCommand", new PathPlannerAuto("TestAutov2"));
    // m_chooser.addOption("Straight Auto", new PathPlannerAuto("Straight"));
    // SmartDashboard.putData("Auto choices", m_chooser);
  }

  @Override
  public void robotPeriodic() {

    // CommandScheduler.getInstance().run();
    drivebase.periodic();

    // visionTables.putInfoOnDashboard();
  }

  @Override
  public void autonomousInit() {

    m_autoSelected = m_chooser.getSelected();
    
    if (m_autoSelected != null) {
      m_autoSelected.schedule();
    }

  
    // visionTables.putInfoOnDashboard();

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // if (m_autoSelected != null) {
    //   m_autoSelected.cancel();
    // }
  
  }

  @Override
  public void teleopPeriodic() {

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
    // SmartDashboard.putNumber("Vision yPose", visAlign.getY());
   SmartDashboard.putNumber("rot", rot);

    if(driver.getYButton()){
      fieldRelative = ! fieldRelative;
    }

    //  if (driver.getTrigger()) {
    //    rot = driver.getYaw();
    //  }

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
     }
    //  } else if (driver.getAButton()){
    //     //drivebase.drive(0, 0, visAlign.getRotSpeed(), fieldRelative);
    //     drivebase.drive(visAlign.getXSpeed(), visAlign.getYSpeed(), visAlign.getRotSpeed(), fieldRelative);
     else {
       drivebase.drive(xSpeed, ySpeed, rot, fieldRelative);
      
     }

    // visionTables.putInfoOnDashboard();
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
