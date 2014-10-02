/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 2, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;

public class MiscUtils {

    @SuppressWarnings("deprecation")
    public static void setPanelSpacing(ComplexPanel panel, int spacing) {
        for (int i = 0; i < panel.getWidgetCount(); ++i) {
            DOM.getParent(panel.getWidget(i).getElement()).getStyle().setPadding(spacing, Unit.PX);
        }
    }
}
