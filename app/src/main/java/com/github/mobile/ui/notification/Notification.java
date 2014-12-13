package com.github.mobile.ui.notification;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jacob on 2014-11-26.
 */
public class Notification implements Comparable<Notification>, Serializable{

    private long id;
    private String contentText, contentTitle;
    private Date date;
    private boolean hasBeenViewed;

    public enum Type{
        NEW_COMMIT,
        NEW_BRANCH,
        NEW_REPOSITORY
    }

    public Notification(){

    }

    public Notification(Type type, String repoName, String branchName){
        this.date = new Date();
        switch (type){
            case NEW_COMMIT:
                this.contentTitle = "New Commit to repo: '" + repoName + "'";
                this.contentText = "New commit to branch: '" + branchName + "'";
                break;
            case NEW_BRANCH:
                this.contentTitle = "New Branch to repo: '" + repoName + "'";
                this.contentText = "New branch added: '" + branchName + "'";
                break;
            case NEW_REPOSITORY:
                this.contentTitle = "New Repository";
                this.contentText = "New Repository added: '" + repoName + "'";
                break;
            default:
                break;
        }
    }

    public String getContentText() {
        return contentText;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public Date getDate() {
        return date;
    }

    public boolean hasBeenViewed() {
        return hasBeenViewed;
    }

    public void setHasBeenViewed(boolean hasBeenViewed) {
        this.hasBeenViewed = hasBeenViewed;
    }

    @Override
    public int compareTo(Notification o){
        return this.date.compareTo(o.getDate());

    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

}
