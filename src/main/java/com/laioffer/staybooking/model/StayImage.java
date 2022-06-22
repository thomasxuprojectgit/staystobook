package com.laioffer.staybooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.io.Serializable;

/**
 * for StayImage table
 */
@Entity
@Table(name = "stay_image")
public class StayImage implements Serializable {
    // version ID for this table structure
    // used for version control
    private static final long serialVersionUID = 1L;

    // URL is unique and can be primary key
    @Id
    private String url;

    // @ManyToOne image to stay is N to 1
    // @JoinColumn JPA auto create column stay_id(forign key) connect to Stay table's primary key
    // @JsonIgnore Stay will have a list of StayImage, the updates will be in Stay side, not here, stop circle
    @ManyToOne
    @JoinColumn(name = "stay_id")
    @JsonIgnore
    private Stay stay;

    // Hibernate(used by JPA) use this constructor, get data from database, and convert data to StayImage obj
    public StayImage() {}

    public StayImage(String url, Stay stay) {
        this.url = url;
        this.stay = stay;
    }

    public String getUrl() {
        return url;
    }

    public StayImage setUrl(String url) {
        this.url = url;
        return this;
    }

    public Stay getStay() {
        return stay;
    }

    public StayImage setStay(Stay stay) {
        this.stay = stay;
        return this;
    }
}

