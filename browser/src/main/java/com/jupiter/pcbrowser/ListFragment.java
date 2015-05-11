package com.jupiter.pcbrowser;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.jupiter.Adapter.NewsListItemAdapter;
import com.jupiter.model.CachedNews;

import com.jupiter.model.NewsListItem;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.orm.SugarRecord;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private SwipyRefreshLayout mSwipyRefreshLayout;

    private NewsListItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<NewsListItem> myDataset;

    private String category;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.myDataset = new ArrayList<NewsListItem>();
        return fragment;
    }

    public ListFragment(){
        myDataset = new ArrayList<NewsListItem>();
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.newslist);
        //mRecyclerView.addOnItemTouchListener();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

        mAdapter = new NewsListItemAdapter(myDataset);
        mAdapter.setParent(this);
        mRecyclerView.setAdapter(mAdapter);



        mSwipyRefreshLayout = (SwipyRefreshLayout) root.findViewById(R.id.swipe_container);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                updateNewsList(direction == SwipyRefreshLayoutDirection.TOP);
            };
        });
        loadNewsList();
        return root;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onItemClick(Bundle param);
    }

    public void setListItems(ArrayList<NewsListItem> myDataset){
        this.myDataset = myDataset;
    }

    public void onItemClick(Bundle param){
        mListener.onItemClick(param);
    }


    public void clearListItem(){
        this.myDataset.clear();
    }
    public void loadNewsList(){
        //Log.d("ListFragment","load news list "+this.category);
        List<CachedNews> news = CachedNews.
                findWithQuery(CachedNews.class,
                        "Select * from "+ SugarRecord.getTableName(CachedNews.class) +" where category like ? order by id desc limit ? ", category, "30");
        //Log.d("ListFragment", Integer.toString(news.size()));
        this.clearListItem();
        for(int i=0;i<news.size();i++ ) {
            CachedNews item = news.get(i);
            item.setSent(true);
            item.save();
            //Log.d("Updated", item.getCategory());
            this.myDataset.add(new NewsListItem(item.getTitle(),
                    item.getCreateDate(), item.getDescription(),
                    item.getId(),item.isViewed(),item.getContent(),item.getStaticURL(),item.getCategory(),item.getPublishDate()));
        }
        this.mAdapter.notifyDataSetChanged();
    }
    public void updateNewsList(boolean top) {
        //Log.d("ListFragment", "update news list " + this.category);
        List<CachedNews> news;
        if(this.myDataset.size()==0){
            String last_id = "0" ;
            news = CachedNews.findWithQuery(CachedNews.class,
                    "Select * from " + SugarRecord.getTableName(CachedNews.class) +
                            " where id > "+last_id+" and category like ? order by id desc limit ? ",
                    category, "20");
        }
        else if(top) {
            String last_id = Long.toString(this.myDataset.
                    get(0).getId());
             news = CachedNews.findWithQuery(CachedNews.class,
                            "Select * from " + SugarRecord.getTableName(CachedNews.class) +
                                    " where id > "+last_id+" and category like ? order by id desc limit ? ",
                            category, "20");

        } else {
            String last_id = Long.toString(this.myDataset.
                    get(this.myDataset.size()-1).getId());
            news = CachedNews.findWithQuery(CachedNews.class,
                            "Select * from " + SugarRecord.getTableName(CachedNews.class) +
                                    " where id < "+ last_id +" and category like ? order by id asc limit ? ",
                            category, "20");

        }
        //Log.d("ListFragment",Integer.toString(news.size()));
        for(int i=news.size()-1;i>=0;i-- ) {
            CachedNews item = news.get(i);
            item.setSent(true);
            item.save();
            //Log.d("Updated",item.getCategory());
            if( top ) {
                this.myDataset.add(0, new NewsListItem(item.getTitle(),
                        item.getCreateDate(), item.getDescription(),
                        item.getId(), item.isViewed(), item.getContent(), item.getStaticURL(), item.getCategory(),item.getPublishDate()));
            }else{
                this.myDataset.add(new NewsListItem(item.getTitle(),
                        item.getCreateDate(), item.getDescription(),
                        item.getId(), item.isViewed(), item.getContent(), item.getStaticURL(), item.getCategory(),item.getPublishDate()));
            }
        }

        mAdapter.notifyDataSetChanged();
        mSwipyRefreshLayout.setRefreshing(false);

    }
    public void setCategory(String category){
        this.category = category;
        if(this.mAdapter != null){
            this.loadNewsList();
        }
    }
}
