package controller;

import database.DBAppointments;
import database.DBContacts;
import database.DBCustomer;
import database.DBUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointments;
import model.Contacts;
import model.Customer;
import model.Users;
import utility.TimeManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static database.DBContacts.selectedContactName;
import static database.DBCustomer.selectedCustomerName;
import static database.DBUser.selectedUserName;

/** Update Appointment Controller.
     Opens update appointment screen. Here the customer is able to update an appointment in the database. */

public class UpdateAppointmentController implements Initializable {
    public ComboBox<Contacts> contactComboBox;
    public TextField titleTextField;
    public TextField descriptionTextField;
    public TextField locationTextField;
    public TextField appointmentIdTextField;
    public Button cancelButton;
    public Button saveButton;
    public DatePicker datePicker;
    public ComboBox<LocalTime> startTimeComboBox;
    public ComboBox<LocalTime> endTimeComboBox;
    public ComboBox<Customer> customerIdComboBox;
    public ComboBox<Users> userIdComboBox;
    public ComboBox<String> typeComboBox;


    Stage stage;
    Parent scene;

    private ZonedDateTime zonedStartTime;
    private ZonedDateTime zonedEndTime;
    private static Appointments selectedAppointment;

    private int appointmentId;

    // CREATES OBSERVABLE LISTS
    private ObservableList<Users> allUsers = DBUser.getAllUsers();
    private ObservableList<Customer> allCustomers = DBCustomer.getAllCustomers();
    private ObservableList<Contacts> allContacts = DBContacts.getAllContacts();




    /** Initialize method.
      @param url
      @param resourceBundle Initializes screen, fills combo boxes with appropriate values. */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        startTimeComboBox.setItems(TimeManager.getBusinessHours(8));
        endTimeComboBox.setItems(TimeManager.getBusinessHours(9));
        contactComboBox.setItems(allContacts);

        customerIdComboBox.setItems(allCustomers);

        userIdComboBox.setItems(allUsers);

      //  fillTimeComboBoxes();

        // POPULATES COMBO BOX WITH TYPES
        ObservableList<String> typesList = FXCollections.observableArrayList(
                "Coffee Chat", "De-Briefing", "Mentoring", "Planning Session", "Sprint Meeting", "Other");
        typeComboBox.setItems(typesList);

    }


    // CHECKS FOR APPOINTMENT SCHEDULING ERRORS
    public Boolean validAppointments() {

        // LOGICAL ERROR CHECKS
        if(titleTextField.getText().isEmpty() ||
                descriptionTextField.getText().isEmpty() ||
                locationTextField.getText().isEmpty() ||
                contactComboBox.getValue() == null ||
                typeComboBox.getValue() == null ||
                customerIdComboBox.getValue() == null ||
                userIdComboBox.getValue() == null ||
                datePicker.getValue() == null ||
                startTimeComboBox.getValue() == null ||
                endTimeComboBox.getValue() == null){

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty fields");
                alert.setContentText("Please ensure no fields are empty.");
                alert.showAndWait();
                return false;
        }

        LocalDate startDate = datePicker.getValue();
        LocalTime startTime = startTimeComboBox.getValue();
        LocalTime endTime = endTimeComboBox.getValue();

        // CHECKS THAT SELECTED DATE AND TIMES ARE VALID

        if(startDate.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid date");
            alert.setContentText("Please select a valid date.");
            alert.showAndWait();
            return false;
        }
/*
        if(startTime.isBefore(LocalTime.of(8,0)) ||
                (endTime.isAfter(LocalTime.of(22,0)))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Outside business hours");
            alert.setContentText("Please select a time during business hours.");
            alert.showAndWait();
            return false;
        }

 */

        if(endTime.isBefore(startTime) || endTime.equals(startTime)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect times");
            alert.setContentText("Please choose valid start and end times.");
            alert.showAndWait();
            return false;
        }

        // CHECKS FOR OVERLAPPING APPOINTMENTS FOR SELECTED CUSTOMER, EXCLUDING CURRENT APPOINTMENT

        LocalTime selectedStartTime = startTimeComboBox.getValue();
        LocalTime selectedEndTime = endTimeComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        int selectedAppointment = Integer.parseInt(appointmentIdTextField.getText());

        ObservableList<Appointments> customerAppointments = DBAppointments.appointmentsByCustomer(customerIdComboBox.getValue().getCustomerId());
        for (Appointments appointments: customerAppointments) {
            LocalTime proposedStart = appointments.getStartTime();
            LocalTime proposedEnd = appointments.getEndTime();
            LocalDate proposedDate = appointments.getAppointmentDay();
            int proposedAppointment = appointments.getAppointmentId();

            if (proposedAppointment != selectedAppointment) {

                if ((proposedStart.isAfter(selectedStartTime) || proposedStart.equals(selectedStartTime))
                        && proposedStart.isBefore(selectedEndTime) && proposedDate.equals(selectedDate)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Overlapping appointments");
                    alert.setContentText("New appointments cannot overlap with existing appointments.");
                    alert.showAndWait();
                    return false;
                }

                if (proposedEnd.isAfter(selectedStartTime) && (proposedEnd.isBefore(selectedEndTime) ||
                        (proposedEnd.equals(selectedEndTime))) && proposedDate.equals(selectedDate)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Overlapping appointments");
                    alert.setContentText("New appointments cannot overlap with existing appointments.");
                    alert.showAndWait();
                    return false;
                }

                if ((proposedStart.isBefore(selectedStartTime) || proposedStart.equals(selectedStartTime)) &&
                        (proposedEnd.isAfter(selectedEndTime)) || proposedEnd.equals(selectedEndTime)
                        && proposedDate.equals(selectedDate)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Overlapping appointments");
                    alert.setContentText("New appointments cannot overlap with existing appointments.");
                    alert.showAndWait();
                    return false;
                }

            }

        }
        return true;
    }


    /** Save appointment method.
     @param event
     @throws IOException Checks if appointments are valid, then saves entered information into database. */

    public void OnActionSaveAppointment(ActionEvent event) throws SQLException, IOException {

        boolean appointmentsAreValid = validAppointments();


        if(appointmentsAreValid) {

            try {

                int contactId = contactComboBox.getValue().getContactId();
                String title = titleTextField.getText();
                String description = descriptionTextField.getText();
                String location = locationTextField.getText();
                String type = typeComboBox.getValue();
                LocalDateTime startTime = LocalDateTime.of(datePicker.getValue(), startTimeComboBox.getValue());
                LocalDateTime endTime = LocalDateTime.of(datePicker.getValue(), endTimeComboBox.getValue());
                int customerId = customerIdComboBox.getValue().getCustomerId();
                int userId = userIdComboBox.getValue().getUserId();
                DBAppointments.updateAppointment(title, description, location, contactId, type, startTime, endTime, customerId, userId, appointmentId);

                this.stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                this.scene = (Parent) FXMLLoader.load((URL) Objects.requireNonNull(this.getClass().getResource("/view/MainScreen.fxml")));
                this.stage.setScene(new Scene(this.scene));
                this.stage.setTitle("Main Screen");
                this.stage.show();

            } catch (SQLException throwables) {
                throwables.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /** Send Appointment method.
       @param appointments Sends selected appointment information to be modified. */

    public void sendAppointment(Appointments appointments) {

        appointmentId = appointments.getAppointmentId();
        appointmentIdTextField.setText(String.valueOf(appointments.getAppointmentId()));
        contactComboBox.setValue(selectedContactName(appointments.getContactId()));
        titleTextField.setText(appointments.getTitle());
        descriptionTextField.setText(appointments.getDescription());
        locationTextField.setText(appointments.getLocation());
        typeComboBox.setValue(appointments.toString());
        datePicker.setValue(appointments.getAppointmentDay());
        startTimeComboBox.setValue(appointments.getStartTime());
        endTimeComboBox.setValue(appointments.getEndTime());
        customerIdComboBox.setValue(selectedCustomerName(appointments.getCustomerId()));
        userIdComboBox.setValue(selectedUserName(appointments.getUserId())); // NOT SENDING

    }


    /** Cancel method.
     @param event Closes window and returns to Main Screen. */
    public void OnActionCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel?", new ButtonType[0]);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            this.scene = (Parent) FXMLLoader.load((URL) Objects.requireNonNull(this.getClass().getResource("/view/MainScreen.fxml")));
            this.stage.setScene(new Scene(this.scene));
            this.stage.setTitle("Main Screen");
            this.stage.show();
        }
    }

}
