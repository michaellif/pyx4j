/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchItemCrudService;
import com.propertyvista.domain.legal.n4.N4BatchItem;

public class N4BatchItemCrudServiceImpl extends AbstractCrudServiceImpl<N4BatchItem> implements N4BatchItemCrudService {

    public N4BatchItemCrudServiceImpl() {
        super(N4BatchItem.class);
    }

    @Override
    protected void enhanceRetrieved(N4BatchItem bo, N4BatchItem to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.lease()._applicant(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.leaseArrears().unpaidCharges(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.batch(), AttachLevel.ToStringMembers);
    }

    @Override
    protected void enhanceListRetrieved(N4BatchItem bo, N4BatchItem to) {
        super.enhanceListRetrieved(bo, to);

        Persistence.ensureRetrieve(to.lease()._applicant(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.lease().unit().building(), AttachLevel.Attached);
    }
}
