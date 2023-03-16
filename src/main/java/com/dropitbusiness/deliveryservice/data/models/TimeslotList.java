package com.dropitbusiness.deliveryservice.data.models;

import com.dropitbusiness.deliveryservice.models.Timeslot;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeslotList {
    @JsonProperty("courier_available_timeslots")
    private List<Timeslot> courierAvailableTimeslots;
}
