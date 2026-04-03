# Step-by-Step: Connecting MySQL to Ticket System

Follow these 4 simple steps to get your database connected and the app running.

## 1. Prepare MySQL Environment
1.  Ensure you have **MySQL Server** installed (e.g., MySQL Workbench or MySQL Installer).
2.  Make sure the MySQL service is **running** on its default port **3306**.

## 2. Execute the Setup SQL
1.  Open **MySQL Workbench** or your preferred SQL client.
2.  Open the file: [mysql_setup.sql](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/mysql_setup.sql).
3.  **Execute All Commands** in that file. 
    *   This creates the `ticketsystem` database and the necessary tables (`users` and `tickets`).

## 3. Configure Database Credentials
The application is currently set up for the user `root` with the password `password`.
If your MySQL password is different:
1.  Open [auth-service properties](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/microservices/auth-user-service/src/main/resources/application.properties).
2.  Change `spring.datasource.password=password` to **your password**.
3.  Repeat this for the [ticket-service properties](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/microservices/ticket-service/src/main/resources/application.properties).

## 4. Launch the Application
1.  Go to the folder: `c:\Users\Khushboo\Downloads\AWS\ticket-hub\`.
2.  Double-click [restart_app.bat](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/restart_app.bat).
3.  **Wait 1-2 minutes** for all 6 microservices to start in the background.
4.  Open your browser and visit: [http://localhost:9100](http://localhost:9100).

---

### Troubleshooting
- If the browser says "Connection Refused", check the [auth.log](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/auth.log) for error messages.
- If it's a "Bad Credentials" database error, double-check your password in Step 3.
