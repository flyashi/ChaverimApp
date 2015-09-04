package org.chaverim5t.chaverim.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.CallManager;
import org.chaverim5t.chaverim.data.CallerID;


/**
 * A fragment that displays a {@link RecyclerView} of recent calls to the hotline, and allows the
 * user to call back the caller.
 */
public class CallerIDFragment extends Fragment {

  private static final String TAG = CallerIDFragment.class.getSimpleName();
  private RecyclerView recyclerView;
  private TextView noHotlineCallsTextView;

  private CallManager callManager = CallManager.getCallManager(getContext());

  private CallerIDViewAdapter callerIDViewAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;

  public CallerIDFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_caller_id, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.callerid_recycler_view);
    noHotlineCallsTextView = (TextView) view.findViewById(R.id.no_recent_hotline_calls_text);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.callerid_swipe_refresh);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            swipeRefreshLayout.setRefreshing(false);
          }
        }, 1000);
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
    if (callManager.getAllCalls().size() > 0) {
      noHotlineCallsTextView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      noHotlineCallsTextView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    }
  }

  class CallerIDViewAdapter extends RecyclerView.Adapter<CallerIDViewAdapter.CallerIDViewHolder> {
    @Override
    public CallerIDViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.tile_callerid, parent, false);
      return new CallerIDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CallerIDViewHolder holder, int position) {
      CallerID callerID = callManager.getCallerIDs().get(position);
      holder.title.setText(callerID.phoneNumber);
    }

    @Override
    public int getItemCount() {
      return callManager.getCallerIDs().size();
    }

    class CallerIDViewHolder extends RecyclerView.ViewHolder {
      public TextView title;
      public CallerIDViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.phone_number_text);
      }
    }
  }

}
