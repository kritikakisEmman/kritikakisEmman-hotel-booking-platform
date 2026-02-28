-- Stored Procedure για δημιουργία availability records για hotel_id=3 (New York)
-- Από 16/2/2026 έως 31/12/2026

DELIMITER $$

DROP PROCEDURE IF EXISTS CreateAvailabilityForHotel3$$

CREATE PROCEDURE CreateAvailabilityForHotel3()
BEGIN
    DECLARE current_date DATE;
    DECLARE end_date DATE;

    SET current_date = '2026-02-16';
    SET end_date = '2026-12-31';

    WHILE current_date <= end_date DO
        -- Presidential Suite (3 δωμάτια, 600€)
        INSERT INTO bookingapp.availability (available, date, room_name, room_price, type, hotel_id)
        VALUES (3, current_date, 'Presidential Suite', 600, 'Suite', 3);

        -- Executive Room (25 δωμάτια, 320€)
        INSERT INTO bookingapp.availability (available, date, room_name, room_price, type, hotel_id)
        VALUES (25, current_date, 'Executive Room', 320, 'Double', 3);

        -- City View Room (30 δωμάτια, 220€)
        INSERT INTO bookingapp.availability (available, date, room_name, room_price, type, hotel_id)
        VALUES (30, current_date, 'City View Room', 220, 'Single', 3);

        SET current_date = DATE_ADD(current_date, INTERVAL 1 DAY);
    END WHILE;

    SELECT CONCAT('Δημιουργήθηκαν ', COUNT(*), ' availability records!') as Result
    FROM bookingapp.availability
    WHERE hotel_id = 3;
END$$

DELIMITER ;

-- Εκτέλεση του stored procedure
CALL CreateAvailabilityForHotel3();

-- Διαγραφή του stored procedure (προαιρετικό)
DROP PROCEDURE IF EXISTS CreateAvailabilityForHotel3;
