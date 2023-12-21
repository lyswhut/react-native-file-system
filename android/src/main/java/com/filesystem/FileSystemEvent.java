package com.filesystem;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class FileSystemEvent {
//  final String SCREEN_STATE = "screen-state";
//  final String SCREEN_SIZE_CHANGED = "screen-size-changed";

  private final ReactApplicationContext reactContext;
  FileSystemEvent(ReactApplicationContext reactContext) { this.reactContext = reactContext; }

  public void sendEvent(String eventName, @Nullable WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }
}
