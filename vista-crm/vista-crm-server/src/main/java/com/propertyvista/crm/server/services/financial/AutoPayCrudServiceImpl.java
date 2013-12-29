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
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.financial.AutoPayCrudService;
import com.propertyvista.domain.payment.AutopayAgreement;

public class AutoPayCrudServiceImpl extends AbstractCrudServiceImpl<AutopayAgreement> implements AutoPayCrudService {

    public AutoPayCrudServiceImpl() {
        super(AutopayAgreement.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(AutopayAgreement bo, AutopayAgreement to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.tenant().lease(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.createdBy(), AttachLevel.ToStringMembers);
    }

    @Override
    protected void enhanceListRetrieved(AutopayAgreement bo, AutopayAgreement dto) {
        super.enhanceListRetrieved(bo, dto);

        Persistence.ensureRetrieve(dto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.tenant().lease(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(dto.createdBy(), AttachLevel.ToStringMembers);
    }
}
