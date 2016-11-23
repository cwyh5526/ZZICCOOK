package com.example.user.zziccook.Fragment.Camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.zziccook.CameraPreview;
import com.example.user.zziccook.R;

import java.io.EOFException;
import java.io.File;

/**
 * Created by user on 2016-07-07.
 */
public class CameraPreviewFragment extends Fragment{
    private static final String TAG = "CameraPreview";

    /**
     * Id of the camera to access. 0 is the first camera.
     */
    public static final int CAMERA_ID = 0;

    private CameraPreview mPreview;
    private Camera mCamera;

    public int Height;

    public static CameraPreviewFragment newInstance() {
        return new CameraPreviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Open an instance of the first camera and retrieve its info.
        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = null;

        if (mCamera != null) {
            // Get camera info only if the camera is available
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CAMERA_ID, cameraInfo);
        }

        if (mCamera == null || cameraInfo == null) {
            // Camera is not available, display error message
            Toast.makeText(getActivity(), "Camera is not available.", Toast.LENGTH_SHORT).show();
            return inflater.inflate(R.layout.fragment_camera_unavailable, null);
        }

        View root = inflater.inflate(R.layout.fragment_camera, null);

        // Get the rotation of the screen to adjust the preview image accordingly.
        final int displayRotation = getActivity().getWindowManager().getDefaultDisplay()
                .getRotation();

        // Create the Preview view and set it as the content of this Activity.
        mPreview = new CameraPreview(getActivity(), mCamera, cameraInfo, displayRotation);


        FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview);
//
//        DisplayMetrics metrics=new DisplayMetrics();
//        WindowManager windowManager = (WindowManager)getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//
//        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams)preview.getLayoutParams();
//        params.width=metrics.widthPixels;
//        params.height=metrics.heightPixels;
//        preview.setLayoutParams(params);

        preview.addView(mPreview);





        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera access
        mPreview.getHolder().removeCallback(mPreview);//Add Recipe 에서 Add Image 하면 App 꺼지는거 방지하기 위해서사용. 카메라 preview 반납
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public void takePicture(){

        mPreview.takePicture( new Camera.PictureCallback() {

            public void onPictureTaken(byte[] bytes, Camera camera) {
                if(bytes!=null){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        String imageSaveUri = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "사진 저장", "저장되었습니다.");
                        Uri uri = Uri.parse(imageSaveUri);
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        Toast.makeText(getActivity().getApplicationContext(), "찍은 사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        mCamera.startPreview();
                    }
                    catch (Exception e){
                        Log.e("사진저장","저장실패!",e);
                    }
                }
            }
        });
    }

    public CameraPreview getPreview(){
        return mPreview;
    }

}
