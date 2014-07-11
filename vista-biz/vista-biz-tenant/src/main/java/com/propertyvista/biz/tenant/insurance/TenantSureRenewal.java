/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;

class TenantSureRenewal {

    private static final Logger log = LoggerFactory.getLogger(TenantSureRenewal.class);

    public void processRenewal(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        LogicalDate renewalAniversary = new LogicalDate(DateUtils.addYears(runDate, -1));
        log.debug("run Renewal for {}", renewalAniversary);
        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        criteria.ge(criteria.proto().certificate().inceptionDate(), renewalAniversary);
        criteria.eq(criteria.proto().status(), TenantSureStatus.Active);
        criteria.notExists(criteria.proto().renewal());
        ICursorIterator<TenantSureInsurancePolicy> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                final TenantSureInsurancePolicy ts = iterator.next();
                String certificateNumber = ts.certificate().insuranceCertificateNumber().getValue();
                try {
                    new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() throws RuntimeException {
                            renew(ts);
                            return null;
                        }
                    });

                    executionMonitor.addProcessedEvent("Renewal", "Participant Id " + ts.client().tenant().participantId().getValue() + "; Policy ID = "
                            + ts.id().getValue() + "; Cert. Number = " + certificateNumber);
                } catch (Throwable e) {
                    log.error("failed to Renew TenatSure insurance certificate: (#{}) {}", certificateNumber, ts.client().tenant().participantId(), e);
                    executionMonitor.addErredEvent("Renewal", "Participant Id " + ts.client().tenant().participantId().getValue() + "Policy ID = "
                            + ts.id().getValue() + "; Cert. Number = " + certificateNumber, e);
                }
            }
        } finally {
            iterator.close();
        }
    }

    private void renew(TenantSureInsurancePolicy originalInsurancePolicy) {
        // TODO Auto-generated method stub
    }
}
