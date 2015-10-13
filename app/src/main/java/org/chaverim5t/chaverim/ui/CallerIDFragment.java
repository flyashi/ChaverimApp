package org.chaverim5t.chaverim.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.CallManager;
import org.chaverim5t.chaverim.data.CallerID;
import org.chaverim5t.chaverim.data.CallerIDManager;
import org.chaverim5t.chaverim.data.UserManager;
import org.chaverim5t.chaverim.util.PhoneNumberFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A fragment that displays a {@link RecyclerView} of recent calls to the hotline, and allows the
 * user to call back the caller.
 */
public final class CallerIDFragment extends Fragment {

  private static final String TAG = CallerIDFragment.class.getSimpleName();
  private RecyclerView recyclerView;
  private TextView noHotlineCallsTextView;

  private CallerIDManager callerIDManager;
  private UserManager userManager;

  private CallerIDViewAdapter callerIDViewAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Request<JSONObject> request;


  public CallerIDFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    callerIDManager = CallerIDManager.getCallerIDManager(getContext());
    userManager = UserManager.getUserManager(getContext());

    // Inflate the layout for this fragment
    final View view = inflater.inflate(R.layout.fragment_caller_id, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.callerid_recycler_view);
    noHotlineCallsTextView = (TextView) view.findViewById(R.id.no_recent_hotline_calls_text);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.callerid_swipe_refresh);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        if (userManager.isFakeData()) {
          new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              swipeRefreshLayout.setRefreshing(false);
            }
          }, 1000);
        } else {
          if (request != null) {
            Log.d(TAG, "Request is not null, returning...");
            return;
          }
          final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              swipeRefreshLayout.setRefreshing(false);
              request = null;
              Snackbar.make(view, "Error: " + error.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
            }
          };
          Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              request = null;
              swipeRefreshLayout.setRefreshing(false);
              try {
                if (response.has("error") && !TextUtils.isEmpty(response.getString("error"))) {
                  Log.e(TAG, "Got error in response: " + response.getString("error"));
                  errorListener.onErrorResponse(new VolleyError(response.getString("error")));
                }
                updateView();
                // doesn't work: callsViewAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(new CallerIDViewAdapter());
              } catch (JSONException e) {
                Log.e(TAG, "Error getting JSON in response", e);
                errorListener.onErrorResponse(new VolleyError(e));
              }
            }
          };
          request = callerIDManager.updateCallerIDList(true, listener, errorListener);
        }
      }
    });

    // To prevent the swipe to refresh from triggering while scrolling up, we enable it only when
    // we're already scrolled all the way to the top.
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE
            && recyclerView.computeVerticalScrollOffset() == 0) {
          swipeRefreshLayout.setEnabled(true);
        } else {
          swipeRefreshLayout.setEnabled(false);
        }
      }
    });

    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    recyclerView.setHasFixedSize(true);
    callerIDViewAdapter = new CallerIDViewAdapter();
    recyclerView.setAdapter(callerIDViewAdapter);
    updateView();
    return view;
  }

  private void updateView() {
    if (callerIDManager.callerIDList().size() > 0) {
      noHotlineCallsTextView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      noHotlineCallsTextView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    }
  }

  class CallerIDViewAdapter extends RecyclerView.Adapter<CallerIDViewAdapter.CallerIDViewHolder> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public CallerIDViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.tile_callerid, parent, false);
      return new CallerIDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CallerIDViewHolder holder, int position) {
      CallerID callerID = callerIDManager.callerIDList().get(position);
      holder.title.setText(PhoneNumberFormatter.format(callerID.phoneNumber));
      holder.subtitle.setText(simpleDateFormat.format(new Date(callerID.timestamp)));
    }

    @Override
    public int getItemCount() {
      return callerIDManager.callerIDList().size();
    }

    final class CallerIDViewHolder extends RecyclerView.ViewHolder {
      public TextView title;
      public TextView subtitle;
      public CallerIDViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.phone_number_text);
        subtitle = (TextView) itemView.findViewById(R.id.phone_number_subtitle);
      }
    }
  }

}
