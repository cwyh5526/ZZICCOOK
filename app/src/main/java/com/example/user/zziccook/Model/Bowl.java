package com.example.user.zziccook.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.FileUtils;

import org.opencv.core.Point;

/**
 * Created by user on 2016-09-05.
 */
public class Bowl {
    private static final String TAG ="Bowl Class";

    private int mId;
    private String mName;
    private String mType; //그릇 종류

    private Point mEdgeLeftTop;   // 그릇 좌측 상단 모서리 좌표값
    private Point mEdgeLeftDown;   // 그릇 좌측 하단 모서리 좌표값
    private Point mEdgeRightTop;  // 그릇 우측 상단 모서리 좌표값
    private Point mEdgeRightDown;  // 그릇 우측 하단 모서리 좌표값


    private int mRowsLength;

    private int mColsLength;

    private double mHeight; //측정한 그릇 높이
    private double mWidth;  //측정한 그릇 너비
    private double mVolume; //측정한 그릇 부피

    private String mCannyImagePath; // canny 처리 한 이미지

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }


    public Point stringToPoint(String edge){
        String[] coords=edge.split("[{,}]"); //{a,b} 형태에서 a와 b만 추출
//        Log.d(TAG,"Change String to Point\n coords length" +coords.length);
//        for (int i=0; i<coords.length;i++){
//            Log.d(TAG,"Coords "+i+coords[i]);
//        }

        Point point = new Point(Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
        return point;
    }


    public int getColsLength() {
        return mColsLength;
    }

    public void setColsLength(int mColsLength) {
        this.mColsLength = mColsLength;
    }
    public int getRowsLength() {
        return mRowsLength;
    }

    public void setRowsLength(int mRowsLength) {
        this.mRowsLength = mRowsLength;
    }

     public Point getEdgeLeftTop() {
        return mEdgeLeftTop;
    }

    public void setEdgeLeftTop(Point edgeLeftX) {
        this.mEdgeLeftTop = edgeLeftX;
    }
    public void setEdgeLeftTop(String edgeLeftX) {
        mEdgeLeftTop = stringToPoint(edgeLeftX);
    }
    public Point getEdgeLeftDown() {
        return mEdgeLeftDown;
    }
    public void setEdgeLeftDown(Point edgeLeftY) {
        this.mEdgeLeftDown = edgeLeftY;
    }
    public void setEdgeLeftDown(String edgeLeftY) {
        this.mEdgeLeftDown = stringToPoint(edgeLeftY);
    }

    public Point getEdgeRightTop() {
        return mEdgeRightTop;
    }
    public void setEdgeRightTop(Point edgeRightX) {
        this.mEdgeRightTop = edgeRightX;
    }
    public void setEdgeRightTop(String edgeRightX) {
        this.mEdgeRightTop = stringToPoint(edgeRightX);
    }


    public Point getEdgeRightDown() {
        return mEdgeRightDown;
    }
    public void setEdgeRightDown(Point edgeRightY) {
        this.mEdgeRightDown = edgeRightY;
    }
    public void setEdgeRightDown(String edgeRightY) {
        this.mEdgeRightDown = stringToPoint(edgeRightY);
    }

    public double getHeight() {
        return mHeight;
    }
    public void setHeight(double mHeight) {
        this.mHeight = mHeight;
    }

    public double getWidth() {
        return mWidth;
    }

    public void setWidth(double mWidth) {
        this.mWidth = mWidth;
    }

    public double getVolume() {
        return mVolume;
    }

    public void setVolume(double mVolume) {
        this.mVolume = mVolume;
    }

    public String getCannyImagePath() {   return mCannyImagePath;    }

    public void setCannyImagePath(String mCannyImagePath) {     this.mCannyImagePath = mCannyImagePath;
    }


    public boolean hasImage() {

        return getCannyImagePath() != null && !getCannyImagePath().isEmpty();
    }


    /**
     * Get a thumbnail of this Bowl picture, or a default image if the Bowl doesn't have a
     * Image.
     *
     * @return Thumbnail of the profile.
     */
    public Drawable getThumbnail(Context context) {

        return getScaledImage(context, 128, 128);
    }

    /**
     * Get this Bowl's picture, or a default image if the Bowl doesn't have a Image.
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
     * Get a scaled version of this profile's Image, or a default image if the profile doesn't have
     * a Image.
     *
     * @return Image of the profile.
     */
    private Drawable getScaledImage(Context context, int reqWidth, int reqHeight) {

        // If Bowl has a Image.
        if (hasImage()) {

            // Decode the input stream into a bitmap.
            Bitmap bitmap = FileUtils.getResizedBitmap(getCannyImagePath(), reqWidth, reqHeight);

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
