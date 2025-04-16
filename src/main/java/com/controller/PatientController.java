package com.controller;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bean.Appointment;
import com.bean.Patient;
import com.bean.Schedule;
import com.model.service.AppointmentService;
import com.model.service.DoctorService;
import com.model.service.PatientService;

@Controller
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    // 1. View patient profile
    @RequestMapping("/showPatient")
    public ModelAndView showPatientController(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Patient patient = patientService.getPatientById((String) session.getAttribute("userName"));

        if (patient != null) {
            modelAndView.addObject("patient", patient);
            modelAndView.setViewName("ShowPatient");
        } else {
            String message = "Patient with ID " + session.getAttribute("userName") + " does not exist!";
            modelAndView.addObject("message", message);
            modelAndView.setViewName("Output");
        }

        return modelAndView;
    }

    // 2. Request appointment
    @RequestMapping("/requestAppointment")
    public ModelAndView requestAppointmentController() {
        return new ModelAndView("requestAppointmentPage");
    }

    // 2.1 Generate appointment
    @RequestMapping("/generateAppointment")
    public ModelAndView generateAppointmentController(HttpServletRequest request, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        String dateParam = request.getParameter("appointmentDate");
        if (dateParam == null || dateParam.isEmpty()) {
            modelAndView.addObject("message", "Please select a valid date.");
            modelAndView.setViewName("Output");
            return modelAndView;
        }

        Date date = Date.valueOf(dateParam);
        session.setAttribute("date", date);
        List<Schedule> availableDoctorsSchedule = doctorService.getAvailableDoctors(date);

        if (availableDoctorsSchedule != null && !availableDoctorsSchedule.isEmpty()) {
            modelAndView.addObject("availableScheduleList", availableDoctorsSchedule);
            modelAndView.addObject("command2", new Schedule());
            modelAndView.setViewName("ShowAvailableDoctorsSchedulePage");
        } else {
            modelAndView.addObject("message", "No available Doctor schedules to display");
            modelAndView.setViewName("Output");
        }

        return modelAndView;
    }

    public List<String> getAvailableDoctorIds(HttpSession session) {
        List<Schedule> availableDoctorsSchedule = doctorService.getAvailableDoctors(
                Date.valueOf((String) (session.getAttribute("date"))));

        return availableDoctorsSchedule.stream()
                .map(Schedule::getDoctorId)
                .collect(Collectors.toList());
    }

    // 2.2 Book appointment
    @RequestMapping("/bookAppointment")
    public ModelAndView bookAppointmentController(@ModelAttribute("command2") Schedule schedule, HttpSession session, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();

        String patientId = (String) session.getAttribute("userName");
        if (patientId == null) {
            modelAndView.addObject("message", "Please login first");
            modelAndView.setViewName("login");
            return modelAndView;
        }

        String doctorId = schedule.getDoctorId();
        if (doctorId == null || doctorId.trim().isEmpty()) {
            modelAndView.addObject("message", "Please select a doctor");
            modelAndView.setViewName("ShowAvailableDoctorsSchedulePage");
            return modelAndView;
        }

        try {
            Date date = (Date) session.getAttribute("date");
            Appointment appointment = appointmentService.requestAppointment(patientId, doctorId, date);
            if (appointment != null) {
                modelAndView.addObject("myAppointmentList", List.of(appointment));
                modelAndView.setViewName("ShowMyAppointments");
            } else {
                modelAndView.addObject("message", "Unable to book appointment. Please try again.");
                modelAndView.setViewName("ShowAvailableDoctorsSchedulePage");
            }
        } catch (Exception e) {
            modelAndView.addObject("message", "Error booking appointment: " + e.getMessage());
            modelAndView.setViewName("ShowAvailableDoctorsSchedulePage");
        }

        return modelAndView;
    }

    // 3. Cancel appointment - Show list
    @RequestMapping("/cancelAppointment")
    public ModelAndView cancelAppointmentController(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        String id = (String) session.getAttribute("userName");
        List<Appointment> appointments = patientService.getMyAppointments(id);

        if (!appointments.isEmpty()) {
            modelAndView.addObject("appointmentList", appointments);
            modelAndView.setViewName("cancelAppointment");
        } else {
            modelAndView.addObject("message", "No appointments to delete");
            modelAndView.setViewName("Output");
        }

        return modelAndView;
    }

    // 3.1 Cancel selected appointment
    @RequestMapping("/deleteAppointment")
    public ModelAndView deleteAppointmentController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        int id = Integer.parseInt(request.getParameter("appointmentId"));
        String message = patientService.cancelAppointmentRequest(id)
                ? "Appointment with ID " + id + " deleted successfully!"
                : "Appointment with ID " + id + " does not exist!";

        modelAndView.addObject("message", message);
        modelAndView.setViewName("Output");
        return modelAndView;
    }

    // 4. View all appointments
    @RequestMapping("/viewAllAppointments")
    public ModelAndView viewAllAppointmentsController(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        String id = (String) session.getAttribute("userName");
        List<Appointment> appointments = patientService.getMyAppointments(id);

        if (!appointments.isEmpty()) {
            modelAndView.addObject("myAppointmentList", appointments);
            modelAndView.setViewName("ShowMyAppointments");
        } else {
            modelAndView.addObject("message", "No appointments to display");
            modelAndView.setViewName("Output");
        }

        return modelAndView;
    }

    // 5. Reschedule appointment - Show list
    @RequestMapping("/rescheduleAppointment")
    public ModelAndView rescheduleAppointmentController(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        String id = (String) session.getAttribute("userName");
        List<Appointment> appointments = patientService.getMyAppointments(id);

        if (!appointments.isEmpty()) {
            modelAndView.addObject("appointmentList", appointments);
            modelAndView.setViewName("rescheduleAppointment");
        } else {
            modelAndView.addObject("message", "No appointments to display");
            modelAndView.setViewName("Output");
        }

        return modelAndView;
    }

    // 5.1 Reschedule to new date
    @RequestMapping("/rescheduleAppointmentTo")
    public ModelAndView rescheduleAppointmentToController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        int aid = Integer.parseInt(request.getParameter("appointmentId"));
        Date appointmentDate = Date.valueOf(request.getParameter("appointmentDate"));

        boolean success = patientService.rescheduleAppointment(aid, appointmentDate);
        String message = success ? "Appointment Rescheduled successfully."
                : "Couldn't reschedule Appointment. Please try again.";

        modelAndView.addObject("message", message);
        modelAndView.setViewName("Output");
        return modelAndView;
    }
}
