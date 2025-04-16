package com.model.persistence;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bean.Schedule;

@Repository
public interface ScheduleDao extends JpaRepository<Schedule, Integer> {

	@Query(value = "SELECT * FROM regular_schedule WHERE doctor_id = :doctorId", nativeQuery = true)
	List<Schedule> findByDoctorId(@Param("doctorId") String doctorId);

	@Modifying
	@Transactional
	@Query(value = "delete from Schedule where doctorId = :doctorId")
	void deleteScheduleByDoctorId(@Param("doctorId") String doctorId);

	@Query(value = "SELECT * FROM regular_schedule WHERE available_day = :day", nativeQuery = true)
	List<Schedule> getAvailableDoctors(@Param("day") String day);
}
