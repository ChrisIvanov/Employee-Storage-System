package model;

public class Employee extends AbstractEmployee {
    private short id;
    private String name;
    private String startDate;
    private String department;
    private String role;
    private double salary;

    public Employee() {
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public void setIsFired(boolean isFired) {
        super.setIsFired(isFired);
    }

    @Override
    public boolean getIsFired() {
        return super.getIsFired();
    }

    public String toString(){
        return String.format("Id: " + getId() + "\nName: " + getName() + "\nStart Date: " + getStartDate() +
                "\nDepartment: " + getDepartment() + "\nRole: " + getRole() +
                "\nSalary: " + getSalary());
    }
}
