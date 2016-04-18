/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 10, 2015
 * @author vlads
 */
package com.pyx4j.widgets.client.captcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.IWatermarkWidget;

public abstract class AbstractCaptchaComposite extends SimplePanel implements IFocusWidget, IWatermarkWidget {

    private static Logger log = LoggerFactory.getLogger(AbstractCaptchaComposite.class);

    protected static String publicKey;

    /**
     * Use https://www.google.com/recaptcha/admin/create to create your key
     *
     * @param publicKey
     */
    public static void setPublicKey(String publicKey) {
        log.debug("reCAPTCHA key set [{}]", publicKey);
        AbstractCaptchaComposite.publicKey = publicKey;
    }

    public static boolean isPublicKeySet() {
        return CommonsStringUtils.isStringSet(publicKey);
    }

    public abstract String getValueResponse();

    public abstract String getValueChallenge();

    public abstract void createNewChallenge();

    public abstract HandlerRegistration addResponseValueChangeHandler(ValueChangeHandler<String> handler);

    @Override
    public void setDebugId(IDebugId debugId) {
        // Do Nothing.
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public int getTabIndex() {
        return 0;
    }

    @Override
    public void setAccessKey(char key) {
    }

    @Override
    public void setTabIndex(int index) {
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return null;
    }
}
