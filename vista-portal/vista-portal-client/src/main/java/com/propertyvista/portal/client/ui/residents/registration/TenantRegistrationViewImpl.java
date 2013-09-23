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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.client.themes.LandingPagesTheme;
import com.propertyvista.portal.client.ui.components.LandingViewLayoutPanel;
import com.propertyvista.portal.client.ui.components.LandingViewLayoutPanel.Side;
import com.propertyvista.portal.client.ui.residents.login.LandingViewImpl.LandingHtmlTemplates;
import com.propertyvista.portal.client.ui.residents.login.LoginAndSignUpResources;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;

public class TenantRegistrationViewImpl extends Composite implements TenantRegistrationView {

    private static I18n i18n = I18n.get(TenantRegistrationForm.class);

    private TenantRegistrationForm signupform;

    private Presenter presenter;

    private Anchor termsAndConditionsAnchor;

    public TenantRegistrationViewImpl() {
        LandingViewLayoutPanel viewPanel = new LandingViewLayoutPanel();
        bindResitrationPanel(viewPanel.getLeft());
        bindGreetingPanel(viewPanel.getRight());
        initWidget(viewPanel);
    }

    @Override
    public void populate(List<SelfRegistrationBuildingDTO> buildings) {
        signupform.setBuildingOptions(buildings);
        signupform.setUnconditionalValidationErrorRendering(false);
        signupform.setVisited(false);
        signupform.populateNew();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        this.termsAndConditionsAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, presenter.getPortalTermsPlace()));
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
        termsAndConditionsAnchor = new Anchor(i18n.tr("RESIDENT PORTAL TERMS AND CONDITIONS"));
//        termsAndConditionsAnchor.setStylePrimaryName(DefaultWidgetsTheme.StyleName.Anchor.name());
        termsAndConditionsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onShowVistaTerms();
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });
        loginTermsLinkPanel.addAndReplaceElement(termsAndConditionsAnchor, LoginAndSignUpResources.TERMS_AND_AGREEMENTS_ANCHOR_TAG);
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

    @Override
    public void showValidationError(EntityValidationException caught) {
        signupform.setEntityValidationError(caught);
    }

    private void bindGreetingPanel(Side layout) {
        HTML greetingCaption = new HTML();
        greetingCaption.setHTML(formatCaption(i18n.tr("Why choose Us?"), ""));
        layout.getHeader().add(greetingCaption);

        FlowPanel signUpGreetingPanel = new FlowPanel();
        signUpGreetingPanel.setStyleName(LandingPagesTheme.StyleName.LandingGreetingPanel.name());
        HTML row1 = new HTML(i18n.tr("Place To Call Home..."));
        row1.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        row1.addStyleName(LandingPagesTheme.StyleName.SignUpGreetingRow1.name());

        signUpGreetingPanel.add(row1);

        HTML row2 = new HTML(i18n.tr("...is just a click away!"));
        row2.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        row2.addStyleName(LandingPagesTheme.StyleName.SignUpGreetingRow2.name());
        signUpGreetingPanel.add(row2);

        SimplePanel imageHolder = new SimplePanel();
        imageHolder.setStyleName(LandingPagesTheme.StyleName.SignUpGreetingImageHolder.name());
        imageHolder.setWidget(new Image(LoginAndSignUpResources.INSTANCE.key()));
        signUpGreetingPanel.add(imageHolder);

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
        signupform.setEntityValidationError(null);
        signupform.revalidate();
        signupform.setUnconditionalValidationErrorRendering(true);
        if (signupform.isValid()) {
            TenantRegistrationViewImpl.this.presenter.onRegister();
        }
    }

}
