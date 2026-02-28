@echo off
echo Updating availability for Hotel ID 3 (New York)...

curl -X PUT http://localhost:8080/api/hotel/updateAvailableRooms/3 ^
-H "Content-Type: application/json" ^
-d "[{\"fromDate\":\"2026-02-16\",\"toDate\":\"2026-12-31\",\"quantity\":3,\"roomType\":\"Suite\",\"roomPrice\":600,\"roomName\":\"Presidential Suite\"},{\"fromDate\":\"2026-02-16\",\"toDate\":\"2026-12-31\",\"quantity\":25,\"roomType\":\"Double\",\"roomPrice\":320,\"roomName\":\"Executive Room\"},{\"fromDate\":\"2026-02-16\",\"toDate\":\"2026-12-31\",\"quantity\":30,\"roomType\":\"Single\",\"roomPrice\":220,\"roomName\":\"City View Room\"}]"

echo.
echo Done! Check the database for availability records.
pause
