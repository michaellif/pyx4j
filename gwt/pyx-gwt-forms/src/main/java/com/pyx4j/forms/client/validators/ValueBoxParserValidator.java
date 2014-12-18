/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.ui.CValueBoxBase;

/**
 * This validator is used to check that text inside textBox was successfully parsed.
 */
public class ValueBoxParserValidator<E> extends AbstractComponentValidator<E> {

    public ValueBoxParserValidator() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public BasicValidationError isValid() {
        if (getCComponent() instanceof CValueBoxBase) {
            CValueBoxBase<E, ?> field = (CValueBoxBase) getCComponent();
            if (!field.getNativeComponent().isParsedOk()) {
                return new BasicValidationError(getCComponent(), field.getNativeComponent().getParseExceptionMessage());
            }
        }
        return null;
    }
}
