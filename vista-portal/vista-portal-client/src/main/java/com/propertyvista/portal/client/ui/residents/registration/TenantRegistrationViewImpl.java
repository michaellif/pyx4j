/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.registration;

import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.client.themes.LandingPagesTheme;
import com.propertyvista.portal.client.ui.components.LandingViewLayoutPanel;
import com.propertyvista.portal.client.ui.components.LandingViewLayoutPanel.Side;
import com.propertyvista.portal.client.ui.residents.login.LandingViewImpl.LandingHtmlTemplates;
import com.propertyvista.portal.client.ui.residents.login.LoginAndSignUpResources;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationDTO;

public class TenantRegistrationViewImpl extends Composite implements TenantRegistrationView {

    private static I18n i18n = I18n.get(TenantRegistrationForm.class);

    private TenantRegistrationForm signupform;

    private Presenter presenter;

    private HTML greetingCaption;

    private HTML greetingTextHtml;

    public TenantRegistrationViewImpl() {
        LandingViewLayoutPanel viewPanel = new LandingViewLayoutPanel();
        bindResitrationPanel(viewPanel.getLeft());
        bindGreetingPanel(viewPanel.getRight());
        initWidget(viewPanel);
    }

    @Override
    public void populate(List<SelfRegistrationBuildingDTO> buildings) {
        signupform.setBuildingOptions(buildings);
        signupform.setVisited(false);
        signupform.populateNew();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGreeting(String caption, String textHtml) {
        this.greetingCaption.setHTML(formatCaption(caption, ""));
        this.greetingTextHtml.setHTML(textHtml);
    }

    @Override
    public SelfRegistrationDTO getValue() {
        return signupform.getValue();
    }

    @Override
    public void showError(String message) {
        MessageDialog.error(i18n.tr("Registration Error"), message);
    }

    private void bindResitrationPanel(Side layout) {
        layout.getHeader().add(new HTML(formatCaption(i18n.tr("Create your account"), "")));

        signupform = new TenantRegistrationForm();
        signupform.initContent();
        layout.getContent().add(signupform);

        HTMLPanel loginTermsLinkPanel = new HTMLPanel(LoginAndSignUpResources.INSTANCE.signUpViewTermsAgreementText().getText());
        Anchor termsAndConditions = new Anchor(i18n.tr("Terms and Conditions"), new Command() {
            @Override
            public void execute() {
                presenter.onShowVistaTerms();
            }
        });
        loginTermsLinkPanel.addAndReplaceElement(termsAndConditions, LoginAndSignUpResources.TERMS_AND_AGREEMENTS_ANCHOR_TAG);
        layout.getContent().add(loginTermsLinkPanel);

        SimplePanel buttonHolder = new SimplePanel();
        buttonHolder.addStyleName(LandingPagesTheme.StyleName.LandingButtonHolder.name());
        buttonHolder.getElement().getStyle().setTextAlign(TextAlign.RIGHT); // TODO should it be in the THEME? add style dependant?

        Button registerButton = new Button(i18n.tr("Register"), new Command() {
            @Override
            public void execute() {
                onRegister();
            }
        });
        registerButton.addStyleName(LandingPagesTheme.StyleName.LandingButton.name());
        buttonHolder.setWidget(registerButton);
        layout.getFooter().add(buttonHolder);

    }

    private void bindGreetingPanel(Side layout) {
        greetingCaption = new HTML();
        layout.getHeader().add(greetingCaption);

        greetingTextHtml = new HTML();
        greetingTextHtml.setStyleName(LandingPagesTheme.StyleName.LandingGreetingText.name());

        SimplePanel signUpGreetingPanel = new SimplePanel();
        signUpGreetingPanel.setStyleName(LandingPagesTheme.StyleName.LandingGreetingPanel.name());
        signUpGreetingPanel.setWidget(greetingTextHtml);

        layout.getContent().add(signUpGreetingPanel);
    }

    private SafeHtml formatCaption(String emph, String normal) {
        return LandingHtmlTemplates.TEMPLATES.landingCaption(//@formatter:off
                    emph,
                    normal,
                    LandingPagesTheme.StyleName.LandingCaption.name(),
                    LandingPagesTheme.StyleName.LandingCaptionText.name(),
                    LandingPagesTheme.StyleName.LandingCaptionTextEmph.name()
        );//@formatter:on        
    }

    private void onRegister() {
        signupform.revalidate();
        signupform.setUnconditionalValidationErrorRendering(true);
        if (signupform.isValid()) {
            TenantRegistrationViewImpl.this.presenter.onRegister();
        }
    }

}
