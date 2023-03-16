package com.dropitbusiness.deliveryservice.services;


import com.dropitbusiness.deliveryservice.data.AppData;
import com.dropitbusiness.deliveryservice.data.models.HolidayList;
import com.dropitbusiness.deliveryservice.data.models.TimeslotDto;
import com.dropitbusiness.deliveryservice.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dropitbusiness.deliveryservice.data.AppData.MAX_USE_COUNT_TIMESLOT;

@Slf4j
@Service
public class DeliveryService {

    @Autowired
    private AppData appData;
    private final Set<Delivery> deliveries;

    public DeliveryService() {
        this.deliveries = new HashSet<>();
    }

    public synchronized Delivery bookDelivery(int userId, int timeslotId) throws Exception {
        Timeslot selectedTimeslot = appData.getTimeSlots().stream()
                .filter(t -> t.getId() == timeslotId)
                .findFirst().get();

        Holiday holiday = appData.getHolidays().stream().filter(h -> h.getDate().equals(selectedTimeslot.getStartTime().toLocalDate())).findFirst().orElse(null);

        long timeslotCount = deliveries.stream()
                .filter(d -> d.getTimeslot().getId() == timeslotId && d.getStatus().equals(DeliveryStatus.BOOKED))
                .count();

        if (timeslotCount >= MAX_USE_COUNT_TIMESLOT || holiday != null) {
            throw new Exception("Timeslot is not available");
        }

        Delivery newDelivery = new Delivery();
        newDelivery.setUserId(userId);
        newDelivery.setStatus(DeliveryStatus.BOOKED);
        newDelivery.setTimeslot(selectedTimeslot);

        deliveries.add(newDelivery);
        log.info("BookDelivery {}", newDelivery);
        return newDelivery;
    }

    public void cancelDelivery(int deliveryId) throws Exception {
        Delivery delivery = deliveries.stream()
                .filter(d -> d.getId() == deliveryId)
                .findFirst()
                .orElseThrow(() -> new Exception("Delivery not found"));
        delivery.setStatus(DeliveryStatus.CANCELLED);
        log.info("CancelDelivery {}", delivery);
    }

    public List<Delivery> getDailyDeliveries() {
        LocalDate today = LocalDate.now();
        List<Delivery> dailyDeliveries = deliveries.stream()
                .filter(d -> d.getTimeslot().getStartTime().toLocalDate().equals(today)
                         && d.getStatus().equals(DeliveryStatus.BOOKED))
                .collect(Collectors.toList());
        log.info("DailyDeliveries {}", dailyDeliveries);
        return dailyDeliveries;
    }

    public List<Delivery> getWeeklyDeliveries() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        List<Delivery> weeklyDeliveries = deliveries.stream()
                .filter(delivery -> delivery.getStatus().equals(DeliveryStatus.BOOKED))
                .filter(delivery -> {
                    LocalDate deliveryDate = delivery.getTimeslot().getStartTime().toLocalDate();
                    return deliveryDate.isEqual(startOfWeek) ||
                            deliveryDate.isEqual(endOfWeek) ||
                            (deliveryDate.isAfter(startOfWeek) && deliveryDate.isBefore(endOfWeek));
                })
                .collect(Collectors.toList());
        log.info("WeeklyDeliveries {}", weeklyDeliveries);
        return weeklyDeliveries;
    }

    public List<TimeslotDto> getAvailableTimeslots(Address address) {
        List<TimeslotDto> availableTimeslots = appData.getTimeSlots().stream()
                .filter(t -> t.getSupportedPostcodes().contains(address.getPostcode()))
                .map(TimeslotDto::new)
                .collect(Collectors.toList());
        log.info("AvailableTimeslots {}", availableTimeslots);
        return availableTimeslots;
    }

    public List<Delivery> getDeliveries() {
        List<Delivery> allDeliveries = new ArrayList<>(deliveries);
        log.info("AllDeliveries {}", allDeliveries);
        return allDeliveries;
    }
}

