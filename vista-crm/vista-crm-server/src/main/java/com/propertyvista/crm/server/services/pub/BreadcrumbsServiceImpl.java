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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.services.pub.BreadcrumbsService;
import com.propertyvista.domain.breadcrumbs.BreadcrumbTrailDTO;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper.LabelCreator;

public class BreadcrumbsServiceImpl implements BreadcrumbsService {

    private static final Map<Class<? extends IEntity>, LabelCreator> labelCreatorMap;

    private static final Map<Class<? extends IEntity>, Class<? extends IEntity>> dto2dbo;

    static {

        // CUSOMIZE LABEL GENERATION
        Map<Class<? extends IEntity>, LabelCreator> map = new HashMap<Class<? extends IEntity>, LabelCreator>();
        map.put(Building.class, new LabelCreator() {
            @Override
            public String label(IEntity entity) {
                return "Building: " + ((Building) entity).propertyCode().getValue();
            }
        });

        labelCreatorMap = Collections.unmodifiableMap(map);

        // SET UP DTO TO PARENT DBO MAPPING        
        Map<Class<? extends IEntity>, Class<? extends IEntity>> map2 = new HashMap<Class<? extends IEntity>, Class<? extends IEntity>>();
        map2.put(BuildingDTO.class, Building.class);
        map2.put(AptUnitDTO.class, AptUnit.class);
        map2.put(ComplexDTO.class, Complex.class);
        // etc..
        dto2dbo = Collections.unmodifiableMap(map2);
    }

    @Override
    public void breadcrumbtrail(AsyncCallback<BreadcrumbTrailDTO> callback, IEntity entity) {
        BreadcrumbTrailDTO trail = EntityFactory.create(BreadcrumbTrailDTO.class);
        Class<? extends IEntity> dboClass = dto2dbo.get(entity.getInstanceValueClass());
        IEntity dbo = null;
        if (dboClass != null) {
            dbo = entity.duplicate(dboClass);
        } else {
            // optimistic approach :)
            dbo = entity;
        }
        trail.trail().addAll(new BreadcrumbsHelper(labelCreatorMap).breadcrumbTrail(dbo));
        callback.onSuccess(trail);
    }

}
