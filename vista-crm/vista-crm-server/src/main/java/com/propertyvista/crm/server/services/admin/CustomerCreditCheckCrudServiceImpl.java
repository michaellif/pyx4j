/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.CustomerCreditCheckDTO;
import com.propertyvista.crm.rpc.services.admin.CustomerCreditCheckCrudService;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class CustomerCreditCheckCrudServiceImpl extends AbstractCrudServiceDtoImpl<CustomerCreditCheck, CustomerCreditCheckDTO> implements
        CustomerCreditCheckCrudService {

    public CustomerCreditCheckCrudServiceImpl() {
        super(CustomerCreditCheck.class, CustomerCreditCheckDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(CustomerCreditCheck entity, CustomerCreditCheckDTO dto,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget retrieveTraget) {
        Persistence.service().retrieveMember(dto.screening());
        Persistence.service().retrieveMember(dto.screening().screene());
    }

    @Override
    protected void enhanceListRetrieved(CustomerCreditCheck entity, CustomerCreditCheckDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        enhanceRetrieved(entity, dto, RetrieveTraget.View);
    }

}
