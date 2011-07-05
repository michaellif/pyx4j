/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.util;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitiveSet;

import com.propertyvista.common.domain.IAddress;
import com.propertyvista.common.domain.RangeGroup;
import com.propertyvista.portal.domain.dto.AmenityDTO;

public class Formatter {

    public final static String POSTFIX = " \u2022 ";

    public static String formatAddress(IAddress address) {

        if (address.isNull())
            return "";

        StringBuffer addrString = new StringBuffer();

        addrString.append(address.street1().getStringView());
        if (!address.street2().isNull()) {
            addrString.append(" ");
            addrString.append(address.street2().getStringView());
        }

        if (!address.city().isNull()) {
            addrString.append(", ");
            addrString.append(address.city().getStringView());
        }

        if (!address.province().isNull()) {
            addrString.append(", ");
            addrString.append(address.province().getStringView());
        }

        if (!address.postalCode().isNull()) {
            addrString.append(" ");
            addrString.append(address.postalCode().getStringView());
        }

        return addrString.toString();
    }

    public static String formatRange(RangeGroup range, String prefix) {
        if (range.isNull())
            return "";

        StringBuffer rangeString = new StringBuffer((prefix != null ? prefix : ""));
        if (!range.min().isNull()) {
            rangeString.append(range.min().getStringView());
        }

        if (!range.max().isNull()) {
            rangeString.append(" - ");
            rangeString.append(range.max().getStringView());
        }
        return rangeString.toString();

    }

    public static String formatAmenities(IList<AmenityDTO> amenities) {
        if (amenities.isNull() || amenities.isEmpty()) {
            return "";
        }
        StringBuffer strbuffer = new StringBuffer();
        for (AmenityDTO amenity : amenities) {
            if (!amenity.isNull() && !amenity.isEmpty()) {
                strbuffer.append(formatListItem(amenity.getStringView()));
            }
        }
        String finalString = strbuffer.toString();
        int idx = finalString.lastIndexOf(POSTFIX);
        if (idx > -1) {
            finalString = finalString.substring(0, idx);
        }

        return finalString;

    }

    public static String formatStringList(IPrimitiveSet<String> floorplans) {
        if (floorplans.isNull() || floorplans.isEmpty())
            return "";

        StringBuffer strbuffer = new StringBuffer();

        for (String planName : floorplans.getValue()) {
            if (planName != null && !planName.isEmpty()) {
                strbuffer.append(formatListItem(planName));
            }
        }
        String finalString = strbuffer.toString();
        int idx = finalString.lastIndexOf(POSTFIX);
        if (idx > -1) {
            finalString = finalString.substring(0, idx);
        }
        return finalString;
    }

    public static HorizontalPanel formatCardLine(String label, String value) {
        HorizontalPanel item = new HorizontalPanel();
        item.setWidth("100%");
        Label lbl = new Label(label + ":");
        item.add(lbl);
        item.setCellWidth(lbl, "18%");
        lbl = new Label(value);
        item.add(new Label(value));
        item.setCellWidth(lbl, "82%");
        return item;

    }

    private static String formatListItem(String item) {
        if (item == null || item.isEmpty()) {
            return "";
        }
        return item.toUpperCase() + POSTFIX;

    }

}
