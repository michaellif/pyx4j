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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.server.common.util.N4DataConverter;

public class N4ManagementFacadeImpl implements N4ManagementFacade {

    private static final Logger log = LoggerFactory.getLogger(N4ManagementFacadeImpl.class);

    private static final I18n i18n = I18n.get(N4ManagementFacadeImpl.class);

    @Override
    public void issueN4(final N4Batch batch, ExecutionMonitor monitor) throws IllegalStateException, FormFillError {
        Persistence.ensureRetrieve(batch.items(), AttachLevel.Attached);

        final Date batchServiceDate = SystemDateManager.getDate();

        final N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(batch.building(), N4Policy.class);
        final LogicalDate deliveryDate = new N4Manager().calculateDeliveryDate(batchServiceDate, batch.deliveryMethod().getValue(), policy);

        batch.deliveryDate().setValue(deliveryDate);
        batch.serviceDate().setValue(new LogicalDate(batchServiceDate));

        monitor.setExpectedTotal(N4_REPORT_SECTION, batch.items().size());
        for (final N4BatchItem item : batch.items()) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.Web).execute(new Executable<Void, Exception>() {
                    @Override
                    public Void execute() throws Exception {
                        new N4Manager().issueN4ForBatchItem(item, policy, deliveryDate, batchServiceDate);
                        return null;
                    }
                });
            } catch (Exception e) {
                item.lease().setAttachLevel(AttachLevel.Attached);
                String msg = "Failed to generate n4 for lease pk='" + item.lease().getPrimaryKey() + "'";
                log.error(msg, e);
                monitor.addErredEvent(N4_REPORT_SECTION, msg + ": " + errorMessage(e));
            }
            monitor.addProcessedEvent(N4_REPORT_SECTION);
        }
        Persistence.service().persist(batch);
    }

    @Override
    public void issueN4(EvictionCase evictionCase, ExecutionMonitor monitor) throws IllegalStateException, FormFillError {
        Persistence.ensureRetrieve(evictionCase, AttachLevel.Attached);

        // validate preconditions
        EvictionStatusN4 statusN4 = null;
        if (evictionCase.closedOn().isNull()) {
            for (EvictionCaseStatus status : evictionCase.history()) {
                if (EvictionStepType.N4.equals(status.evictionStep().stepType().getValue())) {
                    statusN4 = (EvictionStatusN4) status;
                    Persistence.ensureRetrieve(statusN4.leaseArrears(), AttachLevel.Attached);
                    break;
                }
            }
        }

        try {
            if (statusN4 == null) {
                throw new Error("N4 Status not found for Eviction Case pk='" + evictionCase.getPrimaryKey() + "'");
            } else if (statusN4.leaseArrears().isEmpty()) {
                throw new Error("Lease Arrears not found for Eviction Case pk='" + evictionCase.getPrimaryKey() + "'");
            }

            final Date serviceDate = SystemDateManager.getDate();

            ensureN4LeaseData(statusN4);

            Persistence.ensureRetrieve(evictionCase.lease().unit().building(), AttachLevel.Attached);
            final N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(evictionCase.lease().unit().building(), N4Policy.class);
            final LogicalDate deliveryDate = new N4Manager().calculateDeliveryDate(serviceDate, statusN4.n4Data().deliveryMethod().getValue(), policy);

            statusN4.n4Data().deliveryDate().setValue(deliveryDate);
            statusN4.n4Data().serviceDate().setValue(new LogicalDate(serviceDate));

            final EvictionStatusN4 statusN4final = statusN4;
            monitor.setExpectedTotal(N4_REPORT_SECTION, 1);
            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.Web).execute(new Executable<Void, Exception>() {
                @Override
                public Void execute() throws Exception {
                    new N4Manager().issueN4ForLease(statusN4final.n4Data(), statusN4final.leaseArrears(), policy, deliveryDate, serviceDate);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("Failed to generate N4 for Eviction Case pk='" + evictionCase.getPrimaryKey() + "'", e);
            monitor.addErredEvent(N4_REPORT_SECTION, errorMessage(e));
        }
        monitor.addProcessedEvent(N4_REPORT_SECTION);
    }

    @Override
    public void autoCancelN4(ExecutionMonitor monitor) {
        // find open cases in n4 status and check if the balance is down the threshold or the time has expired
        EntityQueryCriteria<EvictionCaseStatus> crit = EntityQueryCriteria.create(EvictionCaseStatus.class);
        crit.isNull(crit.proto().evictionCase().closedOn());
        crit.asc(crit.proto().evictionCase()); // order by case
        crit.desc(crit.proto().addedOn()); // latest status first
        ICursorIterator<EvictionCaseStatus> result = Persistence.service().query(null, crit, AttachLevel.Attached);
        EvictionCase parentCase = null;
        while (result.hasNext()) {
            EvictionCaseStatus status = result.next();
            if (!status.evictionCase().equals(parentCase) && isN4(status)) {
                parentCase = status.evictionCase();
                // running through an open case in n4 status
                String reason = shouldAutoCancel((EvictionStatusN4) status);
                if (reason != null) {
                    ServerSideFactory.create(EvictionCaseFacade.class).closeEvictionCase(status.evictionCase(),
                            i18n.tr("Closed by Auto Cancellation process: {0}", reason));
                }
            }
        }
        result.close();

    }

    private String shouldAutoCancel(EvictionStatusN4 n4status) {
        if (SystemDateManager.getDate().after(n4status.expiryDate().getValue())) {
            return i18n.tr("status has expired"); // has expired
        } else if (N4GenerationUtils.getN4Balance(n4status.evictionCase().lease()).compareTo(n4status.cancellationBalance().getValue()) < 0) {
            return i18n.tr("balance has been paid"); // has been paid off
        }
        return null;
    }

    private boolean isN4(EvictionCaseStatus status) {
        return EvictionStepType.N4.equals(status.evictionStep().stepType().getValue());
    }

    private void ensureN4LeaseData(EvictionStatusN4 statusN4) {
        if (statusN4.n4Data().isNull()) {
            N4LeaseData n4data = EntityFactory.create(N4LeaseData.class);
            if (!statusN4.originatingBatch().isNull()) {
                N4DataConverter.copyN4BatchToLeaseData(statusN4.originatingBatch(), n4data);
            }
            statusN4.n4Data().set(n4data);
            Persistence.service().persist(n4data);
            Persistence.service().persist(statusN4);
        } else {
            Persistence.ensureRetrieve(statusN4.n4Data(), AttachLevel.Attached);
        }
    }

    private String errorMessage(Exception e) {
        if (e instanceof UserRuntimeException || e instanceof FormFillError) {
            return e.getMessage();
        } else {
            return i18n.tr("Unexpected Error: please contact support for more details");
        }
    }
}
