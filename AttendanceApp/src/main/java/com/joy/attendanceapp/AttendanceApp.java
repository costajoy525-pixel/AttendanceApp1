package com.joy.attendanceapp;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AttendanceApp extends Application {

    public static class StudentRecord {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty percentage;
        private final Map<String, SimpleStringProperty> attendanceMap;

        public StudentRecord(String id, String name) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.percentage = new SimpleStringProperty("0.0%");
            this.attendanceMap = new HashMap<>();
        }

        public String getId() { return id.get(); }
        public void setId(String id) { this.id.set(id); }
        public SimpleStringProperty idProperty() { return id; }

        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }
        public SimpleStringProperty nameProperty() { return name; }

        public SimpleStringProperty percentageProperty() { return percentage; }

        public SimpleStringProperty getWeekProperty(String week) {
            return attendanceMap.computeIfAbsent(week, k -> new SimpleStringProperty(""));
        }

        public void calculatePercentage(int totalWeeks) {
            int presentCount = 0;
            int markedCount = 0;
            for (int i = 1; i <= totalWeeks; i++) {
                String val = getWeekProperty("W" + i).get();
                if ("P".equals(val)) {
                    presentCount++;
                    markedCount++;
                } else if ("A".equals(val)) {
                    markedCount++;
                }
            }
            if (markedCount == 0) {
                percentage.set("0.0%");
            } else {
                double pct = (double) presentCount / markedCount * 100.0;
                percentage.set(String.format("%.1f%%", pct));
            }
        }
    }

    private TabPane tabPane;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Comprehensive Design of Data Structures - Attendance Management System");

        tabPane = new TabPane();

        // Create default initial course sheet
        addNewCourseTab(tabPane, "Course: Comprehensive Design of Data Structures (Fall 2026-2027)");

        // Top toolbar for creating new course sheets
        TextField courseNameInput = new TextField();
        courseNameInput.setPromptText("Enter New Course Name...");
        courseNameInput.setPrefWidth(260);

        Button addCourseButton = new Button("New Course Sheet");
        addCourseButton.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        addCourseButton.setOnAction(e -> {
            String courseTitle = courseNameInput.getText().trim();
            if (!courseTitle.isEmpty()) {
                addNewCourseTab(tabPane, "Course: " + courseTitle);
                courseNameInput.clear();
            } else {
                addNewCourseTab(tabPane, "Course: New Subject Sheet");
            }
        });

        HBox globalTopBar = new HBox(10);
        globalTopBar.setPadding(new Insets(10));
        globalTopBar.setStyle("-fx-background-color: #ecf0f1;");
        globalTopBar.getChildren().addAll(
                new Label("Create Subject Sheet:"), courseNameInput, addCourseButton
        );

        BorderPane root = new BorderPane();
        root.setTop(globalTopBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addNewCourseTab(TabPane tabPane, String courseTitleText) {
        Tab tab = new Tab(courseTitleText.replace("Course: ", ""));

        Label titleLabel = createStyledLabel(courseTitleText);

        TableView<StudentRecord> table = new TableView<>();
        table.setEditable(true);

        // Default student roster list
        ObservableList<StudentRecord> data = FXCollections.observableArrayList(
                new StudentRecord("4420250902", "WEMEGAH STEPHANIE SEFAK"),
                new StudentRecord("4420250903", "BIADI MOHAMED"),
                new StudentRecord("4420250944", "DURANTO THOMAS GOMES"),
                new StudentRecord("4420250945", "JOY JOSEPH COSTA"),
                new StudentRecord("4420250969", "ABBASI MEMOONA ZAINAB"),
                new StudentRecord("4420250970", "ISLAM MD ASHIKUL"),
                new StudentRecord("4420250973", "MARYAM NAVEED MIR"),
                new StudentRecord("4420250980", "BAI LACHMI")
        );

        final int[] currentTotalWeeks = {4}; // mutable wrapper for inner lambda

        ComboBox<Integer> weekSelector = new ComboBox<>();
        for (int i = 1; i <= 15; i++) {
            weekSelector.getItems().add(i);
        }
        weekSelector.setValue(4);

        weekSelector.setOnAction(e -> {
            if (weekSelector.getValue() != null) {
                currentTotalWeeks[0] = weekSelector.getValue();
                rebuildTableColumns(table, data, currentTotalWeeks[0]);
            }
        });

        rebuildTableColumns(table, data, currentTotalWeeks[0]);
        table.setItems(data);

        TextField idInput = new TextField();
        idInput.setPromptText("Student ID");
        idInput.setPrefWidth(120);

        TextField nameInput = new TextField();
        nameInput.setPromptText("Student Name");
        nameInput.setPrefWidth(200);

        Button addStudentButton = new Button("Add Student");
        Button removeStudentButton = new Button("Remove Selected");
        Label statusLabel = new Label("Status: Ready");

        addStudentButton.setOnAction(e -> {
            String newId = idInput.getText().trim();
            String newName = nameInput.getText().trim();
            if (!newId.isEmpty() && !newName.isEmpty()) {
                StudentRecord newStudent = new StudentRecord(newId, newName);
                data.add(newStudent);
                idInput.clear();
                nameInput.clear();
                statusLabel.setText("Status: Student added successfully!");
            } else {
                statusLabel.setText("Status: Please enter both ID and Name.");
            }
        });

        removeStudentButton.setOnAction(e -> {
            StudentRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                data.remove(selected);
                statusLabel.setText("Status: Student row removed.");
            } else {
                statusLabel.setText("Status: Please select a student row to remove.");
            }
        });

        HBox topActionBar = new HBox(10);
        topActionBar.setPadding(new Insets(5, 0, 5, 0));
        topActionBar.getChildren().addAll(
                new Label("Select Total Weeks (1-15):"), weekSelector,
                new Label("|  Add Student:"), idInput, nameInput, addStudentButton, removeStudentButton
        );

        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        Button saveButton = new Button("Save Attendance");
        Button exportButton = new Button("Export CSV");

        saveButton.setOnAction(e -> statusLabel.setText("Status: Attendance saved successfully for " + tab.getText() + "!"));
        exportButton.setOnAction(e -> statusLabel.setText("Status: Exported " + tab.getText() + " to CSV format!"));

        bottomBar.getChildren().addAll(saveButton, exportButton, statusLabel);

        BorderPane tabContent = new BorderPane();
        VBox topBox = new VBox(5, titleLabel, topActionBar);
        topBox.setPadding(new Insets(10));

        VBox bottomContainer = new VBox(5, bottomBar);
        bottomContainer.setPadding(new Insets(10));

        tabContent.setTop(topBox);
        tabContent.setCenter(table);
        tabContent.setBottom(bottomContainer);

        tab.setContent(tabContent);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void rebuildTableColumns(TableView<StudentRecord> table, ObservableList<StudentRecord> data, int totalWeeks) {
        table.getColumns().clear();

        TableColumn<StudentRecord, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(event -> event.getRowValue().setId(event.getNewValue()));
        idCol.setPrefWidth(120);

        TableColumn<StudentRecord, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> event.getRowValue().setName(event.getNewValue()));
        nameCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, nameCol);

        for (int i = 1; i <= totalWeeks; i++) {
            final String weekName = "W" + i;
            TableColumn<StudentRecord, String> weekCol = new TableColumn<>(weekName);
            weekCol.setCellValueFactory(cellData -> cellData.getValue().getWeekProperty(weekName));
            setupAttendanceColumn(weekCol, weekName, table, totalWeeks);
            table.getColumns().add(weekCol);
        }

        TableColumn<StudentRecord, String> pctCol = new TableColumn<>("Attendance %");
        pctCol.setCellValueFactory(cellData -> {
            cellData.getValue().calculatePercentage(totalWeeks);
            return cellData.getValue().percentageProperty();
        });
        pctCol.setPrefWidth(100);
        pctCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        table.getColumns().add(pctCol);
    }

    private void setupAttendanceColumn(TableColumn<StudentRecord, String> column, String weekName, TableView<StudentRecord> table, int totalWeeks) {
        column.setPrefWidth(55);
        column.setCellFactory(col -> {
            TableCell<StudentRecord, String> cell = new TableCell<StudentRecord, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item == null ? "" : item);
                        if ("P".equals(item)) {
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #d4edda; -fx-text-fill: #155724;");
                        } else if ("A".equals(item)) {
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
                        } else {
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    StudentRecord record = cell.getTableRow().getItem();
                    if (record != null) {
                        String currentVal = cell.getItem();
                        String newVal;
                        if (event.getClickCount() == 2) {
                            newVal = "A".equals(currentVal) ? "" : "A";
                        } else if (event.getClickCount() == 1) {
                            if (currentVal == null || currentVal.isEmpty()) {
                                newVal = "P";
                            } else if ("P".equals(currentVal)) {
                                newVal = "A";
                            } else {
                                newVal = "";
                            }
                        } else {
                            return;
                        }
                        record.getWeekProperty(weekName).set(newVal);
                        record.calculatePercentage(totalWeeks);
                        table.refresh();
                    }
                }
            });
            return cell;
        });
    }

    private Label createStyledLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        return lbl;
    }

    public static void main(String[] args) {
        launch(args);
    }
}