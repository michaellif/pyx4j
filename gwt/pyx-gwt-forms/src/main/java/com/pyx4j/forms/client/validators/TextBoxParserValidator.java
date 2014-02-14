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

import com.pyx4j.forms.client.ui.CTextFieldBase;

/**
 * This validator is used to check that text inside textBox was successfully parsed.
 */
public class TextBoxParserValidator<E> extends AbstractComponentValidator<E> {

    public TextBoxParserValidator() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public FieldValidationError isValid() {
        if (getComponent() instanceof CTextFieldBase) {
            CTextFieldBase<E, ?> field = (CTextFieldBase) getComponent();
            try {
                field.getWidget().getNativeValue();
            } catch (ParseException e) {
                return new FieldValidationError(getComponent(), e.getMessage());
            }
        }
        return null;
    }

}
