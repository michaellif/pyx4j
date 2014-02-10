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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningCrudService;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.dto.CustomerScreeningDTO;

public class CustomerScreeningCrudServiceImpl extends AbstractVersionedCrudServiceDtoImpl<CustomerScreening, CustomerScreeningDTO> implements
        CustomerScreeningCrudService {

    public CustomerScreeningCrudServiceImpl() {
        super(CustomerScreening.class, CustomerScreeningDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected CustomerScreeningDTO init(InitializationData initializationData) {
        CustomerScreeningInitializationData initData = (CustomerScreeningInitializationData) initializationData;

        CustomerScreeningDTO screening = EntityFactory.create(CustomerScreeningDTO.class);
        screening.screene().set(Persistence.service().retrieve(Customer.class, initData.screene().getPrimaryKey(), AttachLevel.ToStringMembers, false));

        return screening;
    }

    @Override
    protected void enhanceRetrieved(CustomerScreening bo, CustomerScreeningDTO to, RetrieveTarget retrieveTarget) {
        // load detached entities:        
        Persistence.service().retrieve(to.version().incomes());
        Persistence.service().retrieve(to.version().assets());
        Persistence.service().retrieve(to.version().documents());
        Persistence.service().retrieve(to.screene(), AttachLevel.ToStringMembers, false);
    }

    @Override
    protected CustomerScreeningDTO duplicateForDraftEdit(CustomerScreeningDTO to) {
        to = super.duplicateForDraftEdit(to);
        ServerSideFactory.create(ScreeningFacade.class).registerUploadedDocuments(to);
        return to;
    }
}
