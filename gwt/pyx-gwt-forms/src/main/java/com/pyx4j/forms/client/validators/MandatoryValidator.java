/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.ui.CComponent;

public class MandatoryValidator<E> implements EditableValueValidator<E> {

    private final String validationMessage;

    public MandatoryValidator(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    @Override
    public MandatoryValidationFailure isValid(CComponent<E> component, E value) {
        return !component.isValueEmpty() ? null : new MandatoryValidationFailure(component, validationMessage);
    }

}
