package org.chaverim5t.chaverim.data;

import android.content.Context;

/**
 * Created by yakov on 9/17/15.
 */
public class SettingsManager {
  private static SettingsManager settingsManager;
  private final Context context;

  private SettingsManager(Context context) {
    this.context = context;
  }

  public static SettingsManager getSettingsManager(Context context) {
    if (settingsManager == null) {
      settingsManager = new SettingsManager(context);
    }
    return settingsManager;
  }

}
