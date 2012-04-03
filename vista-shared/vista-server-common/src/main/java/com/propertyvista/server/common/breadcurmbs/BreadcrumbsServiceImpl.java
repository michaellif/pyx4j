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
package com.propertyvista.server.common.breadcurmbs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.breadcrumbs.BreadcrumbTrailDTO;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper.LabelCreator;

public class BreadcrumbsServiceImpl implements BreadcrumbsService {

    private static final Map<Class<? extends IEntity>, LabelCreator> labelCreatorMap;
    static {

        Map<Class<? extends IEntity>, LabelCreator> map = new HashMap<Class<? extends IEntity>, LabelCreator>();

        map.put(Building.class, new LabelCreator() {
            @Override
            public String label(IEntity entity) {
                return "Building:" + ((Building) entity).propertyCode().getValue();
            }
        });

        labelCreatorMap = Collections.unmodifiableMap(map);
    }

    @Override
    public void breadcrumbtrail(AsyncCallback<BreadcrumbTrailDTO> callback, IEntity entity) {
        BreadcrumbTrailDTO trail = EntityFactory.create(BreadcrumbTrailDTO.class);
        trail.trail().addAll(new BreadcrumbsHelper(labelCreatorMap).breadcrumbTrail(entity));
        callback.onSuccess(trail);
    }

}
