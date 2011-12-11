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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;

/**
 * This validator is used to check that text inside textBox was successfully parsed.
 */
public class TextBoxParserValidator<E> implements EditableValueValidator<E> {

    private final String validationMessage;

    public TextBoxParserValidator(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    @Override
    public String getValidationMessage(CComponent<E, ?> component, E value) {
        return validationMessage;
    }

    @Override
    public boolean isValid(CComponent<E, ?> component, E value) {
        if (component instanceof CTextFieldBase) {
            if (component.isWidgetCreated()) {
                try {
                    return EqualsHelper.equals(component.asWidget().getNativeValue(), value);
                } catch (ParseException e) {
                    return false;
                }
            }
        }
        return false;
    }

}
