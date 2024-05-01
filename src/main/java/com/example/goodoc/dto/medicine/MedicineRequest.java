package com.example.goodoc.dto.medicine;

import com.example.goodoc.model.Reception;
import com.example.goodoc.model.ReleaseForm;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MedicineRequest {
    private String name;
    private String releaseForm;
    private String reception;

    private String month;
    private int day;
    private int year;

    private int hour;
    private int minute;
    private String timePeriod;

    private String additionally;
}
