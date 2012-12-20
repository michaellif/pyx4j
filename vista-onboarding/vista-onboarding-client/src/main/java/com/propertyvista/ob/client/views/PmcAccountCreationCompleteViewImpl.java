/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.ob.client.themes.OnboardingStyles;

public class PmcAccountCreationCompleteViewImpl extends Composite implements PmcAccountCreationCompleteView {

    public enum Styles {
        PmcAccountCreationCompleteLabel, PmcAccountCreationCompleteAnchor;
    }

    public static class PmcSiteRedirectPanel extends Composite {

        private final Anchor redirectToCrmSite;

        private final Label completionCongratulationsMessage;

        public PmcSiteRedirectPanel() {
            FlowPanel panel = new FlowPanel();
            completionCongratulationsMessage = new Label();
            completionCongratulationsMessage.addStyleName(Styles.PmcAccountCreationCompleteLabel.name());
            completionCongratulationsMessage.setText(i18n.tr("Congratulations! Your PMC Account is Ready!"));
            panel.add(completionCongratulationsMessage);

            SimplePanel redirectPanel = new SimplePanel();
            redirectPanel.addStyleName(Styles.PmcAccountCreationCompleteAnchor.name());
            redirectToCrmSite = new Anchor(i18n.tr("Go to your CRM!"));
            redirectPanel.setWidget(redirectToCrmSite);
            panel.add(redirectPanel);

            initWidget(panel);
        }

        public void setCrmSiteUrl(String crmSiteUrl) {
            completionCongratulationsMessage.setVisible(crmSiteUrl != null);
            redirectToCrmSite.setVisible(crmSiteUrl != null);
            redirectToCrmSite.setHref(crmSiteUrl != null ? crmSiteUrl : "");
        }

    }

    private static final I18n i18n = I18n.get(PmcAccountCreationCompleteViewImpl.class);

    private final PmcSiteRedirectPanel completionPanel;

    public PmcAccountCreationCompleteViewImpl() {
        this.completionPanel = new PmcSiteRedirectPanel();
        this.completionPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        this.completionPanel.getElement().getStyle().setProperty("marginRight", "auto");
        this.completionPanel.getElement().getStyle().setProperty("width", "300px");

        FlowPanel panel = new FlowPanel();
        panel.addStyleName(OnboardingStyles.VistaObView.name());
        panel.add(completionPanel);
        initWidget(panel);
    }

    @Override
    public void setCrmSiteUrl(String crimeSiteUrl) {
        this.completionPanel.setCrmSiteUrl(crimeSiteUrl);
    }

    @Override
    public IMemento getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeState(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void restoreState() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showVisor(IsWidget widget, String caption) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }

}
