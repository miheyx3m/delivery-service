package com.dropitbusiness.deliveryservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Delivery {
    private static int lastAssignedId = 0;
    private int id;
    private int userId;
    private Timeslot timeslot;
    private DeliveryStatus status;

    public Delivery() {
        this.id = generateId();
    }

    private synchronized int generateId() {
        lastAssignedId++;
        return lastAssignedId;
    }
}