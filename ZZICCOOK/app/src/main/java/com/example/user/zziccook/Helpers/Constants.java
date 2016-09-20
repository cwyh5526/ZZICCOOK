package com.example.user.zziccook.Helpers;

import android.os.Environment;

import com.example.user.zziccook.R;

import java.io.File;

/**
 * Created by Valentine on 4/10/2015.
 */
public class Constants {

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_RECIPE_ID = "recipe_id";
    public static final String ARG_BOWL_ID="bowl_id";
    public static final String ARG_MEASURE_MODE="measure_mode";
    public static final String ARG_LIST_MODE="list_mode";



    public static  final int SIMPLE_MEASURE=1;  //단순 계량
    public static  final int RECIPE_MEASURE=2;  //레시피 계량

    public static final String RECIPLE_LIST="recipe_list";
    public static final String FAVORITE_RECIPE_LIST ="favorite_recipe_list";

    public static final String BOWL_LIST="bowl_list";
    public static final String BOWL_SELECT_LIST="bowl_select_list";

    /* Bowl DB Column */
    public static final String COLUMN_BOWL_ID = "_id";
    public static final String COLUMN_BOWL_NAME = "name";
    public static final String COLUMN_BOWL_TYPE = "type";

    public static final String COLUMN_BOWL_EDGE_LEFT_X = "leftX";
    public static final String COLUMN_BOWL_EDGE_LEFT_Y = "leftY";
    public static final String COLUMN_BOWL_EDGE_RIGHT_X = "rightX";
    public static final String COLUMN_BOWL_EDGE_RIGHT_Y = "rigtY";

    public static final String COLUMN_BOWL_HEIGHT = "height";
    public static final String COLUMN_BOWL_WIDTH = "width";
    public static final String COLUMN_BOWL_VOLUME = "volume";
    public static final String COLUMN_BOWL_CANNY_IMAGE_PATH = "cannyImagePath";

    public static final String BOWL_TYPE_CYLINDER ="cylinder";  // 그릇 타입: 원기둥형
    public static final String BOWL_TYPE_CUP ="cup";             // 그릇 타입: 위가 벌어진 컵형


    /* Recipe DB Column */
    public static final String COLUMN_RECIPE_ID = "_id";
    public static final String COLUMN_STAR = "star";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_INGREDIENT = "ingredient";
    public static final String COLUMN_RECIPE_ORDER = "recipeOrder";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_IMAGE_PATH = "imagePath";

    public static final String FAVORITE_RECIPE="favorite";
    public static final String NON_FAVORITE_RECIPE="non_favorite";

    public static final String SPLIT_DELIMINATOR="<split/>";



    public static final String KEY_IMAGE_URI = "image_uri";

    public static final String KEY_IMAGE_PATH = "key_image_path";
    public static int ACTION_REQUEST_IMAGE = 1000;
    public static int SELECT_IMAGE = 1001;
    public static final String TAKE_PHOTO = "Take Photo";
    public static final String CHOOSE_FROM_GALLERY = "Choose from gallery";
    public static final String CANCEL= "Cancel";

    public static final int DEFAULT_IMAGE_RESOURCE = R.drawable.default_customer_picture;
    public static final String PICTURE_DIRECTORY = Environment.getExternalStorageDirectory()
            + File.separator + "DCIM" + File.separator + "" +
            "" + File.separator;

}
