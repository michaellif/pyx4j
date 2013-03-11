/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.field.client.ui.login;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.components.LoginViewLayoutPanel;
import com.propertyvista.field.client.ui.decorators.WatermarkDecoratorBuilder;

public class LoginViewImpl extends Composite implements LoginView {

    private static final I18n i18n = I18n.get(LoginViewImpl.class);

    public interface LoginHtmlTemplates extends SafeHtmlTemplates {

        public static LoginHtmlTemplates TEMPLATES = GWT.create(LoginHtmlTemplates.class);

        @Template("<div class=\"{2}\"><span class=\"{4}\">{0}</span><span class=\"{3}\">&nbsp;{1}</span></div>")
        SafeHtml landingCaption(String emph, String normal, String style, String textStlye, String emphTextStyle);

        @Template("<div class=\"{0}\"><div style=\"width:0px; height:100%; border-width:1px; border-style:inset;\"/></div>")
        SafeHtml orLineSeparator(String style);

    }

    private class LoginForm extends CEntityDecoratableForm<AuthenticationRequest> {

        private CCaptcha captchaField;

        public LoginForm() {
            super(AuthenticationRequest.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();

            CTextField emailField = (CTextField) inject(proto().email(), new CTextField());
            contentPanel.add(center(new WatermarkDecoratorBuilder<CTextField>(emailField).watermark(i18n.tr("Email")).build()));
            setMandatoryValidationMessage(emailField, i18n.tr("Enter your email address"));

            CPasswordTextField passwordField = (CPasswordTextField) inject(proto().password(), new CPasswordTextField());
            contentPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>(passwordField).watermark(i18n.tr("Password")).build()));
            setMandatoryValidationMessage(passwordField, i18n.tr("Enter your password"));

            captchaField = (CCaptcha) inject(proto().captcha());
            contentPanel
                    .add(center((new DecoratorBuilder(captchaField).customLabel("").labelWidth(0).useLabelSemicolon(false).mandatoryMarker(false).build())));
            setEnableCaptcha(false);

            return contentPanel;
        }

        public final void setEnableCaptcha(boolean isEnabled) {
            captchaField.setVisible(isEnabled);
            if (isEnabled) {
                captchaField.createNewChallenge();
            }

        }

        private Widget center(Widget w) {
            w.addStyleName(FieldTheme.StyleName.LoginInputField.name());
            return w;
        }

        @Deprecated
        // TODO this is workaround to override default validation message(just 'setMandatoryValidationMessage()' is not enough)        
        private <C extends CTextFieldBase<?, ?>> void setMandatoryValidationMessage(C c, final String message) {
            c.setMandatory(false);
            c.addValueValidator(new EditableValueValidator<Object>() {
                @Override
                public ValidationError isValid(CComponent<Object, ?> component, Object value) {
                    if (value == null || ((value instanceof String) && CommonsStringUtils.isEmpty((String) value))) {
                        return new ValidationError(component, message);
                    } else {
                        return null;
                    }
                }
            });
        }
    }

    private LoginView.Presenter presenter;

    private LoginForm loginForm;

    private Button loginButton;

    public LoginViewImpl() {
        LoginViewLayoutPanel viewPanel = new LoginViewLayoutPanel();
        // attach handler to invoke login via ENTER key
        viewPanel.addAttachHandler(new Handler() {

            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)) {
                                onLogin();
                            }
                        }
                    });
                } else {
                    handlerRegistration.removeHandler();
                }
            }

        });

        bindLoginWidgets(viewPanel);
        initWidget(viewPanel);
    }

    private void bindLoginWidgets(LoginViewLayoutPanel panel) {
        panel.getHeader().add(makeCaption(i18n.tr("Welcome."), i18n.tr("Please Login")));

        loginForm = new LoginForm();
        loginForm.initContent();
        panel.getContent().add(loginForm);

        loginButton = new Button(i18n.tr("LOGIN"), new Command() {
            @Override
            public void execute() {
                onLogin();
            }

        });
        loginButton.addStyleName(FieldTheme.StyleName.LoginButton.name());
        SimplePanel loginButtonHolder = new SimplePanel();
        loginButtonHolder.setStyleName(FieldTheme.StyleName.LoginButtonHolder.name());
        loginButtonHolder.setWidget(loginButton);
        panel.getFooter().add(loginButtonHolder);

        SimplePanel resetPasswordAnchorHolder = new SimplePanel();
        resetPasswordAnchorHolder.setWidth("100%");
        resetPasswordAnchorHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        Anchor resetPassword = new Anchor(i18n.tr("forgot your password?"));
        resetPassword.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onResetPassword();
            }
        });

        resetPasswordAnchorHolder.add(resetPassword);
        panel.getFooter().add(resetPasswordAnchorHolder);
    }

    @Override
    public void setPresenter(com.propertyvista.common.client.ui.components.login.LoginView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void enableHumanVerification() {
        loginForm.setEnableCaptcha(true);
    }

    @Override
    public void reset(String userId, boolean rememberUserId) {
        loginForm.setVisited(false);
        loginForm.populateNew();
        if (userId != null) {
            loginForm.get(loginForm.proto().email()).setValue(userId);
        }
        //loginForm.get(loginForm.proto().rememberID()).setValue(rememberUserId);
        loginForm.setEnableCaptcha(false);
    }

    @Override
    public void setWallMessage(SystemWallMessage systemWallMessage) {
        // implement SetWallMessage
        // VladS tells that it the message that says our system is on maintenance, so probably it means the login and singup should be disabled hidden        
    }

    private void onLogin() {
        loginForm.revalidate();
        loginForm.setUnconditionalValidationErrorRendering(true);
        if (loginForm.isValid()) {
            presenter.login(loginForm.getValue());
        }
    }

    private void onResetPassword() {
        presenter.gotoResetPassword();
    }

    private HTML makeCaption(String emph, String normal) {
        return new HTML(LoginHtmlTemplates.TEMPLATES.landingCaption(//@formatter:off
                emph,
                normal,
                FieldTheme.StyleName.LoginCaption.name(),
                FieldTheme.StyleName.LoginCaptionText.name(),
                FieldTheme.StyleName.LoginCaptionTextEmph.name())
        );//@formatter:on
    }

    @Override
    public void setDevLogin(List<? extends DevLoginCredentials> devLoginData, String appModeName) {
        // TODO Auto-generated method stub

    }

}
