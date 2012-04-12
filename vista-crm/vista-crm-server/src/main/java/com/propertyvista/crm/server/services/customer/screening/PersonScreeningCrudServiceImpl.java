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
package com.propertyvista.crm.server.services.customer.screening;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.customer.screening.PersonScreeningCrudService;
import com.propertyvista.domain.tenant.PersonScreening;

public class PersonScreeningCrudServiceImpl extends AbstractCrudServiceImpl<PersonScreening> implements PersonScreeningCrudService {

    public PersonScreeningCrudServiceImpl() {
        super(PersonScreening.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(PersonScreening entity, PersonScreening dto) {
        // load detached entities:        
        Persistence.service().retrieve(dto.documents());
        Persistence.service().retrieve(dto.incomes());
        Persistence.service().retrieve(dto.assets());
        Persistence.service().retrieve(dto.guarantors());
    }

}
