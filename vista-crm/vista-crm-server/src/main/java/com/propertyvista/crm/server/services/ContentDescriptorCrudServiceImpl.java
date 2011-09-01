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

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.ContentDescriptorCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.site.ContentDescriptor;

public class ContentDescriptorCrudServiceImpl extends GenericCrudServiceImpl<ContentDescriptor> implements ContentDescriptorCrudService {

    public ContentDescriptorCrudServiceImpl() {
        super(ContentDescriptor.class);
    }

    @Override
    public void retrieveHomeItem(AsyncCallback<Key> callback) {
        EntityQueryCriteria<ContentDescriptor> criteria = EntityQueryCriteria.create(ContentDescriptor.class);
        List<Key> list = PersistenceServicesFactory.getPersistenceService().queryKeys(criteria);
        if (list.isEmpty()) {
            throw new Error("Home item not found");
        } else {
            callback.onSuccess(list.get(0));
        }
    }
}
