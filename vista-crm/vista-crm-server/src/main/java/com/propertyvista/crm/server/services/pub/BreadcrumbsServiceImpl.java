/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.pub;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.services.pub.BreadcrumbsService;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper;

public class BreadcrumbsServiceImpl implements BreadcrumbsService {

    @Override
    public void breadcrumbtrail(AsyncCallback<Vector<IEntity>> callback, IEntity entity) {
        List<IEntity> temp = new BreadcrumbsHelper().breadcrumbTrail(entity);
        Vector<IEntity> result = new Vector<IEntity>();

        // TODO this is workaround!!! must be done in helper i think
        for (IEntity e : temp) {
            IEntity x = Persistence.service().retrieve(e.getInstanceValueClass(), e.getPrimaryKey());
            x.setAttachLevel(AttachLevel.ToStringMembers);
            result.add(x);
        }
        callback.onSuccess(result);
    }
}
