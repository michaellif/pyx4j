/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

public class MandatoryValidator<E> extends AbstractComponentValidator<E> {

    public MandatoryValidator() {
    }

    @Override
    public MandatoryValidationFailure isValid() {
        return !getComponent().isValueEmpty() ? null : new MandatoryValidationFailure(getComponent(), getComponent().getMandatoryValidationMessage());
    }

}
