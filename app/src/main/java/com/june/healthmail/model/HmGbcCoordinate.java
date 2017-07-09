package com.june.healthmail.model;

import java.io.Serializable;

/**
 * Created by june on 2017/3/5.
 */

public class HmGbcCoordinate implements Serializable{
    private static final long serialVersionUID = 1L;
    private double hm_venue_lat;
    private double hm_venue_lng;

    public double getHm_venue_lat() {
        return hm_venue_lat;
    }

    public void setHm_venue_lat(double hm_venue_lat) {
        this.hm_venue_lat = hm_venue_lat;
    }

    public double getHm_venue_lng() {
        return hm_venue_lng;
    }

    public void setHm_venue_lng(double hm_venue_lng) {
        this.hm_venue_lng = hm_venue_lng;
    }
}
