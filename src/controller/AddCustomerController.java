package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {

    public TextField idTextField;
    public TextField postalCodeTextField;
    public TextField nameTextField;
    public TextArea addressTextField;
    public TextField phoneTextField;
    public ChoiceBox countryComboBox;
    public ChoiceBox divisionComboBox;
    public Button saveButton;
    public Button cancelButton;

    Stage stage;
    Parent scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // For testing purposes
    int uniqueId = 46678;

    /** Save customer method.
     @param event Saves modified part info and returns to Main Screen.
     */

    @FXML
    void OnActionSaveCustomer(ActionEvent event) throws IOException {
        /*
        try {
           // int customerId = uniqueId;
            String customerName = this.nameTextField.getText();
            String phone = this.phoneTextField.getText();
            String address = this.addressTextField.getText();
            String postalCode = this.postalCodeTextField.getText();

            Customer customer = new Customer(customerName, phone, address,postalCode);
            Customer.addCustomer(customer);

            */

            this.stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            this.scene = (Parent)FXMLLoader.load((URL)Objects.requireNonNull(this.getClass().getResource("/view/MainScreen.fxml")));
            this.stage.setScene(new Scene(this.scene));
            this.stage.setTitle("Main Screen");
            this.stage.show();

            /*

        }
        catch (NumberFormatException var10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please ensure all fields are filled in and contain correct data types");
            alert.showAndWait();
        }

         */

    }


    /** Close window method.
     @param event Closes window and returns to Main Screen. */
    @FXML
    void OnActionCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel?", new ButtonType[0]);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            this.scene = (Parent) FXMLLoader.load((URL) Objects.requireNonNull(this.getClass().getResource("/view/MainScreen.fxml")));
            this.stage.setScene(new Scene(this.scene));
            this.stage.setTitle("Main Inventory");
            this.stage.show();
        }
    }

}