/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

public class HorizontalAlignCenterMixin extends Theme {

    public static enum StyleName implements IStyleName {
        HorizontalAlignCenter
    }

    public HorizontalAlignCenterMixin() {
        Style style = new Style(".", StyleName.HorizontalAlignCenter);
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);
    }

}
