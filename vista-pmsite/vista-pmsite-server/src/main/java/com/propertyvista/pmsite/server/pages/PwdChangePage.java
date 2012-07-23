/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.validation.EqualInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import templates.TemplateResources;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Pair;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.portal.rpc.portal.services.PasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;

@AuthorizeInstantiation({ Roles.USER, PMSiteSession.PasswordChangeRequiredRole })
public final class PwdChangePage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PwdChangePage.class);

    private static final I18n i18n = I18n.get(PwdChangePage.class);

    private String oldPassword;

    private String newPassword;

    private final Pair<String, String> captchaResponse;

    private boolean captchaRequired = false;

    private ChangePwdForm form;

    boolean pwdReset = PMSiteSession.get().getRoles().hasRole(PMSiteSession.PasswordChangeRequiredRole);

    public PwdChangePage() {
        this(null);
    }

    public PwdChangePage(final PageParameters parameters) {
        super(parameters);

        add(new FeedbackPanel("feedback"));

        CompoundPropertyModel<PwdChangePage> model = new CompoundPropertyModel<PwdChangePage>(this);
        add(form = new ChangePwdForm("pwdChangeForm", model));

        IRequestParameters params = getRequest().getRequestParameters();
        captchaResponse = new Pair<String, String>(params.getParameterValue("recaptcha_challenge_field").toString(), params.getParameterValue(
                "recaptcha_response_field").toString());
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Change Password");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "pwdchange.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }

    @Override
    protected void onBeforeRender() {
        // Captcha may be required after form.onSubmit() returns
        if (captchaRequired) {
            form.addCaptcha();
        }
        super.onBeforeRender();
    }

    public final class ChangePwdForm extends StatelessForm<PwdChangePage> {

        private static final long serialVersionUID = 1L;

        private final Label captcha = new Label("captcha", "");

        public ChangePwdForm(final String id, final CompoundPropertyModel<PwdChangePage> model) {
            super(id, model);

            WebMarkupContainer oldPwd = new WebMarkupContainer("oldPwdEntry");
            oldPwd.add(new PasswordTextField("oldPassword"));
            add(oldPwd.setVisible(!pwdReset));
            PasswordTextField newPasswd = new PasswordTextField("newPassword");
            add(newPasswd);
            PasswordTextField pwdConfirm = new PasswordTextField("pwdConfirm", new Model<String>(""));
            add(pwdConfirm);
            add(new EqualInputValidator(newPasswd, pwdConfirm));
            // captcha
            add(captcha.setVisible(false));

            add(new Button("submit").add(AttributeModifier.replace("value", i18n.tr("Submit"))));
        }

        public void addCaptcha() {
            String pubKey = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey();
            ReCaptcha c = ReCaptchaFactory.newReCaptcha(pubKey, "", false);
            captcha.setDefaultModelObject(c.createRecaptchaHtml(null, null)).setEscapeModelStrings(false).setVisible(true);
        }

        @Override
        public final void onSubmit() {
            PasswordChangeRequest request = EntityFactory.create(PasswordChangeRequest.class);
            request.currentPassword().setValue(getOldPassword());
            request.newPassword().setValue(getNewPassword());
            request.captcha().setValue(captchaResponse);
            if (pwdReset) {
                LocalService.create(PortalPasswordResetService.class).resetPassword(new AsyncCallback<AuthenticationResponse>() {
                    @Override
                    public void onSuccess(AuthenticationResponse result) {
                        // success - restart wicket session and redirect to target
                        PMSiteSession.get().signIn(null, null);
                        redirectToTarget();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        onCallbackFailure(caught);
                    }
                }, request);
            } else {
                LocalService.create(PasswordChangeUserService.class).changePassword(new AsyncCallback<VoidSerializable>() {
                    @Override
                    public void onSuccess(VoidSerializable result) {
                        // success - redirect to target
                        redirectToTarget();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        onCallbackFailure(caught);
                    }
                }, request);
            }
        }
    }

    private void onCallbackFailure(Throwable caught) {
        if (caught instanceof ChallengeVerificationRequired) {
            captchaRequired = true;
        } else if (caught instanceof UserRuntimeException) {
            error(caught.getMessage());
        } else if (ApplicationMode.isDevelopment()) {
            error(caught.getMessage());
        } else {
            error(i18n.tr("Action failed. Please try again later."));
        }
    }

    private void redirectToTarget() {
        // Success - redirect to target page
        String targetUrl = getPage().getPageParameters().get(PMSiteApplication.ParamNameTarget).toString();
        if (targetUrl == null || targetUrl.length() == 0) {
            setResponsePage(ResidentsPage.class);
        } else {
            // get path relative to context root
            String toRoot = PMSiteApplication.get().getPathToRoot();
            throw new RedirectToUrlException(toRoot + targetUrl);
        }
    }
}