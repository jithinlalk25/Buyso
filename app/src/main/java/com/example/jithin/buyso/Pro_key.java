package com.example.jithin.buyso;
/**
 * Created by jithin on 22/1/18.
 */

public class Pro_key {
    private String Name,Number,District,Loc_name,Loc_id;
    public String gmail = "";

    public String getDistrict() {
        return District;
    }

    public String getName() {
        return Name;
    }

    public String getLoc_id() {
        return Loc_id;
    }

    public String getLoc_name() {
        return Loc_name;
    }

    public String getNumber() {
        return Number;
    }

    public void setDistrict(String district) {
        this.District = district;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setLoc_id(String loc_id) {
        this.Loc_id = loc_id;
    }

    public void setLoc_name(String loc_name) {
        this.Loc_name = loc_name;
    }

    public void setNumber(String number) {
        this.Number = number;
    }
}