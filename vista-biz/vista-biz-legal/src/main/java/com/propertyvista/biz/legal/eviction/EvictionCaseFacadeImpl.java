/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.biz.legal.eviction;

import java.util.List;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.tenant.lease.Lease;

public class EvictionCaseFacadeImpl implements EvictionCaseFacade {

    private static final I18n i18n = I18n.get(EvictionCaseFacadeImpl.class);

    @Override
    public EvictionCase openEvictionCase(Lease leaseId, String note) {
        if (getCurrentEvictionCase(leaseId) != null) {
            throw new UserRuntimeException(i18n.tr("Existing open EvictionCase found."));
        }

        EvictionCase evictionCase = EntityFactory.create(EvictionCase.class);
        evictionCase.lease().set(leaseId);
        evictionCase.note().setValue(note);
        Persistence.service().persist(evictionCase);
        return evictionCase;
    }

    @Override
    public void closeEvictionCase(EvictionCase caseId, String note) {
        EvictionCase evictionCase = Persistence.service().retrieve(EvictionCase.class, caseId.getPrimaryKey());
        if (evictionCase.closedOn().isNull()) {
            evictionCase.note().setValue(evictionCase.note().getStringView() + "\n\n" + note);
            evictionCase.closedOn().setValue(SystemDateManager.getDate());
            Persistence.service().persist(evictionCase);
        }
    }

    @Override
    public EvictionStatus getCurrentEvictionStatus(EvictionCase evictionCase) {
        if (evictionCase.history().isEmpty() || !evictionCase.closedOn().isNull()) {
            return null;
        } else {
            return evictionCase.history().get(evictionCase.history().size() - 1);
        }
    }

    @Override
    public EvictionCase getCurrentEvictionCase(Lease leaseId) {
        EntityQueryCriteria<EvictionCase> crit = EntityQueryCriteria.create(EvictionCase.class);
        crit.eq(crit.proto().lease(), leaseId);
        crit.isNull(crit.proto().closedOn());
        return Persistence.service().retrieve(crit);
    }

    @Override
    public List<EvictionCase> getEvictionHistory(Lease leaseId) {
        EntityQueryCriteria<EvictionCase> crit = EntityQueryCriteria.create(EvictionCase.class);
        crit.eq(crit.proto().lease(), leaseId);
        return Persistence.service().query(crit);
    }

    @Override
    public void addEvictionStatusDetails(EvictionCase evictionCase, String statusName, String note, List<EvictionDocument> attachments) {
        // - check that the case is not closed
        if (!evictionCase.closedOn().isNull()) {
            return;
        }
        Persistence.ensureRetrieve(evictionCase.evictionFlowPolicy(), AttachLevel.Attached);
        EvictionFlowStep flowStep = null;
        for (EvictionFlowStep step : evictionCase.evictionFlowPolicy().evictionFlow()) {
            if (step.name().getValue().equals(statusName)) {
                flowStep = step;
                break;
            }
        }
        if (flowStep == null) {
            throw new UserRuntimeException(i18n.tr("No corresponding flow step found in the Eviction Flow Policy: " + statusName));
        }
        // - find the given evictionStep in the case history
        EvictionStatus evictionStatus = null;
        for (EvictionStatus status : evictionCase.history()) {
            if (status.evictionStep().name().equals(statusName)) {
                evictionStatus = status;
                break;
            }
        }
        // - if not found, add the new status
        AbstractUser user = VistaContext.getCurrentUser();
        if (evictionStatus == null) {
            evictionStatus = EntityFactory.create(EvictionStatus.class);
            evictionStatus.evictionStep().set(flowStep);
            evictionStatus.addedBy().set(user);
            evictionStatus.note().setValue(i18n.tr("Auto-generated"));
            evictionCase.history().add(evictionStatus);
        }
        // - add new details to the case status
        EvictionStatusRecord record = EntityFactory.create(EvictionStatusRecord.class);
        record.addedBy().set(user);
        record.note().setValue(note);
        if (attachments != null) {
            record.attachments().addAll(attachments);
        }
        evictionStatus.statusRecords().add(record);
        Persistence.service().persist(evictionCase);
    }
}
