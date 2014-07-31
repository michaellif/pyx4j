/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.converter.TenantConverter;
import com.propertyvista.interfaces.importer.model.AutoPayAgreementIO;
import com.propertyvista.interfaces.importer.model.InsuranceCertificateIO;
import com.propertyvista.interfaces.importer.model.TenantIO;

public class ImportTenantDataProcessor {

    private final static Logger log = LoggerFactory.getLogger(ImportTenantDataProcessor.class);

    private LeaseTermTenant retriveByIdOrName(Lease lease, TenantIO tenantIO) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder().lease(), lease);
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());

        if (!tenantIO.participantId().isNull()) {
            criteria.eq(criteria.proto().leaseParticipant().participantId(), tenantIO.participantId());
        } else {
            criteria.eq(criteria.proto().leaseParticipant().customer().person().name().firstName(), tenantIO.firstName());
            criteria.eq(criteria.proto().leaseParticipant().customer().person().name().lastName(), tenantIO.lastName());
        }
        return Persistence.service().retrieve(criteria);
    }

    private LeaseTermTenant retriveByName(Lease lease, TenantIO tenantIO) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder().lease(), lease);
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());

        criteria.eq(criteria.proto().leaseParticipant().customer().person().name().firstName(), tenantIO.firstName());
        criteria.eq(criteria.proto().leaseParticipant().customer().person().name().lastName(), tenantIO.lastName());

        return Persistence.service().retrieve(criteria);
    }

    private LeaseTermTenant retriveByUnitAndName(Building building, String unitNumber, TenantIO tenantIO) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder().lease().unit().building(), building);
        criteria.eq(criteria.proto().leaseTermV().holder().lease().unit().info().number(), unitNumber);
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());

        criteria.eq(criteria.proto().leaseParticipant().customer().person().name().firstName(), tenantIO.firstName());
        criteria.eq(criteria.proto().leaseParticipant().customer().person().name().lastName(), tenantIO.lastName());

        return Persistence.service().retrieve(criteria);
    }

    public void importModel(ImportProcessorContext context, Lease lease, TenantIO tenantIO) {
        LeaseTermTenant leaseTermTenant;
        if (context.ignoreEntityId) {
            leaseTermTenant = retriveByName(lease, tenantIO);
        } else {
            leaseTermTenant = retriveByIdOrName(lease, tenantIO);
        }

        if (leaseTermTenant == null) {
            if (context.ignoreEntityId) {
                context.monitor.addErredEvent("Tenant", "Tenant " + tenantIO.firstName().getStringView() + " " + tenantIO.lastName().getStringView()
                        + " not found in lease " + lease.leaseId().getStringView());
            } else {
                context.monitor.addErredEvent("Tenant", "Tenant " + tenantIO.participantId().getStringView() + " not found in lease "
                        + lease.leaseId().getStringView());
            }
            return;
        }

        if (context.renamedBuilding != null) {
            LeaseTermTenant oldLeaseTermTenant = retriveByUnitAndName(context.renamedBuilding, lease.unit().info().number().getValue(), tenantIO);
            if (oldLeaseTermTenant != null) {
                // change customer and remove unused
                Customer currentCutomer = leaseTermTenant.leaseParticipant().customer();
                if (!currentCutomer.equals(oldLeaseTermTenant.leaseParticipant().customer())) {
                    Customer deprecatedCutomer = currentCutomer.duplicate();
                    leaseTermTenant.leaseParticipant().customer().set(oldLeaseTermTenant.leaseParticipant().customer());
                    Persistence.service().persist(leaseTermTenant.leaseParticipant());
                    Persistence.service().delete(deprecatedCutomer);
                    log.debug("customer merged {}", leaseTermTenant.leaseParticipant().customer());
                }
            }
        }

        if (new TenantConverter().updateBO(tenantIO, leaseTermTenant)) {
            ServerSideFactory.create(CustomerFacade.class).persistCustomer(leaseTermTenant.leaseParticipant().customer());
        }

        if (!tenantIO.vistaPasswordHash().isNull() && !leaseTermTenant.leaseParticipant().customer().user().isNull()) {
            ServerSideFactory.create(CustomerFacade.class).setCustomerPasswordHash(leaseTermTenant.leaseParticipant().customer(),
                    tenantIO.vistaPasswordHash().getValue());
        }

        for (AutoPayAgreementIO autoPayIO : tenantIO.autoPayAgreements()) {
            new ImportAutoPayAgreementsDataProcessor().importModel(context, lease, leaseTermTenant, autoPayIO);
        }

        for (InsuranceCertificateIO certificateIO : tenantIO.insurance()) {
            new ImportInsuranceCertificateDataProcessor().importModel(context, lease, leaseTermTenant.leaseParticipant(), certificateIO);
        }

        // Notify tenant that account number had Changed
        if (tenantIO.hadDirectDebitPayments().getValue(false)) {
            ServerSideFactory.create(NotificationFacade.class).directDebitAccountChanged(leaseTermTenant);
        }
    }
}
