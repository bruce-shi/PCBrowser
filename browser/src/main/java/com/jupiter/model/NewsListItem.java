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
}
