/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-1-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.services.customer.CustomerCreditCheckLongReportCrudService;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class CustomerCreditCheckLongReportCrudServiceImpl extends AbstractCrudServiceDtoImpl<CustomerCreditCheck, CustomerCreditCheckLongReportDTO> implements
        CustomerCreditCheckLongReportCrudService {

    public CustomerCreditCheckLongReportCrudServiceImpl() {
        super(CustomerCreditCheck.class, CustomerCreditCheckLongReportDTO.class);
    }

    @Override
    protected void bind() {
    }

    @Override
    protected void enhanceRetrieved(CustomerCreditCheck entity, CustomerCreditCheckLongReportDTO dto, RetrieveTraget retrieveTraget) {
    }
}
