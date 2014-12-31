/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<N4Batch, N4BatchDTO> implements N4BatchCrudService {

    public N4BatchCrudServiceImpl() {
        super(N4Batch.class, N4BatchDTO.class);
    }

    @Override
    protected void enhanceRetrieved(N4Batch bo, N4BatchDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.items(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(N4Batch bo, N4BatchDTO to) {
        super.enhanceListRetrieved(bo, to);
    }
}
