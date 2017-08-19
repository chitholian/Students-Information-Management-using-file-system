package team7.cu.utils;

import team7.cu.sim.Admin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class Helper {
    /* ********* Constants ********* */
    public final static String DIR = "cu_sim_data";
    public final static String ADMIN_FILE = "cu_sim_data/admins.txt";
    public final static String STUDENT_FILE = "cu_sim_data/students.txt";
    public final static String DEPARTMENT_FILE = "cu_sim_data/departments.txt";
    public final static String TMP_FILE = "cu_sim_data/tmp.txt";
    public final static String AVATAR_LOCATION = "cu_sim_data/avatars/";

    /* ******* Check Files ******** */
    public static void checkFiles() {
        try {
            File file = new File(AVATAR_LOCATION);
            file.mkdirs();

            file = new File(ADMIN_FILE);
            if (!file.isFile()) file.createNewFile();

            file = new File(STUDENT_FILE);
            if (!file.isFile()) file.createNewFile();

            file = new File(DEPARTMENT_FILE);
            if (!file.isFile()) file.createNewFile();

            // Ensure at least one admin exists.
            Admin.checkDefaults();

        } catch (IOException e) {
            // Go out! I don't like to...
        }
    }

    /**
     * Checks if a target string contains of the words in the keyword string.
     *
     * @param keywords space separated words.
     * @param target   a log string where to search for keywords.
     * @return true if the target contains any of the words in the keywords or the keywords is null, false otherwise.
     */
    public static boolean searchOK(String keywords, String target) {
        if (keywords == null) return true;
        else if (target == null) return false;

        // To ignore case problems turn both into lowercase.
        target = target.toLowerCase();
        keywords = keywords.toLowerCase();
        // System.out.println(keywords + " ---- " + target);

        // replace comma and other whitespaces with single space, then split by single space.
        String[] tokens = keywords.replaceAll("[,\\s]+", " ").split(" ");
        for (String term : tokens)
            if (target.contains(term)) return true;
        return false;
    }

    /**
     * Checks if a date is possible
     *
     * @param year  Year Value i.e. 1997
     * @param month Month number in the year; 1 for January, 2 for February, 12 for December etc;
     * @param day   Number of the day in month; range [1, 31]
     * @return true if the date is possible, false otherwise
     */
    public static boolean isValidDate(int year, int month, int day) {
        // Check basic facts
        if (year < 0 || month < 1 || day < 1 || month > 12 || day > 31) return false;

        // February and Leap Year fact
        if (month == 2) {
            return day < 29 || isLeapYear(year) && day < 30;
        }

        // April and June fact (February is already checked)
        if (month < 8) // for April, June
            return day != 31 || month % 2 == 1;

        // September to December fact
        return day != 31 || month % 2 == 0;
    }

    /**
     * Checks if a year is leap year
     *
     * @param year Year to work on
     * @return true if the year is leap year, false otherwise
     */
    public static boolean isLeapYear(int year) {
        return year >= 0 && year % 4 == 0;
    }

    /* **************************** */

    /**
     * A simple password hashing technique.
     * It replaces all the characters with arbitrary upper case english letter.
     *
     * @param password Password String to make hash.
     * @return Hashed string.
     */
    public static String hashifyPassword(String password) {
        int div = 26, sm = 307;
        StringBuilder ans = new StringBuilder();
        for (char c : password.toCharArray()) {
            sm = ((int) c + sm) % div;
            ans.append((char) (65 + sm));
        }
        return ans.toString();
    }
}
