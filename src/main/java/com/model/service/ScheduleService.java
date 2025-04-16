package com.model.service;

import com.bean.Schedule;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getDoctorSchedules(String doctorId);
    Schedule addSchedule(Schedule schedule);
    void deleteSchedule(Long scheduleId);
} 