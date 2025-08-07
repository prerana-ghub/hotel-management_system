CREATE DATABASE hotel ;

use hotel;


CREATE TABLE Guest (
    Guest_ID INT PRIMARY KEY,
    Name VARCHAR(100),
    Contact_Info VARCHAR(255),
    Nationality VARCHAR(50),
    Gender VARCHAR(10),
    Reservation_History VARCHAR(255)
);


CREATE TABLE Hotel (
    Hotel_ID INT PRIMARY KEY,
    Name VARCHAR(100),
    Location VARCHAR(255),
    Num_Rooms INT,
    Rating DECIMAL(3, 1),
    Contact_Info VARCHAR(255)
);


CREATE TABLE Reservation (
    Reservation_ID INT PRIMARY KEY,
    Guest_ID INT,
    Hotel_ID INT,
    Check_in_Date DATE,
    FOREIGN KEY (Guest_ID) REFERENCES Guest(Guest_ID),
    FOREIGN KEY (Hotel_ID) REFERENCES Hotel(Hotel_ID)
);


CREATE TABLE Department (
    Department_ID INT PRIMARY KEY,
    D_Head VARCHAR(100),
    D_Role VARCHAR(100),
    Staff_Count INT,
    Contact_Info VARCHAR(255)
);


CREATE TABLE Staff (
    Staff_ID INT PRIMARY KEY,
    Name VARCHAR(100),
    Age INT,
    Contact_Info VARCHAR(255),
    Salary DECIMAL(10, 2),
    Department_ID INT,
    FOREIGN KEY (Department_ID) REFERENCES Department(Department_ID)
);


CREATE TABLE Room (
    Room_No INT PRIMARY KEY,
    Category VARCHAR(50),
    Rent DECIMAL(10, 2),
    Status VARCHAR(20)
);




DELIMITER ;

use hotel;

-- Trigger 1: Auto-update room status when a reservation is made
DELIMITER //
CREATE TRIGGER update_room_status
AFTER INSERT ON Reservation
FOR EACH ROW
BEGIN
    UPDATE Room
    SET Status = 'Booked'
    WHERE Room_No = NEW.Hotel_ID; -- Assuming one room per hotel for simplicity
END;
//
DELIMITER ;

-- Trigger 2: Auto-update hotel rating when a reservation is made
DELIMITER //
CREATE TRIGGER update_hotel_rating
AFTER INSERT ON Reservation
FOR EACH ROW
BEGIN
    UPDATE Hotel
    SET Rating = LEAST(Rating + 0.1, 5.0)
    WHERE Hotel_ID = NEW.Hotel_ID;
END;
// DELIMITER ;

-- Trigger 3: Prevent guest deletion if they have an active reservation
DELIMITER //
CREATE TRIGGER prevent_guest_deletion
BEFORE DELETE ON Guest
FOR EACH ROW
BEGIN
    DECLARE reservation_count INT;
    
    SELECT COUNT(*) INTO reservation_count
    FROM Reservation
    WHERE Guest_ID = OLD.Guest_ID;

    IF reservation_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete guest with active reservations';
    END IF;
END;
//
DELIMITER ;