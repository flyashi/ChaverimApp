package org.chaverim5t.chaverim.util;

import android.util.Log;

/**
 * Handles testing and uniform display of various phone numbers, e.g.
 * +1 222 333 4444 -> (222) 333-4444
 * 1234567890      -> (123) 456-7890
 */
public class PhoneNumberFormatter {
  private static final String TAG = PhoneNumberFormatter.class.getSimpleName();

  public static String format(String phoneNumber) {
    if (!isValid(phoneNumber)) {
      Log.d(TAG, "Asked to format an invalid phone number: '" + phoneNumber + "'");
      return "(invalid phone number)";
    }
    String onlyDigits = phoneNumber.replaceAll("\\D+", "");
    if (onlyDigits.length() == 11 && onlyDigits.charAt(0) == '1') {
      onlyDigits = onlyDigits.substring(1);
    }
    if (onlyDigits.length() == 10) {
      return "(" + onlyDigits.substring(0, 3) + ")" + onlyDigits.substring(3, 6) + "-" + onlyDigits.substring(6, 10);
    }
    Log.e(TAG, "This should never happen, but, formatting an invalid phone number: '" + phoneNumber + '"');
    return "(invalid phone number)";
  }

  public static boolean isValid(String phoneNumber) {
    if (phoneNumber == null) return false;
    String onlyDigits = phoneNumber.replaceAll("\\D+", "");
    if (onlyDigits.length() == 10) {
      return true;
    }
    if (onlyDigits.length() == 11 && onlyDigits.charAt(0) == '1') {
      return true;
    }
    return false;
  }
}
