package com.jupiter.Adapter;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jupiter.model.CachedNews;
import com.jupiter.model.NewsListItem;
import com.jupiter.pcbrowser.*;

import java.util.List;

/**
 * Created by lovew_000 on 2015/5/5.
 */
public class NewsListItemAdapter extends RecyclerView.Adapter<NewsListItemAdapter.NewsListItemHolder> {

    private List<NewsListItem> newsList;
    private ListFragment parent;
    public NewsListItemAdapter(List<NewsListItem> newsList){
        this.newsList = newsList;
    }
    public static class NewsListItemHolder extends RecyclerView.ViewHolder{
        protected TextView  vTitle;
        protected TextView vTimestamp;
        protected TextView vDescription;
        private Context context;

        public NewsListItem currentItem;

        public NewsListItemHolder(View v, final Context context, final ListFragment parent){
            super(v);
            this.context = context;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CachedNews news = CachedNews.findById(CachedNews.class, currentItem.getId());
                    news.setViewed(true);
                    news.save();
                    currentItem.setRead(true);
                    vTimestamp.setTextAppearance(context, R.style.viewed);
                    vDescription.setTextAppearance(context, R.style.viewed);
                    vTitle.setTextAppearance(context, R.style.viewed);


                    Bundle bd = new Bundle();
                    bd.putLong(PCConstant.NEWS_ID,currentItem.getId());
                    bd.putString(PCConstant.NEWS_TITLE, currentItem.getTitle());
                    bd.putString(PCConstant.NEWS_CONTENT, currentItem.getContent());
                    bd.putString(PCConstant.NEWS_TIME,currentItem.getPublish_date());
                    bd.putString(PCConstant.NEWS_URL,currentItem.getStaticURL());

                    parent.onItemClick(bd);


                }
            });
            vTitle = (TextView) v.findViewById(R.id.news_list_item_title);
            vTimestamp = (TextView) v.findViewById(R.id.news_list_item_time);
            vDescription = (TextView) v.findViewById(R.id.news_list_item_desc);
        }
    }
    @Override
    public int getItemCount(){
        return this.newsList.size();

    }

    @Override
    public void onBindViewHolder(NewsListItemHolder nh,int i){
        NewsListItem ni = this.newsList.get(i);
        nh.currentItem = ni;
        nh.vTitle.setText(ni.getTitle() + " [" + ni.getCategory()+ "]");
        nh.vDescription.setText(ni.getDescription().replaceAll("\\s+","").replaceAll("(&#61656|&nbsp)",""));
        nh.vTimestamp.setText(ni.getTimestamp());
        //Log.d("isViewed",Boolean.toString(ni.isRead()));
        if(ni.isRead()){
            nh.vDescription.setTextAppearance(parent.getActivity(),R.style.viewed);
            nh.vTitle.setTextAppearance(parent.getActivity(),R.style.viewed);
            nh.vTimestamp.setTextAppearance(parent.getActivity(), R.style.viewed);
        }else{
            nh.vDescription.setTextAppearance(parent.getActivity(),R.style.not_viewed);
            nh.vTitle.setTextAppearance(parent.getActivity(),R.style.not_viewed);
            nh.vTimestamp.setTextAppearance(parent.getActivity(),R.style.not_viewed);
        }
    }
    @Override
    public NewsListItemHolder onCreateViewHolder(ViewGroup viewGroup,int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item, viewGroup, false);

        return new NewsListItemHolder(itemView,viewGroup.getContext(),parent);
    }

    public void setParent(ListFragment fragment){
        this.parent = fragment;
    }
}
