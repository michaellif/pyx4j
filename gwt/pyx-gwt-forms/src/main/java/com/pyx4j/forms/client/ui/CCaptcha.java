/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 29, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Pair;

public class CCaptcha extends CEditableComponent<Pair<String, String>, NativeCaptcha> {

    protected static I18n i18n = I18nFactory.getI18n(CCaptcha.class);

    public CCaptcha() {
        setMandatoryValidationMessage(i18n.tr("Captcha code is required."));
    }

    public void retrieveValue() {
        //TODO validate if that code is needed
        if (isWidgetCreated() && isVisible()) {
            setValue(asWidget().getNativeValue());
        }
    }

    @Override
    public boolean isValueEmpty() {
        if (isWidgetCreated() && isVisible()) {
            return CommonsStringUtils.isEmpty(asWidget().getValueResponse());
        } else {
            return true;
        }
    }

    @Override
    protected NativeCaptcha createWidget() {
        return new NativeCaptcha(this);
    }

    public void createNewChallenge() {
        if (isWidgetCreated()) {
            asWidget().createNewChallenge();
        }
    }

}
