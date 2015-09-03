package org.chaverim5t.chaverim.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.Call;
import org.chaverim5t.chaverim.data.CallManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallsFragment extends Fragment {

  private static final String TAG = CallsFragment.class.getSimpleName();
  private RecyclerView recyclerView;
  private TextView noCallsTextView;

  private CallManager callManager = CallManager.getCallManager(getContext());

  private CallsViewAdapter callsViewAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;

  public CallsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_calls, container, false);
    noCallsTextView = (TextView) view.findViewById(R.id.no_calls_text_view);
    recyclerView = (RecyclerView) view.findViewById(R.id.calls_recycler_view);

    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.calls_swipe_refresh);
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
    callsViewAdapter = new CallsViewAdapter();
    recyclerView.setAdapter(callsViewAdapter);
    updateView();
    return view;
  }

  private void updateView() {
    if (callManager.getAllCalls().size() > 0) {
      noCallsTextView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      noCallsTextView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    }
  }

  class CallsViewAdapter extends RecyclerView.Adapter<CallsViewAdapter.CallTileViewHolder> {
    /**
     * Called when RecyclerView needs a new {@link CallTileViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new CallTileViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new CallTileViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(CallTileViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new CallTileViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(CallTileViewHolder, int)
     */
    @Override
    public CallTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.tile_call, parent, false);
      return new CallTileViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link CallTileViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link CallTileViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The CallTileViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CallTileViewHolder holder, int position) {
      Call call = callManager.getAllCalls().get(position);
      holder.title.setText(call.title);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
      return callManager.getAllCalls().size();
    }

    class CallTileViewHolder extends RecyclerView.ViewHolder {
      public TextView title;

      public CallTileViewHolder(View itemView) {
        super(itemView);
        this.title = (TextView) itemView.findViewById(R.id.title_text);
      }
    }
  }
}
