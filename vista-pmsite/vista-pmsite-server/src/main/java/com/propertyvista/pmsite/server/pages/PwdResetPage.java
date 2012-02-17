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
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;

public final class PwdResetPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(PwdResetPage.class);

    private String email;

    private final Pair<String, String> captchaResponse;

    public PwdResetPage() {
        this(null);
    }

    public PwdResetPage(final PageParameters parameters) {
        super(parameters);

        add(new FeedbackPanel("feedback"));

        PasswordRetrievalRequest request = EntityFactory.create(PasswordRetrievalRequest.class);
        CompoundIEntityModel<PasswordRetrievalRequest> model = new CompoundIEntityModel<PasswordRetrievalRequest>(request);
        add(new ForgotPwdForm("pwdResetForm", model));

        IRequestParameters params = getRequest().getRequestParameters();
        captchaResponse = new Pair<String, String>(params.getParameterValue("recaptcha_challenge_field").toString(), params.getParameterValue(
                "recaptcha_response_field").toString());
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Password Reset");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "pwdreset" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }

    public final class ForgotPwdForm extends StatelessForm<IPojo<PasswordRetrievalRequest>> {

        private static final long serialVersionUID = 1L;

        public ForgotPwdForm(final String id, final CompoundIEntityModel<PasswordRetrievalRequest> model) {
            super(id, model);

            add(new TextField<String>("email", model.bind(model.proto().email())));
            // captcha
            String pubKey = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey();
            ReCaptcha c = ReCaptchaFactory.newReCaptcha(pubKey, "", false);
            add(new Label("captcha", c.createRecaptchaHtml(null, null)).setEscapeModelStrings(false));

            add(new Button("submit").add(AttributeModifier.replace("value", i18n.tr("Submmit"))));
        }

        @Override
        public final void onSubmit() {
            // call reset password service
            PasswordRetrievalRequest request = getModelObject().getEntityValue();
            request.captcha().setValue(captchaResponse);

            LocalService.create(PortalAuthenticationService.class).requestPasswordReset(new AsyncCallback<VoidSerializable>() {
                @Override
                public void onSuccess(VoidSerializable result) {
                    // redirect to login with success parameter
                    throw new RestartResponseException(SignInPage.class, new PageParameters().add(SignInPage.SigninOnResetParam, ""));
                }

                @Override
                public void onFailure(Throwable caught) {
                    // show error message
                    error(caught.getMessage());
                }
            }, request);
        }
    }
}