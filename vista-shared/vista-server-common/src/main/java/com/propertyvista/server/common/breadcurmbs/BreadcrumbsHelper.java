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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;

public class BreadcrumbsHelper {

    public List<IEntity> breadcrumbTrail(IEntity entity) {
        final LinkedList<IEntity> trail = new LinkedList<IEntity>();

        EntityGraph.applyToOwners(entity, new ApplyMethod() {

            // flag for not adding the "Target" of the breadcrumb trail to the trail            
            private boolean isTarget = true;

            @Override
            public boolean apply(IEntity entity) {
                IEntity castedEntity = entity.cast();
                if (castedEntity.isValueDetached()) {
                    Persistence.service().retrieve(castedEntity);
                }
                if (!isTarget) {
                    trail.addFirst(castedEntity);
                } else {
                    isTarget = false;
                }
                return true;
            }
        });
        // TODO detach here
        return trail;
    }

    public interface LabelCreator {

        String label(IEntity entity);
    }
}
