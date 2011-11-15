/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.PTGenerator;
import com.propertvista.generator.PreloadData;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;
import com.propertyvista.server.common.ptapp.ApplicationMgr;
import com.propertyvista.server.domain.ApplicationDocumentData;

public class PtPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PtPreloader.class);

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Charges.class, ChargeLineList.class, ChargeLine.class, TenantChargeList.class, TenantCharge.class, Application.class, Pet.class,
                    EmergencyContact.class, Summary.class, PriorAddress.class, ApplicationDocumentData.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        PTGenerator generator = new PTGenerator(config());

        EntityQueryCriteria<Building> bcriteria = EntityQueryCriteria.create(Building.class);
        bcriteria.add(PropertyCriterion.eq(bcriteria.proto().propertyCode(), PreloadData.REGISTRATION_DEFAULT_PROPERTY_CODE));
        Building building = Persistence.service().retrieve(bcriteria);

        EntityQueryCriteria<AptUnit> ucriteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        ucriteria.add(PropertyCriterion.eq(ucriteria.proto().belongsTo(), building));
        List<AptUnit> units = Persistence.service().query(ucriteria);

        int numCreated = 0;
        for (int i = 1; i <= config().numPotentialTenants; i++) {
            if (units.size() <= i) {
                log.warn("No more units available for PotentialTenants. Change configuration!");
                break;
            }

            String email = DemoData.UserType.PTENANT.getEmail(i);
            User user = UserPreloader.createUser(email, email, VistaBehavior.PROSPECTIVE_TENANT);
            ApplicationSummaryGDO summary = generator.createSummary(user, units.get(i - 1));

            // Update user name
            Persistence.service().persist(user);
            //TODO create users for CoApplicants
            persistFullApplication(summary, generator);
            numCreated++;
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " potential tenants");
        return b.toString();
    }

    private void updateLease(Lease lease) {
        Building building = lease.unit().belongsTo();

        Persistence.service().retrieve(building);
        Persistence.service().retrieve(building.serviceCatalog());

        // update service catalog double-reference lists:
        EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
        List<Service> services = Persistence.service().query(serviceCriteria);
        building.serviceCatalog().services().clear();
        building.serviceCatalog().services().addAll(services);

        Service selectedService = null;
        for (Service service : building.serviceCatalog().services()) {
            if (service.type().equals(lease.type())) {
                Persistence.service().retrieve(service.items());
                for (ServiceItem item : service.items()) {
                    if (lease.unit().equals(item.element())) {
                        lease.serviceAgreement().serviceItem().set(createChargeItem(item));
                        selectedService = service;
                        break;
                    }
                }
            }
        }

        if (!lease.serviceAgreement().serviceItem().isEmpty()) {
            // pre-populate utilities for the new service: 
            Persistence.service().retrieve(selectedService.features());
            for (ServiceFeature feature : selectedService.features()) {
                if (Feature.Type.utility.equals(feature.feature().type().getValue())) {
                    Persistence.service().retrieve(feature.feature().items());
                    for (ServiceItem item : feature.feature().items()) {
                        if (!building.serviceCatalog().includedUtilities().contains(item.type())
                                && !building.serviceCatalog().externalUtilities().contains(item.type())) {
                            lease.serviceAgreement().featureItems().add(createChargeItem(item));
                        }
                    }
                }

            }

            // pre-populate concessions for the new service: 
            Persistence.service().retrieve(selectedService.concessions());
            if (!selectedService.concessions().isEmpty()) {
                lease.serviceAgreement().concessions().add(RandomUtil.random(selectedService.concessions()));
            }
        }
    }

    private ChargeItem createChargeItem(ServiceItem serviceItem) {
        ChargeItem chargeItem = EntityFactory.create(ChargeItem.class);
        chargeItem.item().set(serviceItem);
        chargeItem.price().setValue(serviceItem.price().getValue());
        return chargeItem;
    }

    private void persistFullApplication(ApplicationSummaryGDO summary, PTGenerator generator) {

        updateLease(summary.lease());

        Persistence.service().persist(summary.lease());

        for (TenantSummaryGDO tenantSummary : summary.tenants()) {
            Persistence.service().persist(tenantSummary.tenant());

            tenantSummary.tenantInLease().lease().set(summary.lease());
            Persistence.service().persist(tenantSummary.tenantInLease());

            for (ApplicationDocument applicationDocument : tenantSummary.tenantScreening().documents()) {
                generator.attachDocumentData(applicationDocument);
            }
            for (PersonalIncome income : tenantSummary.tenantScreening().incomes()) {
                for (ApplicationDocument applicationDocument : income.documents()) {
                    generator.attachDocumentData(applicationDocument);
                }
            }

            Persistence.service().persist(tenantSummary.tenantScreening());

            summary.lease().tenants().add(tenantSummary.tenantInLease());
        }

        MasterApplication ma = ApplicationMgr.createMasterApplication(summary.lease());

        Persistence.service().persist(summary.lease());
        Persistence.service().persist(ma);

//TODO
//        log.debug("Charges: " + VistaDataPrinter.print(summary.charges()));
//        Persistence.service().persist(summary.charges());

    }

}
