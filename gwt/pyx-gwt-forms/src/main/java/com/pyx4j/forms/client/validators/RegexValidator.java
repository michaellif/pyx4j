/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on 2008-08-01
 * @author vlads
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.i18n.shared.I18n;

/**
 *
 */
public class RegexValidator<E> extends AbstractComponentValidator<E> {

    private static final I18n i18n = I18n.get(RegexValidator.class);

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

    @Override
    public BasicValidationError isValid() {
        E value = getCComponent().getValue();
        return (getCComponent().isValueEmpty() && !getCComponent().isMandatory()) || ((value != null) && (value.toString().matches(regex))) ? null
                : new BasicValidationError(getCComponent(), getValidationMessage());
    }

    private String getValidationMessage() {
        if (validationMessage == null) {
            return i18n.tr("This field should have the following format") + ":" + regex;
        } else {
            return validationMessage;
        }
    }
}
