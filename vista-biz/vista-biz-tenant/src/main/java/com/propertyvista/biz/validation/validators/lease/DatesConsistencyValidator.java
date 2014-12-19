/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-04
 * @author ArtyomB
 */
package com.propertyvista.biz.validation.validators.lease;

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class DatesConsistencyValidator implements EntityValidator<Lease> {

    private static final I18n i18n = I18n.get(DatesConsistencyValidator.class);

    @Override
    public Set<ValidationFailure> validate(Lease lease) {
        Set<ValidationFailure> validationFailures = new HashSet<ValidationFailure>();
        if (lease.currentTerm().termFrom().isNull()) {
            validationFailures.add(new SimpleValidationFailure(lease.currentTerm().termFrom(), i18n.tr("\"{0}\" is mandatory", lease.currentTerm().termFrom()
                    .getMeta().getCaption())));
        }
        if (lease.currentTerm().type().getValue() != LeaseTerm.Type.Periodic && lease.currentTerm().termTo().isNull()) {
            validationFailures.add(new SimpleValidationFailure(lease.currentTerm().termTo(), i18n.tr("\"{0}\" is mandatory", lease.currentTerm().termTo()
                    .getMeta().getCaption())));
        }
        if (lease.currentTerm().type().getValue() != LeaseTerm.Type.Periodic//@formatter:off
                & !lease.currentTerm().termFrom().isNull()
                & !lease.currentTerm().termTo().isNull()) {//@formatter:on
            if (lease.currentTerm().termFrom().getValue().compareTo(lease.currentTerm().termTo().getValue()) > 0) {
                validationFailures.add(new SimpleValidationFailure(lease, i18n.tr("\"{0}\" date must be before \"{1}\" date", lease.currentTerm().termFrom()
                        .getMeta().getCaption(), lease.currentTerm().termTo().getMeta().getCaption())));
            }
        }
        return validationFailures;
    }
}
