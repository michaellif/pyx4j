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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class EvictionCaseFacadeImpl implements EvictionCaseFacade {

    private static final I18n i18n = I18n.get(EvictionCaseFacadeImpl.class);

    private final Map<Building, EvictionFlowPolicy> policyCache = new HashMap<>();

    @Override
    public EvictionCase openEvictionCase(Lease leaseId, String note) {
        if (getCurrentEvictionCase(leaseId) != null) {
            throw new UserRuntimeException(i18n.tr("Existing open EvictionCase found."));
        }

        EvictionCase evictionCase = EntityFactory.create(EvictionCase.class);
        evictionCase.lease().set(leaseId);
        evictionCase.note().setValue(note);
        evictionCase.evictionFlowPolicy().set(getEvictionFlowPolicy(leaseId));
        evictionCase.createdBy().set(getLoggedEmployee());
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
    public EvictionStatus getCurrentEvictionStatus(Lease leaseId) {
        EvictionCase evictionCase = getCurrentEvictionCase(leaseId);
        return evictionCase == null ? null : getCurrentEvictionStatus(evictionCase);
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
    public EvictionStatus addEvictionStatusDetails(EvictionCase evictionCase, String statusName, String note, List<EvictionDocument> attachments) {
        // - check that the case is not closed
        if (!evictionCase.closedOn().isNull()) {
            return null;
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
        if (evictionStatus == null) {
            evictionStatus = createEvictionStatus(flowStep.stepType().getValue());
            evictionStatus.evictionStep().set(flowStep);
            evictionStatus.addedBy().set(getLoggedEmployee());
            evictionStatus.note().setValue(i18n.tr("Auto-generated for Eviction Status update"));
            evictionCase.history().add(evictionStatus);
        }
        // - add new details to the case status
        addEvictionStatusDetails(evictionStatus, note, attachments);

        Persistence.service().persist(evictionCase);

        return evictionStatus;
    }

    @Override
    public void addEvictionStatusDetails(EvictionStatus evictionStatus, String note, List<EvictionDocument> attachments) {
        EvictionStatusRecord record = EntityFactory.create(EvictionStatusRecord.class);
        record.addedBy().set(getLoggedEmployee());
        record.note().setValue(note);
        evictionStatus.statusRecords().add(record);
        Persistence.service().persist(evictionStatus);

        if (attachments != null) {
            for (EvictionDocument doc : attachments) {
                doc.record().set(record);
            }
            record.attachments().addAll(attachments);
            Persistence.service().persist(record.attachments());
        }

    }

    private Employee getLoggedEmployee() {
        EntityQueryCriteria<Employee> crit = EntityQueryCriteria.create(Employee.class);
        crit.eq(crit.proto().user(), VistaContext.getCurrentUser());
        return Persistence.service().retrieve(crit);
    }

    private EvictionFlowPolicy getEvictionFlowPolicy(Lease lease) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        Building building = lease.unit().building();
        EvictionFlowPolicy policy = policyCache.get(building);
        if (policy == null) {
            policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, EvictionFlowPolicy.class);
            policyCache.put(building, policy);
        }
        if (policy == null) {
            throw new Error("Cannot find EvictionFlowPolicy for building: " + building.propertyCode().getValue());
        }
        return policy;
    }

    private EvictionStatus createEvictionStatus(EvictionStepType flowStep) {
        switch (flowStep) {
        case N4:
            return EntityFactory.create(EvictionStatusN4.class);
        default:
            return EntityFactory.create(EvictionStatus.class);
        }
    }
}
