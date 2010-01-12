/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on 2008-08-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.ui.CEditableComponent;

/**
 *
 */
public class RegexValidator<E> implements EditableValueValidator<E> {

    public static final String DIGITS_ONLY_REGEX = "^\\d*$";

    private String regex;

    protected String validationMessage;

    public RegexValidator(String regex, String validationMessage) {
        this.regex = regex;
        this.validationMessage = validationMessage;
    }

    public RegexValidator(String regex) {
        this(regex, null);
    }

    public boolean isValid(CEditableComponent<E> component, E value) {
        return (component.isValueEmpty() && !component.isMandatory()) || ((value != null) && (value.toString().matches(regex)));
    }

    public String getValidationMessage(CEditableComponent<E> component, E value) {
        if (validationMessage == null) {
            return "This field should have the following format:" + regex;
        } else {
            return validationMessage;
        }
    }
}
