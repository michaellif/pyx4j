/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.decorations;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.decorations.VistaHeaderDecorator;

public class PortalHeaderDecorator extends VistaHeaderDecorator {

    private final HorizontalPanel rightpanel;

    public PortalHeaderDecorator(String caption, String width) {
        super(caption, width);
        rightpanel = new HorizontalPanel();
        rightpanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        rightpanel.getElement().getStyle().setMarginRight(15d, Unit.PX);
        rightpanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        add(rightpanel);
        setCellVerticalAlignment(rightpanel, HorizontalPanel.ALIGN_MIDDLE);
    }

    public void addToTheRight(IsWidget child) {
        rightpanel.add(child);

    }

}
