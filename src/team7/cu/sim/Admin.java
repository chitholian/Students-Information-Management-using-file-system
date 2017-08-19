package team7.cu.sim;

import com.sun.istack.internal.Nullable;
import team7.cu.utils.Helper;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class Admin {
    public String username;
    private String password;

    public Admin(String username) {
        this.username = username;
    }

    /**
     * Find the password of an admin.
     *
     * @param username Username of the admin.
     * @return hashed password of the admin if found, otherwise null.
     */
    public static @Nullable
    String findPasswordOf(String username) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Helper.ADMIN_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length != 2) continue; // Because we have stored only two fields i.e. username \t password
                if (tokens[0].equalsIgnoreCase(username)) {
                    reader.close();
                    return tokens[1];
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Oh! Admin not found.
    }

    /**
     * Check if at least one admin exists.
     * Otherwise create ad admin with default username & password.
     */
    public static void checkDefaults() {
        boolean adminFound = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Helper.ADMIN_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^[^\t]+\t[A-Z]+$")) {
                    adminFound = true;
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!adminFound) { // Create a default admin.
            Admin admin = new Admin("admin");
            admin.setPassword("admin");
            admin.save();
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Helper.hashifyPassword(password); // Keep password hashed.
    }

    /**
     * Save (Edit or Create) an Admin.
     */
    public synchronized void save() {
        boolean edited = false; // Whether an admin was edited (if found indeed).
        try {
            File admins = new File(Helper.ADMIN_FILE);
            File tmp = new File(Helper.TMP_FILE);
            // Read from original file.
            BufferedReader reader = new BufferedReader(new FileReader(admins));
            // Write in temporary file.
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length != 2) continue; // Because we have stored only two fields i.e. username \t password
                if (tokens[0].equalsIgnoreCase(username)) {// Admin exists.
                    writer.write(username + "\t" + password + "\n"); // Write new values.
                    edited = true;
                } else writer.write(line + "\n"); // Write old values.
            }
            reader.close();
            if (!edited) { // Insert as a new row (entity).
                writer.write(username + "\t" + password + "\n");
            }
            writer.close();
            // Rename (move) temporary file to original one i.e. overwrite.
            Files.move(tmp.toPath(), admins.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
