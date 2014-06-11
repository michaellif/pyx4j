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

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.interfaces.importer.model.LeaseIO;
import com.propertyvista.interfaces.importer.model.TenantIO;

public class ImportLeaseDataProcessor {

    private final static Logger log = LoggerFactory.getLogger(ImportLeaseDataProcessor.class);

    public ImportLeaseDataProcessor() {
    }

    private Lease retrive(AptUnit unit, LeaseIO leaseIO) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().unit(), unit);
        criteria.in(criteria.proto().status(), Lease.Status.present());
        if (!leaseIO.leaseId().isNull()) {
            criteria.eq(criteria.proto().leaseId(), leaseIO.leaseId());
        }
        return Persistence.service().retrieve(criteria);
    }

    private Lease retriveLeaseByUnit(AptUnit unit) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().unit(), unit);
        criteria.in(criteria.proto().status(), Lease.Status.present());
        return Persistence.service().retrieve(criteria);
    }

    public void importModel(ImportProcessorContext context, AptUnit unit, LeaseIO leaseIO) {
        Lease lease;
        if (context.ignoreEntityId) {
            lease = retriveLeaseByUnit(unit);
            if (lease == null) {
                context.monitor.addErredEvent("Lease", "Lease for unit " + unit.info().number().getStringView() + " not found");
                return;
            }
        } else {
            lease = retrive(unit, leaseIO);
            if (lease == null) {
                context.monitor.addErredEvent("Lease", "Lease " + leaseIO.leaseId().getStringView() + " not found for unit "
                        + unit.info().number().getStringView());
                return;
            }
        }

        log.debug("importing lease {} {}", lease.id(), lease);

        // If Importing AutoPay, suspend existing. TODO make it configurable
        ServerSideFactory.create(PaymentMethodFacade.class).deleteAutopayAgreements(lease, false);

        // For now process only tenants
        for (TenantIO tenantIO : leaseIO.tenants()) {
            new ImportTenantDataProcessor().importModel(context, lease, tenantIO);
        }

        context.monitor.addProcessedEvent("Lease", "Lease " + lease.leaseId().getStringView() + " imported");
    }
}
