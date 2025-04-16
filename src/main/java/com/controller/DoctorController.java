package com.controller;

import java.sql.Time;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bean.Appointment;
import com.bean.Patient;
import com.bean.Schedule;
import com.model.service.DoctorService;
import com.model.service.PatientService;

@Controller
public class DoctorController {
	@Autowired
	private DoctorService doctorService;
	@Autowired
	private PatientService patientService;
	
//Doctor Functionalities Start ---------------------------------------------------------------------------------------------------------------------
	
	@RequestMapping("/viewPatientEnterId")
	public ModelAndView viewPatientEnterIdController() {
		return new ModelAndView("PatientEnterId");
	}
	@RequestMapping("/viewPatient")
	public ModelAndView viewPatientController(HttpServletRequest request,HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		

		Patient patient = patientService.getPatientById(request.getParameter("pId"));
		if (patient != null) {
			modelAndView.addObject("patient", patient);
			modelAndView.setViewName("ShowPatient");
		}
		else {
			String message="Patient with ID "+request.getParameter("pId")+" does not exist!";
			modelAndView.addObject("message", message);
			modelAndView.setViewName("Output");
		}
		return modelAndView;
	}
	
	
	@RequestMapping("/showAppointment")
	public ModelAndView showAppointmentControllerForDoctor(HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		String id = (String) session.getAttribute("userName");
		String message ;
		List<Appointment> appointmentDoc = doctorService.getMyAppointments(id);
		if(appointmentDoc.isEmpty()) {
			message = "No appointments requested.";
			modelAndView.addObject("message", message);
			modelAndView.setViewName("Output");
			return modelAndView;
			
		}
		else {
			
			return new ModelAndView("ShowMyAppointments", "myAppointmentList", appointmentDoc);
		}
	}
	
	
	@RequestMapping("/viewSchedule")
	public ModelAndView viewScheduleControllerForDoctor(HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		List<Schedule> schedules = doctorService.getDoctorSchedule((String) session.getAttribute("userName"));
		if (schedules != null && !schedules.isEmpty()) {
			modelAndView.addObject("schedules", schedules);
			modelAndView.setViewName("ShowMySchedules");
		}
		else {
			String message="No schedules to display !";
			modelAndView.addObject("message", message);
			modelAndView.setViewName("Output");
		}
		return modelAndView;
	}

	@RequestMapping("/addSchedule")
	public ModelAndView addScheduleController() {
		return new ModelAndView("AddSchedule");
	}

	@RequestMapping("/saveSchedule")
	public ModelAndView saveScheduleController(HttpServletRequest request, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			Schedule schedule = new Schedule();
			schedule.setDoctorId((String) session.getAttribute("userName"));
			schedule.setNameOfDoctor((String) session.getAttribute("name")); // Assuming doctor's name is stored in session
			schedule.setAvailableDay(request.getParameter("availableDay"));
			
			// Convert string time to SQL Time
			Time startTime = Time.valueOf(request.getParameter("slotStart") + ":00");
			Time endTime = Time.valueOf(request.getParameter("slotEnd") + ":00");
			
			schedule.setSlotStart(startTime);
			schedule.setSlotEnd(endTime);
			
			boolean result = doctorService.addSchedule(schedule);
			
			if (result) {
				modelAndView.addObject("message", "Schedule added successfully!");
				modelAndView.addObject("messageType", "success");
			} else {
				modelAndView.addObject("message", "Failed to add schedule. Please try again.");
				modelAndView.addObject("messageType", "error");
			}
		} catch (Exception e) {
			modelAndView.addObject("message", "An error occurred: " + e.getMessage());
			modelAndView.addObject("messageType", "error");
		}
		
		modelAndView.setViewName("AddSchedule");
		return modelAndView;
	}
}
