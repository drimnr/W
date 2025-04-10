package org.firstinspires.ftc.teamcode.hardware.Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Horizontal_Lift {
    private final HardwareMap hardwareMap;
    private Telemetry telemetry;

    private Servo l, r;
    private boolean horliftInitialized = false;

    public static double open = 0.42;
    public static double close = 0.12;

    public Horizontal_Lift(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        initialize();
    }

    private void initialize() {
        try {
            l = hardwareMap.get(Servo.class, "hor_l");
            r = hardwareMap.get(Servo.class, "hor_r");
            horliftInitialized = true;
            telemetry.addData("Horizontal Lift", "Initialized");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Horizontal Lift Error", e.getMessage());
        }
    }

    // Оригинальные названия функций
    public void open() {
        if (isInitialized()) {
            l.setPosition(open);
            r.setPosition(1 - open);
        }
    }

    public void mid() {
        if (isInitialized()) {
            l.setPosition((open + close) / 2);
            r.setPosition(1 - ((open + close) / 2));
        }
    }

    public void close() {
        if (isInitialized()) {
            l.setPosition(close);
            r.setPosition(1 - close);
        }
    }

    public void setPos(double pos) {
        if (isInitialized()) {
            l.setPosition(pos);
            r.setPosition(1 - pos);
        }
    }

    public double getPos() {
        return l.getPosition();
    }

    private boolean isInitialized() {
        if (!horliftInitialized) {
            telemetry.addData("Horizontal Lift Error", "Not initialized");
        }
        return horliftInitialized;
    }
}
