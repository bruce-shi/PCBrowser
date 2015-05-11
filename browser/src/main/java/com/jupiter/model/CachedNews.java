package com.jupiter.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by lovew_000 on 2015/5/7.
 */
public class CachedNews extends SugarRecord<CachedNews> {

    String title;

    String description;
    String content;
    String staticURL;
    String imgUrl;

    Date createDate;
    Boolean viewed;

    String category;

    Boolean sent;

    Date publishDate;

    long remoteId;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean isViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public Boolean getSent(){
        return sent;
    }
    public void setSent(Boolean sent){
        this.sent = sent;

    }

    public String getStaticURL(){
        return this.staticURL;
    }
    public void setStaticURL(String url){
        this.staticURL = url;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}
