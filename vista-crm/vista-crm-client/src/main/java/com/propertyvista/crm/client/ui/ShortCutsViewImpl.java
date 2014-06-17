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

import java.util.Iterator;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.theme.SiteViewTheme;

public class ShortCutsViewImpl extends ScrollPanel implements ShortCutsView {

    public static int MAX_ITEMS = 20;

    private ShortCutsPresenter presenter;

    private final FlowPanel shortcutsListPanel;

    public ShortCutsViewImpl() {
        super();
        setStyleName(SiteViewTheme.StyleName.SiteViewExtra.name());

        shortcutsListPanel = new FlowPanel();
        setWidget(shortcutsListPanel);

        setHeight("100%");
    }

    @Override
    public void setPresenter(final ShortCutsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateShortcutFolder(CrudAppPlace place, IEntity value) {
        for (Iterator<Widget> it = shortcutsListPanel.iterator(); it.hasNext();) {
            ShortcutItem item = (ShortcutItem) it.next();
            if (item.getPlace().equals(place)) {
                it.remove();
            }
        }
        while (shortcutsListPanel.getWidgetCount() >= MAX_ITEMS) {
            shortcutsListPanel.remove(shortcutsListPanel.getWidgetCount() - 1);
        }
        shortcutsListPanel.insert(new ShortcutItem(place, value), 0);
    }

    private class NavigItem extends SimplePanel {

        private AppPlace place;

        public NavigItem(AppPlace placeIn, IEntity value) {
            adoptPlace(placeIn);

            StringBuilder viewLabel = new StringBuilder();

            viewLabel.append(value != null ? value.getStringView() : "");
            viewLabel.append(" (");
            viewLabel.append(AppSite.getHistoryMapper().getPlaceInfo(placeIn).getCaption());
            viewLabel.append(")");

            Anchor anchor = new Anchor(viewLabel.toString(), true);
            anchor.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            anchor.setTitle(viewLabel.toString());
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(NavigItem.this.place);
                }
            });
            setWidget(anchor);

            setStyleName(SiteViewTheme.StyleName.SiteViewExtraItem.name());
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
