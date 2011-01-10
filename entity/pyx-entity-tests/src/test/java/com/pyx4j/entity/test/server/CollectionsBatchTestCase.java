/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 09, 2011
 * @author 
 * @version $Id$
 */

package com.pyx4j.entity.test.server;

import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Province;

public abstract class CollectionsBatchTestCase extends DatastoreTestBase {

    public void testBatchInsertRertieveDelete() {

        Vector<Employee> ve = new Vector<Employee>();
        String[] sarr = { "Bill", "Steve", "Larry", "Serge", "Mike" };
        for (String eName : sarr) {
            Employee emp = EntityFactory.create(Employee.class);
            String empName = eName + uniqueString();
            emp.firstName().setValue(empName);
            ve.add(emp);
        }
        srv.persist(ve); // save employees
        Vector<Long> primaryKeys = new Vector<Long>();
        for (Employee ee : ve) {
            Assert.assertNotNull("persist verify PK Not Null", ee.getPrimaryKey());
            Assert.assertTrue("persist verify PK Value", ee.getPrimaryKey() > 0);
            primaryKeys.add(ee.getPrimaryKey());
        }

        Map<Long, Employee> empsRet = srv.retrieve(Employee.class, primaryKeys); //get employees back as a set of new entities
        for (Employee templ : ve) {
            Assert.assertNotNull("verify retrieve", empsRet.get(templ.getPrimaryKey()));
            Assert.assertEquals("PK Value", empsRet.get(templ.getPrimaryKey()).getPrimaryKey(), templ.getPrimaryKey());
            Assert.assertEquals("Name", empsRet.get(templ.getPrimaryKey()).firstName().getValue(), templ.firstName().getValue());
        }

        srv.delete(Employee.class, primaryKeys); // Batch Delete

        for (long pk : primaryKeys) {
            Employee empRet2 = srv.retrieve(Employee.class, pk); //try to get employee by PK back as a new entity
            Assert.assertNull("verify retrieve by PK " + pk, empRet2);
        }
    }

    public void testBatchPersist() {

        // test batch inserts ////////////////////////////
        Vector<Province> vp = new Vector<Province>();
        String[] sarr = { "Ontario", "Manitoba", "Nunavut", "Yukon", "PEI" };
        for (String pName : sarr) {
            Province prov = EntityFactory.create(Province.class);
            prov.name().setValue(pName + uniqueString());
            vp.add(prov);
        }
        srv.persist(vp); // save as a batch insert _ALL_ 
        Vector<Long> primaryKeys = new Vector<Long>();

        for (Province pr : vp) {
            ///// test PKs were assigned 
            Assert.assertNotNull("persist verify PK Not Null", pr.getPrimaryKey());
            Assert.assertTrue("persist verify PK Value", pr.getPrimaryKey() > 0);
            primaryKeys.add(pr.getPrimaryKey());
        }
        for (int i = 0; i < vp.size(); i++) {
            Province provRet = srv.retrieve(Province.class, vp.get(i).getPrimaryKey()); //get employee back as a new entity
            //// test PKs and names match 
            Assert.assertNotNull("verify retrieve", provRet);
            Assert.assertEquals("PK Value", vp.get(i).getPrimaryKey(), provRet.getPrimaryKey());
            //            Assert.assertEquals("Name", vp.get(i).name().getValue(), provRet.name().getValue());
        }

        // test batch updates ////////////////////////////
        String addStr = uniqueString();
        for (Province prov : vp) {
            prov.name().setValue(prov.name().getValue() + addStr); // update name 
        }
        srv.persist(vp); // save as a batch updates _ALL_ 

        for (int i = 0; i < vp.size(); i++) {
            Province provRet = srv.retrieve(Province.class, vp.get(i).getPrimaryKey());
            //// test PKs and names match 
            Assert.assertNotNull("verify retrieve", provRet);
            Assert.assertEquals("PK Value", vp.get(i).getPrimaryKey(), provRet.getPrimaryKey());
            //            Assert.assertEquals("Name", vp.get(i).name().getValue(), provRet.name().getValue());
        }

        // tests with some Batch inserts and some Batch updates  ////////////////////////////
        addStr = uniqueString();
        for (Province prov : vp) {
            prov.name().setValue(prov.name().getValue() + addStr); // update name 
        }
        String[] morestrings = { "Alberta", "BC", "NS", "NL" };
        for (String pName : morestrings) {
            Province prov = EntityFactory.create(Province.class);
            prov.name().setValue(pName + uniqueString());
            vp.add(prov);
        }
        srv.persist(vp); // save as a batch insert _SOME_ and batch update _SOME_ 

        primaryKeys.clear();
        for (Province pr : vp) {
            primaryKeys.add(pr.getPrimaryKey());
        }
        Map<Long, Province> provMap = srv.retrieve(Province.class, primaryKeys); //get employees back as a set of new entities
        for (Province prov : vp) {
            //// test PKs and names match 
            Assert.assertNotNull("verify Batch retrieve", prov);
            Assert.assertTrue("PK Value", provMap.containsKey(prov.getPrimaryKey()));
            //            Assert.assertEquals("Name", provMap.get(prov.getPrimaryKey()).name().getValue(), prov.name().getValue());
        }

        //TODO: add tests with some Batch inserts with ASSIGNED PKs (TableModel.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED)

    }

    public void testBatchUpdateDelete() {

        Vector<Employee> ve = new Vector<Employee>();
        String[] eNames = { "Bill", "Steve", "Larry", "Serge", "Mike" };
        for (String eName : eNames) {
            Employee emp = EntityFactory.create(Employee.class);
            String empName = eName + uniqueString();
            emp.firstName().setValue(empName);
            ve.add(emp);
        }
        srv.persist(ve); // save employees
        Vector<Long> primaryKeys = new Vector<Long>();
        for (Employee ee : ve) {
            Assert.assertNotNull("persist verify PK Not Null", ee.getPrimaryKey());
            Assert.assertTrue("persist verify PK Value", ee.getPrimaryKey() > 0);
            primaryKeys.add(ee.getPrimaryKey());
        }

        Map<Long, Employee> empsRet = srv.retrieve(Employee.class, primaryKeys); //get employees back as a set of new entities
        Assert.assertTrue("Map size is the same as PK list", primaryKeys.size() == empsRet.size());
        for (Employee templ : ve) {
            Assert.assertNotNull("verify retrieve", empsRet.get(templ.getPrimaryKey()));
            Assert.assertEquals("PK Value", empsRet.get(templ.getPrimaryKey()).getPrimaryKey(), templ.getPrimaryKey());
            //        	Assert.assertEquals("Name", empsRet.get(templ.getPrimaryKey()).firstName().getValue(), templ.firstName().getValue());
        }

        srv.delete(Employee.class, primaryKeys); // Batch Delete

        for (long pk : primaryKeys) {
            Employee empRet2 = srv.retrieve(Employee.class, pk); //try to get employee by PK back as a new entity
            Assert.assertNull("verify retrieve by PK " + pk, empRet2);
        }
    }

}
