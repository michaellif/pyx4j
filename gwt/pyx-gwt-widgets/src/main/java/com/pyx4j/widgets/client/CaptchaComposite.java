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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

/**
 * Application index.html require:
 * 
 * <script type="text/javascript"
 * src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
 * 
 * @see http://recaptcha.net/apidocs/captcha/ for more information.
 * 
 * @author Victora Corda
 * 
 */
public class CaptchaComposite extends Composite {

    private final Grid grid = new Grid(1, 1);

    private static final String DIV_NAME = "recaptcha_div";

    private static String publicKey;

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

    public native String getValueResponse()
    /*-{
        return $wnd.Recaptcha.get_response();
    }-*/;

    public native String getValueChallenge()
    /*-{
        return $wnd.Recaptcha.get_challenge();
    }-*/;

    private native void createChallenge()
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

    public native void createNewChallenge()
    /*-{
        $wnd.Recaptcha.reload();
    }-*/;

    @Override
    protected void onLoad() {
        super.onLoad();
        createChallenge();
    }

    @Override
    protected void onUnload() {
        super.onLoad();
        destroyCaptcha();
    }

}
