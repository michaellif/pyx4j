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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.theme.SiteViewTheme;

public class ShortCutsViewImpl extends FlowPanel implements ShortCutsView {

    private ShortCutsPresenter presenter;

    private final FlowPanel shortcutsList;

    public ShortCutsViewImpl() {
        super();
        setStyleName(SiteViewTheme.StyleName.SiteViewShortCuts.name());

        HTML shortcutsTitle = new HTML("Shortcuts");
        shortcutsTitle.setStyleName(SiteViewTheme.StyleName.SiteViewShortCutsTitle.name());
        add(shortcutsTitle);

        shortcutsList = new FlowPanel();
        add(shortcutsList);

        setHeight("100%");

    }

    @Override
    public void setPresenter(final ShortCutsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateShortcutFolder(CrudAppPlace place, IEntity value) {
        shortcutsList.insert(new ShortcutItem(place, value), 0);
    }

    private class NavigItem extends SimplePanel {

        private AppPlace place;

        public NavigItem(AppPlace placeIn, IEntity value) {
            adoptPlace(placeIn);

            String typeLabel = AppSite.getHistoryMapper().getPlaceInfo(placeIn).getCaption();
            String viewLabel = value != null ? value.getStringView() : "";

            Anchor anchor = new Anchor(viewLabel, true);
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(NavigItem.this.place);
                }
            });
            anchor.setTitle(typeLabel + (value != null ? " - " + value.getStringView() : ""));

            setStyleName(SiteViewTheme.StyleName.SiteViewShortCutsItem.name());

            setWidget(anchor);
        }

        public AppPlace getPlace() {
            return place;
        }

        private void adoptPlace(AppPlace placeIn) {

            if (false) {
                // TODO create appropriate AppPlace descendant here:
//                this.place = new CRMCrudAppPlace().copy(placeIn);

                String val;
                if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
                    Key entityId = new Key(val);
                    entityId.asCurrentKey();
                    place.formPlace(entityId);
                }
            } else {
                this.place = placeIn;
            }
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
