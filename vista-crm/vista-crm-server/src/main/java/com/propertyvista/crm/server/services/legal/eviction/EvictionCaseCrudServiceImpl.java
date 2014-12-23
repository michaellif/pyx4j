/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2014
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseCrudServiceImpl extends AbstractCrudServiceDtoImpl<EvictionCase, EvictionCaseDTO> implements EvictionCaseCrudService {

    public EvictionCaseCrudServiceImpl() {
        super(EvictionCase.class, EvictionCaseDTO.class);
    }

    @Override
    protected boolean persist(EvictionCase bo, EvictionCaseDTO to) {
        if (bo.createdBy().isNull()) {
            bo.createdBy().set(CrmAppContext.getCurrentUserEmployee());
        }

        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(EvictionCase bo, EvictionCaseDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.service().retrieveMember(to.createdBy());
    }
}
