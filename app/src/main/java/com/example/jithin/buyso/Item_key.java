package com.example.jithin.buyso;

/**
 * Created by jithin on 20/1/18.
 */

public class Item_key {

    private String imageId,title,description,price;
    public String key="";

    public Item_key(){}
    public Item_key(String imageId, String title, String description, String price){
        this.imageId = imageId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public void setImageId(String s){imageId = s;}
    public void setTitle(String s){title = s;}
    public void setDescription(String s){description = s;}
    public void setPrice(String s){price = s;}

    public String getImageId(){return imageId;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getPrice(){return price;}

}
