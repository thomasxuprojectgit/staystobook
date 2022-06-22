package com.laioffer.staybooking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.io.Serializable;


import java.util.List;

@Entity
@Table(name = "stay")
@JsonDeserialize(builder = Stay.Builder.class)
public class Stay implements Serializable {

    // @Id annotate primary key (stay_id in table)
    // @GeneratedValue how to generate a value  for this primary key (GenerationType.AUTO means auto generate)
    // database will auto create primary ID, not duplicate will happen(as database take care of it)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String address;
    @JsonProperty("guest_number")
    private int guestNumber;

    // Many Stays to One User
    // @JoinColumn Create and connect user_id column (foreign key) in stay table to primary key in User table
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User host;

    // CascadeType.ALL when delete stay, delete stayImages(all is all CRUD method)
    // fetch=FetchType.EAGER: when get Stay, auto get StayImage
    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private List<StayImage> images;

    public List<StayImage> getImages() {
        return images;
    }

    public Stay setImages(List<StayImage> images) {
        this.images = images;
        return this;
    }


    public Stay() {}

    // use Builder design to convert json from front end to Stay obj
    private Stay(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.address = builder.address;
        this.guestNumber = builder.guestNumber;
        this.host = builder.host;
        this.images = builder.images;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public User getHost() {
        return host;
    }

    public static class Builder {

        @JsonProperty("id")
        private Long id;

        // @JsonProperty use to target json element to field here
        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("address")
        private String address;

        @JsonProperty("guest_number")
        private int guestNumber;

        @JsonProperty("host")
        private User host;

        @JsonProperty("images")
        private List<StayImage> images;


        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setGuestNumber(int guestNumber) {
            this.guestNumber = guestNumber;
            return this;
        }

        public Builder setHost(User host) {
            this.host = host;
            return this;
        }

        public Builder setImages(List<StayImage> images) {
            this.images = images;
            return this;
        }

        public Stay build() {
            return new Stay(this);
        }
    }

}

