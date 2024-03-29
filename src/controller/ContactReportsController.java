package controller;

import database.DBAppointments;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Contacts;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

/** Contacts Report Controller. Displays a tableview of appointments filtered by selected contact
        from combo box on Main Screen Reports Tab. */

public class ContactReportsController implements Initializable {

    public TableView contactReportTable;
    public TableColumn contactReportAppointmentIdColumn;
    public TableColumn contactReportTitleColumn;
    public TableColumn contactReportDescriptionColumn;
    public TableColumn contactReportLocationColumn;
    public TableColumn contactReportContactIdColumn;
    public TableColumn contactReportTypeColumn;
    public TableColumn contactReportDateColumn;
    public TableColumn contactReportStartColumn;
    public TableColumn contactReportEndColumn;
    public TableColumn contactReportCustomerIdColumn;
    public TableColumn contactReportUserIdColumn;
    public Label contactNameLabel;
    public Button logoutButton;
    public Button cancelButton;
    public Button backButton;

    Stage stage;
    Parent scene;

    /** Initialize method.
        @param url
        @param resourceBundle Sets table view with filtered observable list of appointments from database. */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // POPULATES APPOINTMENT TABLE
        contactReportAppointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        contactReportTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contactReportDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactReportLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactReportContactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        contactReportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactReportDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDay"));
        contactReportStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        contactReportEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        contactReportCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        contactReportUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

    }


    /** Send Contact Appointment method.
        @param contacts Sends appointments filtered by contact to table view. */

    public void sendContactAppointment(Contacts contacts) {
        contactReportTable.setItems(DBAppointments.appointmentsByContact(contacts.getContactId()));
        contactNameLabel.setText(contacts.getContactName());
    }


    /** Back method.
     @param event Closes window and returns to Main Screen. */

    public void onActionBack(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to return to previous screen?"
        );
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            this.scene = (Parent) FXMLLoader.load((URL) Objects.requireNonNull(this.getClass().getResource("/view/MainScreen.fxml")));
            this.stage.setScene(new Scene(this.scene));
            this.stage.setTitle("Main Screen");
            this.stage.show();
        }
    }


    /** Logout method.
        @param event Returns to log in screen when log out button clicked. */

    public void onActionLogout(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")));
            stage.setScene(new Scene((Parent) scene));
            stage.setTitle("Main Screen");
            stage.show();
        }
    }
}
