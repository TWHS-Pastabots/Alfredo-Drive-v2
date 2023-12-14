package frc.robot.subsystems.Swerve;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.commands.*;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Ports;
import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drivebase extends SubsystemBase {
  private static Drivebase instance;

  public SwerveModule frontLeft;
  public SwerveModule backLeft;
  public SwerveModule frontRight;
  public SwerveModule backRight;

  private HolonomicPathFollowerConfig config;
  private ChassisSpeeds speeds;

  private static AHRS gyro;
  // Slew rate filter variables for controlling lateral acceleration
  private double currentRotation = 0.0;

  // Odometry class for tracking robot pose
  SwerveDriveOdometry odometry;

  /** Creates a new DriveSubsystem. */
  public Drivebase() {

    // Swerve modules

    frontLeft = new SwerveModule(Ports.leftSpeed1, Ports.leftAngle1, DriveConstants.kFrontLeftChassisAngularOffset);
    backLeft = new SwerveModule(Ports.leftSpeed2, Ports.leftAngle2, DriveConstants.kBackLeftChassisAngularOffset);

    frontRight = new SwerveModule(Ports.rightSpeed1, Ports.rightAngle1, DriveConstants.kFrontRightChassisAngularOffset);
    backRight = new SwerveModule(Ports.rightSpeed2, Ports.rightAngle2, DriveConstants.kBackRightChassisAngularOffset);

    // gyro
    gyro = new AHRS(SPI.Port.kMXP);


    gyro.setAngleAdjustment(90);
    gyro.zeroYaw();

    odometry = new SwerveDriveOdometry(
        DriveConstants.kDriveKinematics,
        Rotation2d.fromDegrees(-gyro.getAngle()), new SwerveModulePosition[] {
            frontLeft.getPosition(),
            backLeft.getPosition(),
            frontRight.getPosition(),
            backRight.getPosition()
        });

    config = new HolonomicPathFollowerConfig(new PIDConstants(1, 0, 0),
    new PIDConstants(1, 0, 0),
    2, Math.sqrt(Math.pow(DriveConstants.kTrackWidth / 2, 2) +
    Math.pow(DriveConstants.kWheelBase / 2, 2)),
    new ReplanningConfig());

  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Gyro Angle:", Math.toRadians(gyro.getAngle()));
    SmartDashboard.putNumber("X-coordinate", getPose().getX());
    SmartDashboard.putNumber("Y-coordinate", getPose().getY());

    // Update the odometry in the periodic block
    odometry.update(Rotation2d.fromDegrees(-gyro.getAngle()),
        new SwerveModulePosition[] { frontLeft.getPosition(), backLeft.getPosition(), frontRight.getPosition(),
            backRight.getPosition() });
  }

  // Returns the currently-estimated pose of the robot
  public Pose2d getPose() {
    return new Pose2d(-odometry.getPoseMeters().getY(), odometry.getPoseMeters().getX(),
        odometry.getPoseMeters().getRotation());
  }

  // Resets the odometry to the specified pose
  public void resetOdometry(Pose2d pose) {
    odometry.resetPosition(
        Rotation2d.fromDegrees(-gyro.getAngle()),
        new SwerveModulePosition[] { frontLeft.getPosition(), backLeft.getPosition(), frontRight.getPosition(),
            backRight.getPosition() },
        pose);
  }

  public void resetOdometry() {
    odometry.resetPosition(
        Rotation2d.fromDegrees(-gyro.getAngle()),
        new SwerveModulePosition[] { frontLeft.getPosition(), backLeft.getPosition(), frontRight.getPosition(),
            backRight.getPosition() },
        getPose());
  }

  public void drive(double forward, double side, double rot, boolean fieldRelative) {

    double xSpeedCommanded;
    double ySpeedCommanded;

    xSpeedCommanded = side;
    ySpeedCommanded = forward;
    currentRotation = rot;

    // Convert the commanded speeds into the correct units for the drivetrain
    double xSpeedDelivered = xSpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
    double ySpeedDelivered = ySpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
    double rotDelivered = currentRotation * DriveConstants.kMaxAngularSpeed;

    var chassisspeeds = fieldRelative
        ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered,
            Rotation2d.fromDegrees(-gyro.getAngle()))
        : new ChassisSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered);

    setChassisSpeed(chassisspeeds);
  }

  public void setChassisSpeed(ChassisSpeeds input) {
    speeds = input;
    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(speeds);

    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);

    frontLeft.setDesiredState(swerveModuleStates[0]);
    backLeft.setDesiredState(swerveModuleStates[2]);
    frontRight.setDesiredState(swerveModuleStates[1]);
    backRight.setDesiredState(swerveModuleStates[3]);
  }

  public ChassisSpeeds getRobotRelativeSpeeds() {
    return speeds;
  }

  public Command getCommand(String pathName) {
  PathPlannerPath path = PathPlannerPath.fromPathFile(pathName);

  return new FollowPathWithEvents(
  new FollowPathHolonomic(path, this::getPose, this::getRobotRelativeSpeeds,
  this::setChassisSpeed, config, this),
  path, this::getPose);
  }

  public void lockWheels() {
    frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    backLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    backRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
  }

  // sets state for all modules
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.desaturateWheelSpeeds(
        desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
    frontLeft.setDesiredState(desiredStates[0]);
    backLeft.setDesiredState(desiredStates[2]);
    frontRight.setDesiredState(desiredStates[1]);
    backRight.setDesiredState(desiredStates[3]);
  }

  // sets drive encoders to 0
  public void resetEncoders() {
    frontLeft.resetEncoders();
    backLeft.resetEncoders();
    frontRight.resetEncoders();
    backRight.resetEncoders();
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    gyro.reset();
  }

  // Returns the heading of the robot(=180 to 180)
  public double getHeading() {
    return Rotation2d.fromDegrees(-gyro.getAngle()).getDegrees();
  }

  // Returns the turn rate of the robot
  public double getTurnRate() {
    return gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  public static Drivebase getInstance() {
    if (instance == null) {
      instance = new Drivebase();
    }
    return instance;
  }
}