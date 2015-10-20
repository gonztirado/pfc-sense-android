package com.celulabs.pfcsense.ble.sensortag;

import com.celulabs.pfcsense.ble.util.Point3D;


/**
 * As a last-second hack i'm storing the barometer coefficients in a global.
 */
public enum MagnetometerCalibrationCoefficients {
  INSTANCE;
  Point3D val = new Point3D(0.0,0.0,0.0);
}
