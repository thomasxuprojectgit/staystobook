package com.laioffer.staybooking.model;

import java.time.LocalDate;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * As the composite primary key of the table Stay Reserved Date
 * @Embeddable noted this class is composite primary key to JPA to create composite primary key
 * In twitch project, we use join table to create N to N table
 * This is not table, it is composite primary key
 */
@Embeddable
public class StayReservedDateKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long stay_id;
    private LocalDate date;

    // mandatory requirement for composite primary key class, no arg constructor, required by JPA, JPA use it
    public StayReservedDateKey() {}

    // standard constructor, programmer use this
    public StayReservedDateKey(Long stay_id, LocalDate date) {
        this.stay_id = stay_id;
        this.date = date;
    }

    public Long getStay_id() {
        return stay_id;
    }

    public StayReservedDateKey setStay_id(Long stay_id) {
        this.stay_id = stay_id;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public StayReservedDateKey setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    // required by JPA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StayReservedDateKey that = (StayReservedDateKey) o;
        return stay_id.equals(that.stay_id) && date.equals(that.date);
    }

    // required by JPA
    @Override
    public int hashCode() {
        return Objects.hash(stay_id, date);
    }


}

