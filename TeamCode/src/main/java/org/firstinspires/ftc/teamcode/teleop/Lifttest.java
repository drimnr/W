package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Commands.Lift;

@TeleOp(name="lift", group="Iterative")
public class Lifttest extends OpMode
{
    Lift lift;
    String mode = "MANUAL";
    @Override
    public void init() {
        lift = new Lift(hardwareMap, telemetry);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        //lift
        if (mode == "PID") {
            if (gamepad2.right_stick_y != 0) {
                mode = "MANUAL";
            }
            if (Math.abs(gamepad2.left_trigger) > 0.3) {
                mode = "PID";
                lift.set_target_position(0);
            }
            if (Math.abs(gamepad2.right_trigger) > 0.3) {
                mode = "PID";
                lift.setHighBasket();
            }
            if (gamepad2.dpad_right) {
                mode = "PID";
                lift.setHighChamber();
            }
            lift.update_pid();
        }
        else {
            if (Math.abs(gamepad2.left_trigger) > 0.3) {
                mode = "PID";
                lift.set_target_position(0);
            }
            if (Math.abs(gamepad2.right_trigger) > 0.3) {
                mode = "PID";
                lift.setHighBasket();
            }
            if (gamepad2.dpad_right) {
                mode = "PID";
                lift.setHighChamber();
            }
            lift.setpowerTeleop(gamepad2.right_stick_y);
        }
    }


    @Override
    public void stop() {
    }


}
