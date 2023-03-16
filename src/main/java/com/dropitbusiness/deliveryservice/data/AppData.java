package com.dropitbusiness.deliveryservice.data;

import com.dropitbusiness.deliveryservice.data.models.HolidayList;
import com.dropitbusiness.deliveryservice.data.models.TimeslotList;
import com.dropitbusiness.deliveryservice.models.Address;
import com.dropitbusiness.deliveryservice.models.Holiday;
import com.dropitbusiness.deliveryservice.models.Timeslot;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class AppData {
    private static final String TIME_SLOTS_FILE_PATH = "src/main/resources/static/courier_available_timeslots.json";
    private static final String HOLIDAYS_FILE_PATH = "src/main/resources/static/holidays.json";
    public static final String GEOAPIFY_API_KEY = "2af2e91f58e24192a489014fea979f97";
    public static final int MAX_USE_COUNT_TIMESLOT = 2;
    @Getter
    List<Timeslot> timeSlots;
    @Getter
    List<Holiday> holidays;
    @Getter
    Set<Address> addresses;


    public void loadData() {
        addresses = new HashSet<>();
        uploadJsonFiles();
    }

    private void uploadJsonFiles() {
        AtomicReference<TimeslotList> timeslotList = new AtomicReference<>(new TimeslotList());
        AtomicReference<HolidayList> holidaysList = new AtomicReference<>(new HolidayList());

        CompletableFuture<Void> timeslotsFuture = CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                objectMapper.registerModule(new JavaTimeModule());
                File src = new File(TIME_SLOTS_FILE_PATH);
                timeslotList.set(objectMapper.readValue(src, TimeslotList.class));
            } catch (IOException e) {
                System.err.println("Failed to load timeslots file: " + e.getMessage());
            }
        });

        CompletableFuture<Void> holidaysFuture = CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                objectMapper.registerModule(new JavaTimeModule());
                holidaysList.set(objectMapper.readValue(new File(HOLIDAYS_FILE_PATH), HolidayList.class));
            } catch (IOException e) {
                System.err.println("Failed to load holidays file: " + e.getMessage());
            }
        });
        // Wait for both futures
        CompletableFuture.allOf(timeslotsFuture, holidaysFuture).join();
        holidays = holidaysList.get().getHolidays();
        timeSlots = timeslotList.get().getCourierAvailableTimeslots();
        log.info("TimeSlots {}", timeSlots);
        log.info("Holidays {}", holidays);
    }
}
