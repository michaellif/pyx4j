/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.field.client.theme.FieldTheme;

public class DisplayPanel extends SimplePanel implements RequiresResize, ProvidesResize {

    public DisplayPanel() {
        setStyleName(FieldTheme.StyleName.SiteViewDisplay.name());
    }

    @Override
    public void onResize() {
        Widget child = getWidget();
        if ((child != null) && (child instanceof RequiresResize)) {
            ((RequiresResize) child).onResize();
        }
    }

}
