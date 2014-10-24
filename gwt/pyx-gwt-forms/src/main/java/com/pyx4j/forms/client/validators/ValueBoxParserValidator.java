/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 * @version $Id$
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
        if (getComponent() instanceof CValueBoxBase) {
            CValueBoxBase<E, ?> field = (CValueBoxBase) getComponent();
            if (!field.getNativeComponent().isParsedOk()) {
                return new BasicValidationError(getComponent(), field.getNativeComponent().getParseExceptionMessage());
            }
        }
        return null;
    }
}
