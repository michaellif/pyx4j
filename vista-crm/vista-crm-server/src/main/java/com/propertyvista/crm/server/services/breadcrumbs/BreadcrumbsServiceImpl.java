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
 */
package com.propertyvista.crm.server.services.breadcrumbs;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;

import com.propertyvista.crm.rpc.services.breadcrumbs.BreadcrumbsService;

public class BreadcrumbsServiceImpl implements BreadcrumbsService {

    @Override
    public void obtainBreadcrumbTrail(AsyncCallback<Vector<IEntity>> callback, IEntity entity) {
        callback.onSuccess(new Vector<IEntity>(new BreadcrumbsHelper().breadcrumbTrail(entity)));
    }
}
