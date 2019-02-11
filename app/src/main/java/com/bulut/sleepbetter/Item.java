package com.bulut.sleepbetter;

/**
 * Created by echessa on 8/27/16.
 */
public class Item {

    private String title;
    private String add;

    public Item() {}

    public Item(String title) {
        this.title = title;
    }
    public Item(String title, String add) {
        this.title = title;
        this.add = add;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdddd() {
        return add;
    }

    public void setAdd(String title) {
        this.add = add;
    }

}
