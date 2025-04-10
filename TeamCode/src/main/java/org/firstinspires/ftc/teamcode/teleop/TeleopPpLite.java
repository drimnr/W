package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Commands.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Commands.Horizontal_Lift;
import org.firstinspires.ftc.teamcode.hardware.Commands.Intake;
import org.firstinspires.ftc.teamcode.hardware.Commands.Lift;
import org.firstinspires.ftc.teamcode.hardware.Commands.Outtake;
import org.firstinspires.ftc.teamcode.hardware.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.hardware.pedroPathing.constants.LConstants;

@TeleOp
public class TeleopPpLite extends OpMode
{
    MultipleTelemetry telemetryA;
    ElapsedTime peredachatimer = new ElapsedTime();
    ElapsedTime timerbt = new ElapsedTime();
    ElapsedTime taketimer = new ElapsedTime();
    ElapsedTime timerpd = new ElapsedTime();
    ElapsedTime lifttimer = new ElapsedTime();
    Lift lift;
    Intake intake;
    Outtake outtake;
    Horizontal_Lift horlift;
    Drivetrain drivetrain;
    String mode = "PID";
    boolean outtaketake = false, take = false, peredtake = false, borttake = false;
    boolean modedrive = false;
    boolean button_a = false, rb = false, holdlift = false;
    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        modedrive = false;
        telemetryA = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());



        lift = new Lift(hardwareMap, telemetryA);
        outtake = new Outtake(hardwareMap, telemetryA);
        intake = new Intake(hardwareMap, telemetryA);
        horlift = new Horizontal_Lift(hardwareMap, telemetryA);
        drivetrain = new Drivetrain(hardwareMap, telemetryA, intake, horlift, outtake, lift);
        telemetryA.addData("Status", "Initialized");
        telemetryA.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        lift.set_target_position(0);
        drivetrain.startTeleOPDrive();
        peredachatimer.reset();
        timerbt.reset();
        taketimer.reset();
        timerpd.reset();



        horlift.close();
        intake.setMidPovisheTake();
        intake.close();
        outtake.setZadTake();
    }
    @Override
    public void loop() {
        // intake
        if(gamepad2.a && !button_a) {

            if(!intake.atTaking()) {
                intake.open();
                intake.setMidTake();
            }
            else {
                take = true;
                taketimer.reset();
                intake.setSampleTake();
                intake.open();
            }
        }
        if (gamepad1.right_bumper) {
            take = true;
            taketimer.reset();
            intake.setSampleTake();
            intake.open();
        }
        button_a = gamepad2.a;

        if (gamepad1.left_bumper) {
            intake.rotate_Mid();
        }
        if (gamepad1.left_trigger > 0.3) {
            intake.rotate_Left();
        }
        if (gamepad1.right_trigger > 0.3) {
            intake.rotate_Right();
        }


        if (take && taketimer.milliseconds() > 50) {
            intake.close();
        }
        if (take && taketimer.milliseconds() > 200) {
            intake.setMidPovisheTake();
            take = false;
        }


        if (gamepad2.x && !intake.atPeredacha()) {
            timerpd.reset();
            peredtake = true;
            intake.open_Chut();
            outtake.setPeredTake();
            horlift.close();
        }
        if(peredtake && timerpd.milliseconds() > 100){
            intake.rotate_Mid();
            intake.setPeredacha();
        }
        if(peredtake && timerpd.milliseconds() > 200){
            intake.close();
            peredtake = false;
        }

        //horizontal lift
        if (gamepad2.left_stick_y < -0.3) {
            horlift.open();
            if(intake.isOpened()) intake.setMidPovisheTake();
        }
        if (gamepad2.left_stick_y > 0.3) {
            horlift.close();
        }




        //outtake
        if (gamepad2.x && intake.atPeredacha()) {
            outtaketake = true;
            outtake.mayatUp1();
            outtake.grab();
            peredachatimer.reset();
        }
        if (outtaketake && peredachatimer.milliseconds() > 150) {
            intake.open();
            outtake.setHB();outtaketake = false;
        }
        if (gamepad1.a || gamepad2.dpad_down) {
            outtake.release();
            outtake.mayatDown();
            outtake.setZadTake();
        }
        if (gamepad1.y || gamepad2.dpad_up) {
            timerbt.reset();
            borttake = true;
            outtake.grab();
        }
        if (borttake && timerbt.milliseconds() > 180) {
            outtake.setAutoSpec();
            borttake = false;
        }


        if (gamepad2.y || gamepad1.left_bumper) {
            outtake.release();
            intake.open();
        }

        //if(gamepad2.touchpad) {
        //    intake.setAuto();
        //}






















        //drivetrain
        if(gamepad1.b) modedrive = true;
        modedrive = drivetrain.robot_centric_with_mode(gamepad1, modedrive);
        if(gamepad1.start) drivetrain.reset();
        //lift

        if (gamepad2.right_stick_y != 0) {
            mode = "MANUAL";
        }
        if (gamepad2.left_bumper) {
            mode = "PID";
            lift.set_target_position(0);
        }
        if (gamepad2.right_bumper && !rb) {
            lifttimer.reset();
            holdlift = true;
            mode = "PID";
            lift.setHighBasket();
        }
        if (!gamepad2.right_bumper) {
            lifttimer.reset();
            holdlift = false;
        }
        if (lifttimer.milliseconds() > 800 && holdlift) {
            lift.setHighChamber();
            holdlift = false;
        }

        if (mode == "PID") {
            lift.update_pid();
        }
        else {
            lift.setpowerTeleop(gamepad2.right_stick_y);
        }

        rb = gamepad2.right_bumper;
        telemetryA.update();
    }


    @Override
    public void stop() {
    }


}
