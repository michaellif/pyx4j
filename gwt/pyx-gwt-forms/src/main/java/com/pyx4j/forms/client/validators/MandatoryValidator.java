/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 */
package com.pyx4j.forms.client.validators;

public class MandatoryValidator<E> extends AbstractComponentValidator<E> {

    public MandatoryValidator() {
    }

    @Override
    public MandatoryValidationError isValid() {
        return getCComponent().isValueEmpty() ? new MandatoryValidationError(getCComponent(), getCComponent().getMandatoryValidationMessage()) : null;
    }
}
