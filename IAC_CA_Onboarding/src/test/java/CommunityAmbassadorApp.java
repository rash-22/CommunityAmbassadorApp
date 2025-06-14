import java.util.Scanner;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class CommunityAmbassadorApp {

    public static void main(String[] args) {
        System.out.println("🔰 IAC Community Ambassador Onboarding System 🔰");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter College: ");
        String college = scanner.nextLine();

        // 2. Generate UTM link
        String username = name.toLowerCase().replaceAll("\\s+", "");
        String utmLink = "https://iac.cloudcounselage.com/?utm_source=" + username +
                "&utm_medium=ca&utm_campaign=onboarding";

        System.out.println("\n🔗 UTM Link Generated:");
        System.out.println(utmLink);

        // 3. Save to MySQL DB
        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root"; // change if needed
        String password = "root"; // change if needed

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            Connection conn = DriverManager.getConnection(url, user, password);

            // Prepare SQL
            String query = "INSERT INTO ambassadors (name, email, college, utm_link) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, college);
            pst.setString(4, utmLink);

            // Execute insert
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("\n✅ Data saved successfully to the database!");
            }

            // Close statement and connection
            pst.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ Error while saving to DB: " + e.getMessage());
        }




        // 4. Simulated Email
        System.out.println("\n📨 Simulated Welcome Email Sent:");
        System.out.println("--------------------------------------------------");
        System.out.println("To: " + email);
        System.out.println("Subject: Welcome to the IAC Community Ambassador Program");
        System.out.println();
        System.out.println("Dear " + name + ",");
        System.out.println();
        System.out.println("Congratulations and welcome to the IAC Community Ambassador program!");
        System.out.println("Here is your unique UTM tracking link:");
        System.out.println(utmLink);
        System.out.println();
        System.out.println("Start sharing this link with your peers and earn rewards as you grow the IAC community.");
        System.out.println();
        System.out.println("Regards,\nTeam IAC");
        System.out.println("--------------------------------------------------");

        sendWelcomeEmail(email, name, utmLink);
        // 5. Ask to view all ambassadors
        System.out.print("\nDo you want to view all ambassadors? (yes/no): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            viewAmbassadors();
        }

        System.out.print("\nDo you want to export ambassador data to CSV? (yes/no): ");
        String exportChoice = scanner.nextLine();

        if (exportChoice.equalsIgnoreCase("yes")) {
            exportAmbassadorsToCSV();
        }

        System.out.print("\nDo you want to search for an ambassador by email? (yes/no): ");
        String searchChoice = scanner.nextLine();

        if (searchChoice.equalsIgnoreCase("yes")) {
            searchAmbassadorByEmail();
        }

        System.out.print("\nDo you want to update an ambassador’s details? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            updateAmbassadorDetails();
        }

        System.out.print("\nDo you want to delete an ambassador? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            deleteAmbassadorByEmail();
        }

        System.out.print("\nDo you want to see the total number of ambassadors? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            showAmbassadorCount();
        }


        // ✅ Close scanner at the very end
        scanner.close();
    }

    // 🧠 Separate method to view ambassadors
    public static void viewAmbassadors() {
        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root";
        String password = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String query = "SELECT * FROM ambassadors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n📋 List of Registered Community Ambassadors:");
            System.out.println("-------------------------------------------------");

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("College: " + rs.getString("college"));
                System.out.println("UTM: " + rs.getString("utm_link"));
                System.out.println("-------------------------------------------------");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ Error fetching ambassadors: " + e.getMessage());
        }
    }


    public static void sendWelcomeEmail(String toEmail, String name, String utmLink) {
        final String fromEmail = "20scs060.rashmi.24@gmail.com"; // Your Gmail
        final String appPassword = "ltutcomjclgrizqk"; //

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to the IAC Community Ambassador Program");

            String msg = "Dear " + name + ",\n\n" +
                    "Welcome to the IAC Community Ambassador Program!\n\n" +
                    "Here is your unique UTM tracking link:\n" + utmLink + "\n\n" +
                    "Start sharing this link with your peers and earn rewards.\n\n" +
                    "Regards,\nTeam IAC";

            message.setText(msg);
            Transport.send(message);
            System.out.println("✅ Real welcome email sent to: " + toEmail);

        } catch (MessagingException e) {
            System.out.println("❌ Email sending failed: " + e.getMessage());
        }
    }




    public static void exportAmbassadorsToCSV() {
        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root";
        String password = "root";

        String csvFile = "ambassadors.csv";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ambassadors");
             FileWriter writer = new FileWriter(csvFile)) {

            // Write header
            writer.append("ID,Name,Email,College,UTM Link\n");

            // Write data rows
            while (rs.next()) {
                writer.append(rs.getInt("id") + ",");
                writer.append(rs.getString("name") + ",");
                writer.append(rs.getString("email") + ",");
                writer.append(rs.getString("college") + ",");
                writer.append(rs.getString("utm_link") + "\n");
            }

            writer.flush();
            System.out.println("\n✅ Ambassador data exported successfully to: " + csvFile);

        } catch (SQLException | IOException e) {
            System.out.println("❌ Error exporting to CSV: " + e.getMessage());
        }



    }


    public static void searchAmbassadorByEmail() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\n🔍 Enter email to search: ");
        String searchEmail = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root";
        String password = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String query = "SELECT * FROM ambassadors WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, searchEmail);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n✅ Ambassador Found:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("College: " + rs.getString("college"));
                System.out.println("UTM Link: " + rs.getString("utm_link"));
            } else {
                System.out.println("❌ No ambassador found with that email.");
            }

            rs.close();
            pst.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ Error searching ambassador: " + e.getMessage());
        }
    }
    public static void updateAmbassadorDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter the email of the ambassador to update: ");
        String email = scanner.nextLine();

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();

        System.out.print("Enter new college: ");
        String newCollege = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root";
        String password = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String query = "UPDATE ambassadors SET name = ?, college = ? WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, newName);
            pst.setString(2, newCollege);
            pst.setString(3, email);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Ambassador details updated successfully.");
            } else {
                System.out.println("❌ No ambassador found with that email.");
            }

            pst.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ Error updating details: " + e.getMessage());
        }
    }
    public static void deleteAmbassadorByEmail() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter the email of the ambassador to delete: ");
        String email = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root";
        String password = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String query = "DELETE FROM ambassadors WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Ambassador deleted successfully.");
            } else {
                System.out.println("❌ No ambassador found with that email.");
            }

            pst.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ Error deleting ambassador: " + e.getMessage());
        }
    }
    public static void showAmbassadorCount() {
        String url = "jdbc:mysql://localhost:3306/iac_onboarding";
        String user = "root";
        String password = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM ambassadors");

            if (rs.next()) {
                System.out.println("\n📊 Total Registered Ambassadors: " + rs.getInt("total"));
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ Error fetching count: " + e.getMessage());
        }
    }

}
