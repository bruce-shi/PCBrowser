package com.jupiter.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lovew_000 on 2015/5/5.
 */
public class NewsListItem {
    private String title;
    private Date timestamp;
    private String description;
    private long newsId;
    private boolean isRead;
    private String content;
    private String staticURL;
    private String category;
    private Date publish_date;
    public NewsListItem(String title,Date timestamp,String description,long newsId,boolean isRead,String content,String staticURL,String category,Date publish_date){
        this.timestamp = timestamp;
        this.description = description;
        this.title = title;
        this.newsId = newsId;
        this.isRead = isRead;
        this.content = content;
        this.staticURL = staticURL;
        this.category = category;
        this.publish_date = publish_date;


    }
    public NewsListItem(String title,Date timestamp,String description){
        this.timestamp = timestamp;
        this.description = description;
        this.title = title;

    }

    public String getTimestamp() {
        long differ = timestamp.getTime() - (new Date()).getTime();
        String timeString = "";
        if(differ < 3600 * 24 * 1000){
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            timeString = sf.format(timestamp);
        }
        else if(differ < 3600 * 24 * 365 * 1000){
            SimpleDateFormat sf = new SimpleDateFormat("MMM dd");
            timeString = sf.format(timestamp);
        }
        else{
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
            timeString = sf.format(timestamp);
        }
        return timeString;
    }
    public long getId(){
        return newsId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isRead(){
        return isRead;
    }
    public void setRead(boolean read){
        this.isRead = read;
    }
    public String getContent(){
        return this.content;
    }
    public String getStaticURL(){return this.staticURL;}

    public String getCategory(){return category;}

    public String getPublish_date() {
        long differ = timestamp.getTime() - (new Date()).getTime();
        String timeString = "";
        if(differ < 3600 * 24 * 1000){
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            timeString = sf.format(timestamp);
        }
        else if(differ < 3600 * 24 * 365 * 1000){
            SimpleDateFormat sf = new SimpleDateFormat("MMM dd");
            timeString = sf.format(timestamp);
        }
        else{
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
            timeString = sf.format(timestamp);
        }
        return timeString;
    }

    public void setPublish_date(Date publish_date) {
        this.publish_date = publish_date;
    }
}
