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
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.interfaces.importer.model.LeaseIO;
import com.propertyvista.interfaces.importer.model.TenantIO;

public class ImportLeaseDataProcessor {

    private Lease retrive(AptUnit unit, LeaseIO leaseIO) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().unit(), unit);
        criteria.in(criteria.proto().status(), Lease.Status.present());
        if (!leaseIO.leaseId().isNull()) {
            criteria.eq(criteria.proto().leaseId(), leaseIO.leaseId());
        }
        return Persistence.service().retrieve(criteria);
    }

    public void importModel(Building buildingId, AptUnit unit, LeaseIO leaseIO, ExecutionMonitor monitor) {
        Lease lease = retrive(unit, leaseIO);
        if (lease == null) {
            monitor.addErredEvent("Lease", "Lease " + leaseIO.leaseId().getStringView() + " not found");
            return;
        }

        // If Importing AutoPay, suspend existing. TODO make it configurable
        ServerSideFactory.create(PaymentMethodFacade.class).terminateAutopayAgreements(lease);

        // For now process only tenants
        for (TenantIO tenantIO : leaseIO.tenants()) {
            new ImportTenantDataProcessor().importModel(buildingId, lease, tenantIO, monitor);
        }

        monitor.addProcessedEvent("Lease", "Lease " + leaseIO.leaseId().getStringView() + " imported");
    }
}
