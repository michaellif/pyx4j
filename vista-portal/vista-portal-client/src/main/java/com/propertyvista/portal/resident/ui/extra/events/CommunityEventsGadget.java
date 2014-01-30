/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.extra.events;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.resident.themes.ExtraGadgetsTheme;
import com.propertyvista.portal.resident.ui.extra.ExtraGadget;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;

public class CommunityEventsGadget extends ExtraGadget<CommunityEventsGadgetDTO> {

    private static final I18n i18n = I18n.get(CommunityEventsGadget.class);

    private static final int MAX_EVENT_TO_SHOW = 3;

    public CommunityEventsGadget(CommunityEventsGadgetDTO gadgetDTO) {
        super(gadgetDTO, i18n.tr("Upcoming Events"));
    }

    @Override
    protected Widget createBody() {
        FlowPanel panel = new FlowPanel();

        int i = 1;
        for (CommunityEvent event : getGadgetDTO().events()) {
            HTML captionHTML = new HTML(event.caption().getValue());
            captionHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventCaption.name());
            captionHTML.setTitle(event.caption().getValue());
            panel.add(captionHTML);

            String dateLocation = createDateAndLocation(event);
            HTML timeAndLocationHTML = new HTML(dateLocation);
            timeAndLocationHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventTimeAndLocation.name());
            timeAndLocationHTML.setTitle(dateLocation);
            panel.add(timeAndLocationHTML);

            HTML descriptionHTML = new HTML(event.description().getValue());
            descriptionHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventDescription.name());
            descriptionHTML.setTitle(event.description().getValue());
            panel.add(descriptionHTML);

            if (++i > MAX_EVENT_TO_SHOW) {
                break;
            }
        }

        return panel;
    }

    private static String createDateAndLocation(CommunityEvent event) {
        String date = event.date().getStringView();
        String time = event.time() == null || event.time().isNull() ? "" : " " + event.time().getStringView();

        return date + time + ((event.location() == null || event.location().isNull()) ? "" : " " + event.location());
    }
}
