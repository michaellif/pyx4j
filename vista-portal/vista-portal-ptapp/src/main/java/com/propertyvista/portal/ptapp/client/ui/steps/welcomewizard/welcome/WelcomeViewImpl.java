/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.welcome;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.ptapp.client.resources.welcomewizard.WelcomeWizardResources;

public class WelcomeViewImpl implements WelcomeView {

    private final static I18n i18n = I18n.get(WelcomeViewImpl.class);

    private final FormFlexPanel panel;

    private Presenter presenter;

    public WelcomeViewImpl() {
        panel = new FormFlexPanel();

        panel.setWidget(0, 0, createTextPanel());
        panel.getFlexCellFormatter().setWidth(0, 0, "50%");
        panel.setWidget(0, 1, createStartPanel());
        panel.getFlexCellFormatter().setWidth(0, 1, "50%");

    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setPresenter(Presenter welcomeActivity) {
        this.presenter = welcomeActivity;
    }

    private IsWidget createTextPanel() {
        FlowPanel textPanel = new FlowPanel();
        textPanel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        textPanel.setWidth("100%");

        HTML youHaveBeenApproved = new HTML(WelcomeWizardResources.INSTANCE.youHaveBeenApproved().getText());
        youHaveBeenApproved.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        youHaveBeenApproved.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        youHaveBeenApproved.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.requirements().getURL() + ") no-repeat");
        textPanel.add(youHaveBeenApproved);

        HTML moveInGuidePurpose = new HTML(WelcomeWizardResources.INSTANCE.functionalityOfTheMoveInGuide().getText());
        moveInGuidePurpose.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        moveInGuidePurpose.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        moveInGuidePurpose.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.time().getURL() + ") no-repeat");
        textPanel.add(moveInGuidePurpose);

        HTML dontWorry = new HTML(PortalResources.INSTANCE.dontWorry().getText());
        dontWorry.getElement().getStyle().setPaddingLeft(95, Unit.PX);
        dontWorry.getElement().getStyle().setPaddingBottom(45, Unit.PX);

        dontWorry.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.dontWorry().getURL() + ") no-repeat");
        textPanel.add(dontWorry);
        return textPanel;
    }

    private IsWidget createStartPanel() {
        VerticalPanel startPanel = new VerticalPanel();
        startPanel.setWidth("100%");
        startPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        HTML letsGetStartedLabel = new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Let's Get Started!")).toSafeHtml().asString());
        letsGetStartedLabel.getElement().getStyle().setFontSize(40, Unit.PX);
        letsGetStartedLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        letsGetStartedLabel.getElement().getStyle().setPaddingBottom(20, Unit.PX);
        startPanel.add(letsGetStartedLabel);

        Button startButton = new Button(i18n.tr("START"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                WelcomeViewImpl.this.presenter.startTheWizard();
            }

        });

        startButton.getElement().getStyle().setFontSize(15, Unit.PX);
        startButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        startPanel.add(startButton);

        return startPanel;
    }
}
