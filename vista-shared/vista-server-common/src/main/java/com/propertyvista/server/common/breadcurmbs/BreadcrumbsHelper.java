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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;

import com.propertyvista.domain.breadcrumbs.BreadcrumbDTO;

public class BreadcrumbsHelper {

    private final Map<Class<? extends IEntity>, LabelCreator> labelCreatorMap;

    public BreadcrumbsHelper(Map<Class<? extends IEntity>, LabelCreator> labelCreatorMap) {
        this.labelCreatorMap = labelCreatorMap;
    }

    public List<BreadcrumbDTO> breadcrumbTrail(IEntity entity) {
        final LinkedList<BreadcrumbDTO> trail = new LinkedList<BreadcrumbDTO>();

        EntityGraph.applyToOwners(entity, new ApplyMethod() {

            @Override
            public boolean apply(IEntity entity) {
                if (entity.isValueDetached()) {
                    Persistence.service().retrieve(entity);
                }
                BreadcrumbDTO breadcrumb = EntityFactory.create(BreadcrumbDTO.class);
                breadcrumb.entityId().setValue(entity.getPrimaryKey());
                breadcrumb.entityClass().setValue(entity.getInstanceValueClass().getName());
                breadcrumb.label().setValue(asLabel(entity));
                trail.addFirst(breadcrumb);
                return true;
            }
        });

        return trail;
    }

    private String asLabel(IEntity entity) {
        LabelCreator creator = labelCreatorMap.get(entity.getInstanceValueClass());
        return creator != null ? creator.label(entity.cast()) : entity.getStringView();
    }

    public interface LabelCreator {

        String label(IEntity entity);
    }
}
