/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.common.client.theme.SiteViewTheme;

public class HelpViewImpl extends FlowPanel implements HelpView {

    private HelpPresenter presenter;

    public HelpViewImpl() {
        super();
        setStyleName(SiteViewTheme.StyleName.SiteViewExtra.name());
    }

    @Override
    public void setPresenter(final HelpPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateContextHelp() {
        add(new HTML("Coming soon."));
    }

}
