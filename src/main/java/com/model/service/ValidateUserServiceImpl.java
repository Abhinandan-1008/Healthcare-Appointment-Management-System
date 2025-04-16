package com.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bean.AdminLogin;
import com.bean.DoctorLogin;
import com.bean.PatientLogin;
import com.model.persistence.AdminLoginDao;
import com.model.persistence.LoginDao;
import com.model.persistence.PatientLoginDao;

@Service
public class ValidateUserServiceImpl implements ValidateUserService {
	@Autowired
	private AdminLoginDao adminLoginDao;
	
	@Autowired
	private LoginDao doctorLoginDao;
	
	@Autowired
	private PatientLoginDao patientLoginDao;
	
	@Override
	public boolean isValid(String id, String password) {
		if (id == null || password == null) {
			return false;
		}
		
		// Trim whitespace and normalize the ID
		String normalizedId = id.trim();
		String normalizedPassword = password.trim();
		
		// Determine user type and check appropriate login table
		if (normalizedId.toUpperCase().startsWith("A")) {
			AdminLogin login = adminLoginDao.findAdminLoginById(normalizedId);
			return login != null && normalizedPassword.equals(login.getPassword());
		} else if (normalizedId.toUpperCase().startsWith("D")) {
			DoctorLogin login = doctorLoginDao.findDoctorLoginById(normalizedId);
			return login != null && normalizedPassword.equals(login.getPassword());
		} else if (normalizedId.toUpperCase().startsWith("P")) {
			PatientLogin login = patientLoginDao.findPatientLoginById(normalizedId);
			return login != null && normalizedPassword.equals(login.getPassword());
		}
		
		return false;
	}

	@Override
	public boolean registerUser(String id, String password) {
		if (id == null || password == null) {
			return false;
		}
		
		// Trim whitespace
		String normalizedId = id.trim();
		String normalizedPassword = password.trim();
		
		try {
			if (normalizedId.toUpperCase().startsWith("A")) {
				adminLoginDao.save(new AdminLogin(normalizedId, normalizedPassword));
			} else if (normalizedId.toUpperCase().startsWith("D")) {
				doctorLoginDao.save(new DoctorLogin(normalizedId, normalizedPassword));
			} else if (normalizedId.toUpperCase().startsWith("P")) {
				patientLoginDao.save(new PatientLogin(normalizedId, normalizedPassword));
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}