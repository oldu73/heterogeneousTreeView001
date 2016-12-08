package model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a department. This is used for saving the
 * department to XML.
 *
 * @author Olivier Durand
 */
@XmlRootElement(name = "department")
public class DepartmentWrapper {

    private String name;

    private List<Employee> employees;

    @XmlAttribute(name = "name", required = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "employee")
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

}