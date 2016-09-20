package com.example.user.zziccook.Fragment.Recipe;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.FileUtils;
import com.example.user.zziccook.Model.Recipe;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMeasure2StartedListener} interface
 * to handle interaction events.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TABLE_NAME = "mytable";
    private static final String TAG ="Recipe Details Fragment";

    private Recipe mRecipe;
    private View mRootView;
    private DatabaseHelper db;

    //ImageProperties
    private String mCurrentImagePath = null;
    private Uri mCapturedImageURI = null;

    private ImageView mPhotoImageView;

    private TextView   mTitleTextView,
                        mIngredientTextView,
                        mRecipeOrderTextView,
                        mAmountTextView;

    private Button  mFavoriteBtn,
                     mEditBtn,
                     mStartMeasure2Btn,
                     mCancelBtn;
    private String mListMode;

    private LinearLayout mRecipeOrderLayout;
    String recipeOrderArray[];


    private OnMeasure2StartedListener mListener;
    public RecipeDetailsFragment() {
       // Required empty public constructor
    }



    public static RecipeDetailsFragment newInstance(String mode, int sectionNumber, long recipeId) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        args.putString(Constants.ARG_LIST_MODE,mode);
        if (recipeId > 0){
            args.putLong(Constants.ARG_RECIPE_ID, recipeId );
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());


        mListMode=getArguments().getString(Constants.ARG_LIST_MODE);

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
        mRootView = inflater.inflate(R.layout.fragment_recipe_details,null);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitializeViews();

    }

    @Override
    public void onResume() {
        super.onResume();
        GetPassedInRecipe();
        PopulateFields();

    }

    private void GetPassedInRecipe(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.ARG_RECIPE_ID)) {
            long recipeId = args.getLong(Constants.ARG_RECIPE_ID, 0);
            if (recipeId > 0) {
                mRecipe = db.getRecipeById(recipeId);
                Log.d("RecipeDetailFrag","title"+mRecipe.getTitle());
                Log.d("RecipeDetailFrag","getRecipeID"+mRecipe.getId());

            }

        }
    }
    private void SetRecipeOrder(){
//        String tempString;
//        tempString= mRecipe.getRecipeOrder();
//        recipeOrderArray= tempString.split(Constants.SPLIT_DELIMINATOR);
//
//        for( int i=0;i<recipeOrderArray.length;i++)
//        {
//            TextView addRecipeOrder = (TextView) new TextView(getContext());
//            addRecipeOrder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            addRecipeOrder.setText(recipeOrderArray[i]);
//            mRecipeOrderLayout.addView(addRecipeOrder);
//        }
        recipeOrderArray=mRecipe.getRecipeOrderArray();
        for( int i=0;i<recipeOrderArray.length;i++)
        {
            TextView addRecipeOrder = (TextView) new TextView(getContext());
            addRecipeOrder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addRecipeOrder.setText(recipeOrderArray[i]);
            mRecipeOrderLayout.addView(addRecipeOrder);
        }

    }
    private void PopulateFields() {
//        TabLayoutActivity myActivity = (TabLayoutActivity) getActivity();
        mTitleTextView.setText(mRecipe.getTitle());
        mIngredientTextView.setText(mRecipe.getIngredient());
        SetRecipeOrder();
//        mRecipeOrderTextView.setText(mRecipe.getRecipeOrder());
        mAmountTextView.setText(mRecipe.getAmount());
        if(mRecipe.getStar().equals(Constants.FAVORITE_RECIPE))
        {
            mFavoriteBtn.setText("★");
        }else{
            mFavoriteBtn.setText("☆");
        }


        // Update profile's Image
        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
            mPhotoImageView.setImageDrawable(new BitmapDrawable(getResources(),
                    FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
        } else {
            mPhotoImageView.setImageDrawable(mRecipe.getImage(getActivity()));
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMeasure2StartedListener) {
            mListener = (OnMeasure2StartedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void InitializeViews() {

        mPhotoImageView = (ImageView)mRootView.findViewById(R.id.imageView1);

        mTitleTextView = (TextView)mRootView.findViewById(R.id.tv_Title);
        mIngredientTextView = (TextView)mRootView.findViewById(R.id.tv_Ingredient);
        mRecipeOrderLayout = (LinearLayout)mRootView.findViewById(R.id.recipe_order_layout_At_Detail);
        //mRecipeOrderTextView = (TextView)mRootView.findViewById(R.id.tv_Order);
        mAmountTextView = (TextView)mRootView.findViewById(R.id.tv_Amount);

        mFavoriteBtn = (Button)mRootView.findViewById(R.id.btn_favorite);
        mFavoriteBtn.setOnClickListener(this);
        mStartMeasure2Btn = (Button)mRootView.findViewById(R.id.btn_start_measure2);
        mStartMeasure2Btn.setOnClickListener(this);
        mCancelBtn = (Button)mRootView.findViewById(R.id.btn_Cancel);
        mCancelBtn.setOnClickListener(this);
//        mEditBtn = (Button)mRootView.findViewById(R.id.);

    }

    @Override
    public void onClick(View v) {
        if(v==mFavoriteBtn){
            Log.d("RecipeDetailFrag","Favorite"+mFavoriteBtn.getText());

            if(mFavoriteBtn.getText().equals("☆")){
                mRecipe.setStar(Constants.FAVORITE_RECIPE);
                Log.d("RecipeDetailFrag","Favorite"+mFavoriteBtn.getText()+"??");
                mFavoriteBtn.setText("★");
                Toast.makeText(getActivity(),"Recipe is added to Favorite list", Toast.LENGTH_SHORT).show();
            }
            else if (mFavoriteBtn.getText().equals("★")){
                mRecipe.setStar(Constants.NON_FAVORITE_RECIPE);
                Log.d("RecipeDetailFrag","Favorite"+mFavoriteBtn.getText());
                mFavoriteBtn.setText("☆");
                Toast.makeText(getActivity(),"Recipe is removed from Favorite list", Toast.LENGTH_SHORT).show();

            }
            db.updateRecipe(mRecipe);
        }
        else if(v==mStartMeasure2Btn){
            if(mListMode.equals(Constants.FAVORITE_RECIPE_LIST)){
                mListener.onMeasure2StartedAtFavoriteDetail(mRecipe.getId());
            }else{
                mListener.onMeasure2StartedAtRecipeDetail(mRecipe.getId());
            }
            Log.d("RecipeDetailFrag","StartMeasure2 pressed at "+mRecipe.getTitle());
//            getActivity().onBackPressed();

        }
        else if(v==mCancelBtn){
          TabLayoutActivity mActivity = (TabLayoutActivity) getActivity();
            mActivity.onBackPressed();
//            if(mListMode.equals(Constants.FAVORITE_RECIPE_LIST)){
//                mActivity.ReplaceFragment(Enums.FragmentEnums.FavoriteListFragment,3, mRecipe.getId());
//                Log.d("Detail","Cancel111111111111111");
//            }else {
//                mActivity.ReplaceFragment(Enums.FragmentEnums.RecipeListFragment, 3, mRecipe.getId());
//                Log.d("Detail","Cancel22222222222222222");
//            }
        }
    }

    public interface OnMeasure2StartedListener {
        // TODO: Update argument type and name
        void onMeasure2StartedAtRecipeDetail(long itemId);
        void onMeasure2StartedAtFavoriteDetail(long itemId);

    }
}
