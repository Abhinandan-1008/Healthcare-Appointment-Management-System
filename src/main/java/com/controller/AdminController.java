package com.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bean.Doctor;
import com.bean.Patient;
import com.model.service.AdminService;

@Controller
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
//  Admin Functionalities Start--------------------------------------------------------------------------------------------------------------------------

	
//  Add Doctor
	@RequestMapping("/addDoctor")
	public ModelAndView addDoctorController() {
		return new ModelAndView("addDoctor");
	}
	@RequestMapping("/saveDoctor")
	public ModelAndView saveDoctorController(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String message = null;
		
		try {
			System.out.println("Starting doctor registration process in controller...");
			Doctor doctor = new Doctor();
			
			// Validate and set doctor name
			String doctorName = request.getParameter("dName");
			System.out.println("Doctor name: " + doctorName);
			if (doctorName == null || doctorName.trim().isEmpty()) {
				throw new IllegalArgumentException("Doctor name cannot be empty");
			}
			doctor.setDoctorName(doctorName);
			
			// Validate and set doctor age
			try {
				String ageStr = request.getParameter("dAge");
				System.out.println("Doctor age (string): " + ageStr);
				int doctorAge = Integer.parseInt(ageStr);
				if (doctorAge <= 0 || doctorAge > 120) {
					throw new IllegalArgumentException("Invalid doctor age");
				}
				doctor.setDoctorAge(doctorAge);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Doctor age must be a valid number");
			}
			
			// Validate and set doctor gender
			String doctorGender = request.getParameter("dGender");
			System.out.println("Doctor gender: " + doctorGender);
			if (doctorGender == null || doctorGender.trim().isEmpty()) {
				throw new IllegalArgumentException("Doctor gender cannot be empty");
			}
			doctor.setDoctorGender(doctorGender);
			
			// Validate and set doctor experience
			try {
				String expStr = request.getParameter("dExperience");
				System.out.println("Doctor experience (string): " + expStr);
				int doctorExperience = Integer.parseInt(expStr);
				if (doctorExperience < 0) {
					throw new IllegalArgumentException("Doctor experience cannot be negative");
				}
				doctor.setDoctorExperience(doctorExperience);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Doctor experience must be a valid number");
			}
			
			// Validate and set doctor contact
			String doctorContact = request.getParameter("dContact");
			System.out.println("Doctor contact: " + doctorContact);
			if (doctorContact == null || doctorContact.trim().isEmpty() || !doctorContact.matches("\\d{10}")) {
				throw new IllegalArgumentException("Doctor contact must be a valid 10-digit number");
			}
			doctor.setDoctorContact(doctorContact);
			
			// Validate and set doctor address
			String doctorAddress = request.getParameter("dAddress");
			System.out.println("Doctor address: " + doctorAddress);
			if (doctorAddress == null || doctorAddress.trim().isEmpty()) {
				throw new IllegalArgumentException("Doctor address cannot be empty");
			}
			doctor.setDoctorAddress(doctorAddress);
			
			// Validate and set doctor department
			String doctorDepartment = request.getParameter("dDepartment");
			System.out.println("Doctor department: " + doctorDepartment);
			if (doctorDepartment == null || doctorDepartment.trim().isEmpty()) {
				throw new IllegalArgumentException("Doctor department cannot be empty");
			}
			doctor.setDoctorDepartment(doctorDepartment);
			
			// Register the doctor
			System.out.println("Calling adminService.registerDoctorToDatabase...");
			boolean result = adminService.registerDoctorToDatabase(doctor);
			System.out.println("Registration result: " + result);
			
			if (result) {
				message = "Doctor Added Successfully";
			} else {
				message = "Doctor Addition Failed. Please try again.";
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Validation error: " + e.getMessage());
			message = "Validation Error: " + e.getMessage();
		} catch (Exception e) {
			System.out.println("Error adding doctor: " + e.getMessage());
			message = "Error adding doctor: " + e.getMessage();
			e.printStackTrace();
		}

		modelAndView.addObject("message", message);
		modelAndView.setViewName("Output");
		return modelAndView;
	}
	
//  Remove Doctor
	
	@ModelAttribute("doctorIds")
	public List<String> getAllDoctorIds(){
		List<Doctor> doctorList = adminService.getAllDoctor();
		
		return doctorList.stream().
				map(Doctor::getDoctorId).
				collect(Collectors.toList());
	}

	@RequestMapping("/removeDoctorByID")
	public ModelAndView removeDoctorByIdController() {

		ModelAndView modelAndView = new ModelAndView();
		List<Doctor> doctorList = adminService.getAllDoctor();
		String message = null;
		if (doctorList != null) {
			modelAndView.addObject("doctorList", doctorList);
			modelAndView.addObject("command2",new Doctor());
			modelAndView.setViewName("ShowAllDoctorsToRemove");
			return modelAndView;
		}
		else {
			message = "No Doctor to show !";

		modelAndView.addObject("message", message);
		modelAndView.setViewName("Output");
		return modelAndView;
		}
	}
	@RequestMapping("/removeDoctor")
	public ModelAndView removeDoctorController(@ModelAttribute("command2") Doctor doctor) {
		ModelAndView modelAndView = new ModelAndView();
		
		String message = null;
		try {
			if (adminService.removeDoctorFromDatabase(doctor.getDoctorId())) {
				message = "Doctor Removed Successfully";
			} else {
				message = "Doctor removal failed. The doctor might not exist or have associated records.";
			}
		} catch (Exception e) {
			message = "Error removing doctor: " + e.getMessage();
		}

		modelAndView.addObject("message", message);
		modelAndView.setViewName("Output");
		return modelAndView;
	}
//Show All Doctors	
	@RequestMapping("/showAllDoctors")
	public ModelAndView showAllDoctors() {
		ModelAndView modelAndView = new ModelAndView();
		
		List<Doctor> doctorList = adminService.getAllDoctor();
		String message = null;
		if (doctorList != null) {
			modelAndView.addObject("doctorList", doctorList);
			modelAndView.setViewName("ShowAllDoctors");
			return modelAndView;
		}
		else {
			message = "No Doctor to show !";

		modelAndView.addObject("message", message);
		modelAndView.setViewName("Output");
		return modelAndView;
		}
	}
	
	
//Remove Patient
	
	@ModelAttribute("patientIds")
	public List<String> getAllPatientIds(){
		List<Patient> patientList = adminService.getAllPatient();
		
		return patientList.stream().
				map(Patient::getPatientId).
				collect(Collectors.toList());
	}
	@RequestMapping("/removePatient")
	public ModelAndView removePatientController() {
		ModelAndView modelAndView = new ModelAndView();
		
		List<Patient> patientList = adminService.getAllPatient();
		String message = null;
		if (patientList != null) {
			modelAndView.addObject("patientList", patientList);
			modelAndView.addObject("command",new Patient());
			modelAndView.setViewName("ShowAllPatientToRemove");
			return modelAndView;
		}
		else {
			message = "No Patient to delete !";

		modelAndView.addObject("message", message);
		modelAndView.setViewName("Output");
		return modelAndView;
		}
	}
	
	@RequestMapping("/removePatientMessage")
	public ModelAndView removePatientMessage(@ModelAttribute("command") Patient patient) {
		ModelAndView modelAndView = new ModelAndView();
		String message = null;
		if (adminService.removePatientFromDatabase(patient.getPatientId())) {
			message = "Patient deleted Successfully";
			modelAndView.addObject("message", message);
			modelAndView.setViewName("Output");
			return modelAndView;
		}
		else {
			message = "Patient deletion failed";
			modelAndView.addObject("message", message);
			modelAndView.setViewName("Output");
			return modelAndView;
		}
		
	}
	
	
	
//Show All Patients	
	@RequestMapping("/showAllPatients")
	public ModelAndView showAllPatients() {
		ModelAndView modelAndView = new ModelAndView();
		
		List<Patient> patientList = adminService.getAllPatient();
		String message = null;
		if (patientList != null) {
			modelAndView.addObject("patientList", patientList);
			modelAndView.setViewName("ShowAllPatient");
			return modelAndView;
		}
		else {
			message = "No Patient to show !";

		modelAndView.addObject("message", message);
		modelAndView.setViewName("Output");
		return modelAndView;
		}
	}
	
//  Admin Functionalities End ------------------------------------------------------------------------------------------------------------------------
	
	

}
