/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class LeaseParticipantUtils {

    public static LeaseParticipantScreeningTO createScreeningPointer(LeaseParticipant<?> participant, CustomerScreening screening) {
        LeaseParticipantScreeningTO to = EntityFactory.create(LeaseParticipantScreeningTO.class);
        to.screening().set(screening);
        if (to.screening().getPrimaryKey() != null) {
            to.setPrimaryKey(new Key(participant.getPrimaryKey().asLong(), screening.getPrimaryKey().getVersion()));
            to.screening().setAttachLevel(AttachLevel.ToStringMembers);
        }
        return to;
    }

    public static LeaseParticipantScreeningTO getCustomerScreeningPointer(LeaseParticipant<?> participant) {
        // Retrieve draft if there are no final version
        return createScreeningPointer(participant,
                ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(participant.customer(), AttachLevel.ToStringMembers));
    }

    public static LeaseParticipantScreeningTO getLeaseTermEffectiveScreeningPointer(Lease lease, LeaseTermParticipant<?> termParticipant) {
        if (isApplicationInPogress(lease, termParticipant.leaseTermV())) {
            // Take customer's Screening, Prefers draft version.
            return getCustomerScreeningPointer(termParticipant.leaseParticipant());
        } else {
            if (!termParticipant.screening().isNull()) {
                CustomerScreening screening = termParticipant.screening().duplicate();
                Persistence.service().retrieve(screening, AttachLevel.ToStringMembers, false);
                return createScreeningPointer(termParticipant.leaseParticipant(), screening);
            } else {
                return null;
            }
        }
    }

    public static CustomerScreening retrieveLeaseTermEffectiveScreening(Lease lease, LeaseTermParticipant<?> termParticipant) {
        CustomerScreening screening;
        if (isApplicationInPogress(lease, termParticipant.leaseTermV())) {
            // Take customer's Screening, Prefers draft version.
            screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(termParticipant.leaseParticipant().customer(),
                    AttachLevel.Attached);
        } else {
            screening = termParticipant.screening();
            if (!screening.isNull()) {
                Persistence.service().retrieve(screening, AttachLevel.Attached, false);
            }

        }
        if ((screening != null) && !screening.isNull()) {
            Persistence.service().retrieve(screening.version().incomes());
            Persistence.service().retrieve(screening.version().assets());
            Persistence.service().retrieve(screening.version().documents());
        }
        return screening;
    }

    public static boolean isApplicationInPogress(Lease lease, LeaseTermV leaseTermV) {
        return (lease.status().getValue() == Lease.Status.Application) && VersionedEntityUtils.isDraft(leaseTermV) && lease.status().getValue().isDraft();
    }

}
