//
// Created by user on 2016-09-05.
//

#include <jni.h>
#include "com_example_user_zziccook_VolumeMeasureActivity.h"
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <stdio.h>
#include <android/log.h>
#include <iostream>
#include <math.h>
#include <opencv/cv.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "libnav", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "libnav", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "libnav", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "libnav", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "libnav", __VA_ARGS__)

using namespace std;
using namespace cv;

static void getThreshold(int, void*);
void DrawEdgeOnRGB(Mat& cvmEdge, Mat cvmRGB);
//int testForContourByCanny(int argc, char** argv);
int* testForContourBySobel(Mat img, Mat& gray);

class Histogram{
public:
	int* array_rows;
	int* array_cols;

	int rows_max_value_up;	//y축상단(row방향)최대값
	int rows_max_Y_up;		//y축상단(row방향)최대값 좌표
	int rows_max_value_down;//y축하단(row방향)최대값
	int rows_max_Y_down;	//y축하단(row방향)최대값 좌표

	int cols_max_value_up;	//x축좌측(col방향)최대값
	int cols_max_X_up;		//x축좌측(col방향)최대값 좌표
	int cols_max_value_down;//x축우측(col방향)최대값
	int cols_max_X_down;	//x축우측(col방향)최대값 좌표

	int rows_bottom_value;	//y축 그릇 바닥 값
	int rows_bottom_Y;		//y축 그릇 바닥 y 좌표

	int arrary_rows_sum;	//y축 평균 내기 위한 sum값.
	int count;

    int	arrary_cols_sum;
	int	countX;
	int	cols_top_left_x;
	int	cols_top_right_x;

public:
	//초기화
	Histogram(){
		rows_max_value_up =0;
		rows_max_Y_up=0;
		rows_max_value_down=0;
		rows_max_Y_down=0;

		cols_max_value_up=0;
		cols_max_X_up=0;
		cols_max_value_down=0;
		cols_max_X_down=0;
		arrary_rows_sum=0;

		rows_bottom_value=0;
		rows_bottom_Y=0;

		arrary_cols_sum=0;
        countX=0;
        cols_top_left_x=0;
        cols_top_right_x=0;
	}

//edge값을 받아 축별edge count
void CountEdgeHistorgam(Mat& cvmEdge, Mat cvmRGB)
{
	arrary_rows_sum=0;
	count=0;
	array_rows = new int[cvmRGB.rows];
	array_cols = new int[cvmRGB.cols];

	//printf("COUNT_HISTOGRAM_ROWS %d \n ",cvmRGB.rows);
	for (int y = 0; y < cvmRGB.rows; y++)
	{
		array_rows[y]=0;

		for (int x = 0; x < cvmRGB.cols; x++)
		{
			unsigned char* pEdge = (cvmEdge.data + (cvmEdge.cols * y) + x);
			if (*pEdge != 0)
			{
				array_rows[y]++;
			}

		}
		if(y>cvmRGB.rows/2&&array_rows[y]!=0){
            arrary_rows_sum+=array_rows[y];//평균값 구하기 위한 sum
            count++;
        }
	}

	for (int x = 0; x < cvmRGB.cols; x++)
	{
		array_cols[x]=0;

		for (int y = 0; y < cvmRGB.rows; y++)
		{
			unsigned char* pEdge = (cvmEdge.data + (cvmEdge.cols * y) + x);
			if (*pEdge != 0)
			{
				array_cols[x]++;
			}
		}
		if(array_cols[x]!=0){
        	arrary_cols_sum+=array_cols[x];//평균값 구하기 위한 sum
        	countX++;
        }
		//printf("%d, ",array_cols[x]);
	}

}

//count 한 값으로 histogram drawing
void DrawEdgeHistogram(Mat cvmRGB,int  color){

	for (int y = 0; y < cvmRGB.rows; y++)
   {
	  // line(cvmRGB,Point(0,y),Point(array_rows[y],y), Scalar(0,0,color));
	}

	//cols 방향 histogram
   for (int x = 0; x < cvmRGB.cols; x++)
   {
	   // line(cvmRGB,Point(x,0),Point(x,array_cols[x]), Scalar(0,0,color));
	}
}

//각 축별 최대값 찾기
void FindMaxCoord(Mat cvmRGB,int  color){

	for (int y = 0; y < cvmRGB.rows; y++)
   {
	   if(y<(cvmRGB.rows/2))
	   {
		  if(array_rows[y]>rows_max_value_up){
			rows_max_value_up=array_rows[y];
			rows_max_Y_up=y;
		}

	   }else{
		   if(array_rows[y]>rows_max_value_down){
		   	rows_max_value_down=array_rows[y];
			rows_max_Y_down=y;
		   }
	   }
	}

	//cols 방향 histogram
   for (int x = 0; x < cvmRGB.cols; x++)
   {
	    if(x<(cvmRGB.cols/2))
	   {
		 if(array_cols[x]>cols_max_value_up)
		 {
		   cols_max_value_up=array_cols[x];
		   cols_max_X_up=x;
		 }

	   }else{
		   if(array_cols[x]>cols_max_value_down)
		 {
		   cols_max_value_down=array_cols[x];
		   cols_max_X_down=x;
		 }
	   }
	}

}

//y축 하단 그릇 bottom찾기
//평균*3을 threshold로 두고 그 이상인 좌표시작과 끝을 찾고
//그 중점을 찾음.
void FindBottomY(Mat cvmRGB){
	int first=0;
	int last=0;


	for (int y = 0; y < cvmRGB.rows; y++)
	{
		if(y>(cvmRGB.rows/2)&&array_rows[y]>arrary_rows_sum/count)
		{
			first = y;
			//printf("%d ",first);
			//line(cvmRGB,Point(0,first),Point(cvmRGB.cols,first),Scalar(0,0,0));//first
			break;

		}
	}

	for (int y = first; y < cvmRGB.rows; y++)
	{
		if(array_rows[y]>arrary_rows_sum/count)
		{
			last = y;
			//printf("%d ",last);

		}
	}
	//line(cvmRGB,Point(0,last),Point(cvmRGB.cols,last),Scalar(0,0,0));	//last
	//line(cvmRGB,Point(0,(first+last)/2),Point(cvmRGB.cols,(first+last)/2),Scalar(0,0,255)); //중간

	rows_bottom_Y=(first+last)/2;
	rows_bottom_value=array_rows[rows_bottom_Y];
}


void DrawEdge(Mat& cvmRGB, int color){
   //circle(cvmRGB,Point(cols_max_X_up,rows_max_Y_up),10,Scalar(0,0,color),3);
  // circle(cvmRGB,Point(cols_max_X_up,rows_bottom_Y),10,Scalar(0,0,color),3);

  // circle(cvmRGB,Point(cols_max_X_down,rows_max_Y_up),10,Scalar(0,0,color),3);
  // circle(cvmRGB,Point(cols_max_X_down,rows_bottom_Y),10,Scalar(0,0,color),3);
}

void FindTopX(Mat cvmRGB){
	int first=0;
	int last=0;


	for (int x = 0; x < cvmRGB.cols; x++)
	{
		if(x<(cvmRGB.cols/2)&&array_cols[x]>arrary_cols_sum/countX)
		{
			first = x;
			//printf("%d ",first);
			//cv::line(cvmRGB,cv::Point(first,0),cv::Point(first,cvmRGB.rows),cv::Scalar::all(0));//first
			break;
		}
	}

	for (int x = first; x < cvmRGB.cols; x++)
	{
		if(array_cols[x]>arrary_cols_sum/countX)
		{
			last = x;
			//printf("%d ",last);

		}
	}
	//cv::line(cvmRGB,cv::Point(last,0),cv::Point(last,cvmRGB.rows),cv::Scalar::all(0));	//last

	//cv::line(cvmRGB,cv::Point(0,arrary_cols_sum/countX),cv::Point(cvmRGB.rows,arrary_cols_sum/countX),cv::Scalar::all(255)); //평균

	cols_top_left_x=first;
	cols_top_right_x=last;

}
};




extern "C" {

JNIEXPORT jintArray JNICALL Java_com_example_user_zziccook_VolumeMeasureActivity_cornerDetect(JNIEnv *env, jobject, jlong addrSrc, jlong addrCorner);

JNIEXPORT jintArray JNICALL Java_com_example_user_zziccook_VolumeMeasureActivity_cornerDetect(JNIEnv *env, jobject, jlong addrSrc, jlong addrCorner) {

    LOGI("jni");
    Mat& mRgb = *(Mat*)addrSrc;
    Mat& mGray = *(Mat*)addrCorner;

    jintArray result  = (env)->NewIntArray(8);
    int* sobelResult = testForContourBySobel(mRgb, mGray);

    jint *cintArray = new jint[8];
    for (int i=0;i<8;i++){
     cintArray[i]= (jint)sobelResult[i];
    }
    env->SetIntArrayRegion(result,0,8,cintArray);

    delete [] cintArray;

return result;



   // int conv;
    //jint retVal;
    //jintArray retVal;

   // LOGI("before");
   // conv = testForContourBySobel(mRgb, mGray);

   // return retVal;

    /*
    LOGI("after");
    retVal = (jint)conv;

    return retVal;
    */

}

}

/*int corner(Mat img, Mat& gray){

    cvtColor(img, gray, CV_BGR2GRAY);	// 이미지 흑백화
    //Canny(gray,gray,50,300,3);
    vector<Point2f> corners;		// 검출할 꼭지점 Point
    goodFeaturesToTrack(gray, corners, 1000, 0.01, 20,noArray(),3, false, 0.04);	//윤곽선 검출 함수

    for(int i=0; i<corners.size() ; i++){
        circle(gray, corners[i], 2, Scalar(255));
        LOGI("JNI log : i = %.f, j = %.f",corners[i].x,corners[i].y);
     }

    return(0);
}


int testForContourByCanny(int argc, char** argv)
{
	char szBuf[512];

	Mat cvmRGB, cvmGray, cvmEdge;

	//for (int i = 1; i <= 12; i++)
	for (int i = 6483; i <= 6490; i++)
	{
		//sprintf_s(szBuf, 512, "%d.jpg", i);
		sprintf_s(szBuf, 512, "IMG_%d.jpg", i);
		cvmRGB = imread(szBuf, CV_LOAD_IMAGE_ANYDEPTH | CV_LOAD_IMAGE_ANYCOLOR);
		cvtColor(cvmRGB, cvmGray, CV_RGB2GRAY);

		int nLowTh, nHighTh;
		namedWindow("Edge", WINDOW_AUTOSIZE);

		createTrackbar("min threshold", "Edge", &nLowTh, 300, getThreshold);
		setTrackbarPos("min threshold", "Edge", 50);

		createTrackbar("max threshold", "Edge", &nHighTh, 300, getThreshold);
		setTrackbarPos("max threshold", "Edge", 150);

		Histogram cannyHisto;

		while(1)
		{
			nLowTh = getTrackbarPos("min threshold", "Edge");
			nHighTh = getTrackbarPos("max threshold", "Edge");

			blur(cvmGray, cvmEdge, Size(3, 3));

	//		Canny(cvmEdge, cvmEdge, 167, 300, 5);/nLowThnHighTh

			cannyHisto.CountEdgeHistorgam(cvmEdge,cvmRGB);
			cannyHisto.DrawEdgeHistogram(cvmRGB,0);
			cannyHisto.FindMaxCoord(cvmRGB,0);
			cannyHisto.FindBottomY(cvmRGB);
			cannyHisto.DrawEdge(cvmRGB,255);

			DrawEdgeOnRGB(cvmEdge, cvmRGB);


			imshow("Edge", cvmEdge);
			imshow("RGB", cvmRGB);


			int nKey = waitKey(1);
			if(nKey == 'q') break;
		}
	}

	return 0;
}
*/
int* testForContourBySobel(Mat img, Mat& gray)
{
  LOGI("1");
    int* edge= new int[8];

	char szBuf[512];
	float fScale = 1.0f;
	int nDelta = 0;

	Mat cvmRGB, cvmGray, cvmSobleY;
	Mat cvmSobelImage;
	Mat cvmSobelImage_vertical;
	Mat cvmSobelImage_horizontal;

//	for (int i = 1; i <= 16; i++)
	//for (int i = 6483; i <= 6490; i++)
//	{
		//sprintf_s(szBuf, 512, "%d.jpg", i);
		// sprintf_s(szBuf, 512, "IMG_%d.jpg", i);

		//cvmRGB = imread(szBuf, CV_LOAD_IMAGE_ANYDEPTH | CV_LOAD_IMAGE_ANYCOLOR);
		cvmRGB = img.clone();
  LOGI("2");
		blur(cvmRGB, cvmRGB, Size(3, 3));

		cvtColor(cvmRGB, cvmGray, CV_RGB2GRAY);
		//imshow("RGB", cvmRGB);

		//sprintf_s(szBuf, 512, "IMG_%d.jpg", i);

		Mat cvmGradX, cvmGradY;
		Mat cvmAbsGradX, cvmAbsGradY;
		//cvmGray.depth();

		Sobel(cvmGray, cvmGradX, CV_32F, 1, 0, 3, fScale, nDelta, BORDER_DEFAULT);
		convertScaleAbs(cvmGradX, cvmAbsGradX);
  LOGI("3");
		Sobel(cvmGray, cvmGradY, CV_32F, 0, 1, 3, fScale, nDelta, BORDER_DEFAULT);
		convertScaleAbs(cvmGradY, cvmAbsGradY);

		// 1. vertical direction edge
		//addWeighted(cvmAbsGradX, 1.0, cvmAbsGradY, 0, 0, cvmSobelImage);
		addWeighted(cvmAbsGradX, 1.0, cvmAbsGradY, 0, 0, cvmSobelImage_vertical);
		// 2. horizontal direction edge
		//addWeighted(cvmAbsGradX, 0, cvmAbsGradY, 1, 0, cvmSobelImage);
		addWeighted(cvmAbsGradX, 0, cvmAbsGradY, 1, 0, cvmSobelImage_horizontal);
		// 3. Combined edge
		addWeighted(cvmAbsGradX, 0.5, cvmAbsGradY, 0.5, 0, cvmSobelImage);
  LOGI("4");
		//namedWindow("Sobel Edge", WINDOW_AUTOSIZE);

		int nLowTh = 15;
		//createTrackbar("threshold", "Sobel Edge", &nLowTh, 255, getThreshold);

		Histogram sobelHisto;
		Histogram sobelHisto_ver;
		Histogram sobelHisto_ho;

		for (int i=0; i<5000; i++)
		{
		//  LOGI("5");
			nLowTh = 15;
			//getTrackbarPos("threshold", "Sobel Edge");

			Mat cvmBinary;
			Mat cvmBinary_vertical;
			Mat cvmBinary_horizontal;

			threshold(cvmSobelImage, cvmBinary, nLowTh, 255, THRESH_BINARY);
			threshold(cvmSobelImage_vertical, cvmBinary_vertical, nLowTh, 255, THRESH_BINARY);
			threshold(cvmSobelImage_horizontal, cvmBinary_horizontal, nLowTh, 255, THRESH_BINARY);

			//imshow("Sobel Edge", cvmSobelImage);

			sobelHisto.CountEdgeHistorgam(cvmBinary,cvmRGB);
			sobelHisto.DrawEdgeHistogram(cvmRGB,0);
			sobelHisto.FindMaxCoord(cvmRGB,0);

			sobelHisto_ver.CountEdgeHistorgam(cvmBinary_vertical,cvmRGB);
			sobelHisto_ver.DrawEdgeHistogram(cvmRGB,100);
			sobelHisto_ver.FindMaxCoord(cvmRGB,0);
            sobelHisto_ver.FindTopX(cvmRGB);

			sobelHisto_ho.CountEdgeHistorgam(cvmBinary_horizontal,cvmRGB);
			sobelHisto_ho.DrawEdgeHistogram(cvmRGB,255);
			sobelHisto_ho.FindMaxCoord(cvmRGB,0);
			sobelHisto_ho.FindBottomY(cvmRGB);


			circle(cvmRGB,Point(sobelHisto_ver.cols_top_left_x,sobelHisto_ho.rows_max_Y_up),10, Scalar(255),3);
			LOGI("X_UP: %d, Y_UP :%d",sobelHisto_ver.cols_top_left_x,sobelHisto_ho.rows_max_Y_up);
			edge[0]=sobelHisto_ver.cols_top_left_x;
			edge[1]=sobelHisto_ho.rows_max_Y_up;

			circle(cvmRGB,Point(sobelHisto_ver.cols_max_X_up,sobelHisto_ho.rows_bottom_Y),10, Scalar(255),3);
            LOGI("X_UP: %d, Bottom_Y :%d",sobelHisto_ver.cols_max_X_up,sobelHisto_ho.rows_bottom_Y);
            edge[2]=sobelHisto_ver.cols_max_X_up;
            edge[3]=sobelHisto_ho.rows_bottom_Y;

			circle(cvmRGB,Point(sobelHisto_ver.cols_top_right_x,sobelHisto_ho.rows_max_Y_up),10, Scalar(255),3);
			LOGI("X_down: %d, Y_UP :%d",sobelHisto_ver.cols_top_right_x,sobelHisto_ho.rows_max_Y_up);
			edge[4]=sobelHisto_ver.cols_top_right_x;
			edge[5]=sobelHisto_ho.rows_max_Y_up;

			circle(cvmRGB,Point(sobelHisto_ver.cols_max_X_down,sobelHisto_ho.rows_bottom_Y),10, Scalar(255),3);
			LOGI("X_down: %d, Bottom_UP :%d",sobelHisto_ver.cols_max_X_down,sobelHisto_ho.rows_bottom_Y);
			edge[6]=sobelHisto_ver.cols_max_X_down;
			edge[7]=sobelHisto_ho.rows_bottom_Y;
/*
			printf("(xUp,yUp)=(%d,%d)\n",sobelHisto_ver.cols_max_X_up,sobelHisto_ho.rows_max_Y_up);
			printf("(xUp,yDown)=(%d,%d)\n",sobelHisto_ver.cols_max_X_up,sobelHisto_ho.rows_bottom_Y);
			printf("(xDown,yUp)=(%d,%d)\n",sobelHisto_ver.cols_max_X_down,sobelHisto_ho.rows_max_Y_up);
			printf("(xDown,yDown)=(%d,%d)\n",sobelHisto_ver.cols_max_X_down,sobelHisto_ho.rows_bottom_Y);

*/
			/*
			DrawEdgeHistorgamOnRGB(cvmBinary,cvmBinary,255);
			DrawEdgeHistorgamOnRGB(cvmBinary_vertical,cvmBinary_vertical,255);
			DrawEdgeHistorgamOnRGB(cvmBinary_horizontal,cvmBinary_horizontal,100);
			*/

			//imshow("RGB", cvmRGB);

			//imshow("Binary Edge", cvmBinary);
			//imshow("Binary Edge Vertical", cvmBinary_vertical);
			//imshow("Binary Edge Horizontal", cvmBinary_horizontal);

			//int nKey = waitKey(1);

			gray = cvmBinary.clone();

 // LOGI("6");
			//if (nKey == 'q') break;
			break;
		}
	//}
  //LOGI("7");
	return edge;
}

static void getThreshold(int, void*)
{
	// do nothing
}

void DrawEdgeOnRGB(Mat& cvmEdge, Mat cvmRGB)
{
	for (int y = 0; y < cvmRGB.rows; y++)
	{
		for (int x = 0; x < cvmRGB.cols; x++)
		{
			unsigned char* pEdge = (cvmEdge.data + (cvmEdge.cols * y) + x);
			if (*pEdge != 0)
			{
				cvmRGB.at<Vec3b>(y, x)[0] = 0;
				cvmRGB.at<Vec3b>(y, x)[1] = 255;
				cvmRGB.at<Vec3b>(y, x)[2] = 0;
			}
		}
	}

}



   /*Mat tmp = img.clone();
    cvtColor(img, tmp, CV_RGBA2GRAY);
    // Canny(gray,gray,100,300,3);
    Mat dst, dst_norm, dst_norm_scaled;
    dst = Mat::zeros(img.size(), CV_32FC1);


    // Detecting corners
    cornerHarris(tmp, dst, 2, 3, 0.04, BORDER_DEFAULT);

    // Normalizing
    normalize( dst, dst_norm, 0, 255, NORM_MINMAX, CV_32FC1, Mat() );
    convertScaleAbs( dst_norm, dst_norm_scaled );

    //   Drawing a circle around corners
    for( int j = 0; j < dst_norm.rows ; j++ )
    { for( int i = 0; i < dst_norm.cols; i++ )
        {
            if( (int) dst_norm.at<float>(j,i) > 200 )
            {
                circle(gray, Point( i, j ), 1, Scalar(0,0,255),1,8,0);
                LOGI("JNI log : i = %d, j = %d",i,j);

                //http://cinema4dr12.tistory.com/entry/OpenCV-Harrison-Corner-Detector
            }

        }
    }
 /*
*/
