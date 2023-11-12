package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jdk.jshell.spi.ExecutionControl;
import model.Employee;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class employeeService {
    private static Employee employee;

    public static void handleInput() {
        // Display the main menu options
        displayMainMenu();
        // Boolean flag
        boolean isCorrectInput = false;

        // Variable takes the user's input.
        String input = localToolbox.sc.nextLine();

        // Do-while loop to reuse the code and siphon the U's input.
        while (!isCorrectInput) {

            // Setting the boolean value to `true` in order to reset the loop if needed.
            isCorrectInput = true;


            switch (input) {
                case "1" -> addEmployee();
                case "2" -> editEmployee();
                case "3" -> fireEmployee();
                case "4" -> listAllEmployee();
                case "5" -> exitProgram();
                default -> {
                    isCorrectInput = false;
                    System.out.println("Please input a number from the menu.");
                    input = localToolbox.sc.nextLine();
                }
            }
        }
    }

    private static void displayMainMenu() {
        try {
            // Read through the available text in the file following the path
            List<String> welcomeMenu = Files.readAllLines(Paths.get(localToolbox.mainMenu));

            // Loop through the available lines of text and print them on the console.
            for (String line : welcomeMenu) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addEmployee() {
        gatherEmployeeData("From add employee");

        updateEmployeeStorageWithNewEmployee(employee);

        System.out.println(employee.toString());

        handleInput();
    }

    // Change employee data
    private static void editEmployee() {
        try {
            List<String> editEmployeeMenu = Files.readAllLines(Paths.get(localToolbox.editEmployeePath));
            String employeeID;
            int parsedEmployeeID = 0;
            for (String line : editEmployeeMenu) {
                if (line.contains("Enter employee ID:")) {
                    System.out.print(line);
                    employeeID = localToolbox.sc.nextLine();
                    while (true) {
                        if (!ensureIdIsANumber(employeeID)) {
                            System.out.print("Enter a valid employee ID: ");
                            employeeID = localToolbox.sc.nextLine();
                            if (ensureIdIsANumber(employeeID)) {
                                parsedEmployeeID = Short.parseShort(employeeID);
                                break;
                            }
                        } else if (ensureIdIsANumber(employeeID)) {
                            parsedEmployeeID = Short.parseShort(employeeID);
                            break;
                        }
                    }
                } else {
                    System.out.println(line);
                }
            }

            List<Employee> allEmployees = getExistingEmployees();
            List<String> editEmployeeInformationLines = Files.readAllLines(Paths.get(localToolbox.editEmployeeInformationPath));
            boolean isExisting = false;

            for (Employee employeeToEdit : allEmployees) {
                if (employeeToEdit.getId() == parsedEmployeeID) {
                    isExisting = true;
                    employee = employeeToEdit;
                    System.out.println("If you would like to edit any of the information enter data. If not just hit Enter");

                    // Read the enter new information menu
                    gatherEmployeeData("From edit employee");

                    // order the list by Id
                    allEmployees.sort(Comparator.comparing(Employee::getId));

                    // update the list with the edited employee
                    updateEmployeeFile(allEmployees);
                }
            }

            if (!isExisting) {
                System.out.println("No employee with this ID was found.");
            }
        } catch (IOException e) {
            System.out.println("No records in the employee database");
        }

        handleInput();
    }

    private static void fireEmployee() {
        try {
            List<String> fireEmployeePath = Files.readAllLines(Paths.get(localToolbox.fireEmployeePath));
            String employeeID;
            Short parsedEmployeeID = 0;
            for (String line : fireEmployeePath) {
                if (line.contains("Enter employee ID:")) {
                    System.out.print(line);
                    employeeID = localToolbox.sc.nextLine();
                    while (true) {
                        if (ensureIdIsANumber(employeeID)) {
                            parsedEmployeeID = Short.parseShort(employeeID);
                            break;
                        } else {
                            System.out.print("Enter a valid employee ID: ");
                            employeeID = localToolbox.sc.nextLine();
                        }
                    }
                } else {
                    System.out.println(line);
                }
            }

            employee = getEmployeeById(parsedEmployeeID);

            if (employee != null) {
                employee.setIsFired(true);
                System.out.println("Employee successfully fired.");

                removeEmployeeFromStorage(parsedEmployeeID);
            } else {
                System.out.println("No employee with this ID exists.2");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        handleInput();
    }

    private static void removeEmployeeFromStorage(Short id) {
        List<Employee> allEmployees = getExistingEmployees();
        allEmployees.removeIf(employee -> employee.getId() == id);
        updateEmployeeFile(allEmployees);
    }

    private static void listAllEmployee() {
        List<Employee> allEmployees = getExistingEmployees();

        for (Employee employee : allEmployees) {
            System.out.println(employee.toString());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        handleInput();
    }

    private static void exitProgram() {
        System.out.println("Goodbye!");
    }

    private static void gatherEmployeeData(String fromMethod) {
        try {
            List<String> employeeDataMenu;
            // Read through the available text in the file following the path
            if (Objects.equals(fromMethod, "From edit employee")) {
                employeeDataMenu = Files.readAllLines(Paths.get(localToolbox.editEmployeeInformationPath));
            } else {
                employee = new Employee();
                employeeDataMenu = Files.readAllLines(Paths.get(localToolbox.readNewEmployeeData));
            }

            // Loop through the available lines of text and print them on the console.
            for (int i = 1; i < employeeDataMenu.size(); i++) {
                if (Objects.equals(fromMethod, "From edit employee") && i == 1) {
                    i += 1;
                }
                System.out.print(employeeDataMenu.get(i));
                String currentEmployeeData = localToolbox.sc.nextLine();

                if (Objects.equals(fromMethod, "From edit employee") && Objects.equals(currentEmployeeData, "")) {
                    continue;
                }

                switch (i) {
                    case 1: {
                        // Set employee ID
                        ensureEmployeeIDIsCorrect(currentEmployeeData);
                        break;
                    }
                    case 2: {
                        // Set employee name
                        employee.setName(currentEmployeeData);
                        break;
                    }
                    case 3: {
                        ensureIsCorrectDateAndWrite(currentEmployeeData);
                        break;
                    }
                    case 4: {
                        // Set employee department
                        employee.setDepartment(currentEmployeeData);
                        break;
                    }
                    case 5: {
                        // Set employee role
                        employee.setRole(currentEmployeeData);
                        break;
                    }
                    case 6: {
                        // Set employee salary
                        ensureSalaryIsCorrectAndWrite(currentEmployeeData);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void ensureEmployeeIDIsCorrect(String id) {
        while (true) {
            if (ensureIdIsANumber(id)) {
                short employeeId = Short.parseShort(id);
                if (!ensureIfEmployeeExists(employeeId)) {
                    employee.setId(employeeId);
                    break;
                } else {
                    System.out.println("Employee with this ID already exists.");
                }
            } else {
                System.out.println("Incorrect ID value. Enter an integer number for ID:");
            }

            System.out.print("Id: ");
            id = localToolbox.sc.nextLine();
        }
    }

    private static boolean ensureIfEmployeeExists(short id) {
        List<Employee> employees = getExistingEmployees();
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return true;
            }
        }

        return false;
    }

    private static void ensureSalaryIsCorrectAndWrite(String currentEmployeeData) {
        while (true) {
            if (ensureIfSalaryIsCorrect(currentEmployeeData)) {
                employee.setSalary(Double.parseDouble(currentEmployeeData));
                break;
            } else {
                System.out.println("You've entered invalid data for salary. Try again:");
                System.out.print("Salary: ");
                currentEmployeeData = localToolbox.sc.nextLine();
            }
        }
    }

    private static void ensureIsCorrectDateAndWrite(String currentEmployeeData) {
        while (true) {
            if (ensureDateIsCorrectFormat(currentEmployeeData)) {
                if (ensureDateIsValid(currentEmployeeData)) {
                    // If date is correct set employee start date
                    employee.setStartDate(currentEmployeeData);
                    break;
                } else {
                    System.out.println("You've entered a non-existing date. Try again:");
                }
            } else {
                System.out.println("Date is not correctly input. Try again (YYYY-MM-DD)");
            }

            System.out.print("Start date: ");
            currentEmployeeData = localToolbox.sc.nextLine();
        }
    }

    private static void updateEmployeeStorageWithNewEmployee(Employee employee) {
        List<Employee> employees = getExistingEmployees();

        if (employees == null) {
            employees = new ArrayList<>();
        }

        // Add new employee
        employees.add(employee);

        // Serialize and write the updated list of employees back to the file
        updateEmployeeFile(employees);
    }

    private static void updateEmployeeFile(List<Employee> employees) {
        try (FileWriter writer = new FileWriter(localToolbox.employeeStoragePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(employees, writer);
            System.out.println("Database was updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> getExistingEmployees() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(localToolbox.employeeStoragePath)));
            Type listType = new TypeToken<List<Employee>>() {
            }.getType();
            return new Gson().fromJson(json, listType);
        } catch (IOException e) {
            // File doesn't exist or empty
            return new ArrayList<>();
        }
    }

    private static Employee getEmployeeById(short id) {
        List<Employee> allEmployees = getExistingEmployees();

        for (Employee currEmployee : allEmployees) {
            if (currEmployee.getId() == id) {
                return currEmployee;
            }
        }

        System.out.println("No employee with this ID exists.");
        return null;
    }

    private static boolean ensureIdIsANumber(String id) {
        try {
            Short.parseShort(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean ensureIfSalaryIsCorrect(String salary) {
        try {
            Double.parseDouble(salary);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if date is valid
    private static boolean ensureDateIsValid(String startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Disable leniency

        try {
            dateFormat.parse(startDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Check if the date is in the correct format
    private static boolean ensureDateIsCorrectFormat(String startDate) {
        // Regex to check if the date matches format "YYYY-MM-DD"
        return startDate.matches("\\d{4}-\\d{2}-\\d{2}");
    }


    private class localToolbox {
        private static final Scanner sc = new Scanner(System.in);
        private static final String mainMenu = ".\\src\\main\\java\\system\\menus\\mainMenu.txt";
        private static final String readNewEmployeeData = ".\\src\\main\\java\\system\\menus\\readNewEmployeeData.txt";
        private static final String employeeStoragePath = ".\\src\\main\\java\\Storage\\employeeFileSystem.json";
        private static final String editEmployeePath = ".\\src\\main\\java\\system\\menus\\editEmployeeMenu.txt";
        private static final String editEmployeeInformationPath = ".\\src\\main\\java\\system\\menus\\readEditEmployeeData.txt";
        private static final String fireEmployeePath = ".\\src\\main\\java\\system\\menus\\fireEmployee.txt";

    }
}
