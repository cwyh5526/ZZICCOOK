package com.example.user.zziccook.Fragment.Recipe;


import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.FileUtils;
import com.example.user.zziccook.Model.Recipe;
import com.example.user.zziccook.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class RecipeAddFragment extends Fragment {
//수정 및 ADD
    private static final String TAG="RecipeAddFragment";
    private boolean InEditMode;
    private Recipe mRecipe;
    private View mRootView;
    private DatabaseHelper db;


    //Image properties
    private String mCurrentImagePath = null;
    private Uri mCapturedImageURI = null;
  //  private ImageButton mProfileImageButton;//??? 사진가져오는앤가

    private EditText mTitleEditText,
            mIngredientEditText,
            mRecipeOrderEditText,
            mAmountEditText;

    private LinearLayout mRecipeOrderLayout;

    private EditText mAddRecipeOrderEditText;
    private int mRecipeOrderNum;
    private Button mSaveBtn, mImageSelectBtn, mAddRecipeOrderBtn, mAmountMinus,mAmountPlus;


    private ImageView mImageView;
//    private Button mFavoriteBtn;
    private int mStar=0;

    OnRecipeSavedListener mCallback;

//    public static RecipeAddFragment newInstance(int sectionNumber, long recipeId) {
//        RecipeAddFragment fragment = new RecipeAddFragment();
//        Bundle args = new Bundle();
//        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
//        if (recipeId > 0){
//            args.putLong(Constants.ARG_RECIPE_ID, recipeId );
//        }
//        fragment.setArguments(args);
//        return fragment;
//    }



    public RecipeAddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
        mRecipeOrderNum=1;
//        setHasOptionsMenu(true);
//        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

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
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        InitializeViews();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecipe = new Recipe();
    }

//    private void PopulateFields() {
////        TabLayoutActivity myActivity = (TabLayoutActivity) getActivity();
//        mTitleEditText.setText(mRecipe.getTitle());
//        mIngredientEditText.setText(mRecipe.getIngredient());
//        mRecipeOrderEditText.setText(mRecipe.getRecipeOrder());
//        mAmountEditText.setText(mRecipe.getAmount());
//
//
//
//        // Update profile's Image
//        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
//            mImageView.setImageDrawable(new BitmapDrawable(getResources(),
//                    FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
//        } else {
//            mImageView.setImageDrawable(mRecipe.getImage(getActivity()));
//        }
//
//    }

    private void InitializeViews() {
        mTitleEditText = (EditText) mRootView.findViewById(R.id.edit_text_Title);
        mIngredientEditText = (EditText) mRootView.findViewById(R.id.edit_text_Ingredient);
        mRecipeOrderEditText = (EditText) mRootView.findViewById(R.id.edit_text_recipe_order);
        mAmountEditText = (EditText) mRootView.findViewById(R.id.edit_text_Amount);

        mImageView = (ImageView) mRootView.findViewById(R.id.imageView1);

        mImageSelectBtn = (Button) mRootView.findViewById(R.id.btn_select_gallery);
        mImageSelectBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mRecipeOrderLayout = (LinearLayout) mRootView.findViewById(R.id.recipe_order_layout);

        mAddRecipeOrderBtn =(Button) mRootView.findViewById(R.id.btn_addOrder);
        mAddRecipeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipeOrder();
            }
        });

        mAmountMinus=(Button)mRootView.findViewById(R.id.btn_minus);
        mAmountMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeAmount(v);
            }
        });

         mAmountPlus=(Button)mRootView.findViewById(R.id.btn_plus);
         mAmountPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeAmount(v);
            }
        });
        mSaveBtn = (Button) mRootView.findViewById(R.id.btn_save);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveRecipe();
            }
        });

//        mProfileImageButton = (ImageButton)mRootView.findViewById(R.id.customer_image_button);
//        mProfileImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnRecipeSavedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
//        ((TabLayoutActivity) activity).onSectionAttached(
//                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
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
//                SaveRecipe();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void addRecipeOrder(){
        mRecipeOrderNum++;
        mAddRecipeOrderEditText = (EditText) new EditText(getContext());
        mAddRecipeOrderEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        //mAddRecipeOrderEditText.setId(id);
        mAddRecipeOrderEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        mAddRecipeOrderEditText.setId(mRecipeOrderNum);
        mRecipeOrderLayout.addView(mAddRecipeOrderEditText);
    }
    private void ChangeAmount(View view){
        int amount = Integer.parseInt(mAmountEditText.getText().toString());
        if(view==mAmountMinus){
            if(amount>0) {  amount--;  }
        }else if(view ==mAmountPlus){
            amount++;
        }
        mAmountEditText.setText(String.valueOf(amount));
    }

    private String mergeRecipeOrder(){
        String mergedOrder="";

        for(int i=0;i<mRecipeOrderNum;i++)
        {
            Log.d(TAG,"NUM:"+mRecipeOrderNum+" \n"+mRecipeOrderLayout.getChildAt(i));
            EditText temp = (EditText)mRecipeOrderLayout.getChildAt(i);
            mergedOrder+=temp.getText().toString()+Constants.SPLIT_DELIMINATOR;
        }
        return mergedOrder;
    }

    private void SaveRecipe(){
        mRecipe.setTitle(mTitleEditText.getText().toString());
        mRecipe.setIngredient(mIngredientEditText.getText().toString());
        mRecipe.setRecipeOrder(mergeRecipeOrder());


        mRecipe.setAmount(mAmountEditText.getText().toString());
        mRecipe.setStar("0");

        //Check to see if there is valid image path temporarily in memory
        //Then save that image path to the database and that becomes the profile
        //Image for this user.
        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty())
        {
            mRecipe.setImagePath(mCurrentImagePath);
        }

        long result = db.addRecipe(mRecipe);
        if (result == -1 ){
            Toast.makeText(getActivity(), "Unable to add Recipe: " + mRecipe.getTitle(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "Recipe: " + mRecipe.getTitle()+" Saved", Toast.LENGTH_LONG).show();

            mTitleEditText.setText("");
            mIngredientEditText.setText("");
            mRecipeOrderEditText.setText("");
            mAmountEditText.setText("0");
            mImageView.setImageDrawable(mRecipe.setDefaultImage(getActivity()));

        }
        mTitleEditText.setError(null);
        mCallback.onRecipeSaved();

        //getActivity().onBackPressed();
    }
    private void chooseImage(){

        mCallback.onAddImage();

        //We need the title to to save the image file
        if (mTitleEditText.getText() != null && !mTitleEditText.getText().toString().isEmpty()) {
            // Determine Uri of camera image to save.
            final File rootDir = new File(Constants.PICTURE_DIRECTORY);

             //noinspection ResultOfMethodCallIgnored
            rootDir.mkdirs();

            // Create the temporary file and get it's URI.

            //Get the  title
            String recipeTitle = mTitleEditText.getText().toString();

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
        }
        else {
            mTitleEditText.setError("Please enter Recipe Title");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                    // Update client's picture
                    if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {

                          mImageView.setImageDrawable(new BitmapDrawable(getResources(),
                           FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
                    }
                }
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
    public interface OnRecipeSavedListener {
        // TODO: Update argument type and name
        void onRecipeSaved();
        void onAddImage();
    }
}
