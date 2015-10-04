package org.chaverim5t.chaverim.ui;


import android.os.Build;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.Call;
import org.chaverim5t.chaverim.data.CallManager;
import org.chaverim5t.chaverim.data.UserManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


/**
 * A fragment that displays open and recent dispatched calls for assistance in a
 * {@link RecyclerView} with {@link android.support.v7.widget.CardView} tiles. Each tile displays
 * information about the call, with options which vary based on permissions. For example, responders
 * can respond to a call, and dispatchers can cancel or edit the call.
 */
public class CallsFragment extends Fragment {

  private static final String TAG = CallsFragment.class.getSimpleName();
  private RecyclerView recyclerView;
  private TextView noCallsTextView;

  private CallManager callManager;
  private UserManager userManager;

  private CallsViewAdapter callsViewAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;

  private Request<JSONObject> request;
  public CallsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    callManager = CallManager.getCallManager(getContext());
    userManager = UserManager.getUserManager(getContext());

    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_calls, container, false);
    noCallsTextView = (TextView) view.findViewById(R.id.no_calls_text_view);
    recyclerView = (RecyclerView) view.findViewById(R.id.calls_recycler_view);

    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.calls_swipe_refresh);
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
            return;
          }
          final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              swipeRefreshLayout.setRefreshing(false);
              request = null;
              Snackbar.make(getView(), "Error: " + error.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
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
                callsViewAdapter.notifyDataSetChanged();
              } catch (JSONException e) {
                Log.e(TAG, "Error getting JSON in response", e);
                errorListener.onErrorResponse(new VolleyError(e));
              }
            }
          };
          request = callManager.updateCalls(listener, errorListener);
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
      final Call call = callManager.getAllCalls().get(position);
      if (call.problem.equals("Flat")) {
        holder.callTypeImage.setImageResource(R.drawable.flat);
      }
      holder.title.setText(call.title);
      holder.callNumberText.setText(Integer.toString(call.callNumber));
      if (userManager.isDispatcher()) {
        holder.callerNameNumberView.setVisibility(View.VISIBLE);
        holder.callerNameNumberText.setText(call.callerName + " - " + call.phoneNumber);
      } else {
        holder.callerNameNumberView.setVisibility(View.GONE);
      }
      holder.locationText.setText(call.location);
      if (userManager.isResponder()) {
        holder.durationView.setVisibility(View.VISIBLE);
        holder.durationText.setText("(unknown duration");
        if (Build.VERSION.SDK_INT >= 23) {
          holder.durationText.setTextAppearance(android.R.style.TextAppearance_Small);
        } else {
          // TODO(yakov): Make the text small on other devices.
        }
      } else {
        holder.durationView.setVisibility(View.GONE);
      }
      holder.vehicleText.setText(call.vehicle);

      if (userManager.isResponder()) {
        holder.actionRespondView.setVisibility(View.VISIBLE);
        holder.actionRespondImage.setImageResource(R.drawable.ic_directions_run_black_24dp);
        if (callManager.myRespondingCalls().contains(call)) {
          /* user is already responding... */
          holder.actionRespondText.setText("You are responding");
          holder.actionRespondView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ((MainActivity) getActivity()).mViewPager.setCurrentItem(0 /* first page */, true /* smooth scroll */);
            }
          });
        } else {
          holder.actionRespondText.setText("Respond");
          holder.actionRespondView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              call.coverage.add(userManager.userID());
              callManager.updateRespondingList();
              ((MainActivity) getActivity()).mViewPager.setCurrentItem(0 /* first page */, true /* smooth scroll */);
            }
          });
        }
      } else {
        holder.actionRespondView.setVisibility(View.GONE);
      } /* isResponder*/

      if (userManager.isDispatcher()) {
        holder.actionCancelReopenView.setVisibility(View.VISIBLE);
        holder.actionEditView.setVisibility(View.VISIBLE);

        holder.actionEditImage.setImageResource(R.drawable.ic_create_black_24dp);
        holder.actionEditText.setText("Edit");
        holder.actionEditView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // TODO(yakov): Implement edit!
            Toast.makeText(getContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
          }
        });

        // TODO(yakov): Make this more robust
        if (call.status.equals("Open")) {
          // TODO(yakov): Get a good "cancel" icon
          holder.actionCancelReopenImage.setImageResource(R.drawable.ic_settings_black_24dp);
          holder.actionCancelReopenText.setText("Cancel");
          holder.actionCancelReopenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              call.status = "Canceled";
              recyclerView.getAdapter().notifyDataSetChanged();
              Toast.makeText(getContext(), "Canceled", Toast.LENGTH_LONG).show();
            }
          });
        } else {
          // TODO(yakov): Get a good "reopen" icon
          holder.actionCancelReopenImage.setImageResource(android.R.drawable.ic_menu_revert);
          holder.actionCancelReopenText.setText("Reopen");
          holder.actionCancelReopenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              call.status = "Open";
              recyclerView.getAdapter().notifyDataSetChanged();
              Toast.makeText(getContext(), "Reopened", Toast.LENGTH_LONG).show();
            }
          });
        }
      } else {
        holder.actionCancelReopenView.setVisibility(View.GONE);
        holder.actionEditView.setVisibility(View.GONE);
      }
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
      public ImageView callTypeImage;
      public TextView callNumberText;
      public View callerNameNumberView;
      public TextView callerNameNumberText;
      public View locationView;
      public TextView locationText;
      public View durationView;
      public TextView durationText;
      public TextView vehicleText;
      public View actionRespondView;
      public TextView actionRespondText;
      public ImageView actionRespondImage;
      public View actionCancelReopenView;
      public TextView actionCancelReopenText;
      public ImageView actionCancelReopenImage;
      public View actionEditView;
      public TextView actionEditText;
      public ImageView actionEditImage;

      public CallTileViewHolder(View itemView) {
        super(itemView);

        // Text views

        this.title = (TextView) itemView.findViewById(R.id.title_text);
        this.callTypeImage = (ImageView) itemView.findViewById(R.id.call_type_image);
        this.callNumberText = (TextView) itemView.findViewById(R.id.call_number_text);
        this.callerNameNumberView = itemView.findViewById(R.id.caller_name_and_number_text_and_image);
        this.callerNameNumberText = (TextView) callerNameNumberView.findViewById(R.id.text);
        ((ImageView) callerNameNumberView.findViewById(R.id.image)).setImageResource(R.drawable.ic_call_black_24dp);
        this.locationView = itemView.findViewById(R.id.call_location_text_and_image);
        this.locationText = (TextView) locationView.findViewById(R.id.text);
        ((ImageView) locationView.findViewById(R.id.image)).setImageResource(android.R.drawable.ic_menu_mapmode);
        this.durationView = itemView.findViewById(R.id.duration_layout);
        this.durationText = (TextView) durationView.findViewById(R.id.text);
        durationView.findViewById(R.id.image).setVisibility(View.GONE);

        View vehicleView = itemView.findViewById(R.id.vehicle_text_and_image);
        this.vehicleText = (TextView) vehicleView.findViewById(R.id.text);
        // TODO(yakov): Show a small picture of the type of vehicle? Pre-load the top 20? Google Web Search & pull the first one?
        vehicleView.findViewById(R.id.image).setVisibility(View.GONE);

        // Action buttons

        this.actionRespondView = itemView.findViewById(R.id.action_row_respond);
        this.actionRespondText = (TextView) actionRespondView.findViewById(R.id.text);
        this.actionRespondImage = (ImageView) actionRespondView.findViewById(R.id.image);

        this.actionCancelReopenView = itemView.findViewById(R.id.action_row_cancel_reopen);
        this.actionCancelReopenText = (TextView) actionCancelReopenView.findViewById(R.id.text);
        this.actionCancelReopenImage = (ImageView) actionCancelReopenView.findViewById(R.id.image);

        this.actionEditView = itemView.findViewById(R.id.action_row_edit);
        this.actionEditText = (TextView) actionEditView.findViewById(R.id.text);
        this.actionEditImage = (ImageView) actionEditView.findViewById(R.id.image);
      }
    }
  }
}
