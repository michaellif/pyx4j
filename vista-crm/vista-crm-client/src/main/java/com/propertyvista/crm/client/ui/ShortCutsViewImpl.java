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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.NavigFolder;

public class ShortCutsViewImpl extends StackLayoutPanel implements ShortCutsView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_ShortCuts";

    public static enum StyleSuffix implements IStyleName {
        Item, SearchBar
    }

    private ShortCutsPresenter presenter;

    private final SearchBox search;

    private FlowPanel shortcutsList;

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
    public void setNavigationFolders(List<NavigFolder> folders) {
        this.clear();
        for (NavigFolder folder : folders) {
            FlowPanel list = new FlowPanel() {
                @Override
                public void insert(Widget w, int beforeIndex) {
                    if (w instanceof ShortcutItem) {
                        for (Widget item : getChildren()) {
                            if (((ShortcutItem) w).equals(item)) {
                                remove(item); // remove the same shortcut if exists...
                                break;
                            }
                        }
                    }

                    super.insert(w, beforeIndex);
                }
            };

// TODO: hided in 1st version (till real implementation)            
            SimplePanel searchcontainer = new SimplePanel();
//            searchcontainer.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchBar);
//            searchcontainer.setHeight("2em");
//            searchcontainer.setWidth("100%");
//            searchcontainer.add(search);
            list.add(searchcontainer);

            switch (folder.getType()) {
            case Shortcuts:
                shortcutsList = list;
                break;
            default:
                for (final AppPlace place : folder.getNavigItems()) {
                    list.insert(new NavigItem(place), 1);
                }
            }

            ScrollPanel scroll = new ScrollPanel();
            scroll.setWidget(list);
            add(scroll, folder.getTitle(), 3);
        }
    }

    @Override
    public void updateShortcutFolder(CrudAppPlace place, IEntity value) {
        if (shortcutsList != null) {
            shortcutsList.insert(new ShortcutItem(place, value), 1);
        }
    }

    private class NavigItem extends SimplePanel {

        private final AppPlace place;

        public NavigItem(AppPlace place) {
            this(place, null);
        }

        public NavigItem(AppPlace place, IEntity value) {
            this.place = place;

            Anchor anchor = new Anchor("<i>" + AppSite.getHistoryMapper().getPlaceInfo(place).getCaption()
                    + (value != null ? ": </i>" + value.getStringView() : "</i>"), true);
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(NavigItem.this.place);
                }
            });

            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Item);
            setWidget(anchor);
        }

        public AppPlace getPlace() {
            return place;
        }
    }

    private class ShortcutItem extends NavigItem {

        public ShortcutItem(AppPlace place, IEntity value) {
            super(place, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (getClass() == obj.getClass()) {
                return getPlace().equals(((ShortcutItem) obj).getPlace());
            }
            return false;
        }
    }
}
