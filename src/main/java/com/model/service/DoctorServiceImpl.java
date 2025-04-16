package com.model.service;

import java.sql.Date;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.Appointment;
import com.bean.Doctor;
import com.bean.Patient;
import com.bean.Schedule;
import com.bean.Login;
import com.bean.DoctorLogin;
import com.model.persistence.AppointmentDao;
import com.model.persistence.DoctorDao;
import com.model.persistence.LoginDao;
import com.model.persistence.PatientDao;
import com.model.persistence.ScheduleDao;

@Service
public class DoctorServiceImpl implements DoctorService {

	private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);
	
	@Autowired
	private DoctorDao doctorDao;
	
	@Autowired
	private ScheduleDao scheduleDao;
	
	@Autowired
	private AppointmentDao appointmentDao;
	
	@Autowired
	private LoginDao loginDao;
	
	@Override
	public List<Doctor> getAllDoctor() {
		// TODO Auto-generated method stub
		return doctorDao.findAll();
	}

	@Override
	public boolean addDoctor(Doctor doctor) {
		if (doctor == null) {
			throw new IllegalArgumentException("Doctor cannot be null");
		}
		
		try {
			System.out.println("Starting doctor registration process...");
			Doctor doctor2 = new Doctor();
			String doctorId = setDoctorId();
			System.out.println("Generated doctor ID: " + doctorId);
			doctor2.setDoctorId(doctorId);
			doctor2.setDoctorName(doctor.getDoctorName());
			
			// Convert gender to single character (m/f)
			String gender = doctor.getDoctorGender();
			if (gender != null) {
				if (gender.equalsIgnoreCase("Male")) {
					doctor2.setDoctorGender("m");
				} else if (gender.equalsIgnoreCase("Female")) {
					doctor2.setDoctorGender("f");
				} else {
					doctor2.setDoctorGender(gender.substring(0, 1).toLowerCase());
				}
			}
			
			doctor2.setDoctorAge(doctor.getDoctorAge());
			doctor2.setDoctorAddress(doctor.getDoctorAddress());
			doctor2.setDoctorContact(doctor.getDoctorContact());
			doctor2.setDoctorDepartment(doctor.getDoctorDepartment());
			doctor2.setDoctorExperience(doctor.getDoctorExperience());
			
			System.out.println("Attempting to save doctor to database...");
			Doctor savedDoctor = doctorDao.save(doctor2);
			System.out.println("Doctor saved successfully: " + (savedDoctor != null));
			return savedDoctor != null && savedDoctor.getDoctorId() != null;
		} catch (Exception e) {
			System.out.println("Error adding doctor: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Transactional
	public boolean deleteDoctor(String doctorId) {
		logger.info("Starting deletion process for doctor ID: {}", doctorId);
		
		Doctor doctor = getDoctorById(doctorId);
		if (doctor != null) {
			try {
				// Delete doctor's login record first
				logger.info("Attempting to delete login record for doctor ID: {}", doctorId);
				DoctorLogin doctorLogin = loginDao.findDoctorLoginById(doctorId);
				if (doctorLogin != null) {
					loginDao.delete(doctorLogin);
					logger.info("Successfully deleted login record for doctor ID: {}", doctorId);
				} else {
					logger.info("No login record found for doctor ID: {}", doctorId);
				}
				
				// Delete related appointments
				logger.info("Attempting to delete appointments for doctor ID: {}", doctorId);
				List<Appointment> appointments = appointmentDao.getAllAppointmentsByDoctorId(doctorId);
				if (appointments != null && !appointments.isEmpty()) {
					appointments.forEach(appointment -> {
						appointmentDao.deleteById(appointment.getAppointmentId());
						logger.info("Deleted appointment ID: {} for doctor ID: {}", appointment.getAppointmentId(), doctorId);
					});
				} else {
					logger.info("No appointments found for doctor ID: {}", doctorId);
				}
				
				// Delete related schedule
				logger.info("Attempting to delete schedule for doctor ID: {}", doctorId);
				List<Schedule> schedules = scheduleDao.findByDoctorId(doctorId);
				if (!schedules.isEmpty()) {
					schedules.forEach(schedule -> scheduleDao.deleteById(schedule.getScheduleId()));
					logger.info("Successfully deleted schedules for doctor ID: {}", doctorId);
				} else {
					logger.info("No schedule found for doctor ID: {}", doctorId);
				}
				
				// Finally delete the doctor
				logger.info("Attempting to delete doctor record for ID: {}", doctorId);
				doctorDao.deleteById(doctorId);
				logger.info("Successfully deleted doctor with ID: {}", doctorId);
				return true;
			} catch (Exception e) {
				logger.error("Error during doctor deletion process: {}", e.getMessage(), e);
				e.printStackTrace();
				return false;
			}
		} else {
			logger.warn("Doctor not found with ID: {}", doctorId);
			return false;
		}
	}

	@Override
	public Doctor getDoctorById(String doctorId) {
		return doctorDao.findById(doctorId).orElse(null);
	}

	@Override
	public String getLastDoctorId() {
		try {
			System.out.println("Finding top doctor by ID...");
			Doctor doctor = doctorDao.findTopByOrderByDoctorIdDesc();
			if (doctor != null && doctor.getDoctorId() != null) {
				System.out.println("Found doctor with ID: " + doctor.getDoctorId());
				return doctor.getDoctorId();
			}
			System.out.println("No doctors found in database");
			return null; // no doctors found yet
		} catch (Exception e) {
			System.out.println("Error finding last doctor ID: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String setDoctorId() {
		try {
			System.out.println("Getting last doctor ID...");
			String lastId = getLastDoctorId();
			System.out.println("Last doctor ID: " + lastId);
			
			if (lastId != null && !lastId.isEmpty()) {
				try {
					int id = Integer.parseInt(lastId.substring(1));
					++id;
					String newId = "D" + id;
					System.out.println("Generated new doctor ID: " + newId);
					return newId;
				} catch (NumberFormatException e) {
					System.out.println("Error parsing doctor ID: " + e.getMessage());
					// Log this issue in real applications
					return "D101"; // fallback
				}
			} else {
				System.out.println("No existing doctors found, using default ID: D101");
				return "D101"; // first doctor
			}
		} catch (Exception e) {
			System.out.println("Error generating doctor ID: " + e.getMessage());
			e.printStackTrace();
			return "D101"; // fallback in case of any error
		}
	}
	
	@Override
	public List<Schedule> getDoctorSchedule(String doctorId) {
		return scheduleDao.findByDoctorId(doctorId);
	}
	
	@Override
	public List<Appointment> getMyAppointments(String id) {
		return appointmentDao.getAllAppointmentsByDoctorId(id);
	}

	@Override
	public List<Schedule> getAvailableDoctors(Date date) {
		Format f = new SimpleDateFormat("EEEE");  
		String day = f.format(date);
		return scheduleDao.getAvailableDoctors(day);
	}

	@Override
	public boolean addSchedule(Schedule schedule) {
		try {
			logger.info("Adding schedule for doctor ID: {}", schedule.getDoctorId());
			
			// Get doctor name if not set
			if (schedule.getNameOfDoctor() == null || schedule.getNameOfDoctor().trim().isEmpty()) {
				Doctor doctor = getDoctorById(schedule.getDoctorId());
				if (doctor != null) {
					schedule.setNameOfDoctor(doctor.getDoctorName());
				}
			}
			
			Schedule savedSchedule = scheduleDao.save(schedule);
			if (savedSchedule != null) {
				logger.info("Successfully added schedule for doctor ID: {}", schedule.getDoctorId());
				return true;
			} else {
				logger.error("Failed to save schedule for doctor ID: {}", schedule.getDoctorId());
				return false;
			}
		} catch (Exception e) {
			logger.error("Error adding schedule: {}", e.getMessage(), e);
			return false;
		}
	}
}