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

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.NavigFolder;
import com.propertyvista.crm.client.activity.NavigFolder.Type;

public class ShortCutsViewImpl extends StackLayoutPanel implements ShortCutsView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_ShortCuts";

    public static enum StyleSuffix implements IStyleName {
        Item, SearchBar
    }

    private ShortCutsPresenter presenter;

    private final SearchBox search;

    private FlowPanel historyList;

    public ShortCutsViewImpl() {
        super(Unit.EM);
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");

        search = new SearchBox();
        search.setWidth("90%");
        search.getElement().getStyle().setMarginLeft(0.33, Unit.EM);
    }

    @Override
    public void setPresenter(final ShortCutsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setShortCutFolders(List<NavigFolder> folders) {
        this.clear();
        for (NavigFolder folder : folders) {
            ScrollPanel scroll = new ScrollPanel();

            SimplePanel searchcontainer = new SimplePanel();
            searchcontainer.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchBar);
            searchcontainer.setHeight("2em");
            searchcontainer.setWidth("100%");
            searchcontainer.add(search);

            FlowPanel list = new FlowPanel();
            list.add(searchcontainer);

            for (final AppPlace place : folder.getNavigItems()) {
                list.insert(createListItem(place), 1);
            }

            scroll.setWidget(list);
            add(scroll, folder.getTitle(), 3);

            if (folder.getType() == Type.History) {
                historyList = list;
            }
        }
    }

    @Override
    public void updateHistoryFolder(CrudAppPlace place) {
        if (historyList != null) {
            historyList.insert(createListItem(place), 1);
        }
    }

    private Widget createListItem(final AppPlace place) {
        Anchor anchor = new Anchor(AppSite.getHistoryMapper().getPlaceInfo(place).getCaption());
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(place);
            }
        });

        SimplePanel item = new SimplePanel();
        item.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Item);
        item.setWidget(anchor);

        return item;
    }
}
