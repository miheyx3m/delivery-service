package com.dropitbusiness.deliveryservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Timeslot {
    private static int lastAssignedId = 0;
    private int id;
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @JsonProperty("supported_postcodes")
    private List<String> supportedPostcodes;

    public Timeslot() {
        this.id = generateId();
    }

    private synchronized int generateId() {
        lastAssignedId++;
        return lastAssignedId;
    }
}