package com.jupiter.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jupiter.model.NewsListItem;
import com.jupiter.pcbrowser.MainActivity;
import com.jupiter.pcbrowser.NewsContentActivity;
import com.jupiter.pcbrowser.PCConstant;
import com.jupiter.pcbrowser.R;

import java.util.List;

/**
 * Created by lovew_000 on 2015/5/5.
 */
public class NewsListItemAdapter extends RecyclerView.Adapter<NewsListItemAdapter.NewsListItemHolder> {

    private List<NewsListItem> newsList;
    public NewsListItemAdapter(List<NewsListItem> newsList){
        this.newsList = newsList;
    }
    public static class NewsListItemHolder extends RecyclerView.ViewHolder{
        protected TextView  vTitle;
        protected TextView vTimestamp;
        protected TextView vDescription;
        private Context context;
        public NewsListItem currentItem;
        public NewsListItemHolder(View v, final Context context){
            super(v);
            this.context = context;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = currentItem.getTitle();
                    Log.d("Item", message);
                    Intent intent = new Intent(context, NewsContentActivity.class);

                    intent.putExtra(PCConstant.NEWS_TITLE,message);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
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
        nh.vTitle.setText(ni.getTitle());
        nh.vDescription.setText(ni.getDescription());
        nh.vTimestamp.setText(ni.getTimestamp());
    }
    @Override
    public NewsListItemHolder onCreateViewHolder(ViewGroup viewGroup,int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item,viewGroup,false);

        return new NewsListItemHolder(itemView,viewGroup.getContext());
    }
}
