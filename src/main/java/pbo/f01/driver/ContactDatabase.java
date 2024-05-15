package pbo.f01.driver;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pbo.f01.model.*;

public class ContactDatabase extends AbstractDatabase {

    public ContactDatabase(String url) throws SQLException {
        super(url);
    }

    protected void createTables() throws SQLException {
        String dropSQLs[] = {
            "DROP TABLE IF EXISTS Student",
            "DROP TABLE IF EXISTS Dorm",
        };

        String Dorm = "CREATE TABLE IF NOT EXISTS Dorm (" +
            "Name VARCHAR(255) NOT NULL PRIMARY KEY," +
            "Gender TEXT NOT NULL," +
            "Capacity INTEGER NOT NULL" +
            ")";

        String Student = "CREATE TABLE IF NOT EXISTS Student (" +
            "Id VARCHAR(30) PRIMARY KEY," + 
            "Name VARCHAR(255) NOT NULL," +
            "Gender TEXT NOT NULL," +
            "EntranceYear INTEGER NOT NULL," +
            "DormName VARCHAR(255)" + // Tambahkan kolom DormName
            ")";

        Statement statement = this.getConnection().createStatement();
        for (String sql : dropSQLs) {
            statement.execute(sql);
        }

        statement.execute(Dorm);
        statement.execute(Student);

        statement.close();
    }

    public void addStudent(String Id, String Name, int EntranceYear, String Gender) throws SQLException {
        String sql = "INSERT INTO Student (Id, Name, EntranceYear, Gender) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, Id);
        statement.setString(2, Name);
        statement.setInt(3, EntranceYear);
        statement.setString(4, Gender);
        statement.executeUpdate();
        statement.close();
    }

    public void addDorm(String Name, int Capacity, String Gender) throws SQLException {
        String sql = "INSERT INTO Dorm (Name, Capacity, Gender) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, Name);
        statement.setInt(2, Capacity);
        statement.setString(3, Gender);
        statement.executeUpdate();
        statement.close();
    }

    public void assign(String Id, String DormName) throws SQLException {
        String sql = "UPDATE Student SET DormName = ? WHERE Id = ?";
        PreparedStatement statement = this.getConnection().prepareStatement(sql);
        statement.setString(1, DormName);
        statement.setString(2, Id);
        statement.executeUpdate();
        statement.close();
    }

    public void displayAll() throws SQLException {
        String dormSQL = "SELECT * FROM Dorm ORDER BY Name ASC"; //men-seleksi semua data dari tabel Dorm dan diurutkan berdasarkan nama
        String studentSQL = "SELECT * FROM Student WHERE DormName = ? ORDER BY Name ASC"; //men-seleksi semua data dari tabel Student yang memiliki DormName tertentu dan diurutkan berdasarkan nama

        //ekskusi query untuk dorm
        Statement dormStatement = this.getConnection().createStatement();
        ResultSet dormResultSet = dormStatement.executeQuery(dormSQL);

        //mengambil data dari tabel Dorm
        while (dormResultSet.next()) {
            String dormName = dormResultSet.getString("Name"); //mengambil data dari kolom Name
            String dormGender = dormResultSet.getString("Gender"); //mengambil data dari kolom gender
            int dormCapacity = dormResultSet.getInt("Capacity"); //mengambil data dari kolom capacity

            String dormDetail = dormName + "|" + dormGender + "|" + dormCapacity; //format output

            //eksekusi query untuk student
            PreparedStatement studentStatement = this.getConnection().prepareStatement(studentSQL);
            studentStatement.setString(1, dormName); //mengisi parameter query dengan dormName
            ResultSet studentResultSet = studentStatement.executeQuery();
            
            //mengambil data dari tabel Student
            int studentCount = 0;
            StringBuilder studentDetails = new StringBuilder();

            //mengambil data dari tabel Student
            while (studentResultSet.next()) {
                String studentId = studentResultSet.getString("Id"); //mengambil data dari kolom Id
                String studentName = studentResultSet.getString("Name"); //mengambil data dari kolom Name 
                int entranceYear = studentResultSet.getInt("EntranceYear"); //mengambil data dari kolom EntranceYear
                
                studentDetails.append("\n").append(studentId).append("|").append(studentName).append("|").append(entranceYear); //format output
                studentCount++; //menghitung jumlah student
            }

            //format output akhir
            dormDetail += "|" + studentCount;
            System.out.println(dormDetail);
            System.out.println(studentDetails.toString().trim()); //menghapus spasi di akhir

            studentStatement.close();
        }

        dormResultSet.close();
        dormStatement.close();
    }

    @Override
    public void prepareTables() {
        try {
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
