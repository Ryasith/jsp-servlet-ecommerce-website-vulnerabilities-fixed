package com.ecommerce.dao;

import com.ecommerce.database.Database;
import com.ecommerce.entity.Account;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Base64;

public class AccountDao {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    // Method to get blob image from database.
    private String getBase64Image(Blob blob) throws SQLException, IOException {
        InputStream inputStream = blob.getBinaryStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    // Method to execute get account query.
    private Account queryGetAccount(ResultSet resultSet ) {
        Account account = new Account();
        try {
            if (resultSet.next()) {
                account.setId(resultSet.getInt(1));
                account.setUsername(resultSet.getString(2));
                account.setPassword(resultSet.getString(3));
                account.setIsSeller(resultSet.getInt(4));
                account.setIsAdmin(resultSet.getInt(5));
                account.setAddress(resultSet.getString(7));
                account.setFirstName(resultSet.getString(8));
                account.setLastName(resultSet.getString(9));
                account.setEmail(resultSet.getString(10));
                account.setPhone(resultSet.getString(11));

                // Get profile image from database.
                if (resultSet.getBlob(6) == null) {
                    account.setBase64Image(null);
                } else {
                    account.setBase64Image(getBase64Image(resultSet.getBlob(6)));
                }

                return account;
            }
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Method to get account by id.
    public Account getAccount(int accountId) {
        String query = "SELECT * FROM account WHERE account_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,accountId);
            resultSet = preparedStatement.executeQuery();
            return queryGetAccount(resultSet);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Method to get login account from database.
    public Account checkLoginAccount(String username, String password) {
        String query = "SELECT * FROM account WHERE account_name = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            Account logAccount = queryGetAccount(resultSet);
            if (logAccount != null){
                boolean passwordMatch = BCrypt.checkpw(password, logAccount.getPassword());
                if (passwordMatch){
                    return logAccount;
                }else{
                    return null;
                }
            }
            return null;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Method to login using Google account.
    public Account checkGoogleLoginAccount(String username, String UID) {
        String query = "SELECT * FROM account WHERE account_name = ? AND google_user_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,UID);
            resultSet = preparedStatement.executeQuery();
            return queryGetAccount(resultSet);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Method to check is username exist or not.
    public boolean checkUsernameExists(String username) {
        String query = "SELECT * FROM account WHERE account_name = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            return (queryGetAccount(resultSet) != null);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Method to create an account.
    public void createAccount(String username, String password, InputStream image, String google_user_id) {
        String query = "INSERT INTO account (account_name, account_password, account_image, google_user_id, account_is_seller, account_is_admin ) VALUES (?, ?, ?, ?, 0, 0)";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setBinaryStream(3, image);
            preparedStatement.setString(4, google_user_id);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to edit profile information.
    public void editProfileInformation(int accountId, String firstName, String lastName, String address, String email, String phone, InputStream image) {
        String query = "UPDATE account SET " +
                "account_first_name = ?, " +
                "account_last_name = ?, " +
                "account_address = ?, " +
                "account_email = ?, " +
                "account_phone = ?, " +
                "account_image = ?" +
                "WHERE account_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setBinaryStream(6, image);
            preparedStatement.setInt(7, accountId);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Update profile catch: " + e.getMessage());
        }
    }

    // Method to update profile information.
    public void updateProfileInformation(int accountId, String firstName, String lastName, String address, String email, String phone) {
        String query = "UPDATE account SET " +
                "account_first_name = ?, " +
                "account_last_name = ?, " +
                "account_address = ?, " +
                "account_email = ?, " +
                "account_phone = ? " +
                "WHERE account_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Database database = Database.getInstance();
            connection = database.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setInt(6, accountId);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Update profile catch: " + e.getMessage());
        }
    }
}
