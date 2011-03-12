/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;

import com.pyx4j.forms.client.ui.CComponent;

public class VistaDecoratorsFlowPanel extends FlowPanel {

    private final boolean readOnlyMode;

    public VistaDecoratorsFlowPanel() {
        readOnlyMode = false;
    }

    public VistaDecoratorsFlowPanel(boolean readOnlyMode) {
        this.readOnlyMode = readOnlyMode;
    }

    public void add(final CComponent<?> component, double componentWidth) {
        this.add(component, 10, componentWidth);
    }

    public void add(final CComponent<?> component, double labelWidth, double componentWidth) {
        DecorationData decorData = new DecorationData();
        decorData = new DecorationData();
        decorData.componentWidth = componentWidth;
        decorData.labelWidth = labelWidth;
        decorData.readOnlyMode = readOnlyMode;
        if (readOnlyMode) {
            decorData.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
        }
        super.add(new VistaWidgetDecorator(component, decorData));
        if (!readOnlyMode) {
            super.add(new HTML());
        }
    }
}
