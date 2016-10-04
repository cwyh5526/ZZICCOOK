package com.example.user.zziccook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.zziccook.Fragment.Camera.CameraPreviewFragment;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.OpenCV.MeasureEdgeBySobel;
import com.example.user.zziccook.OpenCV.MeasureHeight;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.imgproc.Imgproc.Sobel;

public class VolumeMeasureActivity extends AppCompatActivity {

    private static final String TAG = "Volume Measure Activity";
    FragmentManager mFragmentManager;
    CameraPreviewFragment mCamFragment;

    private TextView mDistanceTextView,
            mHeightTextView,
            mTutorialTextView,
            mDistanceAngleTextView,
            mHeightAngleTextView ;
    private ImageView mGuideLine;
    private ImageView mSobelImage;

    private Button mDistanceBtn, mHeightBtn,mResetBtn;
    private Button mCaptureBtn;
    private float mPhoneHeight;

    //Height
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;


    private MeasureHeight mMeasureHeight;


    private double mDistance,mHeight,mWidth;
    private double mCurrentAngle, mSobelAngle;



    private boolean mCheckDistance=true;
    private boolean mCheckHeight=false;




    //Sobel
    private MeasureEdgeBySobel mMeasureEdgeBySobel;

    private Uri mImageUri;

    private Mat cvmRGB,mCorner;

    Bitmap mThumbnail;
    private Point[] mEdge;
    static {
        try {
            System.loadLibrary("opencv_java3");
        } catch (UnsatisfiedLinkError e) {
            Log.v("ERROR", "" + e);
        }
    }
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    System.loadLibrary("nativegray");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_measure);
        Log.i(TAG, "Trying to load OpenCV library");

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mOpenCVCallBack))
        {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }



        initializeHeightMeasureSystem();
        initializeEdgeMeasureSystem();
        initializeView();
    }

    private void initializeView(){
        //set Camera
        mFragmentManager= getSupportFragmentManager();
        mCamFragment=new CameraPreviewFragment();
        mFragmentManager.beginTransaction().replace(R.id.volume_measure_camera_fragment,mCamFragment)
                .commit();

        //set  Views & Btn
        mTutorialTextView = (TextView)findViewById(R.id.tutorial_text);

        mDistanceTextView = (TextView) findViewById(R.id.tv_Bowl_Distance_value);
        mDistanceBtn=(Button)findViewById(R.id.btn_measure_step1);
        mDistanceBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCheckDistance = false;
                mCheckHeight = true;
                mHeightTextView.setTypeface(null, Typeface.NORMAL);
                mTutorialTextView.setText("카메라의 중앙선을 그릇의 상단에 맞추고 Height 버튼을 누르세요");
            }
        });

        mHeightTextView =(TextView)findViewById(R.id.tv_Bowl_Height_value);
        mHeightBtn=(Button) findViewById(R.id.btn_measure_step2);
        mHeightBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCheckHeight = false;
//                mGuideLine.setVisibility(View.VISIBLE);
                mTutorialTextView.setText("가이드 선 안에 그릇을 맞추고 Capture 버튼을 누르세요");

            }
        });

        mResetBtn =(Button)findViewById(R.id.btn_reset);
        mResetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCheckDistance = true;
                mCheckHeight = false;
                mHeightTextView.setTypeface(null, Typeface.ITALIC);
//                mGuideLine.setVisibility(View.INVISIBLE);
                mTutorialTextView.setText("카메라의 중앙선을 그릇의 바닥선에 맞추고 Distance 버튼을 누르세요");

            }
        });

        mCaptureBtn = (Button)findViewById(R.id.btn_measure_step3);
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSobelAngle();
                takePicture_measureEdgeBySobel();
                //edge 데이터 보내고 Acticity 종료

            }
        });


    }

    private void initializeHeightMeasureSystem(){

        //get Phone Height
        SharedPreferences sharedPreferences = getSharedPreferences("setting",0);
        mPhoneHeight=sharedPreferences.getFloat(Constants.ARG_PHONE_HEIGHT,0);

        //make Height Measuring instance
        mMeasureHeight = new MeasureHeight(mPhoneHeight);

        //get Sensor & measure distance & height
        mSensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer =mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                mMeasureHeight.setXYZ(event.values[0], event.values[1], event.values[2]);
                float direction = event.values[1] * -1.0f;
                //mTargetDirection = normalizeDegree(direction);

                if(mCheckDistance)
                {
                    mDistance = mMeasureHeight.getDistance();
                    mDistanceTextView.setText(mDistance + "cm");
                }
                if(mCheckHeight)
                {
                    mHeight = mMeasureHeight.getHeight();
                    mHeightTextView.setText(mHeight + "cm");
                }
                //현재 각도
                mCurrentAngle=mMeasureHeight.getAngle();

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        }, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void initializeEdgeMeasureSystem(){
        mEdge = new Point[4];
        mMeasureEdgeBySobel = new MeasureEdgeBySobel();
        mSobelImage = (ImageView) findViewById(R.id.sobel_image);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mCamFragment=new CameraPreviewFragment();
        mFragmentManager.beginTransaction().replace(R.id.volume_measure_camera_fragment,mCamFragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFragmentManager.beginTransaction().remove(mCamFragment)
                .commit();
    }



////////////////////////////Measure Edges by Sobel//////////////////////////////////////////////////////
    public native int[] cornerDetect(long matAddrSrc, long matAddrCorner);


    public void takePicture_measureEdgeBySobel() {
        mCamFragment.getPreview().takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

                String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss").format( new Date( ));
                String output_file_name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + timeStamp + ".jpeg";

                File pictureFile = new File(output_file_name);
                if (pictureFile.exists()) {
                    pictureFile.delete();
                }

                if(bytes!=null){
                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ExifInterface exif=new ExifInterface(pictureFile.toString());
                        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));

                        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                            bitmap= rotate(bitmap, 90);
                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                            bitmap= rotate(bitmap, 270);
                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                            bitmap= rotate(bitmap, 180);
                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                            bitmap= rotate(bitmap, 90);
                        }

                        boolean bo = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        Log.d("Info", bo + "");

                        fos.close();

                        mThumbnail=bitmap;

                        cvmRGB=new Mat(mThumbnail.getWidth(), mThumbnail.getHeight(), CvType.CV_8UC4);
                        mCorner = new Mat(mThumbnail.getWidth(), mThumbnail.getHeight(), CvType.CV_8UC4);
                        Utils.bitmapToMat(mThumbnail, cvmRGB);


                        int[] edge ;
                        edge = cornerDetect(cvmRGB.getNativeObjAddr(),mCorner.getNativeObjAddr());
                        mEdge[0]=new Point(edge[0],edge[1]);
                        mEdge[1]=new Point(edge[2],edge[3]);
                        mEdge[2]=new Point(edge[4],edge[5]);
                        mEdge[3]=new Point(edge[6],edge[7]);

                        Log.d(TAG,"cvmRGB"+String.valueOf(edge[0])+","+String.valueOf(edge[1])+","+String.valueOf(edge[2])+","+String.valueOf(edge[3])
                                +","+String.valueOf(edge[4])+","+String.valueOf(edge[5])+","+String.valueOf(edge[6])+", "+String.valueOf(edge[7]));
                        Log.d(TAG,"cvmRGB"+cvmRGB.toString());
                        Log.d(TAG,"mThumbnail"+mThumbnail.toString());
                        Log.d(TAG,"mThumbnail width"+mThumbnail.getWidth()+" height"+mThumbnail.getHeight());

                        Utils.matToBitmap(mCorner, mThumbnail);

                        // Sobel 이미지 표시 및 저장
                        mSobelImage.setImageBitmap(mThumbnail);

                        String imageSaveUri = MediaStore.Images.Media.insertImage(getContentResolver(), mThumbnail, "사진 저장", "저장되었습니다.");
                         mImageUri = Uri.parse(imageSaveUri);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mImageUri));

                        Toast.makeText(getApplicationContext(), "찍은 사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                        calculateWidth();
                        sendMeasuredData();

                    }
                    catch (Exception e){
                        Log.e("사진저장","저장실패!",e);
                    }
                }
            }
        });
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private void getSobelAngle(){
        mSobelAngle=mCurrentAngle;
        Log.d(TAG,"Captured Sobel Angle"+String.valueOf(mSobelAngle));
    }

    private void calculateWidth(){
        Log.d(TAG,"Calculate Real Width"+String.valueOf(mSobelAngle));
        //화면상의 그릇 너비 및 높이
        double widthOnScreen,heightOnScreen,heightNormalized;

        heightOnScreen= mEdge[1].y-mEdge[0].y;
//        widthOnScreen= ((mEdge[2].x-mEdge[0].x)+(mEdge[3].x-mEdge[1].x))/2; //위아래 평균
        widthOnScreen =(mEdge[3].x-mEdge[1].x);//아래 너비 값
        heightNormalized = heightOnScreen/Math.sin(Math.toRadians(mSobelAngle));

        mWidth=(mHeight*widthOnScreen)/heightNormalized;
    }

    private void sendMeasuredData( ){
        Intent intent = getIntent();

        //그릇 실제 높이
        intent.putExtra(Constants.ARG_BOWL_HEIGHT,mHeight);
        Log.d(TAG,"sendMeasuredData:: mHeight="+String.valueOf(mHeight));

        //sobel 이미지
        intent.putExtra(Constants.ARG_BOWL_IMAGE_URI,mImageUri);
        Log.d(TAG,"sendMeasuredData:: mImageUri="+String.valueOf(mImageUri));

        //모서리 좌표
        intent.putExtra(Constants.ARG_BOWL_EDGE_LEFT_TOP,mEdge[0].toString());
        intent.putExtra(Constants.ARG_BOWL_EDGE_LEFT_DOWN,mEdge[1].toString());
        intent.putExtra(Constants.ARG_BOWL_EDGE_RIGHT_TOP,mEdge[2].toString());
        intent.putExtra(Constants.ARG_BOWL_EDGE_RIGHT_DOWN,mEdge[3].toString());
        Log.d(TAG,"sendMeasuredData:: mEdge_LEFT_TOP="+mEdge[0].toString());
        Log.d(TAG,"sendMeasuredData:: mEdge_LEFT_DOWN="+mEdge[1].toString());
        Log.d(TAG,"sendMeasuredData:: mEdge_RIGHT_TOP="+mEdge[2].toString());
        Log.d(TAG,"sendMeasuredData:: mEdge_RIGHT_DOWN="+mEdge[3].toString());

        //그릇 실제 너비
        intent.putExtra(Constants.ARG_BOWL_WIDTH,mWidth);
        Log.d(TAG,"sendMeasuredData:: mWidth="+String.valueOf(mWidth));

        //그릇 부피 여기서 계산? AddBowl에서 계산?


        setResult(RESULT_OK,intent);
        finish();
    }


}
