package com.example.user.zziccook.Fragment.Bowl;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


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

    private TextView mEdge;

    private Button  mAddNewBowlBtn, mSaveBtn, mCancelBtn;


    private ImageView mImageView;
//    private Button mFavoriteBtn;
    private int mStar=0;

    OnBowlSavedListener mCallback;


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
    }


    private void InitializeViews() {
        Log.d(TAG,"INITIALIZE VIEWS :: ");
        mNameEditText = (EditText) mRootView.findViewById(R.id.edit_text_Name);

        mBowlTypeSpinner = (Spinner)mRootView.findViewById(R.id.spinner_bowl_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.bowl_type,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBowlTypeSpinner.setAdapter(adapter);

        mEdge=(TextView)mRootView.findViewById(R.id.tv_Bowl_Edge_Measured);
        mImageView = (ImageView) mRootView.findViewById(R.id.imageView_Canny_Bowl);
        mAddNewBowlBtn = (Button) mRootView.findViewById(R.id.btn_Measure_Bowl);
        mAddNewBowlBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mImageView.setVisibility(View.VISIBLE);
                chooseImage();
                //위 대신 openCV열어서 부피측정해야함.
                //startMeasureNewBowl();
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
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.customer_details_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                getActivity().onBackPressed();
//                break;
//            case R.id.action_save_customer:
//                SaveBowl();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }



    private void SaveBowl(){
        Log.d(TAG,"SAVE BOWL :: ");

        mBowl.setName(mNameEditText.getText().toString());
        mBowl.setType(mBowlTypeSpinner.getSelectedItem().toString());

        /*받아온 Edge 저장해줘야하는데... How?*/

        mBowl.setEdgeLeftX("{1,1}");
        mBowl.setEdgeLeftY("{2,2}");
        mBowl.setEdgeRightX("{3,3}");
        mBowl.setEdgeRightY("{4,4}");

        if(!mHeightEditText.getText().toString().isEmpty()){
            mBowl.setHeight(Double.parseDouble(mHeightEditText.getText().toString()));
        }
        if(!mWidthEditText.getText().toString().isEmpty()){
            mBowl.setWidth(Double.parseDouble(mWidthEditText.getText().toString()));
        }if(!mVolumeEditText.getText().toString().isEmpty()){
            mBowl.setVolume(Double.parseDouble(mVolumeEditText.getText().toString()));
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

    }


    private void chooseImage(){
        Log.d(TAG,"CHOOSE IMAGE  :: ");

        mCallback.onAddImage();

        //We need the title to to save the image file
        if (mNameEditText.getText() != null && !mNameEditText.getText().toString().isEmpty()) {
            Log.d(TAG,"CHOOSE IMAGE :: 1111");

            // Determine Uri of camera image to save.
            final File rootDir = new File(Constants.PICTURE_DIRECTORY);

             //noinspection ResultOfMethodCallIgnored
            rootDir.mkdirs();

            // Create the temporary file and get it's URI.

            //Get the  title
            String recipeTitle = mNameEditText.getText().toString();

            //Remove all white space in the title
            recipeTitle.replaceAll("\\s+", "");

            //Use the title to create the file name of the image that will be captured
            File file = new File(rootDir, FileUtils.generateImageName(recipeTitle));
            mCapturedImageURI = Uri.fromFile(file);

            // Initialize a list to hold any camera application intents.
            final List<Intent> cameraIntents = new ArrayList<Intent>();

            // Get the default camera capture intent.
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Get the package manager.
            final PackageManager packageManager = getActivity().getPackageManager();

            // Ensure the package manager exists.
            if (packageManager != null) {

                // Get all available image capture app activities.
                final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

                // Create camera intents for all image capture app activities.
                for(ResolveInfo res : listCam) {

                    // Ensure the activity info exists.
                    if (res.activityInfo != null) {

                        // Get the activity's package name.
                        final String packageName = res.activityInfo.packageName;

                        // Create a new camera intent based on android's default capture intent.
                        final Intent intent = new Intent(captureIntent);

                        // Set the intent data for the current image capture app.
                        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                        intent.setPackage(packageName);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                        // Add the intent to available camera intents.
                        cameraIntents.add(intent);
                    }
                }
            }
            Log.d(TAG,"CHOOSE IMAGE :: 222");

            // Create an intent to get pictures from the filesystem.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.putExtra("crop", "true");
            galleryIntent.putExtra("aspectX", 0);
            galleryIntent.putExtra("aspectY", 0);
            galleryIntent.putExtra("outputX", 200);
            galleryIntent.putExtra("outputY", 150);

            galleryIntent.putExtra("return-data", true);
            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            // Start activity to choose or take a picture.
            startActivityForResult(chooserIntent, Constants.ACTION_REQUEST_IMAGE);
            Log.d(TAG,"CHOOSE IMAGE :: DONE");

        }
        else {
            mNameEditText.setError("Please enter Bowl Name");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"ON DETACH");

        super.onDetach();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"ON ACTIVITY RESULT :: START");


        if (resultCode == Activity.RESULT_OK){
               // Get the resultant image's URI.
                final Uri selectedImageUri = (data == null) ? mCapturedImageURI : data.getData();

                // Ensure the image exists.
                if (selectedImageUri != null) {

                    // Add image to gallery if this is an image captured with the camera
                    //Otherwise no need to re-add to the gallery if the image already exists
                    if (requestCode == Constants.ACTION_REQUEST_IMAGE) {
                        final Intent mediaScanIntent =
                                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                        mediaScanIntent.setData(selectedImageUri);
                        getActivity().sendBroadcast(mediaScanIntent);
                    }

                   mCurrentImagePath = FileUtils.getPath(getActivity(), selectedImageUri);

                    // Update BOWL's picture
                    if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
                          mImageView.setImageDrawable(new BitmapDrawable(getResources(),
                           FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
                    }
                }
            Log.d(TAG,"ON ACTIVITY RESULT :: DONE");


        }
//        if (requestCode == Constants.SELECT_IMAGE) {
//            Bundle extras = data.getExtras();
//            if (extras != null) {
//                Bitmap photo = extras.getParcelable("data");
//                mImageView.setImageBitmap(photo);
//            }
//        }

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBowlSavedListener {
        // TODO: Update argument type and name
        void onBowlSaved();
        void onAddImage();
    }
}
