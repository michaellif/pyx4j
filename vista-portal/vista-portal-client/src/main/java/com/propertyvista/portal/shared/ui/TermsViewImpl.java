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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class TermsViewImpl extends SimplePanel implements TermsView {

    public TermsViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

    }

    @Override
    public void populate(String title, String content) {

        TermsGadget notificationGadget = new TermsGadget(this, title, content);
        notificationGadget.asWidget().setWidth("100%");
        setWidget(notificationGadget);

    }

    class TermsGadget extends AbstractGadget<TermsViewImpl> {

        TermsGadget(TermsViewImpl viewer, String title, String content) {
            super(viewer, null, title, ThemeColor.foreground, 0.3);

            addStyleName(PortalRootPaneTheme.StyleName.TermsGadget.name());

            HTML contentHolder = new HTML(content);
            contentHolder.setStyleName(PortalRootPaneTheme.StyleName.TermsGadgetContent.name());

            setContent(contentHolder);

        }

    }
}
