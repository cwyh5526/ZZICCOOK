package com.example.user.zziccook.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Model.Recipe;
import com.example.user.zziccook.R;

import java.util.List;

/**
 * Created by Valentine on 4/16/2015.
 */
public class RecipeAdapter extends ArrayAdapter<Recipe>{
    private Context mContext;
    private List<Recipe> mRecipes;
    private static final String TAG="RecipeAdapter";
    private DatabaseHelper db;

    private OnFavoriteAddedListener mListener;

    public RecipeAdapter(Context context, List<Recipe> recipes)
    {
        super(context, R.layout.row_recipe_list, recipes);
        context = mContext;
        mRecipes = recipes;

    }



    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Recipe getItem(int position) {
        if (position < mRecipes.size()) {
            return mRecipes.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class RecipeHolder {
        private Button FavoriteBtn;
        private TextView Title;
        private ImageView Thumbnail;
    }



    public void Add(Recipe recipe)
    {
        mRecipes.add(recipe);
        this.notifyDataSetChanged();
    }

    public void Update()
    {
        mRecipes.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        RecipeHolder mHolder;
        if (getContext() instanceof OnFavoriteAddedListener) {
            mListener = (OnFavoriteAddedListener) getContext();
        } else {
            throw new RuntimeException(getContext().toString()
                    + " must implement OnPhoneHeightChangeListener");
        }
        final Recipe recipe= getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_recipe_list, null);

            mHolder = new RecipeHolder();
            mHolder.FavoriteBtn = (Button) view.findViewById(R.id.btn_favorite_at_List);

            mHolder.FavoriteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Button btn =  (Button) v;

                    db=  new DatabaseHelper(getContext());

                    Log.d(TAG,"Recipe ID"+recipe.getId()+"\nName"+recipe.getTitle());
                    if(btn.getText().equals("☆")){
                        btn.setText("★");
                        recipe.setStar(Constants.FAVORITE_RECIPE);
                    }else{
                        btn.setText("☆");
                        recipe.setStar(Constants.NON_FAVORITE_RECIPE);
                    }

                   Log.d(TAG,"Favorite Btn of Position "+position);
                    db.updateRecipe(recipe);
                    mListener.onFavoriteAdded();
                }

            });
            mHolder.Title = (TextView) view.findViewById(R.id.textRecipeTitle);
            mHolder.Thumbnail = (ImageView) view.findViewById(R.id.recipe_image_thumbnail);

            view.setTag(mHolder);
        }else {
            mHolder = (RecipeHolder)view.getTag();
        }
        //Set the Favorite
        if (mHolder.FavoriteBtn != null) {
            if(recipe.getStar().equals(Constants.FAVORITE_RECIPE)) {
                mHolder.FavoriteBtn.setText("★");
            }else {
                mHolder.FavoriteBtn.setText("☆");
            }
        }


        //Set the Recipe Title
        if (mHolder.Title != null) {
            mHolder.Title.setText(recipe.getTitle());
        }

        //set the Recipe Picture Thumbnail
        if (mHolder.Thumbnail != null){
            mHolder.Thumbnail.setImageDrawable(recipe.getThumbnail(getContext()));
        }
        return view;
    }

    public interface OnFavoriteAddedListener {
        // TODO: Update argument type and name
        void onFavoriteAdded();

    }


}
