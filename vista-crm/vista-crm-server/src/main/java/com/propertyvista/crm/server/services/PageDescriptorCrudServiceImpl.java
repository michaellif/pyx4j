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
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.PersistenceServicesFactory;

import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.site.PageDescriptor;

public class PageDescriptorCrudServiceImpl extends GenericCrudServiceImpl<PageDescriptor> implements PageDescriptorCrudService {

    public PageDescriptorCrudServiceImpl() {
        super(PageDescriptor.class);
    }

    @Override
    protected void enhanceRetrieve(PageDescriptor entity, boolean fromList) {
        if (!fromList) {
            PersistenceServicesFactory.getPersistenceService().retrieve(entity.content());

//            EntityQueryCriteria<PageDescriptor> childPagesCriteria = EntityQueryCriteria.create(PageDescriptor.class);
//            childPagesCriteria.add(PropertyCriterion.eq(childPagesCriteria.proto().parent(), entity));
//            entity.childPages().addAll(PersistenceServicesFactory.getPersistenceService().query(childPagesCriteria));
        }
    }
}
