/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertvista.generator.gdo.TenantSummaryGDO;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.portal.domain.ptapp.dto.TenantInfoEditorDTO;
import com.propertyvista.portal.rpc.ptapp.services.TenantInfoService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.util.TenantConverter;

public class TenantInfoServiceImpl extends ApplicationEntityServiceImpl implements TenantInfoService {

    private final static Logger log = LoggerFactory.getLogger(TenantInfoServiceImpl.class);

    static TenantSummaryGDO getTenantSummaryDTO(Key tenantId) {
        TenantSummaryGDO summary = EntityFactory.create(TenantSummaryGDO.class);

        TenantInLease tenantInLease = PersistenceServicesFactory.getPersistenceService().retrieve(TenantInLease.class, tenantId);
        if ((tenantInLease == null) || (!tenantInLease.lease().id().equals(PtAppContext.getCurrentLease().id()))) {
            throw new SecurityViolationException("Invalid data access");
        }
        summary.setPrimaryKey(tenantId);
        summary.tenantInLease().set(tenantInLease);

        summary.tenant().set(tenantInLease.tenant());
        PersistenceServicesFactory.getPersistenceService().retrieve(summary.tenant());

        TenantScreening tenantScreening;
        {
            EntityQueryCriteria<TenantScreening> criteria = EntityQueryCriteria.create(TenantScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenantInLease.tenant()));
            tenantScreening = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
            if (tenantScreening != null) {
                summary.tenantScreening().set(tenantScreening);
            } else {
                summary.tenantScreening().tenant().set(summary.tenant());
            }
        }

        return summary;
    }

    @Override
    public void retrieve(AsyncCallback<TenantInfoEditorDTO> callback, Key tenantId) {
        log.debug("Retrieving Info for tenant {}", tenantId);
        TenantSummaryGDO summary = getTenantSummaryDTO(tenantId);

        TenantInfoEditorDTO dto = new TenantConverter.TenantInfoEditorConverter().dto(summary);
        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<TenantInfoEditorDTO> callback, TenantInfoEditorDTO dto) {
        TenantSummaryGDO summary = getTenantSummaryDTO(dto.getPrimaryKey());

        new TenantConverter.TenantInfoEditorConverter().toDbo(dto, summary);

        PersistenceServicesFactory.getPersistenceService().merge(summary.tenant());
        PersistenceServicesFactory.getPersistenceService().merge(summary.tenantScreening());

        dto = new TenantConverter.TenantInfoEditorConverter().dto(summary);
        callback.onSuccess(dto);
    }
}
