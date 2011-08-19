/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 18, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.dto.BreadcrumbDTO;
import com.propertyvista.crm.rpc.dto.BreadcrumbTrailDTO;
import com.propertyvista.crm.rpc.services.BreadcrumbTrailService;

public class BreadcrumbTrailServiceImpl implements BreadcrumbTrailService {

    @Override
    public void getBreadcrumbTrail(AsyncCallback<BreadcrumbTrailDTO> callback, Key entityId, IEntity entityPrototype) {

        BreadcrumbTrailDTO trail = EntityFactory.create(BreadcrumbTrailDTO.class);

        BreadcrumbDTO breadcrumb = EntityFactory.create(BreadcrumbDTO.class);
        breadcrumb.placeId().setValue("placeId1");
        breadcrumb.entityId().setValue("entityId1");
        breadcrumb.name().setValue("name1");
        trail.breadcrumbs().add(breadcrumb);

        breadcrumb = EntityFactory.create(BreadcrumbDTO.class);
        breadcrumb.placeId().setValue("placeId2");
        breadcrumb.entityId().setValue("entityId2");
        breadcrumb.name().setValue("name2");
        trail.breadcrumbs().add(breadcrumb);

        callback.onSuccess(trail);

    }
}
