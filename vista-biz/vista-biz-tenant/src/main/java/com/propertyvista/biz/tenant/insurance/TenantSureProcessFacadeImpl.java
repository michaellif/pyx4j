/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.report.ReportTableFormater;

import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.admin.domain.tenantsure.TenantSureHQUpdateFile;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;

public class TenantSureProcessFacadeImpl implements TenantSureProcessFacade {

    @Override
    public void processCancellations(RunStats runStats, LogicalDate dueDate) {
        EntityQueryCriteria<InsuranceTenantSure> criteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
        criteria.le(criteria.proto().expiryDate(), dueDate);
        criteria.eq(criteria.proto().status(), InsuranceTenantSure.TenantSureStatus.PendingCancellation);
        ICursorIterator<InsuranceTenantSure> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                InsuranceTenantSure ts = iterator.next();
            }
        } finally {
            iterator.completeRetrieval();
        }

    }

    @Override
    public void processPayments(RunStats runStats, LogicalDate dueDate) {
        TenantSurePayments.processPayments(runStats, dueDate);
    }

    @Override
    public void processReports(RunStats runStats, LogicalDate dueDate, ReportTableFormater formater) {
        TenantSureReports.processReports(runStats, dueDate, formater);
    }

    @Override
    public TenantSureHQUpdateFile reciveHQUpdatesFile() {
        return HQUpdate.reciveHQUpdatesFile();
    }

    @Override
    public void processHQUpdate(RunStats runStats, TenantSureHQUpdateFile fileId) {
        HQUpdate.processHQUpdate(runStats, fileId);
    }

}
