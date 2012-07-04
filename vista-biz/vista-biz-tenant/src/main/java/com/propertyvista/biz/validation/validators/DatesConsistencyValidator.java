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
 * @version $Id$
 */
package com.propertyvista.biz.validation.validators;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.domain.tenant.lease.Lease;

public class DatesConsistencyValidator implements EntityValidator<Lease> {

    private static final I18n i18n = I18n.get(DatesConsistencyValidator.class);

    @Override
    public Set<ValidationFailure> validate(Lease lease) {
        if (lease.leaseTo().isNull() || lease.leaseFrom().isNull() || lease.leaseFrom().getValue().after(lease.leaseTo().getValue())) {
            Set<ValidationFailure> validationFailure = new HashSet<ValidationFailure>();
            validationFailure.add(new SimpleValidationFailure(lease, i18n.tr("\"{0}\" date must be before \"{1}\" date", lease.leaseFrom().getMeta()
                    .getCaption(), lease.leaseTo().getMeta().getCaption())));
            return validationFailure;
        } else {
            return Collections.emptySet();
        }

    }
}
