package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Countries;
import model.Divisions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBCountries {

    // Pulls country list into observable list
    public static ObservableList<Countries> getAllCountries() {
        ObservableList<Countries> allCountries = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM countries";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int countryId = rs.getInt("Country_ID");
                String country = rs.getString("Country");
                Countries countryObject = new Countries(countryId, country);
                allCountries.add(countryObject);
            }
        }

        catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return allCountries;
    }

    public static Countries selectedCountryName(String country) {

        try {
            String sql = "SELECT Country FROM countries WHERE Country_ID = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            ps.setString(1, String.valueOf(country));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

}
