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
package com.propertyvista.crm.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.crm.client.mvp.ContentActivityMapper;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.HeaderActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class CrmRootPane extends RootPane<LayoutPanel> implements IsWidget {

    private final CrmLayoutPanel layoutPanel;

    public CrmRootPane() {
        super(new LayoutPanel());

        HTML feedbackWidgetContainer = new HTML();
        feedbackWidgetContainer.getElement().setAttribute("id", "feedback_widget_container"); //getSatisfaction button container
        asWidget().add(feedbackWidgetContainer); //must be done before add(contentPanel) else the container blocks all interaction with site

        asWidget().setStyleName(CrmSitePanelTheme.StyleName.SiteView.name());

        layoutPanel = new CrmLayoutPanel();
        asWidget().add(layoutPanel);

        bind(new HeaderActivityMapper(), layoutPanel.getHeaderDisplay());

        bind(new FooterActivityMapper(), layoutPanel.getFooterDisplay());
        bind(new NavigActivityMapper(), layoutPanel.getNavigDisplay());
        bind(new ShortCutsActivityMapper(), layoutPanel.getShortcutsDisplay());

        bind(new ContentActivityMapper(), layoutPanel.getContentDisplay());

    }

    @Override
    protected void onPlaceChange(Place place) {
        if (place instanceof PublicPlace ||

        place instanceof CrmSiteMap.PasswordReset ||

        place instanceof CrmSiteMap.Account.AccountRecoveryOptionsRequired ||

        place instanceof CrmSiteMap.RuntimeError) {
            layoutPanel.setMenuVisible(false);
        } else {
            layoutPanel.setMenuVisible(true);
        }
    }

}
