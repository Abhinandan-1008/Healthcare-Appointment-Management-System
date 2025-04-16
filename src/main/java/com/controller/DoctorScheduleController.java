package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bean.Schedule;
import com.model.service.DoctorService;
import com.model.service.ScheduleService;

import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/manage-schedule")
    public String showSchedulePage(Model model, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        if (doctorId == null) {
            return "redirect:/login";
        }

        List<Schedule> schedules = scheduleService.getDoctorSchedules(doctorId);
        model.addAttribute("schedules", schedules);
        return "doctor/manage-schedule";
    }

    @PostMapping("/add-schedule")
    public String addSchedule(@RequestParam String availableDay,
                            @RequestParam String slotStart,
                            @RequestParam String slotEnd,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        String doctorId = (String) session.getAttribute("doctorId");
        if (doctorId == null) {
            return "redirect:/login";
        }

        try {
            Schedule schedule = new Schedule();
            schedule.setDoctorId(doctorId);
            schedule.setAvailableDay(availableDay);
            schedule.setSlotStart(Time.valueOf(slotStart + ":00"));
            schedule.setSlotEnd(Time.valueOf(slotEnd + ":00"));
            
            // Get doctor name
            String doctorName = doctorService.getDoctorById(doctorId).getDoctorName();
            schedule.setNameOfDoctor(doctorName);

            scheduleService.addSchedule(schedule);
            redirectAttributes.addFlashAttribute("message", "Schedule added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding schedule: " + e.getMessage());
        }

        return "redirect:/doctor/manage-schedule";
    }

    @PostMapping("/delete-schedule")
    public String deleteSchedule(@RequestParam Long scheduleId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String doctorId = (String) session.getAttribute("doctorId");
        if (doctorId == null) {
            return "redirect:/login";
        }

        try {
            scheduleService.deleteSchedule(scheduleId);
            redirectAttributes.addFlashAttribute("message", "Schedule deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting schedule: " + e.getMessage());
        }

        return "redirect:/doctor/manage-schedule";
    }
} 