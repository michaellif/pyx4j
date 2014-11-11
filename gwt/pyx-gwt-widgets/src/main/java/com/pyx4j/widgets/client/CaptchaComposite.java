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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.AjaxJSLoader;
import com.pyx4j.i18n.shared.I18n;

/**
 * This class Injects reCAPTCHA Client API code.
 * 
 * @see <a href="http://code.google.com/apis/recaptcha/intro.html">for more information</a>
 * @see <a href="http://code.google.com/apis/recaptcha/docs/customization.html">Customization</a>
 * 
 */
public class CaptchaComposite extends SimplePanel implements IFocusWidget, IWatermarkWidget {

    private static Logger log = LoggerFactory.getLogger(CaptchaComposite.class);

    private static final I18n i18n = I18n.get(CaptchaComposite.class);

    private static int instanceId = 0;

    private final String divName;

    private static String publicKey;

    private boolean created = false;

    private final VerticalPanel divHolder;

    private final SimplePanel recaptchaImage;

    private final StringBox response;

    private final StringBox responseInternal;

    private final Image toText;

    private final Image toAudio;

    private static String javaScriptURL = "www.google.com/recaptcha/api/js/recaptcha_ajax.js";

    public CaptchaComposite() {
        instanceId++;
        divName = "recaptcha_div" + String.valueOf(instanceId);

        divHolder = new VerticalPanel();
        divHolder.getElement().getStyle().setWidth(100, Unit.PCT);
        divHolder.getElement().setId(divName);
        //divHolder.getElement().getStyle().setDisplay(Display.NONE);
        recaptchaImage = new SimplePanel();
        recaptchaImage.getElement().getStyle().setWidth(250, Unit.PX);
        recaptchaImage.getElement().getStyle().setHeight(57, Unit.PX);
        recaptchaImage.getElement().getStyle().setMarginRight(5, Unit.PX);

        HorizontalPanel images = new HorizontalPanel();
        images.add(recaptchaImage);
        images.setCellWidth(recaptchaImage, "100%");

        VerticalPanel buttons = new VerticalPanel();
        images.add(buttons);
        Image refresh = new Image(ImageFactory.getImages().recaptchaRefresh());
        buttons.add(refresh);
        toAudio = new Image(ImageFactory.getImages().recaptchaAudio());
        buttons.add(toAudio);
        toText = new Image(ImageFactory.getImages().recaptchaText());
        buttons.add(toText);
        toText.setVisible(false);
        Image help = new Image(ImageFactory.getImages().recaptchaHelp());
        buttons.add(help);

        refresh.setTitle(i18n.tr("Get A New Challenge"));
        toAudio.setTitle(i18n.tr("Get An Audio Challenge"));
        toText.setTitle(i18n.tr("Get A Visual Challenge"));
        help.setTitle(i18n.tr("Help"));

        toText.setStyleName("recaptcha_only_if_audio");
        toAudio.setStyleName("recaptcha_only_if_image");

        refresh.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!created) {
                    return;
                }
                createNewChallengeImpl();
            }
        });

        help.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!created) {
                    return;
                }
                showhelp();
            }
        });

        toAudio.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!created) {
                    return;
                }
                switchToAudio();
                toAudio.setVisible(false);
                toText.setVisible(true);
            }
        });

        toText.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!created) {
                    return;
                }
                switchToImage();
                toAudio.setVisible(true);
                toText.setVisible(false);
            }
        });

        divHolder.add(images);
        response = new StringBox();
        response.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                responseInternal.setValue(response.getValue());
            }
        });
        response.getElement().getStyle().setProperty("padding", "2px 5px");

        response.setNameProperty("recaptcha_response_field");
        response.getElement().getStyle().setMarginTop(5, Unit.PX);
        response.getElement().getStyle().setWidth(100, Unit.PCT);
        divHolder.add(response);

        responseInternal = new StringBox();
        responseInternal.setVisible(false);
        divHolder.add(responseInternal);

        this.add(divHolder);
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // Do Nothing.
    }

    private void assigneRecaptchaId() {
        recaptchaImage.getElement().setId("recaptcha_image");
        responseInternal.getElement().setId("recaptcha_response_field");
    }

    private void assigneNutralId() {
        recaptchaImage.getElement().setId(divName + "_image");
        responseInternal.getElement().setId(divName + "_response_field");
    }

    /**
     * Use https://www.google.com/recaptcha/admin/create to create your key
     * 
     * @param publicKey
     */
    public static void setPublicKey(String publicKey) {
        log.debug("reCAPTCHA key set [{}]", publicKey);
        CaptchaComposite.publicKey = publicKey;
    }

    public static boolean isPublicKeySet() {
        return CommonsStringUtils.isStringSet(publicKey);
    }

    @Override
    public void setFocus(boolean focused) {
        response.setFocus(focused);
    }

    public native void setFocus()
    /*-{
		$wnd.Recaptcha.focus_response_field();
    }-*/;

    public String getValueResponse() {
        return response.getValue();
    }

    public HandlerRegistration addResponseValueChangeHandler(ValueChangeHandler<String> handler) {
        return response.addValueChangeHandler(handler);
    }

    public native String getValueChallenge()
    /*-{
		return $wnd.Recaptcha.get_challenge();
    }-*/;

    private native void switchToImage()
    /*-{
		$wnd.Recaptcha.switch_type('image');
    }-*/;

    private native void switchToAudio()
    /*-{
		$wnd.Recaptcha.switch_type('audio');
    }-*/;

    private native void showhelp()
    /*-{
		$wnd.Recaptcha.showhelp();
    }-*/;

    /**
     * Old url looks different. "(api-secure|api).recaptcha.net/js/recaptcha_ajax.js"
     */
    public static void setJavaScriptURL(String url) {
        javaScriptURL = url;
    }

    private void createChallenge() {
        assert (publicKey != null) : "Captcha public key was not set";

        if (ApplicationMode.offlineDevelopment) {
            assigneRecaptchaId();
            return;
        }
        AjaxJSLoader.load(javaScriptURL, new AjaxJSLoader.IsJSLoaded() {

            @Override
            public native boolean isLoaded() /*-{
		return typeof $wnd.Recaptcha != "undefined";
    }-*/;

        }, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UserRuntimeException(i18n.tr("Captcha Service unavailable"));
            }

            @Override
            public void onSuccess(Void result) {
                assigneRecaptchaId();
                toAudio.setVisible(true);
                toText.setVisible(false);
                log.debug("createChallenge with key set [{}]", publicKey);
                createChallengeImpl();
                created = true;
            }

        });
    }

    private native void createChallengeImpl()
    /*-{
		$wnd.Recaptcha
				.create(
						@com.pyx4j.widgets.client.CaptchaComposite::publicKey,
						this.@com.pyx4j.widgets.client.CaptchaComposite::divName,
						{
							theme : "custom",
							custom_theme_widget : this.@com.pyx4j.widgets.client.CaptchaComposite::divName
						});
    }-*/;

    private native void destroyCaptcha()
    /*-{
		$wnd.Recaptcha.destroy();
    }-*/;

    public void createNewChallenge() {
        response.setValue(null);
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
        if (isVisible()) {
            createChallenge();
        }
    }

    @Override
    protected void onUnload() {
        if (created) {
            try {
                destroyCaptcha();
            } catch (Throwable ignoreIE8) {
            }
            assigneNutralId();
            created = false;
        }
    }

    @Override
    public void setWatermark(String text) {
        response.setWatermark(text);
    };

    @Override
    public String getWatermark() {
        return response.getWatermark();
    };

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
