package com.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bean.Schedule;
import com.model.persistence.ScheduleDao;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    public List<Schedule> getDoctorSchedules(String doctorId) {
        return scheduleDao.findByDoctorId(doctorId);
    }

    @Override
    @Transactional
    public Schedule addSchedule(Schedule schedule) {
        return scheduleDao.save(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleDao.deleteById(scheduleId.intValue());
    }
} 