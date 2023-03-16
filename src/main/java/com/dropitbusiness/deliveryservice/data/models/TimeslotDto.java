package com.dropitbusiness.deliveryservice.data.models;

import com.dropitbusiness.deliveryservice.models.Timeslot;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeslotDto {
    private int timeslotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeslotDto(Timeslot timeslot) {
        this.timeslotId = timeslot.getId();
        this.startTime = timeslot.getStartTime();
        this.endTime = timeslot.getEndTime();
    }
}
