# Healthcare Appointment System - Fixes

## Issues Fixed

1. **Form Binding Issue**: Updated the form in ShowAvailableDoctorsSchedulePage.html to properly bind to the Schedule object.
2. **Controller Initialization**: Fixed the PatientController to properly initialize the command2 object.
3. **Error Handling**: Added better error handling in the AppointmentServiceImpl.
4. **Stored Procedure**: Created an updated version of the book_appointment stored procedure.

## How to Apply the Fixes

1. **Update the Stored Procedure**:
   - Open your MySQL client (e.g., MySQL Workbench)
   - Connect to your database
   - Run the SQL script in `src/main/resources/update_procedure.sql`

2. **Rebuild the Application**:
   ```
   mvn clean package
   ```

3. **Run the Application**:
   ```
   mvn spring-boot:run
   ```

## Testing the Appointment Booking

1. Login as a patient
2. Go to "Request Appointment"
3. Select a date
4. From the list of available doctors, enter a doctor ID in the form
5. Click "Book Appointment"

## Troubleshooting

If you still encounter issues:

1. Check the database tables to ensure they match the entity classes:
   - appointments
   - procedureappointment
   - patient
   - doctor
   - regular_schedule

2. Verify that the stored procedure was created successfully:
   ```sql
   SHOW PROCEDURE STATUS WHERE Name = 'book_appointment';
   ```

3. Check the application logs for any errors.

4. sql code for this is 
create database hospital;
use Hospital;


create table doctor(
doctor_id varchar(5) primary key,
name_of_doctor varchar(25) not null,
specialisation varchar(30) not null,
experience int not null,
gender char(1) not null,
age int not null,
Contact_number varchar(15) not null unique,
Address varchar(100)
);

create table patient(
patient_id varchar(5) primary key,
name_of_patient varchar(50) not null,
gender char(1) not null,
age int not null,
Contact_number varchar(15) not null,
Address varchar(100),
department varchar(50)
);


CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    name_of_patient VARCHAR(100) NOT NULL,
    slot TIME NOT NULL,
    date_of_appointment DATE NOT NULL,
    doctor_id VARCHAR(50) NOT NULL,
    name_of_doctor VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL
);



-- data

insert ignore into doctor(doctor_id, name_of_doctor, specialisation, experience, gender, age, contact_number, address)
values('D1000','Dr Pullen', 'Cardiologists', 5, 'm', 30 , '+1 582-558-3861', '1128, 3rd Floor, Fat No 13a Wings, Sanjay Society, Prabhadevi'),
	  ('D1001','Dr Fillmore', 'Anesthesiologists', 15, 'f', 50 , '+1 464-485-6722', '91, Shri Krishna Bhavan, Hindu Colony, Dadar(e)'),
      ('D1002','Dr Mangle', 'Dermatologists', 10, 'm', 45 , '+1 312-795-2062', ' 20/1, North Crescent Road, T Nagar'),
      ('D1003','Dr Ken Hurt', 'Neurologists', 8, 'm', 38 , '+1 319-730-5064', '18th Road, Opp Electric Office, Chembur (east)'),
      ('D1004','Dr B. Sick', 'Gastroenterologists', 3, 'f', 34 , '+1 218-654-7743', 'Central Lane, Bengali Market'),
      ('D1005','Dr Watamaniuk', 'Dermatologists', 11, 'm', 45 , '+1 202-411-8219', 'S 614, Nehru Enclave, School Block, Shakarpur'),
      ('D1006','Dr Lipp', 'Cardiologists', 25, 'f', 55 , '+1 305-270-1658', 'A-11, Part 2, Greater Kailash Enclave'),
      ('D1007','Dr Carey', 'Dermatologists', 5, 'f', 40 , '+1 225-897-3950', '106, Sarita Bldg, Near Dahisar Tol Naka, Dahisar'),
      ('D1008','Dr Nervo', 'Neurologists', 11, 'f', 47 , '+1 582-262-9538', 'Opp S B I,dr V S Road, Ambawadi'),
      ('D1009','Dr Hurt', 'Dermatologists', 10, 'm', 50 , '+1 434-723-6523', ' Samta Nagar Tel Exchange, Western Express Highway, Nr Santa');

insert ignore into patient(patient_id, name_of_patient, gender, age, contact_number, address, department)
values('P101','Bhuwnesh', 'm', 24, '9876987655', '11, Welcome Shpg Centre, Old Padra Road, Old Padra Road','Gastroenterologists'),
	  ('P102','Harsha','m', 23, '348628643',' G-3 Anmol, Ground Floor, Grant Road','Anesthesiologists'),
      ('P103','Krishna','f',22,'98647638', '5, Sait Clny 2nd St, Egmore','Cardiologists'),
      ('P104','Suyesh','m',23,'7860992748','20/30/a, Ganga Ind Estate, P L Lokhande Marg, Chembur','Neurologists');




-- appointment

create table patient_login(
id varchar(10) primary key,
password varchar(30)
);

create table doctor_login(
id varchar(10) primary key,
password varchar(30)
);

create table admin_login(
id varchar(10) primary key,
password varchar(30)
);


create table regular_schedule(
schedule_id int primary key auto_increment,
doctor_id varchar(5) not null,
name_of_doctor varchar(25) not null,
available_day varchar(10) not null,
slot_start time not null,
slot_end time not null,
constraint fk_docId foreign key(doctor_id)
references doctor(doctor_id)
);

insert ignore into regular_schedule(doctor_id,name_of_doctor,available_day,slot_start,slot_end)
values('D1000','Dr Pullen','Monday','10:00','12:00'),
	  ('D1001','Dr Fillmore','Monday','12:00','02:00'),
      ('D1002','Dr Mangle','Tuesday','10:00','1:00'),
      ('D1003','Dr Ken Hurt','Thursday','01:00','05:00'),
      ('D1004','Dr B. Sick','Saturday','12:00','03:00'),
      ('D1005','Dr Watamaniuk','Monday','05:00','08:00'),
      ('D1006','Dr Lipp','Tuesday','12:00','2:00'),
      ('D1007','Dr Carey','Friday','03:00','05:00'),
      ('D1008','Dr Nervo','Wednesday','04:00','06:00'),
      ('D1009','Dr Hurt','Sunday','12:00','2:00');
      
      
create table procedureAppointment(
Procedure_Id int primary key auto_increment,
Patient_id varchar(10),
Patient_Name varchar(30),
Starting_Time time,
Ending_Time time,
Doctor_id varchar(10),
Doctor_Name varchar(30),
Department varchar(30) 
);







DELIMITER $$
DROP PROCEDURE IF EXISTS book_appointment$$
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
    DECLARE patient_count INT;
    DECLARE doctor_count INT;
    DECLARE schedule_count INT;

    -- Check if patient exists
    SELECT COUNT(*) INTO patient_count
    FROM patient
    WHERE patient_id = id;

    IF patient_count = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Patient not found with ID: ';
    END IF;

    -- Check if doctor exists
    SELECT COUNT(*) INTO doctor_count
    FROM doctor
    WHERE doctor_id = doc_id;

    IF doctor_count = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Doctor not found with ID: ' ;
    END IF;

    -- Get patient information
    SELECT patient_id, name_of_patient, department 
    INTO pId, pName, pDept
    FROM patient
    WHERE patient_id = id;

    -- Get doctor information
    SELECT doctor_id, name_of_doctor 
    INTO dId, dName
    FROM doctor
    WHERE doctor_id = doc_id;

    -- Check if doctor has schedule
    SELECT COUNT(*) INTO schedule_count
    FROM regular_schedule 
    WHERE doctor_id = dId;

    IF schedule_count = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No schedule found for doctor: ';
    END IF;

    -- Get doctor's schedule
    SELECT slot_start, slot_end 
    INTO slot_s, slot_e
    FROM regular_schedule 
    WHERE doctor_id = dId;

    -- Check if slot is already booked
    IF EXISTS (
        SELECT 1 FROM appointments 
        WHERE doctor_id = dId 
        AND date_of_appointment = CURDATE() 
        AND slot = slot_s
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Slot already booked for doctor: ' ;
    END IF;

    -- Insert into appointments table
    INSERT INTO appointments
    (patient_id, name_of_patient, slot, date_of_appointment, doctor_id, name_of_doctor, department)
    VALUES
    (pId, pName, slot_s, CURDATE(), dId, dName, pDept);

    -- Insert into procedureappointment
    INSERT INTO procedureappointment 
    (Patient_id, Patient_Name, Starting_Time, Ending_Time, Doctor_id, Doctor_Name, Department)
    VALUES 
    (pId, pName, slot_s, slot_e, dId, dName, pDept);

END$$
DELIMITER ;



TRUNCATE TABLE admin_login;
select* from admin_login;
insert into admin_login
values
("A101","A101");

TRUNCATE TABLE patient_login;
select* from patient_login;
insert into patient_login
values
("P101","P101"),
("P102","P102"),
("P103","P103"),
("P104","P104");

TRUNCATE TABLE doctor_login;
select* from doctor_login;
INSERT IGNORE INTO doctor_login
VALUES 
("D1000", "D1000"),
("D1001", "D1001"),
("D1002", "D1002"),
("D1003", "D1003"),
("D1004", "D1004"),
("D1005", "D1005"),
("D1006", "D1006"),
("D1007", "D1007"),
("D1008", "D1008"),
("D1009", "D1009");
