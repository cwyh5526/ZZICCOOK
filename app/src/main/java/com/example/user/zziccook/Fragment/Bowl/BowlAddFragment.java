package com.example.user.zziccook.Fragment.Bowl;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.FileUtils;
import com.example.user.zziccook.Model.Bowl;
import com.example.user.zziccook.R;
import com.example.user.zziccook.VolumeMeasureActivity;

import org.opencv.core.Point;


public class BowlAddFragment extends Fragment {
//수정 및 ADD
    private static final String TAG="Add Bowl Fragment";
    private Bowl mBowl;
    private View mRootView;
    private DatabaseHelper db;


    //Image properties
    private String mCurrentImagePath = null;
    private Uri mCapturedImageURI = null;
  //  private ImageButton mProfileImageButton;//??? 사진가져오는앤가

    private EditText mNameEditText,
            mHeightEditText,
            mWidthEditText,
            mVolumeEditText;

    private Spinner mBowlTypeSpinner;

    private TextView mEdgeTextView;

    private Button  mAddNewBowlBtn, mSaveBtn, mCancelBtn;


    private ImageView mImageView;
//    private Button mFavoriteBtn;
    private int mStar=0;
    private double mPhoneHeight, mBowlHeight, mBowlWidth,mBowlVolume;
    String mLeftTop,mLeftDown, mRightTop,  mRightDown;
    OnBowlSavedListener mCallback;

    private int mRowsLen;
    private int mColsLen;

    public BowlAddFragment() {
        // Required empty public constructor
    }
    public static BowlAddFragment newInstance(int sectionNumber) {
        Log.d(TAG,"newInstance :: ");
        BowlAddFragment fragment = new BowlAddFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"ON CREATE :: ");
        super.onCreate(savedInstanceState);
        mPhoneHeight=getActivity().getSharedPreferences("setting",0).getFloat(Constants.ARG_PHONE_HEIGHT,0);
        if(mPhoneHeight==0){
            Toast.makeText(getContext(),"Set Phone Height in Setting Tab First",Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "onCreate: PHONE_Height"+mPhoneHeight);


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
        Log.d(TAG,"ON SAVE INSTANCE STATE :: ");
        super.onSaveInstanceState(outState);
        if (mCapturedImageURI != null) {
            outState.putString(Constants.KEY_IMAGE_URI, mCapturedImageURI.toString());
        }
        outState.putString(Constants.KEY_IMAGE_PATH, mCurrentImagePath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"ON CREATE VIEW :: ");
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_add_bowl, container, false);
        InitializeViews();
        return mRootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"ON RESUME :: ");
        super.onResume();
        mBowl = new Bowl();
        Log.d(TAG,"onResume:: mBowl "+mBowl.toString());

    }


    private void InitializeViews() {
        Log.d(TAG,"INITIALIZE VIEWS :: ");
        mNameEditText = (EditText) mRootView.findViewById(R.id.edit_text_Name);

        mBowlTypeSpinner = (Spinner)mRootView.findViewById(R.id.spinner_bowl_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.bowl_type,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBowlTypeSpinner.setAdapter(adapter);

        mEdgeTextView =(TextView)mRootView.findViewById(R.id.tv_Bowl_Edge_Measured);
        mImageView = (ImageView) mRootView.findViewById(R.id.imageView_Canny_Bowl);
        mAddNewBowlBtn = (Button) mRootView.findViewById(R.id.btn_Measure_Bowl);
        mAddNewBowlBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mImageView.setVisibility(View.VISIBLE);

                startMeasureNewBowl();
            }
        });

        mHeightEditText = (EditText) mRootView.findViewById(R.id.tv_Bowl_Height_Measured);
        mWidthEditText = (EditText) mRootView.findViewById(R.id.tv_Bowl_Width_Measured);
        mVolumeEditText = (EditText) mRootView.findViewById(R.id.tv_Bowl_Volume_Measured);

        mSaveBtn = (Button) mRootView.findViewById(R.id.btn_save);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveBowl();
            }
        });

        mCancelBtn = (Button)mRootView.findViewById(R.id.btn_AddBowl_Cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"onAttach :: ");

        try {
            mCallback = (OnBowlSavedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private void SaveBowl(){
        Log.d(TAG,"SAVE BOWL :: ");

        mBowl.setName(mNameEditText.getText().toString());
        mBowl.setType(mBowlTypeSpinner.getSelectedItem().toString());

        /*받아온 Edge 저장해줘야하는데... How?*/
        Log.d(TAG,"SaveBowl::mBowl "+mBowl.toString());

        mBowl.setColsLength(mColsLen);
        mBowl.setRowsLength(mRowsLen);

        Log.d(TAG,"SaveBowl::mBowl "+String.valueOf(mBowl.getColsLength()));

        Log.d(TAG,"SaveBowl::mBowl "+String.valueOf(mBowl.getRowsLength()));

        mBowl.setEdgeLeftTop(mLeftTop);
        mBowl.setEdgeLeftDown(mLeftDown);
        mBowl.setEdgeRightTop(mRightTop);
        mBowl.setEdgeRightDown(mRightDown);

        if(!mHeightEditText.getText().toString().isEmpty()){
            mBowl.setHeight(mBowlHeight);
        }
        if(!mWidthEditText.getText().toString().isEmpty()){
            mBowl.setWidth(mBowlWidth);
        }if(!mVolumeEditText.getText().toString().isEmpty()){
            mBowl.setVolume(mBowlVolume);
        }

        //Check to see if there is valid image path temporarily in memory
        //Then save that image path to the database and that becomes the profile
        //Image for this user.
        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty())
        {
            mBowl.setCannyImagePath(mCurrentImagePath);
        }

        long result = db.addBowl(mBowl);
        if (result == -1 ){
            Toast.makeText(getActivity(), "Unable to add Bowl: " + mBowl.getName(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "Bowl: " + mBowl.getName()+" Saved", Toast.LENGTH_LONG).show();
         }
        mNameEditText.setError(null);
        mCallback.onBowlSaved();

        getActivity().onBackPressed();//Fragment 창 꺼지고 List로 돌아가기 위함.
    }

    private void startMeasureNewBowl(){
        mCallback.onAddImage();
        Intent intent =new Intent(getContext(), VolumeMeasureActivity.class);
        getActivity().startActivityForResult(intent,1000);

    }

    public void SetMeasuredValues(Uri uri,double bowlHeight, double sobelAngle, String leftTop,String leftDown,String rightTop, String rightDown, int rowsLen, int colsLen){


        if (uri != null) {
            mCurrentImagePath = FileUtils.getPath(getActivity(), uri);
            Log.d(TAG,"SetMeasuredValues:: uri::"+uri);

            Log.d(TAG,"SetMeasuredValues:: mCurrentImagePath::"+mCurrentImagePath);
            // Update Bowl's picture
            if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
                mImageView.setImageDrawable(new BitmapDrawable(getResources(),
                        FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
//                mImageView.setVisibility(View.VISIBLE);
            }
        }
        mRowsLen=rowsLen;
        mColsLen=colsLen;

        mBowlHeight=bowlHeight;
        mBowl.setHeight(mBowlHeight);
        mHeightEditText.setText(String.valueOf(mBowlHeight));
        mLeftTop=leftTop;
        mLeftDown=leftDown;
        mRightTop=rightTop;
        mRightDown=rightDown;

        mBowl.setEdgeLeftTop(leftTop);
        mBowl.setEdgeLeftDown(leftDown);
        mBowl.setEdgeRightTop(rightTop);
        mBowl.setEdgeRightDown(rightDown);
        mEdgeTextView.setText(mBowl.getEdgeLeftTop()+" "+mBowl.getEdgeLeftDown()+" "+ mBowl.getEdgeRightTop()+" "+ mBowl.getEdgeRightDown());
        Log.d(TAG,"EdgSetMeasuredValuese::mBowl "+mBowl.toString());

        Log.d(TAG,"SetMeasuredValues:: Edge"+mBowl.getEdgeLeftTop());
        Log.d(TAG,"ESetMeasuredValuesdge:: Edge"+mBowl.getEdgeLeftDown());
        Log.d(TAG,"ESetMeasuredValuesdge::Edge "+mBowl.getEdgeRightTop());
        Log.d(TAG,"ESetMeasuredValuesdge:: Edge"+mBowl.getEdgeRightDown());
        mBowlWidth=calculateWidth(sobelAngle);

        mBowl.setWidth(mBowlWidth);
        mWidthEditText.setText(String.valueOf(Math.round(mBowlWidth*10.0)/10.0));

        mBowlVolume=calculateVolume();
        mVolumeEditText.setText(String.valueOf(Math.round(mBowlVolume)*10.0/10.0));

    }
    private double calculateWidth(double sobelAngle){
        Log.d(TAG,"Calculate Real Width"+String.valueOf(sobelAngle));
        //화면상의 그릇 너비 및 높이
        double widthOnScreen,heightOnScreen,heightNormalized,widthNormalized;

        heightOnScreen= mBowl.getEdgeLeftDown().y- mBowl.getEdgeLeftTop().y;
//        widthOnScreen= ((edge[2].x-edge[0].x)+(edge[3].x-edge[1].x))/2; //위아래 평균
        widthOnScreen =(mBowl.getEdgeRightDown().x- mBowl.getEdgeLeftDown().x);//아래 너비 값

        heightNormalized = heightOnScreen/Math.sin(Math.toRadians(sobelAngle));

        widthNormalized= widthOnScreen -(mBowl.getEdgeLeftDown().x-mBowl.getEdgeLeftTop().x);

        return Math.round((mBowlHeight*widthNormalized)/heightNormalized*10.0)/10.0;
    }

    private double calculateVolume(){
        double volume=0.0;

        if(mBowlTypeSpinner.getSelectedItem().toString().equals("cylinder")){
            volume= Math.PI*Math.pow((mBowlWidth/2),2)*mBowlHeight;
        }else if(mBowlTypeSpinner.getSelectedItem().toString().equals("cup")){

        }else{
            Toast.makeText(getContext(),"Select Type of Bowl",Toast.LENGTH_SHORT);
        }
        return Math.round(volume*10.0)/10.0;
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"ON DETACH");

        super.onDetach();

    }


    public interface OnBowlSavedListener {
        // TODO: Update argument type and name
        void onBowlSaved();
        void onAddImage();
    }
}
