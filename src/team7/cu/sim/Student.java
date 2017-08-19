package team7.cu.sim;

import team7.cu.utils.Helper;
import team7.cu.utils.MyDate;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Student {
    public String name, session, father, mother, currentAddress, permanentAddress, phone, email;
    public String religion, gender;
    public int id;
    public Department dept;
    public MyDate birth;

    public Student(int id) {
        this.id = id;
    }

    public static ArrayList<Student> getAll() {
        ArrayList<Student> students = new ArrayList<>();
        try {
            // Input file to read
            File input = new File(Helper.STUDENT_FILE);
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                Student std = buildFrom(line);
                if (std != null) students.add(std);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public static Student findById(int id) {
        try {
            // Input file to read (existing departments)
            File input = new File(Helper.STUDENT_FILE);
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches(id + "\t.*")) {
                    reader.close();
                    return buildFrom(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Student buildFrom(String tsvRow) {
        try {
            Student student;
            String[] tokens = tsvRow.split("\t");
            if (tokens.length > 0) student = new Student(Integer.parseInt(tokens[0]));
            else return null;
            if (tokens.length > 1) student.name = tokens[1];
            if (tokens.length > 2) student.dept = Department.getById(Integer.parseInt(tokens[2]));
            if (tokens.length > 3) student.session = tokens[3];
            if (tokens.length > 4) {
                MyDate birthDate = new MyDate();
                birthDate.parse(tokens[4]);
                student.birth = birthDate;
            }
            if (tokens.length > 5) student.father = tokens[5];
            if (tokens.length > 6) student.mother = tokens[6];
            if (tokens.length > 7) student.currentAddress = tokens[7];
            if (tokens.length > 8) student.permanentAddress = tokens[8];
            if (tokens.length > 9) student.phone = tokens[9];
            if (tokens.length > 10) student.email = tokens[10];
            if (tokens.length > 11) student.religion = tokens[11];
            if (tokens.length > 12) student.gender = tokens[12];
            return student;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static void delete(HashSet<Integer> selectedIds, int entity) {
        if (selectedIds.isEmpty()) return;
        try {
            File targetFile, image;
            if (entity == DataChangeOption.DEPARTMENT)
                targetFile = new File(Helper.DEPARTMENT_FILE);
            else targetFile = new File(Helper.STUDENT_FILE);
            File tmp = new File(Helper.TMP_FILE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
            BufferedReader reader = new BufferedReader(new FileReader(targetFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length == 1 || !selectedIds.contains(Integer.parseInt(tokens[0])))
                    writer.write(line + "\n"); // keep row unchanged
                else if (entity == DataChangeOption.STUDENTS && (image = new File(Helper.AVATAR_LOCATION +
                        Integer.parseInt(tokens[0]) + ".png")).isFile()) image.delete();
            }
            reader.close();
            writer.close();
            Files.move(tmp.toPath(), targetFile.toPath(),
                    REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Search students by constraints
     *
     * @param constraints constraints to match
     * @return ArrayList of Student objects that match the constraints
     */
    public static ArrayList<Student> getAll(SearchConstraints constraints) {
        ArrayList<Student> list = new ArrayList<>();
        Cursor cursor = new Cursor(constraints);
        Student student;
        while ((student = cursor.fetch()) != null) list.add(student);
        cursor.close();

        // apply sorting
        Comparator<Student> comparator = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (constraints.sortBy.equals(SortBy.NAME))
                    return o1.name.compareTo(o2.name);
                else if (constraints.sortBy.equals(SortBy.DEPARTMENT))
                    return o1.dept.fullName.compareTo(o2.dept.fullName);
                else if (constraints.sortBy.equals(SortBy.SESSION))
                    return o1.session.compareTo(o2.session);
                else if (constraints.sortBy.equals(SortBy.BIRTH))
                    return o1.birth.toUnixFormat().compareTo(o2.birth.toUnixFormat());
                else return o1.id - o2.id;
            }
        };
        // sort now
        if (constraints.sortOpt.equals(SortOpt.ASC))
            list.sort(comparator);
        else list.sort(comparator.reversed());

        // return the list
        return list;
    }

    public static Cursor search(SearchConstraints constraints) {
        return new Cursor(constraints);
    }

    public synchronized void save() {
        boolean exists = false; // If current entity already exists
        try {
            File tmp = new File(Helper.TMP_FILE);
            File stdFile = new File(Helper.STUDENT_FILE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
            ArrayList<Student> students = getAll();
            for (Student std : students) {
                if (id == std.id) {
                    writer.write(id + "\t" + name + "\t" + dept.getId() + "\t" + session + "\t" + birth.toUnixFormat() + "\t" + father + "\t" + mother +
                            "\t" + currentAddress + "\t" + permanentAddress + "\t" + phone + "\t" + email +
                            "\t" + religion + "\t" + gender + "\n");
                    exists = true;
                } else
                    writer.write(std.id + "\t" + std.name + "\t" + std.dept.getId() + "\t" + std.session + "\t" + std.birth.toUnixFormat() + "\t" + std.father + "\t" + std.mother +
                            "\t" + std.currentAddress + "\t" + std.permanentAddress + "\t" + std.phone + "\t" + std.email +
                            "\t" + std.religion + "\t" + std.gender + "\n");
            }

            if (!exists) // insert as a new entity
                writer.write(id + "\t" + name + "\t" + dept.getId() + "\t" + session + "\t" + birth.toUnixFormat() + "\t" + father + "\t" + mother +
                        "\t" + currentAddress + "\t" + permanentAddress + "\t" + phone + "\t" + email +
                        "\t" + religion + "\t" + gender + "\n");
            writer.close();
            Files.move(tmp.toPath(), stdFile.toPath(),
                    REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public enum SearchOption {
        ALL, BEFORE, AFTER, BETWEEN, EXACTLY
    }

    public enum SortBy {
        ID, NAME, DEPARTMENT, SESSION, BIRTH
    }

    public enum SortOpt {
        ASC, DESC
    }

    /**
     * This controls fetching students one by one
     */
    public static class Cursor {
        private BufferedReader reader;
        private SearchConstraints searchConstraints;

        private Cursor(SearchConstraints constraints) {
            searchConstraints = constraints;
            try {
                File input = new File(Helper.STUDENT_FILE);
                reader = new BufferedReader(new FileReader(input));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Get next student matching the constraints
         *
         * @return Instance of Student if exists satisfying the constraints null otherwise
         */
        public Student fetch() {
            try {
                String line;
                while (true) {
                    line = reader.readLine();
                    if (line == null) return null;
                    // Apply & Check Constraints First
                    Student student = buildFrom(line);
                    if (student == null) continue;
                    if (searchConstraints == null) return student;
                    // check department
                    if (searchConstraints.department != null && student.dept.getId() != searchConstraints.department.getId())
                        continue;
                    // check birth date
                    if (searchConstraints.dob == SearchOption.BEFORE &&
                            searchConstraints.dobStart.toUnixFormat().compareTo(student.birth.toUnixFormat()) < 0)
                        continue;
                    if (searchConstraints.dob == SearchOption.AFTER &&
                            searchConstraints.dobStart.toUnixFormat().compareTo(student.birth.toUnixFormat()) > 0)
                        continue;
                    if (searchConstraints.dob == SearchOption.BETWEEN &&
                            (searchConstraints.dobStart.toUnixFormat().compareTo(student.birth.toUnixFormat()) > 0 ||
                                    searchConstraints.dobEnd.toUnixFormat().compareTo(student.birth.toUnixFormat()) < 0))
                        continue;

                    // check session
                    if (searchConstraints.session == SearchOption.BEFORE && student.session.compareTo(searchConstraints.sessionStart) > 0)
                        continue;
                    if (searchConstraints.session == SearchOption.AFTER && student.session.compareTo(searchConstraints.sessionStart) < 0)
                        continue;
                    if (searchConstraints.session == SearchOption.BETWEEN &&
                            (student.session.compareTo(searchConstraints.sessionStart) < 0 ||
                                    student.session.compareTo(searchConstraints.sessionEnd) > 0))
                        continue;
                    if (searchConstraints.session == SearchOption.EXACTLY && student.session.compareTo(searchConstraints.sessionStart) != 0)
                        continue;

                    // Ow! you came here, preconditions passed indeed.
                    // Now apply our keyword search algorithm.
                    if (Helper.searchOK(searchConstraints.keyword, line)) // Ok, all conditions passed.
                        return student;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void close() {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final static class SearchConstraints {
        public String keyword, sessionStart, sessionEnd;
        public MyDate dobStart, dobEnd;
        public Department department;
        public Student.SortBy sortBy;
        public SortOpt sortOpt;
        public SearchOption dob, session;
    }
}
