/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 13, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTUnit;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.offering.Service.ServiceType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiParameters;
import com.propertyvista.yardi.YardiServiceException;
import com.propertyvista.yardi.YardiTransactions;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.mapper.BuildingsMapper;
import com.propertyvista.yardi.mapper.UnitsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;
import com.propertyvista.yardi.merger.UnitsMerger;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiGetResidentTransactionsService {

    private final static Logger log = LoggerFactory.getLogger(YardiGetResidentTransactionsService.class);

    /**
     * Updates/creates entities basing on data from YARDI System.
     * 
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateAll(YardiParameters yp) throws YardiServiceException {
        validate(yp);

        YardiClient client = new YardiClient(yp.getServiceURL());

        log.info("Get properties information...");
        List<String> propertyCodes = getPropertyCodes(client, yp);

        log.info("Get all resident transactions...");
        List<ResidentTransactions> allTransactions = getAllResidentTransactions(client, yp, propertyCodes);

        updateBuildings(allTransactions);

        updateLeases(allTransactions);

        updateCharges(allTransactions);

    }

    private void updateBuildings(List<ResidentTransactions> allTransactions) {

        List<Building> importedBuildings = getBuildings(allTransactions);
        Map<String, List<AptUnit>> importedUnits = getUnits(allTransactions);

        log.info("Updating buildings...");
        merge(importedBuildings, getBuildings());

        log.info("Updating units...");
        updateUnitsForBuildings(importedUnits, getBuildings());
    }

    private void updateLeases(List<ResidentTransactions> allTransactions) {
        log.info("Updating leases...");
        for (ResidentTransactions transaction : allTransactions) {
            Property property = transaction.getProperty().get(0);
            boolean isNew = true;
            for (RTCustomer rtCustomer : property.getRTCustomer()) {
                {
                    EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
                    criteria.eq(criteria.proto().leaseId(), rtCustomer.getCustomerID());
                    if (!Persistence.service().query(criteria).isEmpty()) {
                        Lease lease = Persistence.service().query(criteria).get(0);
                        updateLease(rtCustomer, lease);
                    }
                }

                String propertyCode = getPropertyId(property.getPropertyID().get(0));
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.eq(criteria.proto().building().propertyCode(), propertyCode);
                criteria.eq(criteria.proto().info().number(), getUnitId(rtCustomer));
                AptUnit unit = Persistence.service().query(criteria).get(0);

                try {
                    createLease(rtCustomer, unit, propertyCode);
                } catch (Throwable t) {
                    log.info("ERROR - lease not created: ", t);
                }
            }
        }
        log.info("All leases updated.");
    }

    private void updateLease(RTCustomer rtCustomer, Lease lease) {
        // TODO if lease already exists - do something

    }

    private void createLease(RTCustomer rtCustomer, AptUnit unit, String propertyCode) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        Lease lease = leaseFacade.create(Lease.Status.ExistingLease);

        lease.type().setValue(ServiceType.residentialUnit);

        lease.currentTerm().termFrom().setValue(new LogicalDate(yardiLease.getLeaseFromDate().getTimeInMillis()));
        lease.currentTerm().termTo().setValue(new LogicalDate(yardiLease.getLeaseToDate().getTimeInMillis()));
        if (yardiLease.getExpectedMoveInDate() != null) {
            lease.expectedMoveIn().setValue(new LogicalDate(yardiLease.getExpectedMoveInDate().getTimeInMillis()));
        }
        if (yardiLease.getActualMoveIn() != null) {
            lease.actualMoveIn().setValue(new LogicalDate(yardiLease.getActualMoveIn().getTimeInMillis()));
        }
// add price

        for (YardiCustomer yardiCustomer : yardiCustomers) {
            Customer customer = EntityFactory.create(Customer.class);
            Person person = EntityFactory.create(Person.class);
            person.name().firstName().setValue(yardiCustomer.getName().getFirstName());
            person.name().lastName().setValue(yardiCustomer.getName().getLastName());
            person.name().middleName().setValue(yardiCustomer.getName().getMiddleName());
            customer.person().set(person);
            LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
            tenantInLease.leaseParticipant().customer().set(customer);
            if (rtCustomer.getCustomerID().equals(yardiCustomer.getCustomerID()) && yardiCustomer.getLease().isResponsibleForLease()) {
                tenantInLease.role().setValue(LeaseTermParticipant.Role.Applicant);
            } else {
                tenantInLease.role().setValue(
                        yardiCustomer.getLease().isResponsibleForLease() ? LeaseTermParticipant.Role.CoApplicant : LeaseTermParticipant.Role.Dependent);
            }
            lease.currentTerm().version().tenants().add(tenantInLease);
        }

        lease = leaseFacade.init(lease);
        if (unit.getPrimaryKey() != null) {
            leaseFacade.setUnit(lease, unit);
            // TODO double into BigDecimal...might need to be handled differently
            leaseFacade.setLeaseAgreedPrice(lease, yardiLease.getCurrentRent());
        }
        lease.leaseId().setValue(rtCustomer.getCustomerID());
        lease = leaseFacade.persist(lease);
        leaseFacade.activate(lease);
        log.info("Lease {} in building {} successfully updated", rtCustomer.getCustomerID(), propertyCode);
    }

    private void updateCharges(List<ResidentTransactions> allTransactions) {
        YardiChargeProcessor.updateCharges(allTransactions);
    }

    private void merge(List<Building> imported, List<Building> existing) {
        List<Building> merged = new BuildingsMerger().merge(imported, existing);
        for (Building building : merged) {
            update(building);
        }
    }

    private void updateUnitsForBuildings(Map<String, List<AptUnit>> importedUnits, List<Building> buildings) {
        for (Building building : buildings) {
            String propertyCode = building.propertyCode().getValue();
            if (importedUnits.containsKey(propertyCode)) {
                mergeUnits(building, importedUnits.get(propertyCode), getUnits(propertyCode));
            }
        }
    }

    private void mergeUnits(Building building, List<AptUnit> imported, List<AptUnit> existing) {
        Persistence.service().retrieve(building.floorplans());

        List<AptUnit> merged = new UnitsMerger().merge(building, imported, existing);
        for (AptUnit unit : merged) {
            update(unit);
        }
    }

    private void update(Building building) {
        try {
            boolean isNewBuilding = building.updated().isNull();

            Persistence.service().persist(building);

            if (isNewBuilding) {
                ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
                ServerSideFactory.create(DefaultProductCatalogFacade.class).persistFor(building);
            } else {
                ServerSideFactory.create(DefaultProductCatalogFacade.class).updateFor(building);
            }

            log.info("Building with property code {} successfully updated", building.propertyCode().getValue());
        } catch (Exception e) {
            log.error(String.format("Errors during updating building %s", building.propertyCode().getValue()), e);
        }
    }

    private void update(AptUnit unit) {
        try {
            boolean isNewUnit = unit.updated().isNull();

            Persistence.service().retrieve(unit.building());
            Persistence.service().persist(unit);

            if (isNewUnit) {
                ServerSideFactory.create(OccupancyFacade.class).setupNewUnit((AptUnit) unit.createIdentityStub());
                ServerSideFactory.create(DefaultProductCatalogFacade.class).addUnit(unit.building(), unit, true);
            } else {
                ServerSideFactory.create(DefaultProductCatalogFacade.class).updateUnit(unit.building(), unit);
            }

            log.info("Unit {} for building {} successfully updated", unit.info().number().getValue(), unit.building().propertyCode().getValue());
        } catch (Exception e) {
            log.error(
                    String.format("Errors during updating unit %s for building %s", unit.info().number().getValue(), unit.building().propertyCode().getValue()),
                    e);
        }
    }

    private List<Building> getBuildings() {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.asc(criteria.proto().propertyCode());
        return Persistence.service().query(criteria);
    }

    public List<Building> getBuildings(List<ResidentTransactions> allTransactions) {
        BuildingsMapper mapper = new BuildingsMapper();
        return mapper.map(getProperties(allTransactions));
    }

    public Map<String, List<AptUnit>> getUnits(List<ResidentTransactions> allTransactions) {
        UnitsMapper mapper = new UnitsMapper();
        Map<String, List<AptUnit>> units = new HashMap<String, List<AptUnit>>();
        Map<String, List<RTUnit>> imported = getYardiUnits(allTransactions);
        for (Map.Entry<String, List<RTUnit>> entry : imported.entrySet()) {
            List<AptUnit> mapped = mapper.map(entry.getValue());
            if (!mapped.isEmpty()) {
                units.put(entry.getKey(), mapped);
            }
        }
        return units;
    }

    private List<Property> getProperties(List<ResidentTransactions> allTransactions) {
        Map<String, Property> properties = new HashMap<String, Property>();
        for (ResidentTransactions transaction : allTransactions) {

            for (Property property : transaction.getProperty()) {

                for (PropertyID propertyID : property.getPropertyID()) {
                    String propertyId = getPropertyId(propertyID);
                    if (StringUtils.isNotEmpty(propertyId) && !properties.containsKey(propertyId)) {
                        properties.put(propertyId, property);
                    }
                }
            }
        }

        return new ArrayList<Property>(properties.values());
    }

    private Map<String, List<RTUnit>> getYardiUnits(List<ResidentTransactions> allTransactions) {
        Map<String, List<RTUnit>> unitsMap = new HashMap<String, List<RTUnit>>();
        for (ResidentTransactions transaction : allTransactions) {

            for (Property property : transaction.getProperty()) {

                for (PropertyID propertyID : property.getPropertyID()) {

                    String propertyId = getPropertyId(propertyID);
                    if (unitsMap.containsKey(propertyId)) {
                        continue;
                    }

                    Map<String, RTUnit> map = new HashMap<String, RTUnit>();
                    for (RTCustomer customer : property.getRTCustomer()) {
                        String unitId = getUnitId(customer);
                        if (StringUtils.isNotEmpty(unitId) && !map.containsKey(unitId)) {
                            map.put(unitId, customer.getRTUnit());
                        }
                    }

                    unitsMap.put(propertyId, new ArrayList<RTUnit>(map.values()));
                }
            }
        }

        return unitsMap;
    }

    private String getPropertyId(PropertyID propertyID) {
        return propertyID.getIdentification() != null ? getPropertyId(propertyID.getIdentification()) : null;
    }

    private String getPropertyId(Identification identification) {
        return identification != null ? (identification.getPrimaryID()) : null;
    }

    private String getUnitId(RTCustomer customer) {
        return customer.getRTUnit() != null ? customer.getRTUnit().getUnitID() : null;
    }

    private void validate(YardiParameters yp) {
        Validate.notEmpty(yp.getServiceURL(), "ServiceURL parameter can not be empty or null");
        Validate.notEmpty(yp.getUsername(), "Username parameter can not be empty or null");
        Validate.notEmpty(yp.getPassword(), "Password parameter can not be empty or null");
        Validate.notEmpty(yp.getServerName(), "ServerName parameter can not be empty or null");
        Validate.notEmpty(yp.getDatabase(), "Database parameter can not be empty or null");
        Validate.notEmpty(yp.getPlatform(), "Platform parameter can not be empty or null");
        Validate.notEmpty(yp.getInterfaceEntity(), "InterfaceEntity parameter can not be empty or null");
    }

    private List<String> getPropertyCodes(YardiClient client, YardiParameters yp) throws YardiServiceException {
        List<String> propertyCodes = new ArrayList<String>();
        try {
            Properties properties = YardiTransactions.getPropertyConfigurations(client, yp);
            for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
                if (StringUtils.isNotEmpty(property.getCode())) {
                    propertyCodes.add(property.getCode());
                }
            }
            return propertyCodes;
        } catch (Exception e) {
            throw new YardiServiceException("Fail to get properties information from YARDI System", e);
        }
    }

    private List<ResidentTransactions> getAllResidentTransactions(YardiClient client, YardiParameters yp, List<String> propertyCodes) {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            try {
                ResidentTransactions residentTransactions = YardiTransactions.getResidentTransactions(client, yp, propertyCode);
                transactions.add(residentTransactions);
            } catch (Exception e) {
                log.error(String.format("Errors during call getResidentTransactions operation for building %s", propertyCode), e);
            }
        }

        return transactions;
    }

    private List<AptUnit> getUnits(String propertyCode) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building().propertyCode(), propertyCode));
        List<AptUnit> units = Persistence.service().query(criteria);
        return units;
    }

}
