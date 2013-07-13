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
package com.propertyvista.portal.web.client.ui.signup;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;

public class SignUpViewImpl extends Composite implements SignUpView {

    private static I18n i18n = I18n.get(SignUpForm.class);

    private SignUpForm signupform;

    private SignUpPresenter presenter;

    private Anchor termsAndConditionsAnchor;

    public SignUpViewImpl() {
        FlowPanel viewPanel = new FlowPanel();
        bindRegistrationPanel(viewPanel);
        bindGreetingPanel(viewPanel);
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
    public void setPresenter(SignUpPresenter presenter) {
        this.presenter = presenter;
        this.termsAndConditionsAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, presenter.getPortalTermsPlace()));
    }

    @Override
    public void showError(String message) {
        MessageDialog.error(i18n.tr("Registration Error"), message);
    }

    private void bindRegistrationPanel(FlowPanel layout) {
        layout.add(new HTML(i18n.tr("Create your account")));

        signupform = new SignUpForm();
        signupform.initContent();
        layout.add(signupform);

        HTMLPanel loginTermsLinkPanel = new HTMLPanel(SignUpResources.INSTANCE.signUpViewTermsAgreementText().getText());
        termsAndConditionsAnchor = new Anchor(i18n.tr("RESIDENT PORTAL TERMS AND CONDITIONS"));
//        termsAndConditionsAnchor.setStylePrimaryName(DefaultWidgetsTheme.StyleName.Anchor.name());
        termsAndConditionsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showVistaTerms();
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });
        loginTermsLinkPanel.addAndReplaceElement(termsAndConditionsAnchor, SignUpResources.TERMS_AND_AGREEMENTS_ANCHOR_TAG);
        layout.add(loginTermsLinkPanel);

        SimplePanel buttonHolder = new SimplePanel();
        buttonHolder.getElement().getStyle().setTextAlign(TextAlign.RIGHT); // TODO should it be in the THEME? add style dependant?

        Button registerButton = new Button(i18n.tr("Register"), new Command() {
            @Override
            public void execute() {
                register();
            }
        });
        buttonHolder.setWidget(registerButton);
        layout.add(buttonHolder);
    }

    @Override
    public void showValidationError(EntityValidationException caught) {
        signupform.setEntityValidationError(caught);
    }

    private void bindGreetingPanel(FlowPanel layout) {
        HTML greetingCaption = new HTML(i18n.tr("Why choose Us?"));
        layout.add(greetingCaption);

        FlowPanel signUpGreetingPanel = new FlowPanel();
        HTML row1 = new HTML(i18n.tr("Place To Call Home..."));
        row1.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());

        signUpGreetingPanel.add(row1);

        HTML row2 = new HTML(i18n.tr("...is just a click away!"));
        row2.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        signUpGreetingPanel.add(row2);

        layout.add(signUpGreetingPanel);
    }

    private void register() {
        signupform.setEntityValidationError(null);
        signupform.revalidate();
        signupform.setUnconditionalValidationErrorRendering(true);
        if (signupform.isValid()) {
            presenter.register(signupform.getValue());
        }
    }

}
