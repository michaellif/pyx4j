/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.pyx4j.entity.server.AbstractListServiceDtoImpl;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleBillListService;
import com.propertyvista.domain.financial.billing.Bill;

public class BillingCycleBillListServiceImpl extends AbstractListServiceDtoImpl<Bill, BillDataDTO> implements BillingCycleBillListService {

    public BillingCycleBillListServiceImpl() {
        super(Bill.class, BillDataDTO.class);
    }

    @Override
    protected void bind() {
        bind(dboClass, dtoProto.bill(), dboProto);
    }
}