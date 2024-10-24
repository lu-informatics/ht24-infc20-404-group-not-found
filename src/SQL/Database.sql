CREATE TABLE Employee (
   EmployeeNo INT IDENTITY(1, 1), -- Surrogate Key
   EmployeeID INT,  -- Natural key 
   FirstName VARCHAR(20) NOT NULL, -- NOT NULL because it is composite from Name
   LastName VARCHAR(20) NOT NULL,
   CONSTRAINT PK_Employee_EmployeeNo PRIMARY KEY (EmployeeNo),
   CONSTRAINT UQ_Employee_EmployeeID UNIQUE (EmployeeID), --- Setting natural key to unique
);

CREATE TABLE Receipt (
   ReceiptNo INT IDENTITY(1, 1), -- Surrogate Key
   ReceiptDate DATE, -- In built date to get date
   TotalPrice DECIMAL (10, 2),
   EmployeeNo INT,
   CONSTRAINT PK_Receipt_ReceiptNo PRIMARY KEY (ReceiptNo),
   CONSTRAINT FK_Receipt_EmployeeNo FOREIGN KEY (EmployeeNo) REFERENCES Employee(EmployeeNo),
);
CREATE TABLE Drink (
   DrinkNo INT IDENTITY(1, 1), -- Surrogate Key
   DrinkID INT, -- NATURAL KEY
   DrinkName VARCHAR(20),
   DrinkStock INT,
   DrinkPrice DECIMAL (10, 2),
   DrinkType VARCHAR (20) NOT NULL,
   CONSTRAINT PK_Drink_DrinkNo PRIMARY KEY (DrinkNo),
   CONSTRAINT UQ_Drink_DrinkID UNIQUE (DrinkID), --UQ because natural key
);
CREATE TABLE Dish (
   DishNo INT IDENTITY(1, 1), -- Surrogate Key
   DishID INT, -- natural key
   DishName VARCHAR (20),
   DishStock INT,
   DishPrice DECIMAL (10, 2),
   DishType VARCHAR (20) NOT NULL,
   CONSTRAINT PK_Dish_DishNo PRIMARY KEY (DishNo),
   CONSTRAINT UQ_Dish_DishID UNIQUE (DishID), -- UQ because natural key
);

CREATE TABLE ConsistOf ( -- Create table consitof
   DrinkNo INT,
   DishNo INT,
   ReceiptNo INT,
   CONSTRAINT PK_ConsistOf_DrinkNo_DishNo_ReceiptNo PRIMARY KEY (DrinkNo, DishNo, ReceiptNo), -- Compostie primary key because of M:N:P Relation
   CONSTRAINT FK_ConsistOf_DrinkNo FOREIGN KEY (DrinkNo) REFERENCES Drink(DrinkNo)
       ON DELETE CASCADE, -- To allow deletion and making procedure and trigger further down possible
   CONSTRAINT FK_ConsistOf_DishNo FOREIGN KEY (DishNo) REFERENCES Dish(DishNo)
       ON DELETE CASCADE, -- Same as DrinkNo
   CONSTRAINT FK_ConsistOf_ReceiptNo FOREIGN KEY (ReceiptNo) REFERENCES Receipt(ReceiptNo)
);


BEGIN TRANSACTION -- Inserting all are values with the use of an transaction
INSERT INTO Employee(EmployeeID, FirstName, LastName) VALUES
  (101, 'Gary', 'Stu'),
  (102, 'Mary', 'Sue'),
  (103, 'Larry', 'Que')
INSERT INTO Drink(DrinkID, DrinkName, Drinkstock, DrinkPrice, DrinkType) VALUES
  (201, 'Pepsi', 10, 19.90, 'ColdDrink'),
  (202, 'Cola', 10, 19.90, 'ColdDrink'),
  (203, 'Fanta', 10, 19.90, 'ColdDrink'),
  (204, 'Beer', 10, 25.90, 'AlcoholicDrink'),
  (205, 'Wine', 10, 25.90, 'AlcoholicDrink'),
  (206, 'Cocktail', 10, 25.90, 'AlcoholicDrink'),
  (207, 'Hot Chocolate', 10, 15.90, 'WarmDrink'),
  (208, 'Coffee', 10, 15.90, 'WarmDrink'),
  (209, 'Tee', 10, 15.90, 'WarmDrink')
INSERT INTO Dish(DishID, DishName, DishStock, DishPrice, DishType) VALUES
  (301, 'Beef Tartar', 10, 59.90, 'StarterDish'),
  (302, 'Toast Skagen', 10, 59.90, 'StarterDish'),
  (303, 'Lobster Soup', 10, 59.90, 'StarterDish'),
  (304, 'Beef Wellington', 10, 250, 'MainDish'),
  (305, 'Meatballs', 10, 250, 'MainDish'),
  (306, 'Schnitzel', 10, 250, 'MainDish'),
  (307, 'Chocolate Fondant', 10, 89.90, 'DessertDish'),
  (308, 'Crème brûlée', 10, 89.90, 'DessertDish'),
  (309, 'Tiramisu', 10, 89.90, 'DessertDish')
  COMMIT



CREATE  OR ALTER PROCEDURE uspPrintDrinks -- Creating a proceude that print all our drinks
AS
BEGIN
    BEGIN TRY

        BEGIN TRANSACTION;  -- This uses transaction aswell allowing for rollback which is present in all procedures to handle error in a effective way

   
        SELECT 
            DrinkName,
            DrinkStock,
            DrinkPrice,
            DrinkType
        FROM Drink;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;


CREATE OR ALTER PROCEDURE uspPrintDishes  -- Procedure that prints all dishes 
AS
BEGIN
    BEGIN TRY
   
        BEGIN TRANSACTION; 

      
        SELECT 
            DishName,
            DishStock,
            DishPrice,
            DishType
        FROM Dish;

      
        COMMIT TRANSACTION;
    END TRY
     BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;


CREATE OR ALTER PROCEDURE uspDoesEmployeeExist -- Procedure to check wheter an employee exits 
    @EmployeeID INT,
    @Exists BIT OUTPUT -- Output parameter that indicates if the employee exists (1 = Yes, 0 = No)
AS
BEGIN
    BEGIN TRANSACTION; 
    BEGIN TRY
	-- Check if an employee with the given ID exists in the Employee table
        IF EXISTS (SELECT 1 FROM Employee WHERE EmployeeID = @EmployeeID) -- Selects if there it atleast 1 row with the right ID
        BEGIN
            SET @Exists = 1;  -- If found, set @Exists to 1 (true)
        END
        ELSE
        BEGIN
            SET @Exists = 0;  -- If not found, set @Exists to 0 (false)
        END
		 -- Commit the transaction if everything is successful
        COMMIT TRANSACTION; 
    END TRY
     BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;


CREATE OR ALTER PROCEDURE uspDoesDrinkExist  -- Procedure to check wheter a drink exits it uses the same logic the uspDoesEmployeeExist
    @DrinkName VARCHAR(20),
    @Exists BIT OUTPUT
AS
BEGIN
    BEGIN TRANSACTION; 
    BEGIN TRY
        IF EXISTS (SELECT 1 FROM Drink WHERE DrinkName = @DrinkName)
        BEGIN
            SET @Exists = 1; 
        END
        ELSE
        BEGIN
            SET @Exists = 0; 
        END

        COMMIT TRANSACTION; 
    END TRY
     BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;

CREATE OR ALTER PROCEDURE uspDoesDishExist -- Checks wheter or not a dish exits and uses the same logic at the previos two procedures
    @DishName VARCHAR(20),
    @Exists BIT OUTPUT
AS
BEGIN
    BEGIN TRANSACTION; 
    BEGIN TRY
        IF EXISTS (SELECT 1 FROM Dish WHERE DishName = @DishName)
        BEGIN
            SET @Exists = 1; 
        END
        ELSE
        BEGIN
            SET @Exists = 0; 
        END

        COMMIT TRANSACTION; 
    END TRY
   BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;


 CREATE OR ALTER PROCEDURE uspInsertAndPrintReceipt
    @DrinkName VARCHAR(20),  -- Input: Name of the drink, dish and empID
    @DishName VARCHAR(20) ,
    @EmployeeID INT
AS
BEGIN
    BEGIN TRANSACTION; -- transaction to ensure all operations succeed or none at all
    BEGIN TRY
	-- Declaring the different variables that will be used later in the procedure 
        DECLARE @ReceiptNo INT; -- Holds reciptNo after insertion
        DECLARE @DrinkNo INT; -- Holds drinkNO when drink is provided
        DECLARE @DishNo INT; -- Holds dishNo when dish is provided
        DECLARE @DrinkPrice DECIMAL(10, 2);
        DECLARE @DishPrice DECIMAL(10, 2);
        DECLARE @TotalPrice DECIMAL(10, 2) = 0; -- Calculates total price therefore set to zero
        DECLARE @DrinkStock INT;
        DECLARE @DishStock INT;
       
	   -- If a drink is provided, get the drink details from the Drink table
        IF @DrinkName IS NOT NULL
        BEGIN
            SELECT @DrinkNo = DrinkNo, @DrinkPrice = DrinkPrice, @DrinkStock = DrinkStock FROM Drink WHERE DrinkName = @DrinkName;
            
            IF @DrinkStock <= 0  -- Check if the drink is out of stock
            BEGIN
                RAISERROR('Cannot sell %s. Stock is zero.', 16, 1, @DrinkName);
                RETURN; 
            END
        END
		-- Same as with drink
        IF @DishName IS NOT NULL
        BEGIN
            SELECT @DishNo = DishNo, @DishPrice = DishPrice, @DishStock = DishStock FROM Dish WHERE DishName = @DishName;
          
            IF @DishStock <= 0
            BEGIN
                RAISERROR('Cannot sell %s. Stock is zero.', 16, 1, @DishName);
                RETURN; 
            END
        END
        -- Calculate the total price by adding the drink price and dish price
        SET @TotalPrice = @DrinkPrice + @DishPrice;
        -- Insert a new receipt into the Receipt table
        INSERT INTO Receipt (ReceiptDate, TotalPrice, EmployeeNo)
        VALUES (GETDATE(), @TotalPrice, (SELECT EmployeeNo FROM Employee WHERE EmployeeID = @EmployeeID));
      -- Get the generated ReceiptNo (identity value) from the last inserted receipt
        SET @ReceiptNo = SCOPE_IDENTITY();
    -- Insert into ConsistOf table to link the drink, dish, and receipt together
        INSERT INTO ConsistOf (DrinkNo, DishNo, ReceiptNo)
        VALUES (@DrinkNo, @DishNo, @ReceiptNo);

     -- If a drink was included, reduce its stock by 1
        IF @DrinkNo IS NOT NULL
        BEGIN
            UPDATE Drink
            SET DrinkStock = DrinkStock - 1
            WHERE DrinkNo = @DrinkNo;
        END
		-- If a dish was included, reduce its stock by 1
        IF @DishNo IS NOT NULL
        BEGIN
            UPDATE Dish
            SET DishStock = DishStock - 1
            WHERE DishNo = @DishNo;
        END
     
        COMMIT TRANSACTION;   -- Commit the transaction if everything was successful

 SELECT    -- This makes the recipts that will be displayed
    Receipt.ReceiptNo,                                  -- Receipt number
    Employee.EmployeeID,                                    -- Employee ID
    Receipt.ReceiptDate,                                    -- Date of the receipt
    Employee.FirstName + ' ' + Employee.LastName AS EmployeeFullName, -- Employee's full name
    Receipt.TotalPrice,                                     -- Total price of the receipt
    Drink.DrinkName,                                        -- Name of the drink
    Dish.DishName,                                          -- Name of the dish
    Drink.DrinkPrice,                                       -- Price of the drink
    Dish.DishPrice                                          -- Price of the dish
FROM Receipt 
JOIN Employee ON Receipt.EmployeeNo = Employee.EmployeeNo   -- Join Receipt with Employee
LEFT JOIN ConsistOf ON Receipt.ReceiptNo = ConsistOf.ReceiptNo -- Join Receipt with ConsistOf
LEFT JOIN Drink ON ConsistOf.DrinkNo = Drink.DrinkNo        -- Join with Drink table to get drink info
LEFT JOIN Dish ON ConsistOf.DishNo = Dish.DishNo            -- Join with Dish table to get dish info
WHERE Receipt.ReceiptNo = @ReceiptNo;   -- Filter by the specified receipt number
    END TRY
     BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;




CREATE OR ALTER PROCEDURE uspGetOldReceipts
AS 
BEGIN
    -- Retrieve receipt details along with employee, drink, and dish information
    BEGIN TRANSACTION;
    BEGIN TRY
      
        SELECT
            Receipt.ReceiptNo,
            Employee.EmployeeID,
            Receipt.ReceiptDate,
            Employee.FirstName + ' ' + Employee.LastName AS EmployeeName,
            Receipt.TotalPrice,
            Drink.DrinkName,
            Dish.DishName,
            Drink.DrinkPrice,
            Dish.DishPrice
        FROM 
            Receipt
        JOIN 
            Employee ON Receipt.EmployeeNo = Employee.EmployeeNo
        JOIN 
            ConsistOf ON Receipt.ReceiptNo = ConsistOf.ReceiptNo
        JOIN 
            Drink ON ConsistOf.DrinkNo = Drink.DrinkNo
        JOIN 
            Dish ON ConsistOf.DishNo = Dish.DishNo
        ORDER BY 
            Receipt.ReceiptDate DESC;  
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH

    ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;






CREATE TABLE ##DeletedItems ( -- Temptable to store deleted dishes and drinks in
    ItemNo INT,            -- can be either drink or dish
    ItemID INT,          
    ItemName VARCHAR(20),  
    ItemType VARCHAR(20),  
    DeletedDate DATETIME DEFAULT GETDATE() -- Built in
);


CREATE TRIGGER trgAfterDeleteDrink -- TRIGGER that triggers after delete on drink and inserts the deleted drink in temptable
ON Drink
AFTER DELETE
AS
BEGIN
  
    INSERT INTO ##DeletedItems (ItemNo, ItemID, ItemName, ItemType)
    SELECT d.DrinkNo, d.DrinkID, d.DrinkName, 'Drink'
    FROM deleted d;
END;


CREATE TRIGGER trgAfterDeleteDish  -- TRIGGER that triggers after delete on dish and inserts the deleted drink in temptable
ON Dish
AFTER DELETE
AS
BEGIN
    
    INSERT INTO ##DeletedItems (ItemNo, ItemID, ItemName, ItemType)
    SELECT d.DishNo, d.DishID, d.DishName, 'Dish'
    FROM deleted d;
END;




CREATE OR ALTER PROCEDURE uspDeleteDrink  -- Procedure for deleting a drink
    @DrinkName VARCHAR(20) -- input
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
 
        IF EXISTS (SELECT 1 FROM Drink WHERE DrinkName = @DrinkName) -- checks if the drink you wanted to delete exits
        BEGIN
            
            DELETE FROM Drink --Deletes it
            WHERE DrinkName = @DrinkName;

        
            PRINT 'Drink deleted successfully.';
        END
        ELSE
        BEGIN

            RAISERROR('Drink with name %s does not exist.', 16, 1, @DrinkName);
        END


        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
      
        ROLLBACK TRANSACTION;


        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;



CREATE OR ALTER PROCEDURE uspDeleteDish -- Same logic as with delete drink
    @DishName VARCHAR(20)
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
      
        IF EXISTS (SELECT 1 FROM Dish WHERE DishName = @DishName)
        BEGIN

            DELETE FROM Dish
            WHERE DishName = @DishName;

            
            PRINT 'Dish deleted successfully.';
        END
        ELSE
        BEGIN
       
            RAISERROR('Dish with name %s does not exist.', 16, 1, @DishName);
        END

       
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
    
        ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(400) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;





CREATE OR ALTER PROCEDURE uspUpdateDrinkStock -- Procedure for updating drink stock
    @DrinkName  VARCHAR(20), 
    @NewDrinkStock  INT      
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
        UPDATE Drink
        SET DrinkStock = @NewDrinkStock
        WHERE DrinkName = @DrinkName;

        IF @@ROWCOUNT = 0
        BEGIN
            RAISERROR('No drink found with the specified DrinkName.', 16, 1); -- Error in case there isnt a drink
        END
        
        COMMIT TRANSACTION;
    END TRY
     BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;

CREATE OR ALTER PROCEDURE uspUpdateDishStock -- Same logic as with UpdateDishStock
    @DishName  VARCHAR(20), 
    @NewDishStock  INT
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
        UPDATE Dish
        SET DishStock = @NewDishStock
        WHERE DishName = @DishName;

        IF @@ROWCOUNT = 0
        BEGIN
            RAISERROR('No dish found with the specified DishName.', 16, 1);
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
       -- Capture and raise the error message and state
    DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Reduced size for the error message
    DECLARE @ErrorState INT = ERROR_STATE();
    
    RAISERROR(@ErrorMessage, 16, @ErrorState);  -- Raise the error with message and state
END CATCH;
    END;




CREATE PROCEDURE uspWriteDeletedItems -- Procedure that uses the temptable created before to display deleted itmes.
AS
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION;
        
        SELECT ItemNo, ItemID, ItemName, ItemType, DeletedDate
        FROM ##DeletedItems;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        -- Rollback any changes in case of an error
        ROLLBACK TRANSACTION;
        
        -- Capture and raise the error message and state
        DECLARE @ErrorMessage NVARCHAR(500) = ERROR_MESSAGE();  -- Error message (reduced size)
        DECLARE @ErrorState INT = ERROR_STATE();                -- Error state
        
        -- Raise the error to the calling session
        RAISERROR(@ErrorMessage, 16, @ErrorState);
    END CATCH;
END;

EXECUTE uspGetOldReceipts;