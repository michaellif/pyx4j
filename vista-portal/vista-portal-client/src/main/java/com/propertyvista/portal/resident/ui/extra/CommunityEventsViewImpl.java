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
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.extra;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.resident.themes.ExtraGadgetsTheme;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class CommunityEventsViewImpl extends FlowPanel implements CommunityEventsView {

    private static final I18n i18n = I18n.get(CommunityEventsViewImpl.class);

    private static final int MAX_EVENT_TO_SHOW = 3;

    public CommunityEventsViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());
    }

    @Override
    public void populateCommunityEvents(CommunityEventsGadgetDTO notification) {
        clear();
        if (notification != null && notification.events() != null && notification.events().size() > 0) {
            int i = 1;
            for (CommunityEvent event : notification.events()) {
                HTML captionHTML = new HTML(event.caption().getValue());
                captionHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventCaption.name());
                captionHTML.setTitle(event.caption().getValue());
                add(captionHTML);

                String dateLocation = createDateAndLocation(event);
                HTML timeAndLocationHTML = new HTML(dateLocation);
                timeAndLocationHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventTimeAndLocation.name());
                timeAndLocationHTML.setTitle(dateLocation);
                add(timeAndLocationHTML);

                HTML descriptionHTML = new HTML(event.description().getValue());
                descriptionHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventDescription.name());
                descriptionHTML.setTitle(event.description().getValue());
                add(descriptionHTML);

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
}
