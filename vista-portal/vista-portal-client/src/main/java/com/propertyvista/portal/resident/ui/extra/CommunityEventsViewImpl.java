/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 */
package com.propertyvista.portal.resident.ui.extra;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.resident.themes.ExtraGadgetsTheme;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class CommunityEventsViewImpl extends FlowPanel implements CommunityEventsView {

    private static final I18n i18n = I18n.get(CommunityEventsViewImpl.class);

    private static final int MAX_EVENT_TO_SHOW = 3;

    private CommunityEventsPresenter presenter;

    public CommunityEventsViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());
    }

    @Override
    public void populateCommunityEvents(List<CommunityEvent> events) {
        clear();
        if (events != null && events.size() > 0) {
            int i = 1;
            for (final CommunityEvent event : events) {
                Label captionHTML = new Label(event.caption().getValue());
                captionHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventCaption.name());
                captionHTML.setTitle(event.caption().getValue());
                captionHTML.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.showEvent(event.getPrimaryKey());

                    }

                });
                add(captionHTML);

                String dateLocation = createDateAndLocation(event);
                HTML timeAndLocationHTML = new HTML(dateLocation);
                timeAndLocationHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventTimeAndLocation.name());
                timeAndLocationHTML.setTitle(dateLocation);
                add(timeAndLocationHTML);

                String description = event.description().getValue();
                HTML descriptionHTML = new HTML((description.length() <= 80) ? description : description.substring(0, description.indexOf(" ", 80)) + "...");
                descriptionHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventExtraDescription.name());
                descriptionHTML.setTitle(event.description().getValue());
                add(descriptionHTML);

                Anchor more = new Anchor("Read more>>");
                more.setStyleName(ExtraGadgetsTheme.StyleName.ExtraAnchor.name());
                more.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.showEvent(event.getPrimaryKey());

                    }

                });
                add(more);

                if (++i > MAX_EVENT_TO_SHOW) {
                    break;
                }
            }
        } else {
            add(new HTML(i18n.tr("No events")));
        }

    }

    private static String createDateAndLocation(CommunityEvent event) {
        String date = event.date().getStringView();
        String time = event.time() == null || event.time().isNull() ? "" : " " + event.time().getStringView();

        return date + time + ((event.location() == null || event.location().isNull()) ? "" : " " + event.location());
    }

    @Override
    public void setPresenter(CommunityEventsPresenter presenter) {
        this.presenter = presenter;
    }
}
