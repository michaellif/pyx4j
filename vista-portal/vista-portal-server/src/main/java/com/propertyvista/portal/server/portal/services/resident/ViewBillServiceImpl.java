/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.portal.rpc.portal.services.resident.ViewBillService;

public class ViewBillServiceImpl extends AbstractCrudServiceDtoImpl<Bill, BillDTO> implements ViewBillService {

    public ViewBillServiceImpl() {
        super(Bill.class, BillDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Bill entity, BillDTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.lineItems());
        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingRun().building(), AttachLevel.ToStringMembers);
        BillingUtils.enhanceBillDto(entity, dto);
    }

    @Override
    protected void persist(Bill entity, BillDTO dto) {
        throw new IllegalArgumentException();
    }
}
