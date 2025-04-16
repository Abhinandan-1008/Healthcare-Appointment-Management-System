package com.model.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.Appointment;
import com.bean.Patient;
import com.bean.PatientLogin;
import com.model.persistence.AppointmentDao;
import com.model.persistence.PatientDao;
import com.model.persistence.PatientLoginDao;

@Service
public class PatientServiceImpl implements PatientService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private PatientLoginDao loginDao;

    @Override
    public List<Patient> getAllPatient() {
        return patientDao.findAll();
    }

    @Override
    @Transactional
    public Patient addPatient(Patient patient) {
        if (patient == null) {
            logger.error("Patient object is null");
            throw new IllegalArgumentException("Patient cannot be null");
        }

        try {
            String patientId = setNewPatientId();
            patient.setPatientId(patientId);
            
            logger.info("Attempting to save patient with details: ");
            logger.info("Patient ID: " + patientId);
            logger.info("Name: " + patient.getPatientName());
            logger.info("Age: " + patient.getPatientAge());
            logger.info("Gender: " + patient.getPatientGender());
            logger.info("Contact: " + patient.getPatientContact());
            logger.info("Address: " + patient.getPatientAddress());
            logger.info("Symptoms: " + patient.getPatientSymptoms());
            
            Patient savedPatient = patientDao.save(patient);
            logger.info("Successfully saved patient to database");
            
            try {
                PatientLogin login = new PatientLogin();
                login.setId(patientId);
                login.setPassword(patientId);
                loginDao.save(login);
                logger.info("Successfully created login credentials for patient");
            } catch (Exception e) {
                logger.error("Error creating login credentials: " + e.getMessage());
                throw e;
            }
            
            return savedPatient;
        } catch (Exception e) {
            logger.error("Error in patient registration: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deletePatient(String patientId) {
        Patient patient = getPatientById(patientId);
        if (patient != null) {
            try {
                // Delete patient's login record first
                PatientLogin patientLogin = loginDao.findPatientLoginById(patientId);
                if (patientLogin != null) {
                    loginDao.delete(patientLogin);
                }
                
                // Delete related appointments
                List<Appointment> appointments = appointmentDao.getAllAppointmentsByPatientId(patientId);
                if (appointments != null) {
                    appointments.forEach(appointment -> appointmentDao.deleteById(appointment.getAppointmentId()));
                }
                
                // Finally delete the patient
                patientDao.deleteById(patientId);
                return true;
            } catch (Exception e) {
                logger.error("Error deleting patient: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public Patient getPatientById(String patientId) {
        return patientDao.findById(patientId).orElse(null);
    }

    @Override
    public String getLastPatientId() {
        Patient patient = patientDao.findTopByOrderByPatientIdDesc();
        if (patient != null && patient.getPatientId() != null) {
            return patient.getPatientId();
        }
        return null; // no patients found yet
    }

    @Override
    public String setNewPatientId() {
        String lastId = getLastPatientId();
        if (lastId != null && lastId.length() > 1) {
            try {
                int id = Integer.parseInt(lastId.substring(1));
                ++id;
                return "P" + id;
            } catch (NumberFormatException e) {
                // Log this issue in real applications
                return "P101"; // fallback
            }
        } else {
            return "P101"; // first patient
        }
    }

    @Override
    public boolean rescheduleAppointment(int aid, Date newDate) {
        int rows = appointmentDao.reschedule(aid, newDate);
        return rows > 0;
    }

    @Override
    public List<Appointment> getMyAppointments(String pid) {
        return appointmentDao.getAllAppointmentsByPatientId(pid);
    }

    @Override
    public boolean cancelAppointmentRequest(int aid) {
        Optional<Appointment> appointment = appointmentDao.findById(aid);
        if (appointment.isPresent()) {
            appointmentDao.deleteById(aid);
            return true;
        }
        return false;
    }
}
