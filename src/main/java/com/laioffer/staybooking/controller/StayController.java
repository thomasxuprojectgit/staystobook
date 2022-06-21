package com.laioffer.staybooking.controller;

import org.springframework.web.bind.annotation.RestController;

import com.laioffer.staybooking.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.model.Stay;
import org.springframework.web.bind.annotation.*;

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
    public void addStay(@RequestBody Stay stay) {
        stayService.add(stay);
    }

    // delete a stay, per stay id and host name from front end
    @DeleteMapping("/stays")
    public void deleteStay(
            @RequestParam(name = "stay_id") Long stayId,
            @RequestParam(name = "host") String hostName) {
        stayService.delete(stayId, hostName);
    }

}


