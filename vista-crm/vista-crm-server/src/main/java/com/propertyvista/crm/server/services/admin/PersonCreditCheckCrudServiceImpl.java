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

import com.propertyvista.crm.rpc.dto.PersonCreditCheckDTO;
import com.propertyvista.crm.rpc.services.admin.PersonCreditCheckCrudService;
import com.propertyvista.domain.tenant.PersonCreditCheck;

public class PersonCreditCheckCrudServiceImpl extends AbstractCrudServiceDtoImpl<PersonCreditCheck, PersonCreditCheckDTO> implements
        PersonCreditCheckCrudService {

    public PersonCreditCheckCrudServiceImpl() {
        super(PersonCreditCheck.class, PersonCreditCheckDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(PersonCreditCheck entity, PersonCreditCheckDTO dto, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget retrieveTraget) {
        Persistence.service().retrieveMember(dto.screening());
        Persistence.service().retrieveMember(dto.screening().screene());
    }

    @Override
    protected void enhanceListRetrieved(PersonCreditCheck entity, PersonCreditCheckDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        enhanceRetrieved(entity, dto, RetrieveTraget.View);
    }

}
