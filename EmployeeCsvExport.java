
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
public class EmployeeCsvExport {

    private static final String URL =
            "jdbc:mysql://localhost:3306/jdbc_test";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        String sql = "SELECT id, name, salary FROM employees";
        Path csvFile = Path.of("employees.csv");

        try (
            Connection con =
                    DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            BufferedWriter writer = Files.newBufferedWriter(
                    csvFile, StandardCharsets.UTF_8)
        ) {
            writer.write("ID,Name,Salary");
            writer.newLine();

            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String salary = rs.getBigDecimal("salary").toPlainString();

                writer.write(id + "," + escapeCsv(name) + "," + salary);
                writer.newLine();
                count++;
            }

            System.out.println("Export successful!");
            System.out.println("Records exported: " + count);
            System.out.println("CSV file: " + csvFile.toAbsolutePath());

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File-writing error: " + e.getMessage());
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
