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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class LeaseParticipantUtils {

    public static void retrieveCustomerScreeningPointer(Customer customer) {
        // Retrieve draft if there are no final version
        customer.personScreening().set(
                ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(customer, AttachLevel.ToStringMembers));
    }

    public static void retrieveLeaseTermEffectiveScreening(Lease lease, LeaseTermParticipant<?> leaseParticipant, AttachLevel attachLevel) {
        if (isApplicationInPogress(lease, leaseParticipant.leaseTermV())) {
            // Take customer's Screening, Prefers draft version.
            leaseParticipant.effectiveScreening().set(
                    ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(leaseParticipant.leaseParticipant().customer(),
                            attachLevel));
        } else {
            leaseParticipant.effectiveScreening().set(leaseParticipant.screening());
            if (!leaseParticipant.effectiveScreening().isNull()) {
                Persistence.service().retrieve(leaseParticipant.effectiveScreening(), attachLevel, false);
            }
        }
        if ((!leaseParticipant.effectiveScreening().isNull()) && (attachLevel == AttachLevel.Attached)) {
            Persistence.service().retrieve(leaseParticipant.effectiveScreening().version().incomes());
            Persistence.service().retrieve(leaseParticipant.effectiveScreening().version().assets());
            Persistence.service().retrieve(leaseParticipant.effectiveScreening().version().documents());
        }
    }

    public static boolean isApplicationInPogress(Lease lease, LeaseTermV leaseTermV) {
        return VersionedEntityUtils.isDraft(leaseTermV) && lease.status().getValue().isDraft();
    }

}
