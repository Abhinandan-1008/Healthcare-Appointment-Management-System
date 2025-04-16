package com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bean.Doctor;
import com.bean.Patient;
import com.model.service.DoctorService;
import com.model.service.PatientService;
import com.model.service.ValidateUserService;

@Controller
public class ValidationController {
	
	@Autowired
	private ValidateUserService validateUserService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private DoctorService doctorService;
	
	@RequestMapping("/validate")
	public ModelAndView validationController(HttpServletRequest request, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		
		String userName = request.getParameter("user");
		String password = request.getParameter("pass");

		session.setAttribute("userName", userName);
		
		if(validateUserService.isValid(userName, password)) {
			if(userName.toUpperCase().charAt(0) == 'A') {
				modelAndView.setViewName("adminPostLogin");
			} else if(userName.toUpperCase().charAt(0) == 'D') {
				// Get doctor's name and store it in session
				Doctor doctor = doctorService.getDoctorById(userName);
				if (doctor != null) {
					session.setAttribute("name", doctor.getDoctorName());
				}
				modelAndView.setViewName("doctorPostLogin");
			} else if(userName.toUpperCase().charAt(0) == 'P') {
				modelAndView.setViewName("patientPostLogin");
			}
		} else {
			modelAndView.addObject("message", "Invalid Credentials");
			modelAndView.setViewName("Login");
		}
		
		return modelAndView;
	}
	
	
//  Registering Patient--------------------------------------------------------------------------------------------------------------------------------------
	
	@RequestMapping("/inputDetailsToRegisterPatient")
	public ModelAndView inputDetailsToRegisterPatientController(HttpServletRequest request, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		
		try {
			Patient patient = new Patient();
			patient.setPatientName(request.getParameter("pName"));
			patient.setPatientAge(Integer.parseInt(request.getParameter("pAge")));
			patient.setPatientGender(request.getParameter("pGender"));
			patient.setPatientContact(request.getParameter("pContact"));
			patient.setPatientAddress(request.getParameter("pAddress"));
			patient.setPatientSymptoms(request.getParameter("pSymptom"));
			
			System.out.println("Attempting to register patient: " + patient.getPatientName());
			
			Patient newPatient = patientService.addPatient(patient);
			
			if (newPatient != null) {
				session.setAttribute("userName", newPatient.getPatientId());
				modelAndView.addObject("message", "Your Patient ID for login is: " + newPatient.getPatientId());
				modelAndView.setViewName("inputDetailsToRegisterPatient");
				System.out.println("Successfully registered patient with ID: " + newPatient.getPatientId());
			} else {
				modelAndView.addObject("message", "Failed to register Patient. Please try again.");
				modelAndView.setViewName("patientRegister");
				System.out.println("Failed to register patient - returned null");
			}
		} catch (Exception e) {
			System.out.println("Error registering patient: " + e.getMessage());
			e.printStackTrace();
			modelAndView.addObject("message", "An error occurred during registration: " + e.getMessage());
			modelAndView.setViewName("patientRegister");
		}
		
		return modelAndView;
	}

	@RequestMapping("/registerPatient")
	public ModelAndView registerPatientController(HttpServletRequest request,HttpSession session) {
		
		String password = request.getParameter("password");
		String userName = (String) session.getAttribute("userName");
		
		String message;
		if(validateUserService.registerUser(userName, password)) {
			message = "Patient Registered to Database Successfully";
		}else {
			message = "Failed to register Patient. Please try again after some time.";
		}
		
		return new ModelAndView("Output", "message", message);
	}
}