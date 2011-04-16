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

import java.util.List;

import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.propertyvista.crm.client.activity.NavigFolder;

import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class NavigViewImpl extends SimplePanel implements NavigView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_Navig";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover,
    }

    private MainNavigPresenter presenter;

    public NavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");
    }

    @Override
    public void setPresenter(MainNavigPresenter presenter) {
        this.presenter = presenter;

        DecoratedStackPanel stackPanel = new DecoratedStackPanel();
        stackPanel.setSize("100%", "100%");

        List<NavigFolder> folders = presenter.getNavigFolders();
        for (NavigFolder navigFolder : folders) {
            stackPanel.add(new HTML(navigFolder.getTitle()), navigFolder.getTitle());
        }

        setWidget(stackPanel);

    }

}
