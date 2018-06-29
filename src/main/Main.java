package main;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Main {
    // List for input data of employees
    private static List<Employee> employeeList;
    public Factory factory = new Factory();

    public static void main(String[] args) throws IOException {
        //input File names
        System.out.println("Input path to reading objects from file");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String nameInputFile = bufferedReader.readLine();
        if(!nameInputFile.endsWith(".txt")) {
            throw new IncorrectFileFormatException();
        }
        System.out.println("Input path to writing objects into file");
        String nameOutputFile = bufferedReader.readLine();
        Sorter sorter = new Sorter();
        // Read and stored info about employees from input file
        employeeList = Util.readFile(nameInputFile);

        // Calculate the average monthly salary for each employee
        for (Employee employee : employeeList) {
            employee.setAverageMonthlySalary(Util.calculatePayment(employee));
        }
        // Sort the employees above by average monthly salary
        employeeList = sorter.sortAboveByAverageSalary(employeeList);

        Util.writeDataOfEmployeesToFile(employeeList, nameOutputFile);
        System.out.println("ID, name and monthly salary for all employees from collection");
        Util.printInfoAboutAllEmployees(employeeList);
        System.out.println("information about first five employees from collection (problem a)");
        Util.printInfoFromFirstToFiveEmployee(employeeList);
        System.out.println("ID of three last employees from collection (problem b)");
        Util.printIdOfThreeLastEmployees(employeeList);

    }

    public static class IncorrectFileFormatException extends RuntimeException {
    }

    public static class Employee {
        private int id;
        private String name;
        private boolean isFixedPayment;
        private double averageMonthlySalary;

        public Employee(int id, String name, boolean isFixedPayment) {
            this.id = id;
            this.name = name;
            this.isFixedPayment = isFixedPayment;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isFixedPayment() {
            return isFixedPayment;
        }

        public void setFixedPayment(boolean fixedPayment) {
            isFixedPayment = fixedPayment;
        }

        public double getAverageMonthlySalary() {
            return averageMonthlySalary;
        }

        public void setAverageMonthlySalary(double averageMonthlySalary) {
            this.averageMonthlySalary = averageMonthlySalary;
        }
    }

    public static class RentedEmployee extends Employee {
        private double hourlyRate;

        public RentedEmployee(int id, String name, boolean isFixedPayment, double hourlyRate) {
            super(id, name, isFixedPayment);
            this.hourlyRate = hourlyRate;
        }

        public double getHourlyRate() {
            return hourlyRate;
        }

        public void setHourlyRate(double hourlyWage) {
            this.hourlyRate = hourlyWage;
        }
    }

    public static class FullTimeEmployee extends Employee {
        private double fixedPayment;

        public FullTimeEmployee(int id, String name, boolean isFixedPayment, double fixedPayment) {
            super(id, name, isFixedPayment);
            this.fixedPayment = fixedPayment;
        }

        public double getFixedPayment() {
            return fixedPayment;
        }

        public void setFixedPayment(double fixedPayment) {
            this.fixedPayment = fixedPayment;
        }
    }

    public enum TypeOfEmployee {
        FULL_TIME_EMPLOYEE,
        RENTED_EMPLOYEE
    }

    public static class Factory {
        public static Employee getEmployee(TypeOfEmployee typeOfEmployee, String[] dataOfEmployee) {
            Employee employee = null;
            switch (typeOfEmployee) {
                case FULL_TIME_EMPLOYEE:
                    employee = new FullTimeEmployee(Integer.parseInt(dataOfEmployee[0]),
                            dataOfEmployee[1], Boolean.parseBoolean(dataOfEmployee[2]),
                            Double.parseDouble(dataOfEmployee[3]));
                    break;
                case RENTED_EMPLOYEE:
                    employee = new RentedEmployee(Integer.parseInt(dataOfEmployee[0]), dataOfEmployee[1],
                            Boolean.parseBoolean(dataOfEmployee[2]), Double.parseDouble(dataOfEmployee[3]));
                    break;
            }
            return employee;
        }
    }

    public static class Sorter {
        /**
         * For sorting list of employees used the Java's inside collections
         * sort with comparator
         *
         * @param employees - list of employees.
         * @return - sorted list of employees.
         */
        public List<Employee> sortAboveByAverageSalary(List<Employee> employees) {
            Comparator<Employee> eComp = new ComparatorByAverageSalary().thenComparing(new ComparatorByName());
            Collections.sort(employees, eComp);
            System.out.println("sorted by increase average monthly salary successfully");
            return employees;
        }

        //sort employees by increasing average salary
        private class ComparatorByAverageSalary implements Comparator<Employee> {

            @Override
            public int compare(Employee employee1, Employee employee2) {
                return employee1.getAverageMonthlySalary() < employee2.getAverageMonthlySalary()
                        ? -1 : employee1.getAverageMonthlySalary() == employee2.getAverageMonthlySalary()
                        ? 0 : 1;
            }
        }

        // if average monthly salary of employees is duplicated used next comparator for sort by name
        private class ComparatorByName implements Comparator<Employee> {

            @Override
            public int compare(Employee employee1, Employee employee2) {
                return employee1.getName().compareTo(employee2.getName());
            }
        }
    }


    public static class Util {


        public static List<Employee> readFile(String pathToFile) {
            List<Employee> employees = new LinkedList<>();
            String[] employeeData;
            String line;
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(pathToFile));
                while ((line = br.readLine()) != null) {
                    //Split the line on the parts with data, and stored this data to array
                    employeeData = line.split(" ");
                    //create the object of employee depend on he has fixed payment
                    if (Boolean.parseBoolean(employeeData[2])) {
                        employees.add(Factory.getEmployee(TypeOfEmployee.FULL_TIME_EMPLOYEE, employeeData));
                    } else employees.add(Factory.getEmployee(TypeOfEmployee.RENTED_EMPLOYEE, employeeData));
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return employees;
        }

        /**
         * For employees with hourly wage (rented employees) use next formula: “average monthly salary = 20.8*8* hourly rate”,
         * For employees with fixed payment(full-time employees) - “average monthly salary = fixed monthly payment”;
         *
         * @param employee
         * @return amount of salary
         */
        public static double calculatePayment(Employee employee) {
            //check instance of employee
            if (employee.isFixedPayment) {
                //For get the amount of fixed payment of the full-time employee use downcasting
                return ((FullTimeEmployee) employee).getFixedPayment();
            } else return 20.8 * 8 * ((RentedEmployee) employee).getHourlyRate();
        }

        public static void writeDataOfEmployeesToFile(List<Employee> employees, String pathToFile) {
            File file = new File(pathToFile);
            try {
                // check whether a file exists
                if (!file.exists()) {
                    file.createNewFile();
                }
                PrintWriter out = new PrintWriter(file.getAbsoluteFile());
                try {
                    for (Employee employee : employees) {
                        out.print(Util.getInfoAboutEmployee(employee) + System.getProperty("line.separator"));
                    }
                } finally {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //   Print to console information of all employees
        public static void printInfoAboutAllEmployees(List<Employee> employees) {
            for (Employee employee : employees) {
                System.out.println(getInfoAboutEmployee(employee));
            }
        }

        // print to console all information of employees from first to 5th employee from list of employees
        public static void printInfoFromFirstToFiveEmployee(List<Employee> employees) {
            int i = 0;
            for (Employee employee : employees) {
                i++;
                if (i == 6) break;
                System.out.println(getInfoAboutEmployee(employee));
            }
        }

        // print to console ID of three last employees from list of employees
        public static void printIdOfThreeLastEmployees(List<Employee> employees) {
            for (int i = employees.size() - 3; i < employees.size(); i++) {
                System.out.println(getIdOfEmployee(i, employees));
            }
        }

        /**
         * Get all needs information from object "employee"
         *
         * @param employee - object "employee"
         * @return - String with needs employee's information
         */
        private static String getInfoAboutEmployee(Employee employee) {
            return "ID - " + employee.getId() + ", name - " + employee.getName() +
                    ", average salary - " + employee.getAverageMonthlySalary();
        }

        /**
         * Get ID needs information from object "employee"
         *
         * @param i - number of employee in the list of employee
         * @return - String with needs employee's information
         */
        private static String getIdOfEmployee(int i, List<Employee> employees) {
            return "ID - " + employees.get(i).getId();
        }

    }

}

