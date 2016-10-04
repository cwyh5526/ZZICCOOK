package com.example.user.zziccook.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.zziccook.Helpers.Enums;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;

public class Tab4Fragment extends Fragment {
    private static final String TAG ="Tab 4 Fragment";
    //private OnPhoneHeightChangeListener mListener;
    TabLayoutActivity myActivity;

    public Tab4Fragment() {
        // Required empty public constructor
    }


    public static Tab4Fragment newInstance() {
        Tab4Fragment fragment = new Tab4Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = (TabLayoutActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab4, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        if(savedInstanceState==null){
            //go to edit the selected client
            myActivity.ReplaceFragment(Enums.FragmentEnums.FavoriteListFragment, 3, 0);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        myActivity.ReplaceFragment(Enums.FragmentEnums.FavoriteListFragment, 3, 0);
    }
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnPhoneHeightChangeListener) {
//            mListener = (OnPhoneHeightChangeListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnPhoneHeightChangeListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }
//
//    public interface OnPhoneHeightChangeListener {
//        void onFragmentInteraction(Uri uri);
//    }
}
