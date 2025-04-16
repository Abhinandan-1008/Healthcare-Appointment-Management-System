-- Drop the existing procedure if it exists
DROP PROCEDURE IF EXISTS book_appointment;

-- Create the updated procedure
DELIMITER $$
CREATE PROCEDURE book_appointment(
    IN id VARCHAR(5), 
    IN doc_id VARCHAR(5)
)
BEGIN
    DECLARE pId VARCHAR(5);
    DECLARE pName VARCHAR(25);
    DECLARE pDept VARCHAR(50);
    DECLARE slot_S TIME;
    DECLARE slot_e TIME;
    DECLARE dId VARCHAR(5);
    DECLARE dName VARCHAR(50);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error occurred during appointment booking';
    END;

    START TRANSACTION;

    -- Get patient information
    SELECT patient_id, name_of_patient, department 
    INTO pId, pName, pDept
    FROM patient
    WHERE patient_id = id;

    IF pId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Patient not found with ID: ' || id;
    END IF;

    -- Get doctor information
    SELECT doctor_id, name_of_doctor 
    INTO dId, dName
    FROM doctor
    WHERE doctor_id = doc_id;

    IF dId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Doctor not found with ID: ' || doc_id;
    END IF;

    -- Get doctor's schedule
    SELECT slot_start, slot_end 
    INTO slot_s, slot_e
    FROM regular_schedule 
    WHERE doctor_id = dId;

    IF slot_s IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Doctor schedule not found';
    END IF;

    -- Insert into procedureappointment
    INSERT INTO procedureappointment 
    (Patient_id, Patient_Name, Starting_Time, Ending_Time, Doctor_id, Doctor_Name, Department)
    VALUES 
    (pId, pName, slot_s, slot_e, dId, dName, pDept);

    COMMIT;
END$$
DELIMITER ; 