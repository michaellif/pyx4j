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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.propertyvista.crm.client.activity.NavigFolder;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class NavigViewImpl extends SimplePanel implements NavigView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_Navig";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label, Item
    }

    public static enum StyleDependent implements IStyleDependent {
        hover
    }

    private MainNavigPresenter presenter;

    public NavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");
    }

    @Override
    public void setPresenter(final MainNavigPresenter presenter) {
        this.presenter = presenter;

        StackLayoutPanel stackPanel = new StackLayoutPanel(Unit.EM);
        stackPanel.setSize("100%", "100%");

        List<NavigFolder> folders = presenter.getNavigFolders();
        for (NavigFolder navigFolder : folders) {
            ScrollPanel scroll = new ScrollPanel();

            FlowPanel list = new FlowPanel();

            for (final AppPlace place : navigFolder.getNavigItems()) {
                SimplePanel line = new SimplePanel();
                //VS to add spacing
                line.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Item);
                Anchor anchor = new Anchor(presenter.getNavigLabel(place));
                anchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.navigTo(place);
                    }
                });
                line.setWidget(anchor);
                list.add(line);
            }

            scroll.setWidget(list);
            //VS 2 was chaned for 4
            stackPanel.add(scroll, navigFolder.getTitle(), 4);
        }

        setWidget(stackPanel);

    }

}
