-- Fix login credentials table
-- First, let's check the current data
SELECT * FROM login_credentials;

-- Update the patient IDs to ensure consistent format (P followed by 3 digits)
UPDATE login_credentials 
SET id = CONCAT('P', LPAD(SUBSTRING(id, 2), 3, '0'))
WHERE id LIKE 'P%' AND LENGTH(id) < 5;

-- Update the patient IDs that have spaces
UPDATE login_credentials 
SET id = REPLACE(id, ' ', '')
WHERE id LIKE '% %';

-- Update passwords to match IDs (if that's the intended behavior)
UPDATE login_credentials 
SET password = id
WHERE id LIKE 'P%';

-- Fix specific patient IDs mentioned in the issue
UPDATE login_credentials 
SET id = 'P001', password = 'P001'
WHERE id = 'PIOI';

UPDATE login_credentials 
SET id = 'P102', password = 'P102'
WHERE id = 'P 102';

UPDATE login_credentials 
SET id = 'P103', password = 'P103'
WHERE id = 'P 103';

UPDATE login_credentials 
SET id = 'P104', password = 'P104'
WHERE id = 'p 104';

-- Verify the changes
SELECT * FROM login_credentials;

-- If needed, insert missing patient credentials
INSERT INTO login_credentials (id, password)
SELECT p.patient_id, p.patient_id
FROM patient p
LEFT JOIN login_credentials lc ON p.patient_id = lc.id
WHERE lc.id IS NULL;

-- Verify all patients have login credentials
SELECT p.patient_id, lc.id
FROM patient p
LEFT JOIN login_credentials lc ON p.patient_id = lc.id
WHERE lc.id IS NULL; 