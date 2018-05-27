package ca.javajeff.projettw;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetView;

/**
 * This class provides the adapter for a tweet recycle view
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private TweetView[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TweetView mTweetView;
        public ViewHolder(TweetView v) {
            super(v);
            mTweetView = v;
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of dataset)
     * @param myDataset
     */
    public MyAdapter(TweetView[] myDataset) {
        mDataset = myDataset;
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return view holder
     */
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        TweetView v = (TweetView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_tweet_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }



    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTweetView = mDataset[position];

    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     * @return dataset lenght
     */
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
