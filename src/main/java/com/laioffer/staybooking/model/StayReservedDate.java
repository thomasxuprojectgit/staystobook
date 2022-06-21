package com.laioffer.staybooking.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Use for StayReservedDate table
 * Use StayReservedDateKey as composite primary key
 * annotate the class and private field to make it supported by Hibernate.
 */
@Entity
@Table(name = "stay_reserved_date")
public class StayReservedDate implements Serializable {

    // For different version of table structure
    private static final long serialVersionUID = 1L;

    // @EmbeddedId use StayReservedDateKey as composite primary key
    @EmbeddedId
    private StayReservedDateKey id;

    // column stay_id also is a foreign key of table stay
    // @MapsId("stay_id") map "stay_id" column in StayReservedDateKey to Stay's primary key
    // @MapsId used mostly used in composite primary key
    // @ManyToOne means multiple StayReservedDate to one Stay
    @ManyToOne
    @MapsId("stay_id")
    private Stay stay;

    public StayReservedDate() {}

    public StayReservedDate(StayReservedDateKey id, Stay stay) {
        this.id = id;
        this.stay = stay;
    }

    public StayReservedDateKey getId() {
        return id;
    }

    public Stay getStay() {
        return stay;
    }

}
