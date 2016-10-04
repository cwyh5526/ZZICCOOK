package com.example.user.zziccook.Fragment.Bowl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.zziccook.Adapter.BowlAdapter;
import com.example.user.zziccook.Data.DatabaseHelper;
import com.example.user.zziccook.Helpers.Constants;
import com.example.user.zziccook.Helpers.Enums;
import com.example.user.zziccook.Model.Bowl;
import com.example.user.zziccook.R;
import com.example.user.zziccook.TabLayoutActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class BowlListFragment extends Fragment  {
    private static final String TAG="BowlList Fragment";
    private List<Bowl> mBowls;
    private ListView mBowlListView;
    private BowlAdapter mAdapter;

    private FloatingActionButton mAddBtn;
    private Button mAddBowlBtn;

    private View mRootView;
    private DatabaseHelper db;

    private String mListMode;

    private OnBowlSelectedListener mListener;

    public static BowlListFragment newInstance(String mode,int sectionNumber) {
        BowlListFragment fragment = new BowlListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        args.putString(Constants.ARG_LIST_MODE,mode);
        fragment.setArguments(args);
        return fragment;
    }


    public BowlListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        db = new DatabaseHelper(getActivity());
        Bundle args = getArguments();
        mListMode=args.getString(Constants.ARG_LIST_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_bowl_list, container, false);

        return mRootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        InitializeViews();
        LoadListData();
    }

    private void LoadListData()
    {
        //First clear the adapter of any Bowl it has
        mAdapter.Update();

        //Get the list of Bowls from the database
        mBowls = db.getAllBowls();
        Log.d(TAG,"ALL BOWLS");


        if (mBowls != null){
            for (Bowl bowl: mBowls){
                mAdapter.add(bowl);
            }
        }

    }

    private void InitializeViews() {

        if(mListMode.equals(Constants.BOWL_LIST)) {
            mAddBowlBtn = (Button) mRootView.findViewById(R.id.btn_AddBowl);
            mAddBowlBtn.setVisibility(View.VISIBLE);
            mAddBowlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayoutActivity myActivity = (TabLayoutActivity) getActivity();
                    myActivity.ReplaceFragment(Enums.FragmentEnums.BowlAddFragment, 3, 0);
                }
            });
        }

        mBowlListView = (ListView) mRootView.findViewById(R.id.bowl_list);
        mBowls = new ArrayList<Bowl>();
        mAdapter = new BowlAdapter(getActivity(), mBowls);


        mBowlListView.setAdapter(mAdapter);
        TextView emptyText = (TextView) mRootView.findViewById(R.id.bowl_list_empty);

        mBowlListView.setEmptyView(emptyText);

        mBowlListView.setItemsCanFocus(true);
        mBowlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bowl tempBowl = mBowls.get(position);
                Log.d(TAG, "BOWL LIST ITEM CLICKED :: " + tempBowl.getId() + " " + tempBowl.getName());

                if(mListMode.equals(Constants.BOWL_LIST)) {
                     TabLayoutActivity myActivity = (TabLayoutActivity) getActivity();
                    myActivity.ReplaceFragment(Enums.FragmentEnums.BowlDetailsFragment, 3, tempBowl.getId());
                }else if(mListMode.equals(Constants.BOWL_SELECT_LIST)){
                    mListener.onBowlSelected(tempBowl);

                }

            }
        });
        mBowlListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int positionToRemove = position;

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete "+ mBowls.get(positionToRemove).getName());

                adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeBowl(mBowls.get(positionToRemove));
                        mAdapter.notifyDataSetChanged();//리스트 데이터 바뀐것 notify
                        LoadListData();//바뀐 리스트데이터 다시 로드
                    }});
                adb.setNegativeButton("Cancel",null);
                adb.show();
                return true;
            }
        });

//        mAddBtn = (FloatingActionButton) mRootView.findViewById(R.id.fabtn_addBowl);
    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.customer_list_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id){
//            case R.id.action_add_customer:
//                MainActivity myActivity = (MainActivity)getActivity();
//                myActivity.ReplaceFragment(Enums.FragmentEnums.CustomerDetailsFragment, 3, 0);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnBowlSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
//        ((TabLayoutActivity) activity).onSectionAttached(
//                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    public interface OnBowlSelectedListener {

        void onBowlSelected(Bowl bowl);
    }

}
