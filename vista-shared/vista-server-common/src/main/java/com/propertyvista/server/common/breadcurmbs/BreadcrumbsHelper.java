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
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;

public class BreadcrumbsHelper {

    public List<IEntity> breadcrumbTrail(IEntity targetEntity) {

        final LinkedList<IEntity> trail = new LinkedList<IEntity>();

        IEntity startFromTarget = Persistence.service().retrieve(EntityFactory.resolveDTOClass(targetEntity), targetEntity.getPrimaryKey());

        EntityGraph.applyToOwners(startFromTarget, new ApplyMethod() {

            @Override
            public boolean apply(IEntity owner) {
                if (owner.getPrimaryKey() == null) {
                    // Breaks on non existent owner 
                    return false;
                }
                if (owner.isValueDetached()) {
                    Persistence.service().retrieve(owner);
                }
                IEntity toStringOnlyEntity = owner.duplicate();
                toStringOnlyEntity.setAttachLevel(AttachLevel.ToStringMembers);
                trail.addFirst(toStringOnlyEntity);
                return true;
            }

        });
        return trail;
    }

}
