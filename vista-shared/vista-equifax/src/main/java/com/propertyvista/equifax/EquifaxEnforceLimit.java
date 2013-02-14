/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.EquifaxLimit;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.operations.domain.vista2pmc.DefaultEquifaxLimit;
import com.propertyvista.server.jobs.TaskRunner;

class EquifaxEnforceLimit {

    private static final I18n i18n = I18n.get(EquifaxEnforceLimit.class);

    static void assertLimit(final AuditRecordEventType eventType) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        EquifaxLimit pmcLimit = TaskRunner.runInOperationsNamespace(new Callable<EquifaxLimit>() {
            @Override
            public EquifaxLimit call() {
                Persistence.service().retrieveMember(pmc.equifaxInfo());
                return pmc.equifaxInfo().limit();
            }
        });

        DefaultEquifaxLimit defaultEquifaxLimit = TaskRunner.runInOperationsNamespace(new Callable<DefaultEquifaxLimit>() {
            @Override
            public DefaultEquifaxLimit call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxLimit.class));
            }
        });

        EquifaxLimit limit = EntityFactory.create(EquifaxLimit.class);

        setNonNullMember(limit.dailyRequests(), pmcLimit, defaultEquifaxLimit);
        setNonNullMember(limit.dailyReports(), pmcLimit, defaultEquifaxLimit);

        Integer currentCount = TaskRunner.runInOperationsNamespace(new Callable<Integer>() {
            @Override
            public Integer call() {
                EntityQueryCriteria<AuditRecord> criteria = EntityQueryCriteria.create(AuditRecord.class);
                criteria.eq(criteria.proto().event(), eventType);
                criteria.eq(criteria.proto().namespace(), pmc.namespace());
                Date dayStart = DateUtils.dayStart(new Date());
                criteria.ge(criteria.proto().created(), dayStart);
                return Persistence.service().count(criteria);
            }
        });

        switch (eventType) {
        case EquifaxReadReport:
            if (currentCount >= limit.dailyRequests().getValue()) {
                throw new UserRuntimeException(i18n.tr("Read Report Daily limit exceeded"));
            }
            break;
        case EquifaxRequest:
            if (currentCount >= limit.dailyRequests().getValue()) {
                throw new UserRuntimeException(i18n.tr("Request Daily limit exceeded"));
            }
            break;
        default:
            throw new IllegalArgumentException("Unsupported limit");
        }
    }

    @SuppressWarnings("unchecked")
    private static <S extends Serializable> void setNonNullMember(IPrimitive<S> dst, IEntity... srcs) {
        for (IEntity src : srcs) {
            if (!src.getMember(dst.getFieldName()).isNull()) {
                dst.set((IPrimitive<S>) src.getMember(dst.getFieldName()));
                break;
            }
        }
    }
}
