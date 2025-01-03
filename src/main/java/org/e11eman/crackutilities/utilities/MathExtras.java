package org.e11eman.crackutilities.utilities;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Random;

import static java.lang.Math.*;

@SuppressWarnings("unused")
public class MathExtras {
    public static double calculateDistanceBetweenPoints(
            double x1,
            double y1,
            double x2,
            double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
    public static Vector3f yawPitchToUnitVector(float yaw, float pitch) {
        float x = (float) (-cos(pitch) * sin(yaw));
        float y = (float) -sin(pitch);
        float z = (float) (cos(pitch) * cos(yaw));

        return new Vector3f(x, y, z);
    }

    public static Vector3f yawPitchSpeedToVelocity(float yaw, float pitch, float speed) {
        Vector3f velocity = yawPitchToUnitVector((float) toRadians(yaw), (float) toRadians(pitch));

        return scale(speed, velocity);
    }

    public static Vector3f scale(float scalar, Vector3f vec) {
        float x = vec.x * scalar;
        float y = vec.y * scalar;
        float z = vec.z * scalar;
        return vec.set(x, y, z);
    }

    public static Vector3d generateRandomPointInSphere(Vector3d center, double radius) {
        Random random = new Random();

        double r = random.nextDouble() * radius;
        double theta = random.nextDouble() * Math.PI;
        double phi = random.nextDouble() * 2 * Math.PI;

        double x_offset = r * Math.sin(theta) * Math.cos(phi);
        double y_offset = r * Math.sin(theta) * Math.sin(phi);
        double z_offset = r * Math.cos(theta);

        double x = center.x + x_offset;
        double y = center.y + y_offset;
        double z = center.z + z_offset;

        return new Vector3d(x, y, z);
    }

    public static Vector2d calculateLookAway(Vector3d direction) {
        double yaw = Math.atan2(direction.x, direction.z);
        double pitch = Math.atan2(-direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));

        yaw = Math.toDegrees(yaw);
        pitch = Math.toDegrees(pitch);

        return new Vector2d(yaw, pitch);
    }
}
