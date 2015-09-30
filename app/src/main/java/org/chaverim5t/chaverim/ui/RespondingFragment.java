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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.Call;
import org.chaverim5t.chaverim.data.CallManager;


/**
 * A fragment that contains a {@link RecyclerView} with {@link android.support.v7.widget.CardView}s
 * for the calls the user is responding to. Only shown to responders.
 */
public class RespondingFragment extends Fragment {

  private static final String TAG = RespondingFragment.class.getSimpleName();
  private RecyclerView recyclerView;
  private TextView notRespondingTextView;

  private CallManager callManager;

  private RespondingViewAdapter respondingViewAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  public RespondingFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    callManager = CallManager.getCallManager(getContext());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_responding, container, false);
    notRespondingTextView = (TextView) view.findViewById(R.id.not_responding_text_view);
    recyclerView = (RecyclerView) view.findViewById(R.id.responding_recycler_view);

    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.responding_swipe_refresh);
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
    respondingViewAdapter = new RespondingViewAdapter();
    recyclerView.setAdapter(respondingViewAdapter);
    updateView();
    return view;
  }

  private void updateView() {
    if (callManager.isResponding()) {
      notRespondingTextView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      notRespondingTextView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    }
  }

  class RespondingViewAdapter extends RecyclerView.Adapter<RespondingViewAdapter.RespondingTileViewHolder> {

    /**
     * Called when RecyclerView needs a new {@link RespondingTileViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new RespondingTileViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new RespondingTileViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RespondingTileViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new RespondingTileViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RespondingTileViewHolder, int)
     */
    @Override
    public RespondingTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.tile_responding, parent, false);
      return new RespondingTileViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RespondingTileViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link RespondingTileViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The RespondingTileViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RespondingTileViewHolder holder, int position) {
      Call call = callManager.myRespondingCalls().get(position);
      holder.title.setText(call.title);
      holder.callNumber.setText(Integer.toString(call.callNumber));
      holder.callerName.setText(call.callerName + " - " + call.callerNumber);
      holder.locationTextView.setText(call.location);
      holder.vehicleTextView.setText(call.vehicle);

    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
      return callManager.myRespondingCalls().size();
    }

    class RespondingTileViewHolder extends RecyclerView.ViewHolder {
      public TextView title;
      public TextView callNumber;
      public ImageView icon;
      public TextView callerName;
      public View callerNameView;
      public TextView locationTextView;
      public View locationView;
      public TextView vehicleTextView;
      public TextView requestBackup;
      public TextView callComplete;

      public RespondingTileViewHolder(View itemView) {
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.title_text);
        callNumber = (TextView) itemView.findViewById(R.id.call_number_text);

        callerNameView = itemView.findViewById(R.id.text_and_image_caller_info);
        callerName = (TextView) callerNameView.findViewById(R.id.text);

        locationView = itemView.findViewById(R.id.text_and_image_location);
        locationTextView = (TextView) locationView.findViewById(R.id.text);

        View vehicleView = itemView.findViewById(R.id.text_and_image_vehicle);
        vehicleTextView = (TextView) vehicleView.findViewById(R.id.text);
        View vehicleImageView = vehicleView.findViewById(R.id.image);
        if (vehicleImageView != null) {
          Log.e(TAG, "Couldn't find vehicleImageView!");
          vehicleImageView.setVisibility(View.GONE);
        }

        View requestBackupView = itemView.findViewById(R.id.image_and_text_request_backup);
        requestBackup = (TextView) requestBackupView.findViewById(R.id.text);
        requestBackup.setText("Request Backup");

        View completeView = itemView.findViewById(R.id.image_and_text_call_complete);
        TextView completeTextView = (TextView) completeView.findViewById(R.id.text);
        completeTextView.setText("Complete");
      }
    }
  }
}
