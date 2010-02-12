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
 * Created on Jan 26, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

/**
 * This class Injects reCAPTCHA Client API code.
 * 
 * @see http://recaptcha.net/apidocs/captcha/ for more information.
 * 
 * @author Victora Corda
 * 
 */
public class CaptchaComposite extends Composite {

    private static Logger log = LoggerFactory.getLogger(CaptchaComposite.class);

    private final Grid grid = new Grid(1, 1);

    private static final String DIV_NAME = "recaptcha_div";

    @SuppressWarnings("unused")
    private static String publicKey;

    private boolean created = false;

    private static boolean alreadyInjected = false;

    /** there are no callback in ga.js, so we use timer */
    private static boolean loaded = false;

    private static Timer loadTimer;

    private static int timeoutCountdown = 30;

    public CaptchaComposite() {
        this.initWidget(grid);
        Label dummyLable = new Label();
        grid.setWidget(0, 0, dummyLable);
        Element divElement = dummyLable.getElement();
        divElement.setAttribute("id", DIV_NAME);
    }

    public static void setPublicKey(String publicKey) {
        CaptchaComposite.publicKey = publicKey;
    }

    public native void setFocus()
    /*-{
        return $wnd.Recaptcha.focus_response_field();
    }-*/;

    public native String getValueResponse()
    /*-{
        return $wnd.Recaptcha.get_response();
    }-*/;

    public native String getValueChallenge()
    /*-{
        return $wnd.Recaptcha.get_challenge();
    }-*/;

    private void createChallenge() {
        if (!loaded) {
            if (load()) {
                return;
            }
        }
        createChallengeImpl();
        created = true;
    }

    private native void createChallengeImpl()
    /*-{
        $wnd.Recaptcha.create(
        @com.pyx4j.widgets.client.CaptchaComposite::publicKey,
        @com.pyx4j.widgets.client.CaptchaComposite::DIV_NAME, 
        { theme: "white", callback: $wnd.Recaptcha.focus_response_field} );
    }-*/;

    private native void destroyCaptcha()
    /*-{
        $wnd.Recaptcha.destroy();
    }-*/;

    public void createNewChallenge() {
        if (isVisible() && created) {
            createNewChallengeImpl();
        }
    }

    public native void createNewChallengeImpl()
    /*-{
        $wnd.Recaptcha.reload();
    }-*/;

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && !created) {
            createChallenge();
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (isVisible()) {
            createChallenge();
        }
    }

    @Override
    protected void onUnload() {
        super.onLoad();
        if (created) {
            destroyCaptcha();
        }
    }

    private boolean load() {
        // Allow for "Standard Setup" in html page
        if (isInstalled()) {
            loaded = true;
        } else {
            if (!alreadyInjected) {
                alreadyInjected = true;
                injectJS();
                loadTimer = new Timer() {
                    @Override
                    public void run() {
                        if (isInstalled()) {
                            loaded = true;
                            loadTimer.cancel();
                            loadTimer = null;
                            log.debug("reCAPTCHA loaded");
                            createChallengeImpl();
                            created = true;
                        } else {
                            timeoutCountdown--;
                            if (timeoutCountdown == 0) {
                                loadTimer.cancel();
                                loadTimer = null;
                                log.error("reCAPTCHA load timeout");
                            }
                        }
                    }
                };
                loadTimer.scheduleRepeating(500);
            }
        }
        return !loaded;
    }

    private static void injectJS() {
        String protocolPrefix = Window.Location.getProtocol().equals("https:") ? "https://api-secure" : "http://api";
        Document doc = Document.get();
        ScriptElement script = doc.createScriptElement();
        script.setSrc(protocolPrefix + ".recaptcha.net/js/recaptcha_ajax.js");
        script.setType("text/javascript");
        doc.getBody().appendChild(script);
    }

    private static final native boolean isInstalled()
    /*-{
        return typeof $wnd.Recaptcha != "undefined";
    }-*/;
}
