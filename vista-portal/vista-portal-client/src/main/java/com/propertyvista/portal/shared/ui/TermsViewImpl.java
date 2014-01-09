/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.NotificationPageView.NotificationPagePresenter;
import com.propertyvista.portal.shared.ui.NotificationPageViewImpl.NotificationGadget;
import com.propertyvista.portal.shared.ui.NotificationPageViewImpl.NotificationGadget.NotificationToolbar;

public class TermsViewImpl extends SimplePanel implements TermsView {

    private HTML termsHtml;

    private NotificationPagePresenter presenter;

    public TermsViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

    }

    @Override
    public void populate(String termsText) {

        TermsGadget notificationGadget = new TermsGadget(this);
        notificationGadget.asWidget().setWidth("100%");
        setWidget(notificationGadget);

        termsHtml.setHTML(termsText);

    }

    class TermsGadget extends AbstractGadget<TermsViewImpl> {

        TermsGadget(TermsViewImpl viewer) {
            super(viewer, null, "", ThemeColor.foreground, 0.3);

            addStyleName(PortalRootPaneTheme.StyleName.NotificationGadget.name());

            FlowPanel viewPanel = new FlowPanel();
            viewPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

            termsHtml = new HTML();
            viewPanel.add(termsHtml);

            setContent(viewPanel);

        }

    }
}
