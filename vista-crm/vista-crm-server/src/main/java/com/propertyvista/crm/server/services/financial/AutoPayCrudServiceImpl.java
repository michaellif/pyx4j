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

import java.math.BigDecimal;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.financial.AutoPayDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayCrudService;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;

public class AutoPayCrudServiceImpl extends AbstractCrudServiceDtoImpl<AutopayAgreement, AutoPayDTO> implements AutoPayCrudService {

    public AutoPayCrudServiceImpl() {
        super(AutopayAgreement.class, AutoPayDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(AutopayAgreement bo, AutoPayDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.tenant().lease(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.createdBy(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.reviewOfPap(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(AutopayAgreement bo, AutoPayDTO dto) {
        super.enhanceListRetrieved(bo, dto);

        Persistence.ensureRetrieve(dto.tenant().lease(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(dto.createdBy(), AttachLevel.ToStringMembers);

        dto.price().setValue(BigDecimal.ZERO);
        dto.payment().setValue(BigDecimal.ZERO);
        for (AutopayAgreementCoveredItem item : dto.coveredItems()) {
            dto.price().setValue(dto.payment().getValue().add(item.billableItem().agreedPrice().getValue()));
            dto.payment().setValue(dto.payment().getValue().add(item.amount().getValue()));
        }
    }
}
