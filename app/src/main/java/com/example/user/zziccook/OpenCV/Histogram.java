package com.example.user.zziccook.OpenCV;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

/**
 * Created by user on 2016-10-02.
 */
public class Histogram {
    public int[] array_rows;
    public int[] array_cols;

    public int rows_max_value_up;	//y축상단(row방향)최대값
    public int rows_max_Y_up;		//y축상단(row방향)최대값 좌표
    public int rows_max_value_down;//y축하단(row방향)최대값
    public int rows_max_Y_down;	//y축하단(row방향)최대값 좌표

    public int cols_max_value_up;	//x축좌측(col방향)최대값
    public  int cols_max_X_up;		//x축좌측(col방향)최대값 좌표
    public  int cols_max_value_down;//x축우측(col방향)최대값
    public int cols_max_X_down;	//x축우측(col방향)최대값 좌표

    public int rows_bottom_value;	//y축 그릇 바닥 값
    public int rows_bottom_Y;		//y축 그릇 바닥 y 좌표

    public int arrary_rows_sum;	//y축 평균 내기 위한 sum값.
    public  int count;

    public  int	arrary_cols_sum;
    public  int	countX;
    public   int	cols_top_left_x;
    public  int	cols_top_right_x;

    public Histogram(){
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
   public void CountEdgeHistorgam(Mat cvmEdge, Mat cvmRGB)
    {
        arrary_rows_sum=0;
        count=0;
        array_rows = new int[cvmRGB.rows()];
        array_cols = new int[cvmRGB.cols()];

        //printf("COUNT_HISTOGRAM_ROWS %d \n ",cvmRGB.rows);
        for (int y = 0; y < cvmRGB.rows(); y++)
        {
            array_rows[y]=0;

            for (int x = 0; x < cvmRGB.cols(); x++)
            {
                long pEdge = (cvmEdge.dataAddr() + (cvmEdge.cols() * y) + x);
                if (pEdge != 0)
                {
                    array_rows[y]++;
                }

            }
            if(y>cvmRGB.rows()/2&&array_rows[y]!=0){
                arrary_rows_sum+=array_rows[y];//평균값 구하기 위한 sum
                count++;
            }
            //printf("%d, ",array_rows[y]);
        }

        for (int x = 0; x < cvmRGB.cols(); x++)
        {
            array_cols[x]=0;

            for (int y = 0; y < cvmRGB.rows(); y++)
            {
                long pEdge = (cvmEdge.dataAddr() + (cvmEdge.cols() * y) + x);
                if (pEdge != 0)
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



    //각 축별 최대값 찾기
    public  void FindMaxCoord(Mat cvmRGB,int  color){

        for (int y = 0; y < cvmRGB.rows(); y++)
        {
            if(y<(cvmRGB.rows()/2))
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
        for (int x = 0; x < cvmRGB.cols(); x++)
        {
            if(x<(cvmRGB.cols()/2))
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
    public  void FindBottomY(Mat cvmRGB){
        int first=0;
        int last=0;


        for (int y = 0; y < cvmRGB.rows(); y++)
        {
            if(y>(cvmRGB.rows()/2)&&array_rows[y]>arrary_rows_sum/count)
            {
                first = y;
                //printf("%d ",first);

                break;
            }
        }

        for (int y = first; y < cvmRGB.rows(); y++)
        {
            if(array_rows[y]>arrary_rows_sum/count)
            {
                last = y;
                //printf("%d ",last);

            }
        }

        rows_bottom_Y=(first+last)/2;
        rows_bottom_value=array_rows[rows_bottom_Y];
    }

    public void FindTopX(Mat cvmRGB){
        int first=0;
        int last=0;


        for (int x = 0; x < cvmRGB.cols(); x++)
        {
            if(x<(cvmRGB.cols()/2)&&array_cols[x]>arrary_cols_sum/countX)
            {
                first = x;
                break;
            }
        }

        for (int x = first; x < cvmRGB.cols(); x++)
        {
            if(array_cols[x]>arrary_cols_sum/countX)
            {
                last = x;
                //printf("%d ",last);

            }
        }

        cols_top_left_x=first;
        cols_top_right_x=last;

    }



}
