/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.propertyvista.field.client.IsFullScreenWidget;

public class FieldScreenViewImpl extends LayoutPanel implements FieldScreenView {

    private Presenter presenter;

    private final DisplayPanel toolbar;

    private final DisplayPanel lister;

    private final DisplayPanel details;

    private IsWidget overlappingWidget;

    public FieldScreenViewImpl() {
        toolbar = initDisplay();
        lister = initDisplay();
        details = initDisplay();

        setSize("100%", "100%");
        add(toolbar);

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSize("100%", "100%");
        hPanel.add(lister);
        hPanel.add(details);

        add(hPanel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public DisplayPanel getToolbarDisplay() {
        return toolbar;
    }

    @Override
    public DisplayPanel getListerDisplay() {
        return lister;
    }

    @Override
    public DisplayPanel getDetailsDisplay() {
        return details;
    }

    private DisplayPanel initDisplay() {
        DisplayPanel display = new DisplayPanel();
        display.setSize("100%", "100%");
        return display;
    }

    @Override
    public void setWidget(IsWidget widget) {
        clearOverlap();
        if (widget instanceof IsFullScreenWidget) {
            hideChildren();
            add(widget);
        }
        this.overlappingWidget = widget;
    }

    private void clearOverlap() {
        if (overlappingWidget != null) {
            remove(overlappingWidget);
            overlappingWidget = null;
        }
    }

    private void hideChildren() {
        for (int i = 0; i < getWidgetCount(); i++) {
            getWidget(i).setVisible(false);
        }
    }
}
