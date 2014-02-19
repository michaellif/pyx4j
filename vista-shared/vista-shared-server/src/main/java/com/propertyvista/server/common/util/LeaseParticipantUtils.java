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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class LeaseParticipantUtils {

    public static LeaseParticipantScreeningTO retrieveCustomerScreeningPointer(LeaseParticipant<?> participant) {
        // Retrieve draft if there are no final version
        LeaseParticipantScreeningTO to = EntityFactory.create(LeaseParticipantScreeningTO.class);
        to.screening().set(
                ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(participant.customer(), AttachLevel.ToStringMembers));
        if (to.screening().getPrimaryKey() != null) {
            to.setPrimaryKey(participant.getPrimaryKey());
        }
        return to;
    }

    public static void retrieveLeaseTermEffectiveScreening(Lease lease, LeaseTermParticipant<?> leaseParticipant, AttachLevel attachLevel) {
        if (isApplicationInPogress(lease, leaseParticipant.leaseTermV())) {
            // Take customer's Screening, Prefers draft version.
            leaseParticipant.effectiveScreeningOld().set(
                    ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(leaseParticipant.leaseParticipant().customer(),
                            attachLevel));
        } else {
            leaseParticipant.effectiveScreeningOld().set(leaseParticipant.screening());
            if (!leaseParticipant.effectiveScreeningOld().isNull()) {
                Persistence.service().retrieve(leaseParticipant.effectiveScreeningOld(), attachLevel, false);
            }
        }
        if ((!leaseParticipant.effectiveScreeningOld().isNull()) && (attachLevel == AttachLevel.Attached)) {
            Persistence.service().retrieve(leaseParticipant.effectiveScreeningOld().version().incomes());
            Persistence.service().retrieve(leaseParticipant.effectiveScreeningOld().version().assets());
            Persistence.service().retrieve(leaseParticipant.effectiveScreeningOld().version().documents());
        }
    }

    public static boolean isApplicationInPogress(Lease lease, LeaseTermV leaseTermV) {
        return VersionedEntityUtils.isDraft(leaseTermV) && lease.status().getValue().isDraft();
    }

}
