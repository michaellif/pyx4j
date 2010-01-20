package com.example.jdo;

import java.io.IOException;
import java.util.Vector;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.jdo.domain.Department;
import com.example.jdo.domain.Employee;

@SuppressWarnings("serial")
public class DBServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");

        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
        PersistenceManager pm = pmf.getPersistenceManager();

        for (int dept = 0; dept < 3; dept++) {

            Department dep = new Department();
            dep.name = "dept_" + dept;
            dep.employees = new Vector<Employee>();
            pm.makePersistent(dep);

            Employee manager = new Employee("manager_" + dept);
            //manager.deptNo = dept;
            pm.makePersistent(manager);

            for (int i = 0; i < 2; i++) {
                Employee emp = new Employee("emp_" + dept + "_" + i);
                //emp.deptNo = dept;
                //emp.manager = manager;
                pm.makePersistent(emp);
                dep.employees.add(emp);
            }

        }

        resp.getWriter().println("Data created");
    }
}
