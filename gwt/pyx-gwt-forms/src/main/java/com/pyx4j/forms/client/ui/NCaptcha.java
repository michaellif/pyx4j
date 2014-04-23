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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.Pair;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.WatermarkComponent;

public class NCaptcha extends NFocusField<Pair<String, String>, CaptchaComposite, CCaptcha, CaptchaComposite> implements
        INativeFocusField<Pair<String, String>>, WatermarkComponent {

    public static enum StyleDependent implements IStyleDependent {
        invalid
    }

    private final CaptchaComposite captchaComposite;

    public NCaptcha(final CCaptcha component) {
        super(component);

        captchaComposite = new CaptchaComposite();

        captchaComposite.addResponseValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                component.stopEditing();
            }
        });
    }

    @Override
    public void setWatermark(String text) {
        captchaComposite.setWatermark(text);
    }

    @Override
    public void setNativeValue(Pair<String, String> value) {
        if (value == null) {
            captchaComposite.createNewChallenge();
        }
    }

    @Override
    public Pair<String, String> getNativeValue() {
        if (ApplicationMode.offlineDevelopment) {
            return new Pair<String, String>("off", captchaComposite.getValueResponse());
        } else {
            return new Pair<String, String>(captchaComposite.getValueChallenge(), captchaComposite.getValueResponse());
        }
    }

    public String getValueResponse() {
        return captchaComposite.getValueResponse();
    }

    public void createNewChallenge() {
        captchaComposite.createNewChallenge();
    }

    @Override
    protected CaptchaComposite createEditor() {
        return captchaComposite;
    }

    @Override
    protected CaptchaComposite createViewer() {
        return captchaComposite;
    }

}
