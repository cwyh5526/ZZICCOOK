package com.example.user.zziccook.Fragment.Bowl;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.FileUtils;
import com.example.user.zziccook.Model.Bowl;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;


public class BowlDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG ="Bowl Details Fragment";

    private Bowl mBowl;
    private View mRootView;
    private DatabaseHelper db;

    //ImageProperties
    private String mCurrentImagePath = null;
    private Uri mCapturedImageURI = null;

    private ImageView mPhotoImageView;

    private TextView   mNameTextView,
                        mTypeTextView,
                        mEdgeTextView,
                        mHeightTextView,
                        mWidthTextView,
                        mVolumeTextView;

    private Button     mCancelBtn;


    public BowlDetailsFragment() {
       // Required empty public constructor
    }

    public static BowlDetailsFragment newInstance( int sectionNumber, long bowlId) {
        Log.d(TAG,"new Instances :: " +bowlId);
        BowlDetailsFragment fragment = new BowlDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        if (bowlId > 0){
            args.putLong(Constants.ARG_BOWL_ID, bowlId );
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCREATE :: " );
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());

        // Ensure there is a saved instance state.
        if (savedInstanceState != null) {

            // Get the saved Image uri string.
            String ImageUriString = savedInstanceState.getString(Constants.KEY_IMAGE_URI);

            // Restore the Image uri from the Image uri string.
            if (ImageUriString != null) {
                mCapturedImageURI = Uri.parse(ImageUriString);
            }
            mCurrentImagePath = savedInstanceState.getString(Constants.KEY_IMAGE_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCapturedImageURI != null) {
            outState.putString(Constants.KEY_IMAGE_URI, mCapturedImageURI.toString());
        }
        outState.putString(Constants.KEY_IMAGE_PATH, mCurrentImagePath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCREATE VIEW :: " );
        mRootView = inflater.inflate(R.layout.fragment_bowl_details,null);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onVIEW CREATE :: " );
        InitializeViews();

    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume :: " );
        super.onResume();
        GetPassedInBowl();
        PopulateFields();

    }

    private void GetPassedInBowl(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.ARG_BOWL_ID)) {
            long bowlId = args.getLong(Constants.ARG_BOWL_ID, 0);
            if (bowlId > 0) {
                mBowl = db.getBowlById(bowlId);
                Log.d(TAG,"NAME"+ mBowl.getName());
                Log.d(TAG,"ID"+ mBowl.getId());

            }

        }
    }

    private void PopulateFields() {

        mNameTextView.setText(mBowl.getName());
        mTypeTextView.setText(mBowl.getType());

        String edgeInfo =  "LEFT TOP: "          + mBowl.getEdgeLeftY().toString()
                        +  "\nLEFT BOTTOM: "    + mBowl.getEdgeLeftX().toString()
                        +  "\nRIGHT TOP: "      + mBowl.getEdgeRightY().toString()
                        +  "\nRIGHT BOTTOM: "   + mBowl.getEdgeRightX().toString();
        mEdgeTextView.setText(edgeInfo);

        mHeightTextView.setText(String.valueOf(mBowl.getHeight()));
        mWidthTextView.setText(String.valueOf(mBowl.getWidth()));
        mVolumeTextView.setText(String.valueOf(mBowl.getVolume()));


        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
            mPhotoImageView.setImageDrawable(new BitmapDrawable(getResources(),
                    FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
        } else {
            mPhotoImageView.setImageDrawable(mBowl.getImage(getActivity()));
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnMeasure2StartedListener) {
//            mListener = (OnMeasure2StartedListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnPhoneHeightChangeListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }
    private void InitializeViews() {

        mPhotoImageView = (ImageView)mRootView.findViewById(R.id.bowl_imageView);

        mNameTextView = (TextView)mRootView.findViewById(R.id.tv_Bowl_Name);
        mTypeTextView = (TextView)mRootView.findViewById(R.id.tv_Bowl_Type);

        mEdgeTextView = (TextView)mRootView.findViewById(R.id.tv_Bowl_Edge);
        mHeightTextView = (TextView)mRootView.findViewById(R.id.tv_Bowl_Height);
        mWidthTextView = (TextView)mRootView.findViewById(R.id.tv_Bowl_Width);
        mVolumeTextView = (TextView)mRootView.findViewById(R.id.tv_Bowl_Volume);

        mCancelBtn = (Button)mRootView.findViewById(R.id.btn_Bowl_Cancel);
        mCancelBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
         if(v==mCancelBtn){
          TabLayoutActivity mActivity = (TabLayoutActivity) getActivity();
            mActivity.onBackPressed();
        }
    }

//    public interface OnMeasure2StartedListener {
//        // TODO: Update argument type and name
//        void onMeasure2StartedAtRecipeDetail(long itemId);
//        void onMeasure2StartedAtFavoriteDetail(long itemId);
//
//    }
}
