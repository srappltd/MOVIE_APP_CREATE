package com.sandhya.movieappcreate.model;

public class ContentModel {
    String id,title,description,video,thumb,key,date;


    public ContentModel(String id, String title, String description, String video, String thumb, String key, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.video = video;
        this.thumb = thumb;
        this.key = key;
        this.date = date;
    }

    public ContentModel() {
    }
    //    public ContentModel(String title, String description, String video, String thumb, String key) {
//        this.title = title;
//        this.description = description;
//        this.video = video;
//        this.thumb = thumb;
//        this.key = key;
//    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
