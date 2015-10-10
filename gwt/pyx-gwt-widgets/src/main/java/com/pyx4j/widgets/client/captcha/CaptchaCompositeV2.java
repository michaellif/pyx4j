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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.gwt.commons.AjaxJSLoader;
import com.pyx4j.i18n.shared.I18n;

/**
 * This class Injects reCAPTCHA Client API V2 code.
 *
 * @see <a href="https://developers.google.com/recaptcha/docs/display">for more information</a>
 *
 */
public class CaptchaCompositeV2 extends AbstractCaptchaComposite implements HasValueChangeHandlers<String> {

    private static Logger log = LoggerFactory.getLogger(CaptchaCompositeV2.class);

    private static final I18n i18n = I18n.get(CaptchaCompositeV2.class);

    private static int instanceId = 0;

    private String divName;

    private boolean created = false;

    private int captchaWidgetId;

    private SimplePanel divHolder;

    private int tabindex;

    private static String javaScriptURL = "www.google.com/recaptcha/api.js";

    public CaptchaCompositeV2() {
        resetPlaceholder();
    }

    private void resetPlaceholder() {
        this.clear();
        divName = "recaptcha_div" + String.valueOf(instanceId++);
        divHolder = new SimplePanel();
        divHolder.getElement().getStyle().setWidth(100, Unit.PCT);
        divHolder.getElement().getStyle().setHeight(74, Unit.PX);
        divHolder.getElement().setId(divName);
        this.add(divHolder);
    }

    private void createChallenge() {
        assert (publicKey != null) : "Captcha public key was not set";

        AjaxJSLoader.load(javaScriptURL, new AjaxJSLoader.IsJSLoaded() {

            @Override
            public native boolean isLoaded()
            /*-{
            return typeof $wnd.grecaptcha != "undefined";
            }-*/;

        }, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UserRuntimeException(i18n.tr("Captcha Service unavailable"));
            }

            @Override
            public void onSuccess(Void result) {
                log.debug("createChallenge with key set [{}]", publicKey);
                if (created) {
                    createNewChallengeImpl();
                } else {
                    captchaWidgetId = createChallengeImpl();
                    created = true;
                }
            }

        });
    }

    private native int createChallengeImpl()
    /*-{
    	var thisComposite = this;
    	var verifyCallback = function(val) {
    		thisComposite.@com.pyx4j.widgets.client.captcha.CaptchaCompositeV2::successfulCAPTCHAcallback(Ljava/lang/String;)(val);
    	};
    
    	return $wnd.grecaptcha
    			.render(
    					this.@com.pyx4j.widgets.client.captcha.CaptchaCompositeV2::divName,
    					{
    						'sitekey' : @com.pyx4j.widgets.client.captcha.AbstractCaptchaComposite::publicKey,
    						'callback' : verifyCallback,
    						'theme' : 'light',
    						'size' : 'normal',
    						'tabindex' : this.@com.pyx4j.widgets.client.captcha.CaptchaCompositeV2::tabindex
    					});
    }-*/;

    private void successfulCAPTCHAcallback(String response) {
        ValueChangeEvent.fire(this, response);
    }

    @Override
    public int getTabIndex() {
        return tabindex;
    }

    @Override
    public void setTabIndex(int index) {
        tabindex = index;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && !created) {
            createChallenge();
        }
    }

    @Override
    protected void onLoad() {
        if (isVisible()) {
            createChallenge();
        }
    }

    @Override
    protected void onUnload() {
        created = false;
        resetPlaceholder();
    };

    @Override
    public void setFocus(boolean focused) {
    }

    @Override
    public void setWatermark(String watermark) {
    }

    @Override
    public String getWatermark() {
        return null;
    }

    @Override
    public String getValueResponse() {
        if (created && isVisible()) {
            return getValueResponseImpl();
        } else {
            return null;
        }
    }

    private native String getValueResponseImpl()
    /*-{
    	return $wnd.grecaptcha
    			.getResponse(this.@com.pyx4j.widgets.client.captcha.CaptchaCompositeV2::captchaWidgetId);
    }-*/;

    @Override
    public String getValueChallenge() {
        return "reCAPTCHA-v2";
    }

    @Override
    public void createNewChallenge() {
        if (isVisible() && created) {
            createNewChallengeImpl();
        }
    }

    public native void createNewChallengeImpl()
    /*-{
    	$wnd.grecaptcha
    			.reset(this.@com.pyx4j.widgets.client.captcha.CaptchaCompositeV2::captchaWidgetId);
    }-*/;

    @Override
    public HandlerRegistration addResponseValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addResponseValueChangeHandler(handler);
    }

}
