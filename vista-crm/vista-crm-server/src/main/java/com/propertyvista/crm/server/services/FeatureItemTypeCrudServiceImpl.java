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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.FeatureItemTypeCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class FeatureItemTypeCrudServiceImpl extends GenericCrudServiceImpl<ServiceItemType> implements FeatureItemTypeCrudService {

    public FeatureItemTypeCrudServiceImpl() {
        super(ServiceItemType.class);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<ServiceItemType>> callback, EntityListCriteria<ServiceItemType> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), ServiceItemType.Type.feature));
        super.list(callback, criteria);
    }
}
