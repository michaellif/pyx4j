/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;

public class GadgetServiceImplTestBase extends IntegrationTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        building("B1").create("ON");
        building("B2").create("ON");
        building("B3").create("ON");

        portfolio("P1").create().addBuildings("B1");
        portfolio("P2").create().addBuildings("B2");
        portfolio("P3").create().addBuildings("B3");

        employee("employeeAdmin@pyx4j.com").create("admin", "adminovich", "12345");
        employee("employee1@pyx4j.com").create("employee1", "employee1ovich", "12345").restrictToPortfolios(portfolio("P1"), portfolio("P3"));
        employee("employee2@pyx4j.com").create("employee2", "employee2ovich", "12345").restrictToPortfolios(portfolio("P2"), portfolio("P3"));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        return Arrays.<Class<? extends MockDataModel<?>>> asList(//@formatter:off
                PmcDataModel.class,
                LocationsDataModel.class
        );//@formatter:on
    }

    private class BuildingWrapper {

        Building building;

        private final String propertyCode;

        public BuildingWrapper(String propertyCode) {
            EntityQueryCriteria<Building> c = EntityQueryCriteria.create(Building.class);
            c.eq(c.proto().propertyCode(), propertyCode);
            this.propertyCode = propertyCode;
            building = Persistence.service().retrieve(c);
        }

        // TODO add complete address
        public BuildingWrapper create(String provinceCode) {
            if (building != null) {
                throw new IllegalStateException("the building with property code " + propertyCode + " already exists");
            }
            Building building = EntityFactory.create(Building.class);

            building.propertyCode().setValue(propertyCode);

            EntityQueryCriteria<Province> provinceCriteria = EntityQueryCriteria.create(Province.class);
            provinceCriteria.eq(provinceCriteria.proto().code(), provinceCode);
            building.info().address().province().set(Persistence.service().retrieve(provinceCriteria));
            Persistence.service().persist(building);

            // TODO:
            //ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
            //ServerSideFactory.create(DefaultProductCatalogFacade.class).persistFor(building);

            Persistence.service().persist(building);
            this.building = building;

            return this;
        }
    }

    private class PortfolioWrapper {

        private Portfolio portfolio;

        private final String name;

        public PortfolioWrapper(String name) {
            EntityQueryCriteria<Portfolio> c = EntityQueryCriteria.create(Portfolio.class);
            c.eq(c.proto().name(), name);
            this.portfolio = Persistence.service().retrieve(c);
            this.name = name;
        }

        public PortfolioWrapper create() {
            this.portfolio = EntityFactory.create(Portfolio.class);
            this.portfolio.name().setValue(name);
            Persistence.service().persist(portfolio);
            return this;
        }

        public PortfolioWrapper addBuildings(String... propertyCodes) {
            List<BuildingWrapper> bb = new ArrayList<GadgetServiceImplTestBase.BuildingWrapper>();
            for (String propertyCode : propertyCodes) {
                bb.add(new BuildingWrapper(propertyCode));
            }
            addBuilding(bb.toArray(new BuildingWrapper[bb.size()]));
            return this;
        }

        public PortfolioWrapper addBuilding(BuildingWrapper... buildings) {
            for (BuildingWrapper b : buildings) {
                this.portfolio.buildings().add(b.building);
            }
            Persistence.service().merge(portfolio);
            return this;
        }
    }

    private class EmployeeWrapper {

        Employee employee;

        private final String email;

        public EmployeeWrapper(String employeeEmail) {
            EntityQueryCriteria<Employee> c = EntityQueryCriteria.create(Employee.class);
            c.eq(c.proto().email(), employeeEmail);

            employee = Persistence.service().retrieve(c);
            this.email = employeeEmail;
        }

        public EmployeeWrapper create(String firstName, String lastName, String password) {
            if (employee != null) {
                throw new IllegalStateException("This employee already exists " + email);
            }
            EmployeeDTO dto = EntityFactory.create(EmployeeDTO.class);

            dto.email().setValue(email);
            dto.name().firstName().setValue(firstName);
            dto.name().lastName().setValue(lastName);
            dto.password().setValue(password);
            dto.passwordConfirm().setValue(password);
            dto.passwordConfirm().setValue(password);
            ServerSideFactory.create(EmployeeCrudService.class).create(new AsyncCallback<Key>() {
                @Override
                public void onSuccess(Key result) {
                    employee = Persistence.service().retrieve(Employee.class, result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new RuntimeException(caught);
                }
            }, dto);

            return this;
        }

        public EmployeeWrapper restrictToPortfolios(PortfolioWrapper... portfolios) {
            final EmployeeDTO[] dto = new EmployeeDTO[1];

            ServerSideFactory.create(EmployeeCrudService.class).retrieve(new AsyncCallback<EmployeeDTO>() {
                @Override
                public void onSuccess(EmployeeDTO result) {
                    dto[0] = result;
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new RuntimeException(caught);
                }

            }, employee.id().getValue(), RetrieveTarget.Edit);

            for (PortfolioWrapper p : portfolios) {
                dto[0].portfolios().add(p.portfolio);
            }

            ServerSideFactory.create(EmployeeCrudService.class).save(new AsyncCallback<Key>() {

                @Override
                public void onSuccess(Key result) {
                    employee = Persistence.service().retrieve(Employee.class, result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new RuntimeException(caught);
                }

            }, dto[0]);
            return this;

        }

    }

    protected final BuildingWrapper building(String propertyCode) {
        return new BuildingWrapper(propertyCode);
    }

    protected final PortfolioWrapper portfolio(String name) {
        return new PortfolioWrapper(name);
    }

    protected final EmployeeWrapper employee(String email) {
        return new EmployeeWrapper(email);
    }

}
