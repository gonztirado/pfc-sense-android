package com.celulabs.pfcsense.ble.util;

public abstract class CustomTimerCallback {

  protected abstract void onTimeout();

  protected abstract void onTick(int i);
}
