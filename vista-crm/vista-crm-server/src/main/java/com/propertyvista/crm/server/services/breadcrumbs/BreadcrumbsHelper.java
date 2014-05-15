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
package com.propertyvista.crm.server.services.breadcrumbs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;

import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckDTO;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class BreadcrumbsHelper {

    public List<IEntity> breadcrumbTrail(IEntity targetEntity) {
        IEntity startFromTarget = Persistence.service().retrieve(EntityFactory.resolveBOClass(targetEntity), targetEntity.getPrimaryKey());
        List<IEntity> trail = getOwners(startFromTarget);
        Collections.reverse(trail);
        return trail;
    }

    private static IEntity toStringDuplicate(IEntity entity) {
        IEntity toStringOnlyEntity = entity.duplicate();
        toStringOnlyEntity.setAttachLevel(AttachLevel.ToStringMembers);
        return toStringOnlyEntity;
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

            if (owner instanceof Lease) {
                // Redirect to Applications
                Lease lease = (Lease) owner;
                if (lease.status().getValue() == Lease.Status.Application) {
                    LeaseApplicationDTO leaseApplication = EntityFactory.create(LeaseApplicationDTO.class);
                    leaseApplication.set(lease.duplicate(LeaseApplicationDTO.class));
                    owner = leaseApplication;
                }
            }

            lastTrailEntity = owner;
            owners.add(toStringDuplicate(owner));
            return true;
        }
    }

    private static List<IEntity> getgetOwnersAndThis(IEntity startFromTarget) {
        List<IEntity> trail = new ArrayList<IEntity>();
        if (startFromTarget.getPrimaryKey() != null) {
            if (startFromTarget.isValueDetached()) {
                Persistence.service().retrieve(startFromTarget);
            }
            trail.add(toStringDuplicate(startFromTarget));
            trail.addAll(getOwners(startFromTarget));
        }
        return trail;
    }

    private static List<IEntity> getOwners(IEntity startFromTarget) {
        final List<IEntity> trail = new ArrayList<IEntity>();

        if (startFromTarget instanceof CustomerCreditCheckDTO) {
            return trail; // no trail to display (we have no Customer view)!..
        }

        // Special case for no business owned
        if (startFromTarget instanceof LeaseParticipantScreeningTO) {
            startFromTarget = Persistence.service().retrieve(LeaseParticipant.class, startFromTarget.getPrimaryKey());
        }

        if (startFromTarget instanceof Lease) {
            trail.addAll(getgetOwnersAndThis(((Lease) startFromTarget).unit()));
        }

        OwnersItterator iter = new OwnersItterator(trail);
        EntityGraph.applyToOwners(startFromTarget, iter);

        if (iter.lastTrailEntity instanceof Lease) {
            trail.addAll(getgetOwnersAndThis(((Lease) iter.lastTrailEntity).unit()));
        }

        return trail;
    }
}
