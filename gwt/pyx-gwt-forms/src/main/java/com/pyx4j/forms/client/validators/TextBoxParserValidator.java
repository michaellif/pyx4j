/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import java.text.ParseException;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;

/**
 * This validator is used to check that text inside textBox was successfully parsed.
 */
public class TextBoxParserValidator<E> implements EditableValueValidator<E> {

    public TextBoxParserValidator() {
    }

    @Override
    public ValidationFailure isValid(CComponent<E, ?> component, E value) {
        if (component instanceof CTextFieldBase) {
            if (component.isWidgetCreated()) {
                try {
                    component.asWidget().getNativeValue();
                } catch (ParseException e) {
                    return new ValidationFailure(e.getMessage());
                }
            }
        }
        return null;
    }
}
