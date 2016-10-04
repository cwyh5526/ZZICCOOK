package com.example.user.zziccook.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.zziccook.Helpers.Enums;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;

public class Tab1Fragment extends Fragment {
    private static final String TAG = "First Tab Fragment";
 private OnFragmentInteractionListener mListener;
    TabLayoutActivity myActivity;

    public Tab1Fragment() {
        // Required empty public constructor
    }

      public static Tab1Fragment newInstance() {
        Tab1Fragment fragment = new Tab1Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"TAB1 onCreate!!!");

        super.onCreate(savedInstanceState);

        myActivity= (TabLayoutActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG,"TAB1 onCreateView!!!");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab1, null);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Log.i(TAG,"TAB1 onViewCreated!!!");

    }


    @Override
    public void onResume() {
        Log.i(TAG,"TAB1 onRESUME!!!");
        super.onResume();

        myActivity.ReplaceFragment(Enums.FragmentEnums.MeasureFragment, 3, 0);
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG,"TAB1 onAttach!!!");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhoneHeightChangeListener");
        }
    }

    @Override
    public void onDetach() {
        Log.i(TAG,"TAB1 onDetach!!!");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        Log.i(TAG,"TAB1 ONPAUSE!!!");
        super.onPause();

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
