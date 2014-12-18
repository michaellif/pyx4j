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
 */
package com.propertyvista.interfaces.importer;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.converter.LeaseConverter;
import com.propertyvista.interfaces.importer.model.LeaseIO;
import com.propertyvista.shared.config.VistaFeatures;

public class ExportLeaseDataRetriever {

    private Lease retriveLease(AptUnit unit) {
        Lease lease = ServerSideFactory.create(OccupancyFacade.class).retriveCurrentLease(unit);

        if ((lease != null) && (lease.status().getValue() == Lease.Status.Completed) && (!lease.leaseTo().isNull())
                && VistaFeatures.instance().yardiIntegration()) {
            // filter out leases that are not coming in yardi interface
            if (lease.leaseTo().getValue().before(DateUtils.addMonths(SystemDateManager.getDate(), -2))) {
                return null;
            }
        }

        return lease;
    }

    public LeaseIO getModel(AptUnit unit) {
        return getModel(retriveLease(unit));
    }

    public LeaseIO getModel(Lease lease) {
        if (lease == null) {
            return null;
        }
        LeaseIO leaseIO = new LeaseConverter().createTO(lease);

        for (LeaseTermTenant leaseTermTenant : lease.currentTerm().version().tenants()) {
            leaseIO.tenants().add(new ExportTenantDataRetriever().getModel(leaseTermTenant));
        }

        return leaseIO;
    }
}
