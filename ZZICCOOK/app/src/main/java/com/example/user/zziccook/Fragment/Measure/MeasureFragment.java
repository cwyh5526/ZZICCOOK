package com.example.user.zziccook.Fragment.Measure;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.v13.app.FragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Fragment.Bowl.BowlListFragment;
import com.example.user.zziccook.Fragment.Camera.CameraPreviewFragment;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.Enums;
import com.example.user.zziccook.Helpers.FileUtils;
import com.example.user.zziccook.Model.Bowl;
import com.example.user.zziccook.Model.Recipe;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureFragment.OnRecipeMeasureListener} interface
 * to handle interaction events.
 * Use the {@link MeasureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureFragment extends android.support.v4.app.Fragment
        implements FragmentCompat.OnRequestPermissionsResultCallback,
        BowlListFragment.OnBowlSelectedListener

{
    /* Common member variables of Measure Fragment*/
    private static final String TAG = "Measure Fragment";
    private static final int REQUEST_CAMERA=0;


    private int mMeasureMode=Constants.SIMPLE_MEASURE;   //현재 계량 모드; SIMPLE_MEASURE 또는 RECIPE_MEASURE

    private int mMeasuringValue; // 사용자로부터 입력받은 계량값
    private String mMeasuringUnit;// 사용자가 선택한 계량 단위
   // OpenCVCameraFragment camera_preview_fragment; //카메라 프리뷰
    CameraPreviewFragment camera_preview_fragment; //카메라 프리뷰

//    BowlListFragment bowlListFragment;

    /* Views for Measure1 */
    private Button mStartMeasureBtn;           //계량 시작 버튼
    private Spinner mMeasuringUnitSpinner;    //계량 단위 선택 스피너
    private EditText mMeasuringValueEditText;     // 계량값 입력창

    /* Views for Measure 2*/
    private SlidingUpPanelLayout mSlidingPanelLayout;   //레시피 보여줄 슬라이드 레이아웃
    private Button mSlidingBtn;                          //레시피 보기위한 슬라이드 패널 올리는 버튼
    private LinearLayout mSlidingDragView;              //레시피 들어가는 슬라이드 패널
    private TextView mRecipeOrderTextView;              //레시피 순서 들어가는곳.
    private TextView mRecipePreviewText;                //순서 미리보기
    private int mCurrentRecipeOrder;

    //ImageProperties
    private String mCurrentImagePath = null;
    private Uri mCapturedImageURI = null;

    private ImageView mPhotoImageView;

    private Button mPreviousOrderBtn, mNextOrderBtn;

    private TextView   mTitleTextView,
            mIngredientTextView,
            mAmountTextView;
    private LinearLayout mRecipeOrderLayout;

    private Recipe mRecipe;     //선택된 레시피
    private DatabaseHelper db;   //레시피가 저장된 DB

    private OnRecipeMeasureListener mListener; // TabActivity와 통신하기 위한 수단.

    private Bowl mBowl;
    private FrameLayout mBowlSelectFrameLayout;
    public MeasureFragment() {
        // Required empty public constructor
    }


    public static MeasureFragment newInstance(int mode, int sectionNumber, long recipeId) {
        MeasureFragment fragment = new MeasureFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_MEASURE_MODE,mode);
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        if(mode==Constants.RECIPE_MEASURE) {    //모드가 simple이면 recipe 관련 정보필요없음. 레시피 계량모드이면 recipe 정보 저장
            if (recipeId > 0) {
                args.putLong(Constants.ARG_RECIPE_ID, recipeId);
                Log.d("MeausreFragment","newInstance maded with recipe ID"+recipeId);
            }
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());


    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        initializeSimpleMeasureView(view);
        initializeRecipeMeasureView(view);
    }

    private void initializeSimpleMeasureView(final View view){


        mMeasuringValueEditText = (EditText) view.findViewById(R.id.edit_measuring_value);

        /*Spinner*/
        mMeasuringUnitSpinner = (Spinner) view.findViewById(R.id.spinner_measuring_unit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.measuring_unit,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMeasuringUnitSpinner.setAdapter(adapter);

        /*Button*/
        mStartMeasureBtn = (Button) view.findViewById(R.id.btn_start_measure);
        mStartMeasureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMeasure(v);
            }
        });

        mBowlSelectFrameLayout = (FrameLayout) view.findViewById(R.id.select_bowl_fragment);
        mBowlSelectFrameLayout.setVisibility(View.GONE);

    }
    private void initializeRecipeMeasureView(final View view){
        mSlidingBtn = (Button) view.findViewById(R.id.btn_sliding);
        mSlidingDragView = (LinearLayout) view.findViewById(R.id.dragView);

        mSlidingPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);

        mSlidingPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);

                // 슬라이드창 올라와있으면 아래화살표, 슬라이드창 내려가있으면 위화살표로 그림 변겅
                if(mSlidingPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.EXPANDED){
                    mSlidingBtn.setBackgroundResource(R.drawable.icon_slide_down);
                }else if(mSlidingPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.COLLAPSED){
                    mSlidingBtn.setBackgroundResource(R.drawable.icon_slide_up);
                }
            }
        });
        mSlidingPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        mRecipePreviewText =(TextView)view.findViewById(R.id.recipe_preview_text);

        mPreviousOrderBtn = (Button)mSlidingPanelLayout.findViewById(R.id.btn_previous_order_atMeasure2);

        mNextOrderBtn = (Button)mSlidingPanelLayout.findViewById(R.id.btn_next_order_atMeasure2);

        mPhotoImageView = (ImageView)mSlidingPanelLayout.findViewById(R.id.imageView1_atMeasure2);
        mTitleTextView = (TextView)mSlidingPanelLayout.findViewById(R.id.tv_Title_atMeasure2);
        mIngredientTextView = (TextView)mSlidingPanelLayout.findViewById(R.id.tv_Ingredient_atMeasure2);
        mRecipeOrderLayout = (LinearLayout)mSlidingPanelLayout.findViewById(R.id.recipe_order_layout_atMeasure2);
        mAmountTextView = (TextView)mSlidingPanelLayout.findViewById(R.id.tv_Amount_atMeasure2);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_measure, null);
    }



    private void showCameraPreview() {
        //camera_preview_fragment = new OpenCVCameraFragment(); // 카메라 프래그먼트 생성 후 넣음.
        camera_preview_fragment = new CameraPreviewFragment(); // 카메라 프래그먼트 생성 후 넣음.
        getChildFragmentManager().beginTransaction()
                .replace(R.id.camera_fragment, camera_preview_fragment)
//                .addToBackStack(null)
                .commit();
    }


    public void startMeasure(View view){
        //Start Button 누르면 실행됨
          //startActivity(intent);
        mMeasuringValue= Integer.parseInt(mMeasuringValueEditText.getText().toString());
        mMeasuringUnit = mMeasuringUnitSpinner.getSelectedItem().toString();
        mMeasureMode=Constants.SIMPLE_MEASURE;
        onResume();
        Toast.makeText(getActivity(), "START MEASURE "+mMeasuringValue+" "+mMeasuringUnit, Toast.LENGTH_SHORT).show();

        TabLayoutActivity myActivity =(TabLayoutActivity)getActivity();
        mBowlSelectFrameLayout.setVisibility(View.VISIBLE);
        myActivity.ReplaceFragment(Enums.FragmentEnums.BowlSelectListFragment,0,0);

    }


    public void bowlSelected(){
        Log.d(TAG,"Replace DONE::"+mBowl.getName());
        Fragment frag = getFragmentManager().findFragmentById(R.id.select_bowl_fragment);
        if(null != frag ){
            getChildFragmentManager().beginTransaction().remove(frag).commit();
        }
        mBowlSelectFrameLayout.setVisibility(View.GONE);
        /*화면에 뿌려주면됨*/












    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnRecipeMeasureListener) {
            mListener = (OnRecipeMeasureListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        showCameraPreview();
        changeViewByMeasureMode();
    }

    private void GetPassedInRecipe(){
        Bundle args = getArguments();
        long recipeId = args.getLong(Constants.ARG_RECIPE_ID);

        if (recipeId > 0) {
            mRecipe = db.getRecipeById(recipeId);
            Log.d("MeasureFragment", "title" + mRecipe.getTitle());
            Log.d("MeasureFragment", "getRecipeID" + mRecipe.getId());
            mSlidingDragView.setVisibility(View.VISIBLE);
            if(mRecipe!=null)
                mRecipePreviewText.setText(mRecipe.getTitle());
        }
    }

    private void changeViewByMeasureMode() {
        Bundle args = getArguments();
        mMeasureMode=args.getInt(Constants.ARG_MEASURE_MODE);

        if(mMeasureMode==Constants.RECIPE_MEASURE) {
            GetPassedInRecipe(); //레시피 DB에서 가져오고
            mSlidingDragView.setVisibility(View.VISIBLE);//슬라이드 패널 보이게 하고

            if(mRecipe!=null) {//레시피 있으면 각 정보를 슬라이드 패널 안에 넣어요

                PopulateFields();

            }
        } else if (mMeasureMode==Constants.SIMPLE_MEASURE) {
            mSlidingDragView.setVisibility(View.GONE);
        }
    }

    private void PopulateFields() {
        if(mRecipe.getRecipeOrderArray().length>0) {
            mRecipePreviewText.setText(mRecipe.getRecipeOrderArray()[0]);//여기에 첫번째 레시피가 들어가고, 두번째부터는 버튼 누르면 바뀌어야함
            mCurrentRecipeOrder = 0;
            mPreviousOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((mCurrentRecipeOrder > 0) && (mCurrentRecipeOrder < mRecipe.getRecipeOrderArray().length)) {
                        mCurrentRecipeOrder--;
                        mRecipePreviewText.setText(mRecipe.getRecipeOrderArray()[mCurrentRecipeOrder]);
                    }
                }
            });
            mNextOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((mCurrentRecipeOrder >= 0) && (mCurrentRecipeOrder < mRecipe.getRecipeOrderArray().length - 1)) {
                        mCurrentRecipeOrder++;
                        mRecipePreviewText.setText(mRecipe.getRecipeOrderArray()[mCurrentRecipeOrder]);
                    }
                }
            });
        }
        mTitleTextView.setText(mRecipe.getTitle());
        mIngredientTextView.setText(mRecipe.getIngredient());

        for( int i=0;i<mRecipe.getRecipeOrderArray().length;i++)
        {
            TextView addRecipeOrder = (TextView) new TextView(getContext());
            addRecipeOrder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addRecipeOrder.setText(mRecipe.getRecipeOrderArray()[i]);
            addRecipeOrder.setTextColor(Color.WHITE);
            mRecipeOrderLayout.addView(addRecipeOrder);
        }

//        mRecipeOrderTextView.setText(mRecipe.getRecipeOrder());
        mAmountTextView.setText(mRecipe.getAmount());


        // Update profile's Image
        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
            mPhotoImageView.setImageDrawable(new BitmapDrawable(getResources(),
                    FileUtils.getResizedBitmap(mCurrentImagePath, 512, 512)));
        } else {
            mPhotoImageView.setImageDrawable(mRecipe.getImage(getActivity()));
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("MeasureFragment","Measure Destroyed");
    }

    @Override
    public void onBowlSelected(Bowl bowl) {
        // 화면에 투명 이미지 뿌려뿌려뿌려
        Toast.makeText(getActivity(),"Bowl:"+bowl.getName(),Toast.LENGTH_SHORT);
        Log.d(TAG,"ON BOWL SELECTED");
    }

    public Bowl getBowl() {
        return mBowl;
    }

    public void setBowl(Bowl bowl) {
        this.mBowl = bowl;
    }


    public interface OnRecipeMeasureListener {

        void onRecipeMeasure(int mode, long recipeId);
    }
}
