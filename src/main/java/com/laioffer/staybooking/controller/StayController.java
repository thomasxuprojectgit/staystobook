package com.laioffer.staybooking.controller;

import com.laioffer.staybooking.model.User;
import org.springframework.web.bind.annotation.RestController;

import com.laioffer.staybooking.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.model.Stay;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class StayController {
    private StayService stayService;

    @Autowired
    public StayController(StayService stayService) {
        this.stayService = stayService;
    }

    // get the list of stays per this user
    // front end will send /stays/host = xxx
    @GetMapping(value = "/stays")
    public List<Stay> listStays(@RequestParam(name = "host") String hostName) {
        return stayService.listByUser(hostName);
    }

    // get a stay of this user, per stay id and host name
    @GetMapping(value = "/stays/id")
    public Stay getStay(
            @RequestParam(name = "stay_id") Long stayId,
            @RequestParam(name = "host") String hostName) {
        return stayService.findByIdAndHost(stayId, hostName);
    }

    // add a stay
    // translate json to Stay obj, use stayService.add to add this obj to database
    @PostMapping("/stays")
    public void addStay(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam("host") String host,
            @RequestParam("guest_number") int guestNumber,
            @RequestParam("images") MultipartFile[] images) {

        Stay stay = new Stay.Builder().setName(name)
                .setAddress(address)
                .setDescription(description)
                .setGuestNumber(guestNumber)
                .setHost(new User.Builder().setUsername(host).build())
                .build();
        stayService.add(stay, images);
    }


    // delete a stay, per stay id and host name from front end
    @DeleteMapping("/stays")
    public void deleteStay(
            @RequestParam(name = "stay_id") Long stayId,
            @RequestParam(name = "host") String hostName) {
        stayService.delete(stayId, hostName);
    }

}


