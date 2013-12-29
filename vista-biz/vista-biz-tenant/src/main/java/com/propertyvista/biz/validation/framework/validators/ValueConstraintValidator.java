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
package com.propertyvista.biz.validation.framework.validators;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.PrimitiveValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;

public class ValueConstraintValidator<T extends Serializable> implements PrimitiveValidator<T> {

    private static final I18n i18n = I18n.get(ValueConstraintValidator.class);

    private final T[] acceptableValues;

    public ValueConstraintValidator(T... acceptableValues) {
        this.acceptableValues = acceptableValues;
    }

    @Override
    public Set<ValidationFailure> validate(IPrimitive<T> primitive) {
        boolean isValid = false;
        if (!primitive.isNull()) {
            T value = primitive.getValue();
            for (T acceptableValue : acceptableValues) {
                if (value.equals(acceptableValue)) {
                    isValid = true;
                    break;
                }
            }
        }
        if (isValid) {
            return Collections.emptySet();
        } else {
            Set<ValidationFailure> validationFailure = new HashSet<ValidationFailure>();
            String message = acceptableValues.length > 1 ? i18n.tr("{0} is expected to be one of: \"{1}\", but is \"{2}\"", primitive.getMeta().getCaption(),
                    StringUtils.join(acceptableValues, ", "), primitive.getValue()) : i18n.tr("{0} is expected to be \"{1}\", but is \"{2}\"", primitive
                    .getMeta().getCaption(), acceptableValues[0], primitive.getValue());
            validationFailure.add(new SimpleValidationFailure(primitive, message));
            return validationFailure;
        }
    }

}
