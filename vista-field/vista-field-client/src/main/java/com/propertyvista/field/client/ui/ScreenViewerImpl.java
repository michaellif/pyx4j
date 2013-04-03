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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.field.rpc.ScreenMode.ScreenLayout;

public class ScreenViewerImpl extends FlowPanel implements ScreenViewer {

    private Presenter presenter;

    private final DisplayPanel header;

    private final DisplayPanel lister;

    private final DisplayPanel details;

    private final DisplayPanel fullScreen;

    private ScreenLayout layout;

    public ScreenViewerImpl() {
        header = initDisplay();
        lister = initDisplay();
        details = initDisplay();
        fullScreen = initDisplay();

        setSize("100%", "100%");

        add(header);
        add(lister);
        add(details);
        add(fullScreen);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public DisplayPanel getHeaderDisplay() {
        return header;
    }

    @Override
    public DisplayPanel getListerDisplay() {
        return lister;
    }

    @Override
    public DisplayPanel getDetailsDisplay() {
        return details;
    }

    @Override
    public DisplayPanel getFullScreenDisplay() {
        return fullScreen;
    }

    private DisplayPanel initDisplay() {
        DisplayPanel display = new DisplayPanel();
        display.setSize("100%", "100%");
        return display;
    }

    @Override
    public void setWidget(IsWidget widget) {
        if (ScreenLayout.FullScreen == layout) {
            fullScreen.setWidget(widget);
        }
    }

    private void hideDisplays() {
        header.setVisible(false);
        lister.setVisible(false);
        details.setVisible(false);
        fullScreen.setVisible(false);

    }

    @Override
    public void setScreenLayout(ScreenLayout layout) {
        this.layout = layout;

        switch (layout) {
        case FullScreen:
            initFullScreen();
            break;
        case HeaderLister:
            initHeaderLister();
            break;
        case HeaderListerDetails:
            initHeaderListerDetails();
            break;

        default:
            break;
        }
    }

    private void initFullScreen() {
        hideDisplays();
        fullScreen.setVisible(true);
    }

    private void initHeaderLister() {
        hideDisplays();
        header.setVisible(true);
        lister.setVisible(true);
    }

    private void initHeaderListerDetails() {
        hideDisplays();
        header.setVisible(true);
        lister.setVisible(true);
        details.setVisible(true);
    }

}
