package com.dropitbusiness.deliveryservice.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDeliveryRequest {
    private int userId;
    private int timeslotId;
}
