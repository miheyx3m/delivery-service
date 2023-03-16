package com.dropitbusiness.deliveryservice.api.controllers;

import com.dropitbusiness.deliveryservice.api.models.BookDeliveryRequest;
import com.dropitbusiness.deliveryservice.data.AppData;
import com.dropitbusiness.deliveryservice.data.models.TimeslotDto;
import com.dropitbusiness.deliveryservice.models.Address;
import com.dropitbusiness.deliveryservice.models.Delivery;
import com.dropitbusiness.deliveryservice.models.Timeslot;
import com.dropitbusiness.deliveryservice.services.DeliveryService;
import com.dropitbusiness.deliveryservice.services.GeoapifyGeocodingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@Api(tags = "Delivery System")
public class DeliveryController {

    @Autowired
    private AppData appData;
    @Autowired
    private GeoapifyGeocodingService geoapifyGeocodingService;
    @Autowired
    private DeliveryService deliveryService;

    //POST /resolve-address - resolves a single line address into a structured one (See 'Address' model)
    @PostMapping("/resolve-address")
    public ResponseEntity<Address> resolveAddress(@RequestParam String searchTerm) {
        Address resolvedAddress = geoapifyGeocodingService.resolveAddress(searchTerm);
        return ResponseEntity.ok(resolvedAddress);
    }

    // POST /timeslots - retrieve all available Time Slots (See ‘Timeslot’ model) for a formatted address ??
    // strange: changed the request to GET
    @PostMapping("/timeslots")
    public ResponseEntity<List<TimeslotDto>> getAvailableTimeslots(@RequestBody Address address) {
        List<TimeslotDto> filteredTimeslots = deliveryService.getAvailableTimeslots(address);
        return ResponseEntity.ok(filteredTimeslots);
    }

    //POST /deliveries - book a delivery ?? maybe need to change the endpoint name
    @PostMapping("/deliveries")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Timeslot not found"),
            @ApiResponse(code = 200, message = "Delivery booked"),
            @ApiResponse(code = 400, message = "Timeslot is not available")
    })
    public synchronized ResponseEntity<Delivery> bookDelivery(@RequestBody BookDeliveryRequest bookDeliveryRequest) {
        try {
            Timeslot selectedTimeslot = appData.getTimeSlots().stream()
                    .filter(t -> t.getId() == bookDeliveryRequest.getTimeslotId())
                    .findFirst()
                    .orElse(null);
            if (selectedTimeslot == null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("bookDeliveryRequest {}", bookDeliveryRequest);
            Delivery newDelivery = deliveryService.bookDelivery(bookDeliveryRequest.getUserId(), bookDeliveryRequest.getTimeslotId());
            return ResponseEntity.ok(newDelivery);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //DELETE /deliveries/{DELIVERY_ID} - cancel a delivery
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Delivery was deleted"),
            @ApiResponse(code = 400, message = "Delivery not found")
    })
    @DeleteMapping("/deliveries/{deliveryId}")
    public ResponseEntity<Void> cancelDelivery(@PathVariable int deliveryId) {
        try {
            deliveryService.cancelDelivery(deliveryId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //GET /deliveries/daily - retrieve all today’s deliveries
    @GetMapping("/deliveries/daily")
    public ResponseEntity<List<Delivery>> getDailyDeliveries() {
        List<Delivery> dailyDeliveries = deliveryService.getDailyDeliveries();
        return ResponseEntity.ok(dailyDeliveries);
    }

    //GET /deliveries/weekly - retrieve the deliveries for current week
    @GetMapping("/deliveries/weekly")
    public ResponseEntity<List<Delivery>> getWeeklyDeliveries() {
        List<Delivery> weeklyDeliveries = deliveryService.getWeeklyDeliveries();
        return ResponseEntity.ok(weeklyDeliveries);
    }

    @GetMapping("/deliveries/all")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        List<Delivery> deliveries = deliveryService.getDeliveries();
        return ResponseEntity.ok(deliveries);
    }

}