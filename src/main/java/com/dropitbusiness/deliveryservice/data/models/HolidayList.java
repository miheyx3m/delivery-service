package com.dropitbusiness.deliveryservice.data.models;

import com.dropitbusiness.deliveryservice.models.Holiday;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HolidayList {
    private List<Holiday> holidays;

}
