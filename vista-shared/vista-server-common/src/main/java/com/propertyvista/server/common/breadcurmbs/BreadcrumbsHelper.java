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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;

import com.propertyvista.domain.tenant.lease.Lease;

public class BreadcrumbsHelper {

    public List<IEntity> breadcrumbTrail(IEntity targetEntity) {
        IEntity startFromTarget = Persistence.service().retrieve(EntityFactory.resolveDBOClass(targetEntity), targetEntity.getPrimaryKey());
        List<IEntity> trail = getOwners(startFromTarget);
        Collections.reverse(trail);
        return trail;
    }

    private static class OwnersItterator implements ApplyMethod {

        final List<IEntity> owners;

        IEntity lastTrailEntity;

        public OwnersItterator(List<IEntity> owners) {
            this.owners = owners;
        }

        @Override
        public boolean apply(IEntity owner) {
            if (owner.getPrimaryKey() == null) {
                // Breaks on non existent owner 
                return false;
            }
            if (owner.isValueDetached()) {
                Persistence.service().retrieve(owner);
            }
            lastTrailEntity = owner;
            IEntity toStringOnlyEntity = owner.duplicate();
            toStringOnlyEntity.setAttachLevel(AttachLevel.ToStringMembers);
            owners.add(toStringOnlyEntity);
            return true;
        }
    }

    private static List<IEntity> getgetOwnersAndThis(IEntity startFromTarget) {
        List<IEntity> trail = new ArrayList<IEntity>();
        if (startFromTarget.getPrimaryKey() != null) {
            if (startFromTarget.isValueDetached()) {
                Persistence.service().retrieve(startFromTarget);
            }
            IEntity toStringOnlyEntity = startFromTarget.duplicate();
            toStringOnlyEntity.setAttachLevel(AttachLevel.ToStringMembers);
            trail.add(toStringOnlyEntity);

            trail.addAll(getOwners(startFromTarget));
        }
        return trail;
    }

    private static List<IEntity> getOwners(IEntity startFromTarget) {
        final List<IEntity> trail = new ArrayList<IEntity>();

        OwnersItterator iter = new OwnersItterator(trail);
        EntityGraph.applyToOwners(startFromTarget, iter);

        // Special case for no business owned
        if (startFromTarget instanceof Lease) {
            trail.addAll(getgetOwnersAndThis(((Lease) startFromTarget).unit()));
        }

        if (iter.lastTrailEntity instanceof Lease) {
            trail.addAll(getgetOwnersAndThis(((Lease) iter.lastTrailEntity).unit()));
        }

        return trail;
    }
}
