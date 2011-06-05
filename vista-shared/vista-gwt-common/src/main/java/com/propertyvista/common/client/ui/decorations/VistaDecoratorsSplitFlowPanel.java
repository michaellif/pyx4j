/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-01
 * @author leon
 * @version $Id$
 */
package com.propertyvista.common.client.ui.decorations;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;

public class VistaDecoratorsSplitFlowPanel extends FlowPanel {

    private final VistaDecoratorsFlowPanel right;

    private final VistaDecoratorsFlowPanel left;

    public VistaDecoratorsSplitFlowPanel() {
        right = new VistaDecoratorsFlowPanel(12);
        left = new VistaDecoratorsFlowPanel(12);
        left.setWidth("50%");
        this.add(left);
        this.add(right);
        this.getElement().getStyle().setProperty("minWidth", "900px");
        right.setWidth("50%");
        right.getElement().getStyle().setFloat(Style.Float.LEFT);
        left.setWidth("50%");
        left.getElement().getStyle().setFloat(Style.Float.LEFT);
    }

    public VistaDecoratorsFlowPanel getRightPanel() {
        return right;
    }

    public VistaDecoratorsFlowPanel getLeftPanel() {
        return left;
    }
}
