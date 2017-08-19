package team7.cu.sim;

import team7.cu.utils.Helper;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Department {
    public String fullName, shortName;
    private int id; // database row id

    public Department(String full_name, String short_name) {
        fullName = full_name;
        shortName = short_name;
    }

    public Department(String fullName) {
        this(fullName, null);
    }

    public synchronized static ArrayList<Department> getAll() {
        ArrayList<Department> depts = new ArrayList<>();
        try {
            // Input file to read (existing departments)
            File input = new File(Helper.DEPARTMENT_FILE);
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                Department dept = buildFrom(line);
                if (dept != null) depts.add(dept);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        depts.sort(new Comparator<Department>() {
            @Override
            public int compare(Department o1, Department o2) {
                return o1.fullName.compareTo(o2.fullName);
            }
        });
        return depts;
    }

    private static Department buildFrom(String tsvRow) {
        try {
            Department department = new Department(null);
            String[] tokens = tsvRow.split("\t");
            if (tokens.length > 1) {
                department.id = Integer.parseInt(tokens[0]);
                department.fullName = tokens[1];
                if (tokens.length > 2) department.shortName = tokens[2];
                return department;

            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static Department getById(int id) {
        try {
            // Input file to read (existing departments)
            File input = new File(Helper.DEPARTMENT_FILE);
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                // if (tokens.length != 3) continue;
                if (Integer.parseInt(tokens[0]) == id) {
                    reader.close(); // close it before return
                    return buildFrom(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized Department getByName(String name) {
        for (Department department : getAll())
            if (department.fullName.equalsIgnoreCase(name)) return department;
        return null;
    }

    public static void delete(HashSet<Integer> selectedDeptIds) {
        Student.delete(selectedDeptIds, DataChangeOption.DEPARTMENT);
    }

    public int getId() {
        return id;
    }

    public synchronized void save() {
        try {
            File deptFile = new File(Helper.DEPARTMENT_FILE);
            if (id == 0) {
                int id = getMaxId() + 1;
                BufferedWriter writer = new BufferedWriter(new FileWriter(deptFile, true));
                writer.write(id + "\t" + fullName + "\t" + String.valueOf(shortName) + "\n");
                writer.close();
                return;
            }
            File tmp = new File(Helper.TMP_FILE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
            BufferedReader reader = new BufferedReader(new FileReader(deptFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("^" + id + "\t.*")) // row starts with the target id, edit this row
                    writer.write(id + "\t" + fullName + "\t" + String.valueOf(shortName) + "\n");
                else writer.write(line + "\n"); // keep row unchanged
            }
            reader.close();
            writer.close();
            Files.move(tmp.toPath(), deptFile.toPath(),
                    REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to insert new department we need the maximum id
    private int getMaxId() {
        int max = 0;
        for (Department dept : Department.getAll()) {
            max = Math.max(max, dept.id);
        }
        return max;
    }

    @Override
    public String toString() {
        if (shortName == null || shortName.isEmpty()) return fullName;
        return fullName + " (" + shortName + ")";
    }
}
