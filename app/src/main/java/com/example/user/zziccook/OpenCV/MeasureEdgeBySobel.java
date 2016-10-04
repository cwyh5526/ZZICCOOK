package com.example.user.zziccook.OpenCV;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.user.zziccook.Fragment.Measure.MeasureFragment;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

/**
 * Created by user on 2016-10-02.
 */
public class MeasureEdgeBySobel {
    public static final String TAG="Measure Edge By Sobel";
    //    char szBuf[512];
    float fScale = 1.0f;
    int nDelta = 0;

    private Mat cvmRGB, cvmGray, cvmSobleY;
    private Mat cvmSobelImage;
    private Mat cvmSobelImage_vertical;
    private Mat cvmSobelImage_horizontal;
    Mat cvmGradX ,cvmGradY ;
    Mat cvmAbsGradX, cvmAbsGradY;

    Histogram sobelHisto;
    Histogram sobelHisto_ver;
    Histogram sobelHisto_ho;

    Mat cvmBinary;
    Mat cvmBinary_vertical;
    Mat cvmBinary_horizontal;
    Bitmap mThumbnail;


    public Point[] measureEdgeBySobel(Mat src){
        cvmRGB =src;
        cvmGray=new Mat();
        cvmGradX= new Mat();
        cvmGradY= new Mat();
        cvmAbsGradX= new Mat();
        cvmAbsGradY= new Mat();
        cvmSobelImage_vertical = new Mat();
        cvmSobelImage_horizontal =new Mat();
        cvmSobelImage= new Mat();
        cvmBinary = new Mat();
        cvmBinary_vertical= new Mat();
        cvmBinary_horizontal=new Mat();

        sobelHisto = new Histogram();
        sobelHisto_ver = new Histogram();
        sobelHisto_ho = new Histogram();



        Imgproc.blur(cvmRGB,cvmRGB,new Size(3,3));
        Imgproc.cvtColor(cvmRGB,cvmGray,Imgproc.COLOR_RGB2GRAY);

        Log.d(TAG,"");

        Imgproc.Sobel(cvmGray, cvmGradX, CvType.CV_16S, 1, 0, 3, fScale, nDelta, Imgproc.BORDER_DEFAULT);
        Core.convertScaleAbs(cvmGradX, cvmAbsGradX);

        Imgproc.Sobel(cvmGray, cvmGradY, CvType.CV_16S, 0, 1, 3, fScale, nDelta, Imgproc.BORDER_DEFAULT);
       Core.convertScaleAbs(cvmGradY, cvmAbsGradY);

// 1. vertical direction edge
        //cv::addWeighted(cvmAbsGradX, 1.0, cvmAbsGradY, 0, 0, cvmSobelImage);
        Core.addWeighted(cvmAbsGradX, 1.0, cvmAbsGradY, 0, 0, cvmSobelImage_vertical);
        // 2. horizontal direction edge
        //cv::addWeighted(cvmAbsGradX, 0, cvmAbsGradY, 1, 0, cvmSobelImage);
        Core.addWeighted(cvmAbsGradX, 0, cvmAbsGradY, 1, 0, cvmSobelImage_horizontal);
        // 3. Combined edge
        Core.addWeighted(cvmAbsGradX, 0.5, cvmAbsGradY, 0.5, 0, cvmSobelImage);

        int nLowTh = 15;

        Imgproc.threshold(cvmSobelImage, cvmBinary, nLowTh, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(cvmSobelImage_vertical, cvmBinary_vertical, nLowTh, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(cvmSobelImage_horizontal, cvmBinary_horizontal, nLowTh, 255,Imgproc.THRESH_BINARY);


        sobelHisto.CountEdgeHistorgam(cvmBinary,cvmRGB);
        //sobelHisto.DrawEdgeHistogram(cvmRGB,0);
        sobelHisto.FindMaxCoord(cvmRGB,0);

        sobelHisto_ver.CountEdgeHistorgam(cvmBinary_vertical,cvmRGB);
        //sobelHisto_ver.DrawEdgeHistogram(cvmRGB,0);
        sobelHisto_ver.FindMaxCoord(cvmRGB,0);
        sobelHisto_ver.FindTopX(cvmRGB);

        sobelHisto_ho.CountEdgeHistorgam(cvmBinary_horizontal,cvmRGB);
        //sobelHisto_ho.DrawEdgeHistogram(cvmRGB,255);
        sobelHisto_ho.FindMaxCoord(cvmRGB,0);
        sobelHisto_ho.FindBottomY(cvmRGB);

        Point[] result= new Point[4];
        result[0]= new Point(sobelHisto_ver.cols_top_left_x,sobelHisto_ho.rows_max_Y_up);
        result[1]= new Point(sobelHisto_ver.cols_max_X_up,sobelHisto_ho.rows_bottom_Y);
        result[2]= new Point( sobelHisto_ver.cols_top_right_x,sobelHisto_ho.rows_max_Y_up);
        result[3]= new Point(sobelHisto_ver.cols_max_X_down,sobelHisto_ho.rows_bottom_Y);

        return result;

    }


}
