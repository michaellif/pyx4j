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

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.domain.person.Person;

public class HasPhoneValidator implements EntityValidator<Person> {

    private static final I18n i18n = I18n.get(HasPhoneValidator.class);

    @Override
    public Set<ValidationFailure> validate(Person entity) {
        if (entity.homePhone().isNull() | entity.mobilePhone().isNull()) {
            Set<ValidationFailure> validationFailures = new HashSet<ValidationFailure>();
            validationFailures.add(new SimpleValidationFailure(entity, i18n.tr("Either home or mobile phone is required")));
            return validationFailures;
        } else {
            return Collections.emptySet();
        }
    }

}
