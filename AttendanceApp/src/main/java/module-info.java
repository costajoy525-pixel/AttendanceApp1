module com.joy.attendanceapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.joy.attendanceapp to javafx.fxml;
    exports com.joy.attendanceapp;
}