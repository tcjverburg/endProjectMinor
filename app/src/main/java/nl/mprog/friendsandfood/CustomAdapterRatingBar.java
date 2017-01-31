package nl.mprog.friendsandfood;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gebruiker on 30-1-2017.
 */

public class CustomAdapterRatingBar extends BaseAdapter {
    private Context mContext;
    private List<Review> mReviewList;


    public CustomAdapterRatingBar(Context mContext, List<Review> mReviewList) {
        this.mContext = mContext;
        this.mReviewList = mReviewList;
    }

    @Override
    public int getCount() {
        return mReviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_review_list, null);
        TextView writerTextView = (TextView)v.findViewById(R.id.name_writer_list);
        RatingBar bar = (RatingBar)v.findViewById(R.id.rating_bar_list);
        writerTextView.setText(mReviewList.get(position).getWriter());
        bar.setRating(mReviewList.get(position).getRating());
        v.setTag(mReviewList.get(position).getReviewID());
        return v;
    }
}
