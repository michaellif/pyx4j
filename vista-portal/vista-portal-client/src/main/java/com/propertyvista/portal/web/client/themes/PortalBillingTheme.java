/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 17, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.Style;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;

import com.propertyvista.common.client.theme.BillingTheme;

public class PortalBillingTheme extends BillingTheme {

    public PortalBillingTheme() {

        Style style = new Style(".", StyleName.BillingLineItem);
        style.addProperty("color", "#B3B3B3");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.BillingLineItemTitle);
        addStyle(style);

        style = new Style(".", StyleName.BillingLineItemAmount);
        style.addProperty("color", "#666666");
        style.addProperty("font-style", "normal");
        style.addProperty("text-align", "left");
        addStyle(style);
        // ----------------------------------------------------
        style = new Style(".", StyleName.BillingDetailItem);
        style.addProperty("color", "#B3B3B3");
        style.addProperty("font-style", "italic");
        style.addProperty("font-size", "smaller");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailItemDate);
        style.addProperty("padding-left", "4px");
        style.addProperty("padding-right", "4px");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailItemTitle);
        style.addProperty("padding-left", "4px");
        style.addProperty("padding-right", "4px");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailItemAmount);
        style.addProperty("color", "#666666");
        style.addProperty("font-style", "normal");
        style.addProperty("text-align", "left");
        addStyle(style);
        // ----------------------------------------------------
        style = new Style(".", StyleName.BillingDetailTotal);
        style.addProperty("color", "#B3B3B3");
        style.addProperty("font-size", "smaller");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailTotalTitle);
        style.addProperty("padding-top", "4px");
        style.addProperty("padding-right", "6px");
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailTotalAmount);
        style.addProperty("color", "#666666");
        style.addProperty("border-top", "1px solid black");
        style.addProperty("padding-top", "4px");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.BillingBillTotal, " .", DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel);
        style.addProperty("color", "#666");
        addStyle(style);

        style = new Style(".", StyleName.BillingBillTotal, " .", DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponent);
        style.addProperty("color", "#666");
        style.addProperty("border-top", "2px solid #bbb");
        style.addProperty("padding-top", "4px");
        addStyle(style);
    }
}
