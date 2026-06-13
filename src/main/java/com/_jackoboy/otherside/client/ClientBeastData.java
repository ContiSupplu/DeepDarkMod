package com._jackoboy.otherside.client;

/**
 * Client-side cache for beast state data received from server.
 * Includes breach surface breakout positions for distance-based fog.
 * Replaces the old ClientInfectionData (W1 Worldbeast Rework).
 */
public class ClientBeastData {
    // Beast mass (0-100) for fog/gloom calculations
    public static float mass = 0.0f;

    // Breach surface breakout positions (synced from server)
    public static int breachCount = 0;
    public static double[] breachX = new double[0];
    public static double[] breachZ = new double[0];

    public static void setBreachPositions(double[] x, double[] z) {
        breachX = x;
        breachZ = z;
        breachCount = x.length;
    }
}
