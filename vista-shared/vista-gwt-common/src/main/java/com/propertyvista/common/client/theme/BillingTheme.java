/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

public class BillingTheme extends Theme {

    public static enum StyleName implements IStyleName {
        BillingLineItem, BillingLineItemTitle, BillingLineItemAmount, BillingDetailItem, BillingDetailItemTitle, BillingDetailItemAmount, BillingDetailTotal, BillingDetailTotalAmount
    }

    public BillingTheme() {
        Style style = new Style(".", StyleName.BillingLineItem);
        style.addProperty("color", "#B3B3B3");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.BillingLineItemAmount);
        style.addProperty("color", "#666666");
        style.addProperty("text-align", "right");
//        style.addProperty("font-style", "normal");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailItem);
        style.addProperty("margin", "99px");
        style.addProperty("color", "#B3B3B3");
        style.addProperty("font-style", "italic");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailItemAmount);
        style.addProperty("text-align", "right");
        style.addProperty("color", "#666666");
        style.addProperty("font-weight", "bold");
//        style.addProperty("font-style", "normal");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailTotal);
        style.addProperty("font-size", "12px");
        style.addProperty("color", "#B3B3B3");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.BillingDetailTotalAmount);
        style.addProperty("color", "#666666");
        style.addProperty("border-top", "1px solid black");
        style.addProperty("padding-top", "4px");
        style.addProperty("text-align", "right");
        style.addProperty("font-style", "normal");
        addStyle(style);
    }
}
