package com.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bean.AdminLogin;

@Repository
public interface AdminLoginDao extends JpaRepository<AdminLogin, String> {
    
    @Query(value = "SELECT * FROM admin_login WHERE id = :id", nativeQuery = true)
    AdminLogin findAdminLoginById(@Param("id") String id);
} 