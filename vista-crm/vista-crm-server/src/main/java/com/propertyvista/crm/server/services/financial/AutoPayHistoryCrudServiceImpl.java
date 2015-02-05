/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2013
 * @author VladL
 */
package com.propertyvista.crm.server.services.financial;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.crm.server.services.AbstractCrmPrimeCrudServiceImpl;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.TaskRunner;

public class AutoPayHistoryCrudServiceImpl extends AbstractCrmPrimeCrudServiceImpl<AutopayAgreement, AutoPayHistoryDTO> implements AutoPayHistoryCrudService {

    public AutoPayHistoryCrudServiceImpl() {
        super(AutopayAgreement.class, AutoPayHistoryDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(final AutopayAgreement bo, AutoPayHistoryDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.tenant().lease(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.createdBy(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.reviewOfPap(), AttachLevel.Attached);

        final Pmc pmc = VistaDeployment.getCurrentPmc();

        AuditRecord lastAuditRecord = TaskRunner.runInOperationsNamespace(new Callable<AuditRecord>() {
            @Override
            public AuditRecord call() {
                EntityQueryCriteria<AuditRecord> criteria = EntityQueryCriteria.create(AuditRecord.class);
                criteria.eq(criteria.proto().event(), AuditRecordEventType.Update);
                criteria.eq(criteria.proto().entityId(), bo.getPrimaryKey());
                criteria.eq(criteria.proto().entityClass(), bo.getEntityMeta().getEntityClass().getSimpleName());
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.desc(criteria.proto().id());
                return Persistence.service().retrieve(criteria);
            }
        });

        if (lastAuditRecord != null) {
            String userText = "system";
            if (!lastAuditRecord.user().isNull()) {
                AbstractUser user = Persistence.service().retrieve(VistaContext.getVistaUserClass(lastAuditRecord.userType().getValue()),
                        lastAuditRecord.user().getValue());
                if (user != null) {
                    userText = lastAuditRecord.userType().getValue() + " " + user.getStringView();
                }
            }
            to.auditDetails().setValue(
                    SimpleMessageFormat.format("Last change {0} by {1} on {2}", lastAuditRecord.details(), userText, lastAuditRecord.created()));
        }

    }

    @Override
    protected void enhanceListRetrieved(AutopayAgreement bo, AutoPayHistoryDTO dto) {
        super.enhanceListRetrieved(bo, dto);

        Persistence.ensureRetrieve(dto.tenant().lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.createdBy(), AttachLevel.ToStringMembers);

        dto.price().setValue(BigDecimal.ZERO);
        dto.payment().setValue(BigDecimal.ZERO);
        for (AutopayAgreementCoveredItem item : dto.coveredItems()) {
            dto.price().setValue(dto.payment().getValue().add(ServerSideFactory.create(BillingFacade.class).getActualPrice(item.billableItem())));
            dto.payment().setValue(dto.payment().getValue().add(item.amount().getValue()));
        }
    }
}
