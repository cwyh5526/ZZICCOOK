package com.example.user.zziccook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Model.Bowl;
import com.example.user.zziccook.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/15/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Context in which this database exists.
    private Context mContext;

    // Database version.
    public static final int DATABASE_VERSION = 1;

    // Database name.
    public static final String DATABASE_NAME = "ImageExample";

    // Table names.
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_BOWLS="bowls";

    private final static String TAG = DatabaseHelper.class.getSimpleName();


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_BOWL_TABLE);
      }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_BOWLS);
    }

    // Command to create a table of Recipes.
    private static final String CREATE_RECIPE_TABLE = "CREATE TABLE " +  TABLE_RECIPES + " ("
            + Constants.COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, "
            + Constants.COLUMN_TITLE + " TEXT, "
            + Constants.COLUMN_STAR +" TEXT, "
            + Constants.COLUMN_INGREDIENT + " TEXT, "
            + Constants.COLUMN_RECIPE_ORDER + " TEXT, "
            + Constants.COLUMN_AMOUNT + " TEXT, "
            + Constants.COLUMN_IMAGE_PATH + " TEXT) ";

    // Command to create a table of Bowls.
    private static final String CREATE_BOWL_TABLE ="CREATE TABLE " +  TABLE_BOWLS + " ("
            + Constants.COLUMN_BOWL_ID + " INTEGER PRIMARY KEY, "
            + Constants.COLUMN_BOWL_NAME + " TEXT, "
            + Constants.COLUMN_BOWL_TYPE +" TEXT, "
            + Constants.COLUMN_BOWL_ROWS +" INTEGER, "
            + Constants.COLUMN_BOWL_COLS +" INTEGER, "
            + Constants.COLUMN_BOWL_EDGE_LEFT_X + " TEXT, "
            + Constants.COLUMN_BOWL_EDGE_LEFT_Y + " TEXT, "
            + Constants.COLUMN_BOWL_EDGE_RIGHT_X + " TEXT, "
            + Constants.COLUMN_BOWL_EDGE_RIGHT_Y + " TEXT, "
            + Constants.COLUMN_BOWL_HEIGHT + " FLOAT, "
            + Constants.COLUMN_BOWL_WIDTH + " FLOAT, "
            + Constants.COLUMN_BOWL_VOLUME + " FLOAT, "
            + Constants.COLUMN_BOWL_CANNY_IMAGE_PATH + " TEXT) ";

    // Database lock to prevent conflicts.
    public static final Object[] databaseLock = new Object[0];


    public List<Recipe> getAllRecipe() {
        //Initialize an empty list of Customers
        List<Recipe> recipeList = new ArrayList<Recipe>();

        //Command to select all Customers
        String selectQuery = "SELECT * FROM " +  TABLE_RECIPES;

        //lock database for reading
        synchronized (databaseLock) {
            //Get a readable database
            SQLiteDatabase database = getReadableDatabase();

            //Make sure database is not empty
            if (database != null) {

                //Get a cursor for all Customers in the database
                Cursor cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        Recipe recipe = getRecipe(cursor);
                        recipeList.add(recipe);
                        cursor.moveToNext();
                    }
                }
                //Close the database connection
                database.close();
            }
            //Return the list of customers
            return recipeList;
        }

    }
    public List<Bowl> getAllBowls() {
        Log.d(TAG,"GET BOWL LIST START::");

        //Initialize an empty list of Bowls
        List<Bowl> bowlList = new ArrayList<Bowl>();

        //Command to select all Bowls
        String selectQuery = "SELECT * FROM " +  TABLE_BOWLS;

        //lock database for reading
        synchronized (databaseLock) {
            //Get a readable database
            SQLiteDatabase database = getReadableDatabase();

            //Make sure database is not empty
            if (database != null) {

                //Get a cursor for all Customers in the database
                Cursor cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        Bowl bowl = getBowl(cursor);
                        bowlList.add(bowl);
                        cursor.moveToNext();
                    }
                }
                //Close the database connection
                database.close();
            }
            //Return the list of customers
            Log.d(TAG,"GET BOWL LIST DONE::");
            return bowlList;
        }

    }
    public List<Recipe> getAllFavoriteRecipe(){
        Log.d(TAG,"GetAllFavoriteRecipe");
        //Initialize an empty list of Customers

        List<Recipe> recipeList = new ArrayList<Recipe>();

        //Command to select all Customers
        String selectQuery = "SELECT * FROM " +  TABLE_RECIPES+ " WHERE " +Constants.COLUMN_STAR+" = '"+Constants.FAVORITE_RECIPE+"'";
        Log.d(TAG,selectQuery);
        //lock database for reading
        synchronized (databaseLock) {
            //Get a readable database
            SQLiteDatabase database = getReadableDatabase();

            //Make sure database is not empty
            if (database != null) {

                //Get a cursor for all Customers in the database
                Cursor cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        Recipe recipe = getRecipe(cursor);
                        recipeList.add(recipe);
                        cursor.moveToNext();
                    }
                }
                //Close the database connection
                database.close();
            }
            //Return the list of customers
            return recipeList;
        }
    }

    private static Recipe getRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_RECIPE_ID)));
        recipe.setStar(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_STAR)));
        recipe.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TITLE)));
        recipe.setIngredient(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_INGREDIENT)));
        recipe.setRecipeOrder(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_RECIPE_ORDER)));
        recipe.setAmount(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_AMOUNT)));
        recipe.setImagePath(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMAGE_PATH)));
        return recipe;
    }

    private static Bowl getBowl(Cursor cursor) {
        Bowl bowl = new Bowl();

        bowl.setId(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_BOWL_ID)));
        bowl.setName(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_NAME)));
        bowl.setType(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_TYPE)));

        bowl.setRowsLength(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_BOWL_ROWS)));
        bowl.setColsLength(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_BOWL_COLS)));

        bowl.setEdgeLeftTop(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_EDGE_LEFT_X)));
        bowl.setEdgeLeftDown(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_EDGE_LEFT_Y)));
        bowl.setEdgeRightTop(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_EDGE_RIGHT_X)));
        bowl.setEdgeRightDown(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_EDGE_RIGHT_Y)));

        bowl.setHeight(cursor.getFloat(cursor.getColumnIndex(Constants.COLUMN_BOWL_HEIGHT)));
        bowl.setWidth(cursor.getFloat(cursor.getColumnIndex(Constants.COLUMN_BOWL_WIDTH)));
        bowl.setVolume(cursor.getFloat(cursor.getColumnIndex(Constants.COLUMN_BOWL_VOLUME)));

        bowl.setCannyImagePath(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BOWL_CANNY_IMAGE_PATH)));
        Log.d(TAG,"GET BOWL : "+bowl.getId() +" :: " +bowl.getName());

        return bowl;
    }
    public Long addRecipe(Recipe recipe) {
        Long ret = null;

        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Prepare the Recipe information that will be saved to the database
                ContentValues values = new ContentValues();

                values.put(Constants.COLUMN_TITLE, recipe.getTitle());
                values.put(Constants.COLUMN_STAR, recipe.getStar());
                values.put(Constants.COLUMN_INGREDIENT, recipe.getIngredient());
                values.put(Constants.COLUMN_RECIPE_ORDER, recipe.getRecipeOrder());
                values.put(Constants.COLUMN_AMOUNT, recipe.getAmount());
                values.put(Constants.COLUMN_IMAGE_PATH, recipe.getImagePath());

                //Attempt to insert the client information into the transaction table
                try {
                    ret = database.insert( TABLE_RECIPES, null, values);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to add Recipe to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }
    public Long addBowl(Bowl bowl) {
        Long ret = null;

        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Prepare the Recipe information that will be saved to the database
                ContentValues values = new ContentValues();

                values.put(Constants.COLUMN_BOWL_NAME, bowl.getName());
                values.put(Constants.COLUMN_BOWL_TYPE, bowl.getType());

                values.put(Constants.COLUMN_BOWL_ROWS, bowl.getRowsLength());
                values.put(Constants.COLUMN_BOWL_COLS, bowl.getColsLength());

                values.put(Constants.COLUMN_BOWL_EDGE_LEFT_X, bowl.getEdgeLeftTop().toString());
                values.put(Constants.COLUMN_BOWL_EDGE_LEFT_Y, bowl.getEdgeLeftDown().toString());
                values.put(Constants.COLUMN_BOWL_EDGE_RIGHT_X, bowl.getEdgeRightTop().toString());
                values.put(Constants.COLUMN_BOWL_EDGE_RIGHT_Y, bowl.getEdgeRightDown().toString());

                values.put(Constants.COLUMN_BOWL_HEIGHT, bowl.getHeight());
                values.put(Constants.COLUMN_BOWL_WIDTH, bowl.getWidth());
                values.put(Constants.COLUMN_BOWL_VOLUME, bowl.getVolume());
                values.put(Constants.COLUMN_BOWL_CANNY_IMAGE_PATH, bowl.getCannyImagePath());

                //Attempt to insert the client information into the transaction table
                try {
                    ret = database.insert( TABLE_BOWLS, null, values);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to add Bowls to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }
    public Recipe getRecipeById(long id){
        List<Recipe> tempRecipeList = getAllRecipe();
        for (Recipe recipe : tempRecipeList){
            if (recipe.getId() == id){
                return recipe;
            }
        }
        return null;
    }
    public Bowl getBowlById(long id){
        List<Bowl> tempBowlList = getAllBowls();
        for (Bowl bowl : tempBowlList){
            if (bowl.getId() == id){
                return bowl;
            }
        }
        return null;
    }
    public boolean recipeExists(long id){
        //Check if there is an existing customer
        List<Recipe> tempRecipeList = getAllRecipe();
        for (Recipe recipe : tempRecipeList){
            if (recipe.getId() == id){
                return true;
            }
        }
        return false;
    }
    public boolean bowlExists(long id){
        //Check if there is an existing customer
        List<Bowl> tempBowlList = getAllBowls();
        for (Bowl bowl : tempBowlList){
            if (bowl.getId() == id){
                return true;
            }
        }
        return false;
    }
    public int removeRecipe(Recipe recipe){
        int ret =0;
        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Attempt to delete the recipe information into the transaction table
                try {
                    ret = database.delete(TABLE_RECIPES, Constants.COLUMN_RECIPE_ID + "=" + recipe.getId(), null);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to delete Recipe to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }
    public int removeBowl(Bowl bowl){
        int ret =0;
        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Attempt to delete the recipe information into the transaction table
                try {
                    ret = database.delete(TABLE_BOWLS, Constants.COLUMN_BOWL_ID + "=" + bowl.getId(), null);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to delete Bowl to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }
    public int updateRecipe(Recipe recipe) {
        int ret = 0;
        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Prepare the Recipe information that will be saved to the database
                ContentValues values = new ContentValues();

                values.put(Constants.COLUMN_TITLE, recipe.getTitle());
                values.put(Constants.COLUMN_STAR, recipe.getStar());
                values.put(Constants.COLUMN_INGREDIENT, recipe.getIngredient());
                values.put(Constants.COLUMN_RECIPE_ORDER, recipe.getRecipeOrder());
                values.put(Constants.COLUMN_AMOUNT, recipe.getAmount());
                values.put(Constants.COLUMN_IMAGE_PATH, recipe.getImagePath());

                //Attempt to insert the client information into the transaction table
                try {
                    ret = database.update(TABLE_RECIPES, values, Constants.COLUMN_RECIPE_ID + "=" + recipe.getId(), null);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to update Recipe to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }
    public int updateBowl(Bowl bowl) {
        int ret = 0;
        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Prepare the Recipe information that will be saved to the database
                ContentValues values = new ContentValues();

                values.put(Constants.COLUMN_BOWL_NAME, bowl.getName());
                values.put(Constants.COLUMN_BOWL_TYPE, bowl.getType());

                values.put(Constants.COLUMN_BOWL_ROWS, bowl.getRowsLength());
                values.put(Constants.COLUMN_BOWL_COLS, bowl.getColsLength());

                values.put(Constants.COLUMN_BOWL_EDGE_LEFT_X, bowl.getEdgeLeftTop().toString());
                values.put(Constants.COLUMN_BOWL_EDGE_LEFT_Y, bowl.getEdgeLeftDown().toString());
                values.put(Constants.COLUMN_BOWL_EDGE_RIGHT_X, bowl.getEdgeRightTop().toString());
                values.put(Constants.COLUMN_BOWL_EDGE_RIGHT_Y, bowl.getEdgeRightDown().toString());

                values.put(Constants.COLUMN_BOWL_HEIGHT, bowl.getHeight());
                values.put(Constants.COLUMN_BOWL_WIDTH, bowl.getWidth());
                values.put(Constants.COLUMN_BOWL_VOLUME, bowl.getVolume());
                values.put(Constants.COLUMN_BOWL_CANNY_IMAGE_PATH, bowl.getCannyImagePath());

                //Attempt to insert the client information into the transaction table
                try {
                    ret = database.update(TABLE_BOWLS, values, Constants.COLUMN_BOWL_ID + "=" + bowl.getId(), null);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to update Bowl to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }

}


