/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.validators.lease;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.entity.core.ICollection;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.CollectionValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;

public class HasAtLeastOneApplicantValidator implements CollectionValidator<LeaseTermTenant> {

    private static final I18n i18n = I18n.get(HasAtLeastOneApplicantValidator.class);

    @Override
    public Set<ValidationFailure> validate(ICollection<LeaseTermTenant, ?> collection) {
        boolean hasOneApplicant = false;
        if (!(collection.isNull() || collection.isEmpty())) {
            for (LeaseTermTenant tenant : collection) {
                if (tenant.role().getValue() == Role.Applicant) {
                    hasOneApplicant = true;
                    break;
                }
            }
        }
        if (!hasOneApplicant) {
            Set<ValidationFailure> validationFailures = new HashSet<ValidationFailure>();
            validationFailures.add(new SimpleValidationFailure(collection, i18n.tr("{0} is required", Role.Applicant)));
            return validationFailures;
        } else {
            return Collections.emptySet();
        }
    }

}
