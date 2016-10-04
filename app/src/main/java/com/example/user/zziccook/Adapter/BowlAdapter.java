package com.example.user.zziccook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Model.Bowl;
import com.example.user.zziccook.R;

import java.util.List;

/**
 * Created by Valentine on 4/16/2015.
 */
public class BowlAdapter extends ArrayAdapter<Bowl>{
    private Context mContext;
    private List<Bowl> mBowls;
    private static final String TAG="BowlAdapter";
    private DatabaseHelper db;

    private OnFavoriteAddedListener mListener;

    public BowlAdapter(Context context, List<Bowl> bowls)
    {
        super(context, R.layout.row_bowl_list, bowls);
        context = mContext;
        mBowls = bowls;

    }



    @Override
    public int getCount() {
        return mBowls.size();
    }

    @Override
    public Bowl getItem(int position) {
        if (position < mBowls.size()) {
            return mBowls.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class BowlHolder {
        private TextView Name;
        private ImageView Thumbnail;
    }



    public void Add(Bowl bowl)
    {
        mBowls.add(bowl);
        this.notifyDataSetChanged();
    }

    public void Update()
    {
        mBowls.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        BowlHolder mBowlHolder;
        if (getContext() instanceof OnFavoriteAddedListener) {
            mListener = (OnFavoriteAddedListener) getContext();
        } else {
            throw new RuntimeException(getContext().toString()
                    + " must implement OnPhoneHeightChangeListener");
        }
        final Bowl bowl= getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_bowl_list, null);

            mBowlHolder = new BowlHolder();

            mBowlHolder.Name = (TextView) view.findViewById(R.id.textBowlName);
            mBowlHolder.Thumbnail = (ImageView) view.findViewById(R.id.bowl_image_thumbnail);

            view.setTag(mBowlHolder);
        }else {
            mBowlHolder = (BowlHolder)view.getTag();
        }



        //Set the Bowl Name
        if (mBowlHolder.Name != null) {
            mBowlHolder.Name.setText(bowl.getName());
        }

        //set the Bowl  Thumbnail
        if (mBowlHolder.Thumbnail != null){
            mBowlHolder.Thumbnail.setImageDrawable(bowl.getThumbnail(getContext()));
        }
        return view;
    }

    public interface OnFavoriteAddedListener {
        // TODO: Update argument type and name
        void onFavoriteAdded();

    }


}
