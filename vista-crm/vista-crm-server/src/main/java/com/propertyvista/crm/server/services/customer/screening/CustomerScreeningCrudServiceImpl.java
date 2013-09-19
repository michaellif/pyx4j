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

import com.pyx4j.entity.server.AbstractVersionedCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningCrudService;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;

public class CustomerScreeningCrudServiceImpl extends AbstractVersionedCrudServiceImpl<CustomerScreening> implements CustomerScreeningCrudService {

    public CustomerScreeningCrudServiceImpl() {
        super(CustomerScreening.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected CustomerScreening init(InitializationData initializationData) {
        CustomerScreeningInitializationData initData = (CustomerScreeningInitializationData) initializationData;

        CustomerScreening screening = EntityFactory.create(CustomerScreening.class);
        screening.screene().set(Persistence.service().retrieve(Customer.class, initData.screene().getPrimaryKey(), AttachLevel.ToStringMembers));

        return screening;
    }

    @Override
    protected void enhanceRetrieved(CustomerScreening entity, CustomerScreening dto, RetrieveTarget retrieveTarget) {
        // load detached entities:        
        Persistence.service().retrieve(dto.documents());
        Persistence.service().retrieve(dto.version().incomes());
        Persistence.service().retrieve(dto.version().assets());
        Persistence.service().retrieve(dto.screene(), AttachLevel.ToStringMembers);
    }
}
