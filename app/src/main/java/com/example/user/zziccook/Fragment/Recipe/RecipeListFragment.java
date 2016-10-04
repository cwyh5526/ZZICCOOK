package com.example.user.zziccook.Fragment.Recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.zziccook.Adapter.RecipeAdapter;
import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.Enums;
import com.example.user.zziccook.Model.Recipe;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class RecipeListFragment extends Fragment  {
    private static final String TAG="RecipeList Fragment";
    private List<Recipe> mRecipes;
    private ListView mRecipeListView;
    private RecipeAdapter mAdapter;
    private String mListMode;


    private View mRootView;
    private Button mAddFavoriteBtn;
    private Button mAddRecipeBtn;
    private DatabaseHelper db;

    private OnRecipeSelectedListener mListener;

    public static RecipeListFragment newInstance(String mode,int sectionNumber) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        args.putString(Constants.ARG_LIST_MODE,mode);
        Log.d(TAG,"LIST MODE ="+mode);
        fragment.setArguments(args);
        return fragment;
    }


    public RecipeListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        db = new DatabaseHelper(getActivity());
        Bundle args = getArguments();
        mListMode=args.getString(Constants.ARG_LIST_MODE);
        if(mListMode==null){
            mListMode=Constants.RECIPLE_LIST;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);



        return mRootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        InitializeViews();
        LoadListData();
    }

    private void LoadListData()
    {
        //First clear the adapter of any Recipe it has
        mAdapter.Update();

        //Get the list of Recipes from the database
        Bundle args = getArguments();
        mListMode=args.getString(Constants.ARG_LIST_MODE);
        Log.d(TAG,"LIST MODE ="+mListMode);

        if(mListMode.equals(Constants.FAVORITE_RECIPE_LIST)){
                mRecipes = db.getAllFavoriteRecipe();
                Log.d(TAG,"FAVORITE RECIPE");

        }else{
            mRecipes = db.getAllRecipe();
            Log.d(TAG,"ALL RECIPE");
        }


        if (mRecipes != null){
            for (Recipe recipe: mRecipes){
                mAdapter.add(recipe);
            }
        }

    }

    private void InitializeViews() {

        mRecipeListView = (ListView) mRootView.findViewById(R.id.recipe_list);
        mRecipes = new ArrayList<Recipe>();
        mAdapter = new RecipeAdapter(getActivity(), mRecipes);
        mAddFavoriteBtn = (Button) mRootView.findViewById(R.id.btn_favorite_at_List);
        if(mListMode.equals(Constants.RECIPLE_LIST)){
            mAddRecipeBtn = (Button) mRootView.findViewById(R.id.btn_AddRecipe);
            mAddRecipeBtn.setVisibility(View.VISIBLE);
            mAddRecipeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayoutActivity myActivity = (TabLayoutActivity)getActivity();
                    myActivity.ReplaceFragment(Enums.FragmentEnums.RecipeAddFragment,3,0);
                }
            });
        }

        mRecipeListView.setAdapter(mAdapter);
        TextView emptyText = (TextView) mRootView.findViewById(R.id.recipe_list_empty);
        if(mListMode.equals(Constants.FAVORITE_RECIPE_LIST)) {
            emptyText.setText(R.string.no_favorite_recipe);
        }else{
            emptyText.setText(R.string.no_recipe);
        }
        mRecipeListView.setEmptyView(emptyText);

        mRecipeListView.setItemsCanFocus(true);
        mRecipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe tempRecipe = mRecipes.get(position);
                TabLayoutActivity myActivity = (TabLayoutActivity) getActivity();
                if(mListMode.equals(Constants.FAVORITE_RECIPE_LIST)){
                    myActivity.ReplaceFragment(Enums.FragmentEnums.FavoriteDetailsFragment, 3, tempRecipe.getId());
               }else{
                    myActivity.ReplaceFragment(Enums.FragmentEnums.RecipeDetailsFragment, 3, tempRecipe.getId());
                }

                    //Get the selected client


            }
        });
        mRecipeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int positionToRemove = position;

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete "+mRecipes.get(positionToRemove).getTitle());

                adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeRecipe(mRecipes.get(positionToRemove));
                        mAdapter.notifyDataSetChanged();//리스트 데이터 바뀐것 notify
                        LoadListData();//바뀐 리스트데이터 다시 로드
                    }});
                adb.setNegativeButton("Cancel",null);
                adb.show();
                return true;
            }
        });


    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.customer_list_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id){
//            case R.id.action_add_customer:
//                MainActivity myActivity = (MainActivity)getActivity();
//                myActivity.ReplaceFragment(Enums.FragmentEnums.CustomerDetailsFragment, 3, 0);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


//        ((TabLayoutActivity) activity).onSectionAttached(
//                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

//    @Override
//    public void onFavoriteAdded() {
////        onResume();
//    }

    public interface OnRecipeSelectedListener {

        void onRecipeSelected();
    }

}
