package com.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bean.PatientLogin;

@Repository
public interface PatientLoginDao extends JpaRepository<PatientLogin, String> {
    
    @Query(value = "SELECT * FROM patient_login WHERE id = :id", nativeQuery = true)
    PatientLogin findPatientLoginById(@Param("id") String id);
} 