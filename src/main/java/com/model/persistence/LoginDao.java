package com.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bean.DoctorLogin;
import com.bean.PatientLogin;

@Repository
public interface LoginDao extends JpaRepository<DoctorLogin, String> {

	@Query(value = "SELECT * FROM doctor_login WHERE id = :id", nativeQuery = true)
	DoctorLogin findDoctorLoginById(@Param("id") String id);
}