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

import static com.propertyvista.crm.server.services.pub.BreadcrumbsConfig.DTO_TO_DBO_MAP;
import static com.propertyvista.crm.server.services.pub.BreadcrumbsConfig.LABEL_CREATOR_MAP;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.services.pub.BreadcrumbsService;
import com.propertyvista.domain.breadcrumbs.BreadcrumbTrailDTO;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper;

public class BreadcrumbsServiceImpl implements BreadcrumbsService {

    @Override
    public void breadcrumbtrail(AsyncCallback<BreadcrumbTrailDTO> callback, IEntity entity) {
        BreadcrumbTrailDTO trail = EntityFactory.create(BreadcrumbTrailDTO.class);

        Class<? extends IEntity> dboClass = DTO_TO_DBO_MAP.get(entity.getInstanceValueClass());
        IEntity dbo = null;
        if (dboClass != null) {
            dbo = entity.duplicate(dboClass);
        } else {
            // optimistic approach :)
            dbo = entity.cast(); // cast if we got an abstract entity
        }

        trail.trail().addAll(new BreadcrumbsHelper(LABEL_CREATOR_MAP).breadcrumbTrail(dbo));

        callback.onSuccess(trail);
    }

}
