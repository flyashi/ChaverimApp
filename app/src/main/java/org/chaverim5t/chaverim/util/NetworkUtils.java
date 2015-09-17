package org.chaverim5t.chaverim.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by yakov on 9/16/15.
 */
public class NetworkUtils {
  private static NetworkUtils networkUtils;
  private RequestQueue requestQueue;
  private Context context;

  private NetworkUtils(Context context) {
    this.context = context.getApplicationContext();
    this.requestQueue = Volley.newRequestQueue(context);
  }

  public static NetworkUtils getNetworkUtils(Context context) {
    if (networkUtils == null) {
      networkUtils = new NetworkUtils(context);
    }
    return networkUtils;
  }

  public void addRequest(Request request) {
    requestQueue.add(request);
  }
}
