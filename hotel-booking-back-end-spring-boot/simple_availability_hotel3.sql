-- Απλό script για δημιουργία availability (χωρίς stored procedure)
-- ΠΡΟΣΟΧΗ: Αυτό θα τρέξει για όλες τις ημέρες από 16/2/2026 έως 31/12/2026

SET @current_date = '2026-02-16';
SET @end_date = '2026-12-31';

-- Loop για δημιουργία records (χρειάζεται να το τρέξεις πολλές φορές)
-- Αυτό είναι δύσκολο χωρίς stored procedure

-- ΕΝΑΛΛΑΚΤΙΚΑ: Χρησιμοποίησε το API endpoint
-- PUT http://localhost:8080/api/hotel/updateAvailableRooms/3
-- με το JSON που σου έδωσα παραπάνω
