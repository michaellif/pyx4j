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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.validators.password.PasswordStrengthRule.PasswordStrengthVerdict;
import com.pyx4j.i18n.shared.I18n;

public class PasswordStrengthWidget extends SimplePanel {

    private static I18n i18n = I18n.get(PasswordStrengthWidget.class);

    private Label indicator;

    private PasswordStrengthRule rule;

    public PasswordStrengthWidget(PasswordStrengthRule rule) {
        this.rule = rule;
        setWidget(indicator = new Label());
        indicator.getElement().getStyle().setProperty("paddingLeft", "10px");
    }

    public void setValue(PasswordStrengthVerdict verdict) {
        if (verdict == null) {
            indicator.setText("");
        } else {
            indicator.setText(verdict.toString());
            String color = "red";
            switch (verdict) {
            case Invalid:
                color = "#CC0000";
                break;
            case TooShort:
                color = "#808080";
                break;
            case Weak:
                color = "#da5301";
                break;
            case Fair:
                color = "#ccbe00";
                break;
            case Good:
                color = "#1e91ce";
                break;
            case Strong:
                color = "#1e91ce";
                break;
            }
            indicator.getElement().getStyle().setColor(color);
        }
    }

    public void ratePassword(String password) {
        this.setValue(rule != null ? rule.getPasswordVerdict(password) : null);
    }

    public void setPasswordStrengthRule(PasswordStrengthRule rule) {
        this.rule = rule;
    }
}
