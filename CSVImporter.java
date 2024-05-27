package com.task.csv.db;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CSVImporter  {

    public static void main(String[] args) throws CsvValidationException {
        String csvFilePath = "D:\\TestFiles\\Task\\CSV FILE DB INSERT\\EMPLOYEE.csv";
        String dbUrl = "jdbc:mysql://localhost:3306/sys";
        String username = "root";
        String password = "root";

        List<String> columnsToIgnore = Arrays.asList("ID","created_date"); // Add column names to ignore

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {

            // Read the first line of CSV to get column names
            String[] columns = reader.readNext();
            // Construct the INSERT SQL statement
            StringBuilder insertSqlBuilder = new StringBuilder("INSERT INTO employees (");
            StringBuilder valuesBuilder = new StringBuilder(" VALUES (");
            for (int i = 0; i < columns.length; i++) {
                if (!columnsToIgnore.contains(columns[i])) {
                    insertSqlBuilder.append(columns[i]);
                    valuesBuilder.append("?");
                    if (i < columns.length - 1 && !columnsToIgnore.contains(columns[i + 1])) {
                        insertSqlBuilder.append(",");
                        valuesBuilder.append(",");
                    }
                }
            }
            insertSqlBuilder.append(")").append(valuesBuilder).append(")");

            // Prepare the INSERT statement
            String insertSql = insertSqlBuilder.toString();
            System.out.println("Insert Query : " + insertSql);
            PreparedStatement pstmt = connection.prepareStatement(insertSql);

            // Read data from CSV and insert into the database
            String[] rowData;
            while ((rowData = reader.readNext()) != null) {
                int parameterIndex = 1;
                for (int i = 0; i < columns.length; i++) {
                    if (!columnsToIgnore.contains(columns[i])) {
                        pstmt.setString(parameterIndex++, rowData[i]); // Set parameter values for the prepared statement
                    }
                }
                pstmt.executeUpdate(); // Execute the INSERT statement
            }
            System.out.println("Data imported successfully.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
