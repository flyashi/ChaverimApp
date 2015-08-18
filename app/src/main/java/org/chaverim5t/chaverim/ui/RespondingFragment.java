package org.chaverim5t.chaverim.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.Call;
import org.chaverim5t.chaverim.data.CallManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class RespondingFragment extends Fragment {

  private RecyclerView recyclerView;
  private TextView notRespondingTextView;

  private CallManager callManager = CallManager.getCallManager();

  private RespondingViewAdapter respondingViewAdapter;

  public RespondingFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_responding, container, false);
    notRespondingTextView = (TextView) view.findViewById(R.id.not_responding_text_view);
    recyclerView = (RecyclerView) view.findViewById(R.id.responding_recycler_view);

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
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public RespondingTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_responding_tile, parent, false);
      RespondingTileViewHolder viewHolder = new RespondingTileViewHolder(view);
      return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RespondingTileViewHolder holder, int position) {
      Call call = callManager.myRespondingCalls().get(position);
      holder.title.setText(call.title);
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

      public RespondingTileViewHolder(View itemView) {
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.title_text);
      }
    }
  }
}
