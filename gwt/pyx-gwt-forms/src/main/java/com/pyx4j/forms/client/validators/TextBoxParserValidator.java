/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextBox;

/**
 * This validator is used to check that text inside textBox was successfully parsed.
 */
public class TextBoxParserValidator<E> implements EditableValueValidator<E> {

    private final String validationMessage;

    public TextBoxParserValidator(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    @Override
    public String getValidationMessage(CEditableComponent<E, ?> component, E value) {
        return validationMessage;
    }

    @Override
    public boolean isValid(CEditableComponent<E, ?> component, E value) {
        if (component instanceof CTextBox) {
            return ((CTextBox<?>) component).isParsedSuccesfully();
        }
        return false;
    }

}
