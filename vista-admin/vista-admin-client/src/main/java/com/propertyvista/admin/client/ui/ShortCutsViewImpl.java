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
package com.propertyvista.admin.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleName;

import com.propertyvista.admin.client.activity.NavigFolder;

public class ShortCutsViewImpl extends StackLayoutPanel implements ShortCutsView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_ShortCuts";

    public static enum StyleSuffix implements IStyleName {
        Item, SearchBar
    }

    private ShortCutsPresenter presenter;

    private final SearchBox search;

    public ShortCutsViewImpl() {
        super(Unit.EM);
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");

        search = new SearchBox();
        search.setWidth("90%");
        search.getElement().getStyle().setMarginLeft(0.33, Unit.EM);
    }

    /**
     * TODO change implementation later
     */

    @Override
    public void setPresenter(final ShortCutsPresenter presenter) {
        this.presenter = presenter;

        //TODO Clean for now. Implement comparizon later
        this.clear();

        List<NavigFolder> folders = presenter.getNavigFolders();
        for (NavigFolder navigFolder : folders) {

            ScrollPanel scroll = new ScrollPanel();
            SimplePanel searchcontainer = new SimplePanel();
            searchcontainer.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchBar);
            searchcontainer.setHeight("2em");
            searchcontainer.setWidth("100%");
//            searchcontainer.getElement().getStyle().setPaddingTop(0.4, Unit.EM);
            //   searchcontainer.getElement().getStyle().setPaddingLeft(0.4, Unit.EM);
            searchcontainer.add(search);

            FlowPanel list = new FlowPanel();
            list.add(searchcontainer);

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
            add(scroll, navigFolder.getTitle(), 3);
        }
    }
}
