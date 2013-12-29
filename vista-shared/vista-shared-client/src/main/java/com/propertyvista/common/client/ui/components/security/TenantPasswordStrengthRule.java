/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.core.shared.GWT;

import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule;
import com.pyx4j.i18n.shared.I18n;

public class TenantPasswordStrengthRule implements PasswordStrengthRule, HasDescription {

    private static final I18n i18n = I18n.get(TenantPasswordStrengthRule.class);

    private final String name;

    private final String email;

    public TenantPasswordStrengthRule(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public PasswordStrengthVerdict getPasswordVerdict(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        String nPassword = password.toLowerCase();
        if (nPassword.length() < 6) {
            return PasswordStrengthVerdict.TooShort;
        }
        if (name != null && nPassword.contains(name.toLowerCase())) {
            return PasswordStrengthVerdict.Weak;
        }
        if (email != null && nPassword.contains(email.split("@")[0].toLowerCase())) {
            return PasswordStrengthVerdict.Weak;
        }

        boolean hasDigit = GWT.isProdMode() ? nPassword.matches(".*\\d.*") : nPassword.matches(".*\\p{Digit}.*");
        boolean hasPunctuation = GWT.isProdMode() ? nPassword.matches(".*[`~!@#$%^&*()_\\-=+<>/?\\\\|{}\\[\\]].*") : nPassword.matches(".*\\p{Punct}.*");
        boolean hasLetters = nPassword.matches(".*[A-Za-z].*");

        if (!((hasDigit | hasPunctuation) & hasLetters)) {
            return PasswordStrengthVerdict.Weak;
        }

        if (password.length() < 6 && password.length() > 8) {
            return PasswordStrengthVerdict.Fair;
        }

        PasswordStrengthVerdict verdict = PasswordStrengthVerdict.Good;

        if (password.length() >= 15) {
            verdict = PasswordStrengthVerdict.Strong;
        }
        return verdict;
    }

    @Override
    public String getDescription() {
        return i18n.tr(//@formatter:off
                "Password Guidelines:\n" +
                "(1) Use 6 to 20 characters.\n" +
                "(2) Don't use your name or email address.\n" +
                "(3) It should consist of letters and at least one digit or symbol.\n" +
                "(4) Make your password hard to guess."
        );//@formatter:on
    }
}
