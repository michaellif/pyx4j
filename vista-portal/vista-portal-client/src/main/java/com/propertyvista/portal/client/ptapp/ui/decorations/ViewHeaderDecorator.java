/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-17
 * @author VladLL
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ViewHeaderDecorator extends SimplePanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_ViewHeaderDecorator";

    public ViewHeaderDecorator(Widget header) {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setWidget(header);
    }
}
