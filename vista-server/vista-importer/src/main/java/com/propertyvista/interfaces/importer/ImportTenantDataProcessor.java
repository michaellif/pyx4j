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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.converter.TenantConverter;
import com.propertyvista.interfaces.importer.model.AutoPayAgreementIO;
import com.propertyvista.interfaces.importer.model.TenantIO;

public class ImportTenantDataProcessor {

    private LeaseTermTenant retrive(Lease lease, TenantIO tenantIO) {
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder().lease(), lease);
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());

        if (!tenantIO.participantId().isNull()) {
            criteria.eq(criteria.proto().leaseParticipant().participantId(), tenantIO.participantId());
        }
        return Persistence.service().retrieve(criteria);
    }

    public void importModel(Building buildingId, Lease lease, TenantIO tenantIO, ExecutionMonitor monitor) {
        LeaseTermTenant leaseTermTenant = retrive(lease, tenantIO);
        if (leaseTermTenant == null) {
            monitor.addErredEvent("Tenant", "Tenant " + tenantIO.participantId().getStringView() + " not found");
            return;
        }

        if (new TenantConverter().updateBO(tenantIO, leaseTermTenant)) {
            ServerSideFactory.create(CustomerFacade.class).persistCustomer(leaseTermTenant.leaseParticipant().customer());
        }

        if (!tenantIO.vistaPasswordHash().isNull() && !leaseTermTenant.leaseParticipant().customer().user().isNull()) {
            ServerSideFactory.create(CustomerFacade.class).setCustomerPasswordHash(leaseTermTenant.leaseParticipant().customer(),
                    tenantIO.vistaPasswordHash().getValue());
        }

        for (AutoPayAgreementIO autoPayIO : tenantIO.autoPayAgreements()) {
            new ImportAutoPayAgreementsDataProcessor().importModel(buildingId, lease, leaseTermTenant, autoPayIO, monitor);
        }

    }
}
