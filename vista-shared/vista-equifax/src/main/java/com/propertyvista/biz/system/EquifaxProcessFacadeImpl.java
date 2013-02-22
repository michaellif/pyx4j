/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.scheduler.StatisticsRecord;
import com.propertyvista.server.domain.CustomerCreditCheckReport;
import com.propertyvista.server.domain.CustomerCreditCheckReportNoBackup;

public class EquifaxProcessFacadeImpl implements EquifaxProcessFacade {

    @Override
    public void dataRetention(StatisticsRecord dynamicStatisticsRecord) {
        NamespaceManager.setNamespace(VistaNamespace.expiringNamespace);
        try {
            long maxProgress1 = Persistence.service().count(EntityQueryCriteria.create(CustomerCreditCheckReport.class));
            long maxProgress2 = Persistence.service().count(EntityQueryCriteria.create(CustomerCreditCheckReportNoBackup.class));
            dynamicStatisticsRecord.total().setValue(maxProgress1 + maxProgress2);

            long removed = 0;
            long moved = 0;

            StringBuilder message = new StringBuilder();

            {
                Date onlineStorageDeadLine = DateUtils.addDays(Persistence.service().getTransactionSystemTime(), -52);
                EntityQueryCriteria<CustomerCreditCheckReport> criteria = EntityQueryCriteria.create(CustomerCreditCheckReport.class);
                criteria.le(criteria.proto().created(), onlineStorageDeadLine);
                ICursorIterator<CustomerCreditCheckReport> cur = Persistence.service().query(null, criteria, AttachLevel.Attached);
                try {
                    while (cur.hasNext()) {
                        CustomerCreditCheckReport report = cur.next();
                        CustomerCreditCheckReportNoBackup report2 = report.duplicate(CustomerCreditCheckReportNoBackup.class);
                        Persistence.service().persist(report2);
                        Persistence.service().delete(report);
                        moved++;
                    }
                } finally {
                    cur.completeRetrieval();
                }
            }

            {
                Date storageDeadLine = DateUtils.addDays(Persistence.service().getTransactionSystemTime(), -60);
                EntityQueryCriteria<CustomerCreditCheckReportNoBackup> criteria = EntityQueryCriteria.create(CustomerCreditCheckReportNoBackup.class);
                criteria.le(criteria.proto().created(), storageDeadLine);
                removed += Persistence.service().delete(criteria);
            }

            dynamicStatisticsRecord.processed().setValue(removed + moved);

            if (moved != 0) {
                message.append("Moved:").append(moved);
            }
            if (removed != 0) {
                if (message.length() > 0) {
                    message.append(", ");
                }
                message.append("Removed:").append(removed);
            }

            dynamicStatisticsRecord.message().setValue(message.toString());
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        }
    }
}
