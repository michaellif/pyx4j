/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Apr 20, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.validators.password;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule.PasswordStrengthVerdict;
import com.pyx4j.i18n.shared.I18n;

public class PasswordStrengthValueValidator implements EditableValueValidator<String> {

    private static I18n i18n = I18n.get(PasswordStrengthValueValidator.class);

    private final PasswordStrengthRule rule;

    private final Collection<PasswordStrengthVerdict> acceptVerdict;

    public PasswordStrengthValueValidator(PasswordStrengthRule rule) {
        this(rule, EnumSet.of(PasswordStrengthVerdict.Fair, PasswordStrengthVerdict.Good, PasswordStrengthVerdict.Strong));
    }

    public PasswordStrengthValueValidator(PasswordStrengthRule rule, Collection<PasswordStrengthVerdict> acceptVerdict) {
        this.rule = rule;
        this.acceptVerdict = acceptVerdict;
    }

    @Override
    public ValidationError isValid(CComponent<String> component, String value) {
        PasswordStrengthVerdict verdict = rule.getPasswordVerdict(value);
        if (acceptVerdict.contains(verdict)) {
            return null;
        } else {
            return new ValidationError(component, i18n.tr("Password is {0}", verdict));
        }
    }

}
