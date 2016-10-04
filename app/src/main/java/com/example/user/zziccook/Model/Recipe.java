package com.example.user.zziccook.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.FileUtils;


public class Recipe {
    private int mId;
    private String mStar; //즐겨찾기 여부
    private String mTitle; //레시피 명
    private String mIngredient;//재료
    private String mRecipeOrder;//레시피 내용

    private String mRecipeOrderArray[];
    private String mAmount;//몇인분?
    private String mImagePath; //이미지

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getStar() {
        return mStar;
    }

    public void setStar(String name) {
        mStar = name;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getIngredient() {
        return mIngredient;
    }

    public void setIngredient(String ingredient) {
        mIngredient = ingredient;
    }

    public String getRecipeOrder() {
        return mRecipeOrder;
    }

    public void setRecipeOrder(String recipeOrder) {
        mRecipeOrder = recipeOrder;
        mRecipeOrderArray =mRecipeOrder.split(Constants.SPLIT_DELIMINATOR);
    }

    public String[] getRecipeOrderArray() {
        return mRecipeOrderArray;
    }

    public void setRecipeOrderArray(String[] mRecipeOrderArray) {

        this.mRecipeOrderArray = mRecipeOrderArray;
    }


    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }


    public boolean hasImage() {

        return getImagePath() != null && !getImagePath().isEmpty();
    }


    /**
     * Get a thumbnail of this profile's picture, or a default image if the profile doesn't have a
     * Image.
     *
     * @return Thumbnail of the profile.
     */
    public Drawable getThumbnail(Context context) {

        return getScaledImage(context, 128, 128);
    }

    /**
     * Get this profile's picture, or a default image if the profile doesn't have a Image.
     *
     * @return Image of the profile.
     */
    public Drawable getImage(Context context) {

        return getScaledImage(context, 512, 512);
    }

    public Drawable setDefaultImage(Context context){
        return context.getResources().getDrawable(Constants.DEFAULT_IMAGE_RESOURCE);
    }
    /**
     * Get a scaled version of this Recipe's Image, or a default image if the profile doesn't have
     * a Image.
     *
     * @return Image of the profile.
     */
    private Drawable getScaledImage(Context context, int reqWidth, int reqHeight) {

        // If profile has a Image.
        if (hasImage()) {

            // Decode the input stream into a bitmap.
            Bitmap bitmap = FileUtils.getResizedBitmap(getImagePath(), reqWidth, reqHeight);

            // If was successfully created.
            if (bitmap != null) {

                // Return a drawable representation of the bitmap.
                return new BitmapDrawable(context.getResources(), bitmap);
            }
        }

        // Return the default image drawable.
        return context.getResources().getDrawable(Constants.DEFAULT_IMAGE_RESOURCE);
    }
}
