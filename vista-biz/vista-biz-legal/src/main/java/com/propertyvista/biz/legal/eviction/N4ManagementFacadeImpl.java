/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2015
 * @author stanp
 */
package com.propertyvista.biz.legal.eviction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4ManagementFacadeImpl implements N4ManagementFacade {

    private static final Logger log = LoggerFactory.getLogger(N4ManagementFacadeImpl.class);

    private static final I18n i18n = I18n.get(N4ManagementFacadeImpl.class);

    @Override
    public List<LegalNoticeCandidate> getN4Candidates(BigDecimal minAmountOwed, List<Building> buildingIds, ExecutionMonitor progressMonitor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void issueN4(final N4Batch batch, ExecutionMonitor monitor) throws IllegalStateException, FormFillError {
        Persistence.ensureRetrieve(batch, AttachLevel.Attached);

        final Date batchServiceDate = SystemDateManager.getDate();

        batch.deliveryMethod().setValue(N4DeliveryMethod.Mail); // TODO - should be selected in UI
        batch.noticeIssueDate().setValue(new LogicalDate(batch.created().getValue())); // TODO - either created() or batchGenerationDate

        final N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(batch.building(), N4Policy.class);
        final LogicalDate deliveryDate = new N4Manager().calculateDeliveryDate(batch.noticeIssueDate().getValue(), batch.deliveryMethod().getValue(), policy);

        batch.deliveryDate().setValue(deliveryDate);
        batch.serviceDate().setValue(new LogicalDate(batchServiceDate));

        monitor.setExpectedTotal(N4_REPORT_SECTION, batch.items().size());
        for (final N4BatchItem item : batch.items()) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.Web).execute(new Executable<Void, Exception>() {
                    @Override
                    public Void execute() throws Exception {
                        new N4Manager().issueN4ForLease(item, batch, policy, deliveryDate, batchServiceDate);
                        return null;
                    }
                });
            } catch (Exception e) {
                log.error("Failed to generate n4 for lease pk='" + item.lease().getPrimaryKey() + "'", e);
                monitor.addErredEvent(N4_REPORT_SECTION, item.lease().getStringView() + ": " + errorMessage(e));
            }
            monitor.addProcessedEvent(N4_REPORT_SECTION);
        }
        Persistence.service().persist(batch);
    }

    @Override
    public Map<Lease, List<N4LegalLetter>> getN4(List<Lease> leaseIds, LogicalDate generatedCutOffDate) {
        // TODO Auto-generated method stub
        return null;
    }

    private String errorMessage(Exception e) {
        if (e instanceof UserRuntimeException || e instanceof FormFillError) {
            return e.getMessage();
        } else {
            return i18n.tr("Unexpected Error: please contact support for more details");
        }
    }

}
