/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2014
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.validators.lease;

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;

public class LeaseTermParticipantBirthDateValidator<Participant extends LeaseTermParticipant<?>> implements EntityValidator<Participant> {

    private static final I18n i18n = I18n.get(LeaseTermParticipantBirthDateValidator.class);

    @Override
    public Set<ValidationFailure> validate(Participant participant) {
        Set<ValidationFailure> validationFailures = new HashSet<>();
        if (participant.role().getValue() != Role.Dependent) {
            Persistence.ensureRetrieve(participant.leaseParticipant(), AttachLevel.Attached);
            Persistence.ensureRetrieve(participant.leaseParticipant().lease(), AttachLevel.Attached);

            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                    participant.leaseParticipant().lease().unit(), RestrictionsPolicy.class);

            if (restrictionsPolicy.enforceAgeOfMajority().getValue(false)) {
                LogicalDate birthDate = participant.leaseParticipant().customer().person().birthDate().getValue();
                if (birthDate == null || !TimeUtils.isOlderThan(birthDate, restrictionsPolicy.ageOfMajority().getValue())) {
                    validationFailures.add(new SimpleValidationFailure(participant, i18n.tr(
                            "Lease participants with role \"{0}\" must be at least {1} years old.", participant.role().getValue().toString(),
                            restrictionsPolicy.ageOfMajority().getValue())));
                }
            }
        }
        return validationFailures;
    }

}
