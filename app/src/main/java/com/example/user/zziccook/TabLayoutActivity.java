package com.example.user.zziccook;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.example.user.zziccook.Adapter.BowlAdapter;
import com.example.user.zziccook.Adapter.RecipeAdapter;
import com.example.user.zziccook.Adapter.TabPagerAdapter;
import com.example.user.zziccook.Fragment.Bowl.BowlAddFragment;

import com.example.user.zziccook.Fragment.Camera.OpenCVCameraFragment;
import com.example.user.zziccook.Fragment.Recipe.RecipeAddFragment;

import com.example.user.zziccook.Fragment.Bowl.BowlDetailsFragment;
import com.example.user.zziccook.Fragment.Bowl.BowlListFragment;
import com.example.user.zziccook.Fragment.Measure.MeasureFragment;
import com.example.user.zziccook.Fragment.Recipe.RecipeDetailsFragment;
import com.example.user.zziccook.Fragment.Recipe.RecipeListFragment;
import com.example.user.zziccook.Fragment.SettingFragment;
import com.example.user.zziccook.Fragment.Tab1Fragment;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.Enums;
import com.example.user.zziccook.Model.Bowl;

public class TabLayoutActivity extends AppCompatActivity implements
        Tab1Fragment.OnFragmentInteractionListener,
        RecipeAddFragment.OnRecipeSavedListener,
        MeasureFragment.OnRecipeMeasureListener,
//        Tab2Fragment.OnPhoneHeightChangeListener,
        BowlAddFragment.OnBowlSavedListener,
        BowlAdapter.OnFavoriteAddedListener,
        BowlListFragment.OnBowlSelectedListener,
        RecipeDetailsFragment.OnMeasure2StartedListener,
        RecipeListFragment.OnRecipeSelectedListener,
        RecipeAdapter.OnFavoriteAddedListener,
        Camera.PictureCallback
//        Tab5Fragment.OnPhoneHeightChangeListener
{


    private static final int REQUEST_CAMERA=0;


    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;

    private float mPhoneHeight=0;
    private SharedPreferences mSetting;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        startActivity(new Intent(this, SplashActivity.class));

        mSetting=getSharedPreferences("setting",0);//폰이 꺼져도 저장될 정보가 있는 xml 파일 setting


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        tabLayout =
                (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setIcon(
                R.drawable.ic_camera));
        tabLayout.addTab(tabLayout.newTab().setIcon(
                R.drawable.ic_recipe_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(
                R.drawable.ic_measure1));
        tabLayout.addTab(tabLayout.newTab().setIcon(
                R.drawable.ic_favorite));
        tabLayout.addTab(tabLayout.newTab().setIcon(
                R.drawable.ic_setting));


       mViewPager =
                (ViewPager) findViewById(R.id.pager);
       mAdapter = new TabPagerAdapter
                (getSupportFragmentManager(),
                        tabLayout.getTabCount());
       mViewPager.setAdapter(mAdapter);
       mViewPager.setOffscreenPageLimit(4);

       mViewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
       tabLayout.setOnTabSelectedListener(new
               TabLayout.OnTabSelectedListener() {
                   @Override
                   public void onTabSelected(TabLayout.Tab tab) {
                       mViewPager.setCurrentItem(tab.getPosition());

                   }

                   @Override
                   public void onTabUnselected(TabLayout.Tab tab) {

                   }

                   @Override
                   public void onTabReselected(TabLayout.Tab tab) {

                   }
               });


        requestCameraPermission();
    }
    private void requestCameraPermission(){
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA);

    }
    public void ReplaceFragment(Enums.FragmentEnums frag, int sectionNumber, long id){


        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (frag){
            //measure
            case MeasureFragment:
                MeasureFragment measureFrag =  MeasureFragment.newInstance(Constants.SIMPLE_MEASURE,sectionNumber,id);//id::recipe id
                fragmentManager.beginTransaction().replace(R.id.tab1_fragment, measureFrag)
                        .commit();
                break;
            case Measure2Fragment:
                MeasureFragment measure2Frag =  MeasureFragment.newInstance(Constants.RECIPE_MEASURE,sectionNumber,id);//id::recipe id
                fragmentManager.beginTransaction().replace(R.id.tab1_fragment, measure2Frag)
                        .commit();
                break;
            //Recipes
            //List는 더이상 돌아갈 곳이 없으므로 backstack 추가하지 않음.
            case RecipeListFragment:
                RecipeListFragment recipeListFrag =  RecipeListFragment.newInstance(Constants.RECIPLE_LIST,sectionNumber);
                fragmentManager.beginTransaction().replace(R.id.tab2_fragment, recipeListFrag)
                        .commit();
                break;
            case RecipeAddFragment:
                RecipeAddFragment addRecipeFrag =  RecipeAddFragment.newInstance(sectionNumber);//id::bowl id
                fragmentManager.beginTransaction().replace(R.id.tab2_fragment, addRecipeFrag)
                        .addToBackStack(null).commit();
                break;
            case RecipeDetailsFragment:
                RecipeDetailsFragment recipeFrag =  RecipeDetailsFragment.newInstance(Constants.RECIPLE_LIST,sectionNumber, id);//id::recipe id
                fragmentManager.beginTransaction().replace(R.id.tab2_fragment, recipeFrag)
                        .addToBackStack(null).commit();
                break;

            case FavoriteListFragment:
                RecipeListFragment favoriteRecipeListFrag =  RecipeListFragment.newInstance(Constants.FAVORITE_RECIPE_LIST,sectionNumber);
                fragmentManager.beginTransaction().replace(R.id.tab4_fragment, favoriteRecipeListFrag)
                        .commit();
                break;
            case FavoriteDetailsFragment:
                RecipeDetailsFragment favoriteRecipeFrag =  RecipeDetailsFragment.newInstance(Constants.FAVORITE_RECIPE_LIST,sectionNumber, id);//id::recipe id
                fragmentManager.beginTransaction().replace(R.id.tab4_fragment, favoriteRecipeFrag)
                        .addToBackStack(null).commit();
                break;
            //Bowls
            //List는 더이상 돌아갈 곳이 없으므로 backstack 추가하지 않음.
            case BowlListFragment:
                BowlListFragment bowlListFrag =  BowlListFragment.newInstance(Constants.BOWL_LIST,sectionNumber);
                fragmentManager.beginTransaction().replace(R.id.tab3_fragment, bowlListFrag)
                        .commit();
                break;
            case BowlDetailsFragment:
//                Log.d("TAB ACTIVITY","REPLACE FRAGMENT :: " +id);
                BowlDetailsFragment bowlFrag =  BowlDetailsFragment.newInstance(sectionNumber, id);//id::bowl id
                fragmentManager.beginTransaction().replace(R.id.tab3_fragment, bowlFrag)
                        .addToBackStack(null).commit();
                break;
            case BowlAddFragment:
                BowlAddFragment addBowlFrag =  BowlAddFragment.newInstance(sectionNumber);//id::bowl id
                fragmentManager.beginTransaction().replace(R.id.tab3_fragment, addBowlFrag)
                        .addToBackStack(null).commit();
                break;
            case BowlSelectListFragment:
                BowlListFragment bowlSelectFrag =  BowlListFragment.newInstance(Constants.BOWL_SELECT_LIST,sectionNumber);
                fragmentManager.beginTransaction().replace(R.id.select_bowl_fragment, bowlSelectFrag)
                        .addToBackStack(null).commit();
                break;
            case BowlMeasureFragment:
                OpenCVCameraFragment camFrag=new OpenCVCameraFragment();
                fragmentManager.beginTransaction().replace(R.id.tab3_fragment,camFrag)
                        .addToBackStack(null).commit();
//                BowlMeasureFragment bowlMeasureFragment = BowlMeasureFragment.newInstance();
//                fragmentManager.beginTransaction().replace(R.id.tab3_fragment,bowlMeasureFragment)
//                        .addToBackStack(null).commit();
                break;
            case SettingFragment:
                SettingFragment settingFrag = SettingFragment.newInstance();
                fragmentManager.beginTransaction().replace(R.id.tab5_fragment,settingFrag)
                        .commit();

                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((resultCode==RESULT_OK)&(requestCode==1000)){

        BowlAddFragment bowlAddFragment = (BowlAddFragment)getSupportFragmentManager().findFragmentById(R.id.tab3_fragment);

        bowlAddFragment.SetMeasuredValues(data.getExtras(),data.getData(),data.getDoubleExtra(Constants.ARG_BOWL_HEIGHT,0), data.getDoubleExtra(Constants.ARG_BOWL_WIDTH,0),
                data.getStringExtra(Constants.ARG_BOWL_EDGE_LEFT_TOP),
                data.getStringExtra(Constants.ARG_BOWL_EDGE_LEFT_DOWN),
                data.getStringExtra(Constants.ARG_BOWL_EDGE_RIGHT_TOP),
                data.getStringExtra(Constants.ARG_BOWL_EDGE_RIGHT_DOWN));
        }

    }

    @Override
    public void onFragmentInteraction() {
        mViewPager.setOffscreenPageLimit(2);
    }
    @Override
    public void onRecipeSaved() {
//        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBowlSaved() {

    }

    @Override
    public void onAddImage() {
        getSupportFragmentManager().findFragmentById(R.id.tab1_fragment).onDestroy();
//        MeasureFragment measureFragment=(MeasureFragment)getSupportFragmentManager().findFragmentById(R.id.tab1_fragment);
//        measureFragment.releaseCameraPreview();

    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    public void onMeasure2StartedAtRecipeDetail(long itemId) {
        //그냥 list에서 startMeasure 눌렀을 때
  //      getSupportFragmentManager().findFragmentById(R.id.tab2_fragment).onResume();
        MeasureFragment measure2Frag=(MeasureFragment) getSupportFragmentManager().findFragmentById(R.id.tab1_fragment);
        if(measure2Frag!=null){
            measure2Frag.onDestroy();
        }
        ReplaceFragment(Enums.FragmentEnums.Measure2Fragment,3,itemId);
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onMeasure2StartedAtFavoriteDetail(long itemId) {
        //즐겨찾기에서 Startmeasure 눌렀을 때
 //       getSupportFragmentManager().findFragmentById(R.id.tab4_fragment).onResume();
//        ReplaceFragment(Enums.FragmentEnums.Measure2Fragment,3,itemId);
////        mAdapter.instantiateItem(tabLayout,0);
//        MeasureFragment measure2Frag=(MeasureFragment) getSupportFragmentManager().findFragmentById(R.id.first_tab_fragment);
//        if(measure2Frag!=null){
//            measure2Frag.onDestroy();
//        }

        ReplaceFragment(Enums.FragmentEnums.Measure2Fragment,3,itemId);

        mViewPager.setCurrentItem(0);
    }


    @Override
    public void onRecipeSelected() {

    }


    @Override
    public void onRecipeMeasure(int mode, long recipeId) {

    }

    @Override
    public void onFavoriteAdded() {
        //Favorite 버튼 눌리면 실시간으로 페이지 Resume
        getSupportFragmentManager().findFragmentById(R.id.tab4_fragment).onResume();
        getSupportFragmentManager().findFragmentById(R.id.tab2_fragment).onResume();
    }

    @Override
    public void onBowlSelected(Bowl bowl) {
        MeasureFragment measureFragment = (MeasureFragment) getSupportFragmentManager().findFragmentById(R.id.tab1_fragment);
        measureFragment.setBowl(bowl);
        Log.d("TAB Layout Activity","passing Bowl:"+bowl.getName());
        measureFragment.bowlSelected();
    }


    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {

    }
}
