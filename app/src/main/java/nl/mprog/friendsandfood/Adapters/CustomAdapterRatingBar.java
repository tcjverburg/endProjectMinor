package nl.mprog.friendsandfood.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import nl.mprog.friendsandfood.R;
import nl.mprog.friendsandfood.Classes.Review;

import static android.view.View.inflate;

/**
 * Created by Tom Verburg on 30-1-2017.
 * This is a custom simpleAdapter for the SelectedRestaurantActivity so both the ListView and
 * the RatingBar are shown in a single ListView item.
 */

public class CustomAdapterRatingBar extends BaseAdapter {
    private Context mContext;
    private List<Review> mReviewList;

    /** Constructor of the CustomAdapterRatingBar class. */
    public CustomAdapterRatingBar(Context mContext, List<Review> mReviewList) {
        this.mContext = mContext;
        this.mReviewList = mReviewList;
    }

    /** Returns the size of the list of reviews. */
    @Override
    public int getCount() {
        return mReviewList.size();
    }

    /**Gets specific item when it is clicked in the listView. */
    @Override
    public Object getItem(int position) {
        return mReviewList.get(position);
    }

    /** Gets the ID of the selected Item. */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** Sets the various Views, sets their content and combines them into one single View. */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = inflate(mContext, R.layout.item_review_list, null);
        TextView writerTextView = (TextView)v.findViewById(R.id.name_writer_list);
        RatingBar bar = (RatingBar)v.findViewById(R.id.rating_bar_list);

        writerTextView.setText(mReviewList.get(position).getWriter());
        bar.setRating(mReviewList.get(position).getRating());
        v.setTag(mReviewList.get(position).getReviewID());

        return v;
    }
}
