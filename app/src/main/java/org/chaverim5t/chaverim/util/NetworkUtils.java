package org.chaverim5t.chaverim.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities to organize network requests.
 */
public class NetworkUtils {
  private static final String TAG = NetworkUtils.class.getSimpleName();
  private static NetworkUtils networkUtils;
  private RequestQueue requestQueue;
  private Context context;

  private static final String BASE_URL = "http://10.0.2.2:8080/";  // host PC from emulator

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

  public Request makeApiRequest(String apiName, Object[][] params,
                                final Response.Listener<JSONObject> userListener,
                                final Response.ErrorListener userErrorListener) {
    HashMap<String, Object> paramsMap = new HashMap<>();
    for (Object[] keyValueArray : params) {
      paramsMap.put(keyValueArray[0].toString(), keyValueArray[1]);
    }
    return makeApiRequest(apiName, paramsMap, userListener, userErrorListener);
  }
  public Request makeApiRequest(String apiName, Map<String, Object> params,
                                final Response.Listener<JSONObject> userListener,
                                final Response.ErrorListener userErrorListener) {
    return makeRequest(BASE_URL + "api/" + apiName, params, userListener, userErrorListener);
  }

  public Request makeRequest(String url, Map<String, Object> params,
                             final Response.Listener<JSONObject> userListener,
                             final Response.ErrorListener userErrorListener) {
    Response.Listener<JSONObject> requestListener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Log.d(TAG, "Response: '" + response.toString() + "'");
        if (userListener != null) {
          userListener.onResponse(response);
        }
      }
    };
    Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Got error response", error);
        if (error.networkResponse != null) {
          Log.e(TAG, "HTTP return code: " + Integer.toString(error.networkResponse.statusCode));
          for (String headerName : error.networkResponse.headers.keySet()) {
            Log.e(TAG, "HTTP return header: '" + headerName + "' = '" + error.networkResponse.headers.get(headerName) + "'");
          }
        }
        if (userErrorListener != null) {
          userErrorListener.onErrorResponse(error);
        }
      }
    };
    Request request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
        requestListener, requestErrorListener);
    networkUtils.addRequest(request);
    return request;
    //return jsonObjectRequestWithParams;
  }
}
