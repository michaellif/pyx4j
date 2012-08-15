/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public abstract class AbstractDashboard extends Composite {

    private final ScrollPanel dashboardPanel;

    private final IGadgetDirectory gadgetDirectory;

    private final ICommonGadgetSettingsContainer commandGadgetSettingsContainer;

    private final ILayoutManager layoutManager;

    private IBoard board;

    private DashboardMetadata dashboardMetadata;

    public AbstractDashboard(ICommonGadgetSettingsContainer container, IGadgetDirectory gadgetDirectory, ILayoutManager layoutManager) {
        this.commandGadgetSettingsContainer = container;
        this.gadgetDirectory = gadgetDirectory;
        this.layoutManager = layoutManager;

        this.dashboardPanel = new ScrollPanel();
        initWidget(this.dashboardPanel);
    }

    public void setDashboardMetatdata(DashboardMetadata dashboardMetadata) {
        this.dashboardMetadata = dashboardMetadata;

        placeGadgets();
        startGadgets();

    }

    public DashboardMetadata getDashboardMetadata() {
        return this.dashboardMetadata;
    }

    protected abstract void onDashboardMetadataChanged();

    private void placeGadgets() {
        board = new Dashboard();

        if (dashboardMetadata != null) {
            List<IGadgetInstance> gadgets = new ArrayList<IGadgetInstance>();
            for (GadgetMetadata metadata : dashboardMetadata.gadgets()) {
                IGadgetInstance gadget = gadgetDirectory.createGadgetInstance(metadata);
                // TODO stupid way this stupid list is needed to separate layout from dashboard, but the implementation of segregation is not well done, review
                if (gadget != null) {
                    gadgets.add(gadget);
                    commandGadgetSettingsContainer.bindGadget(gadget);
                } else {
                    throw new Error("gadget factory doesn't know how to instantiate gadget type '" + metadata.getInstanceValueClass().getName() + "'");
                }
            }
            layoutManager.restoreLayout(dashboardMetadata, gadgets.iterator(), board);
        } else {
            throw new Error("DashboardMetadata cannot be null");
        }

        board.addEventHandler(new BoardEvent() {
            @Override
            public void onEvent(final Reason reason) {
                // use a deferred command so that the actual event processing unlinked from event!
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        AbstractDashboard.this.proccessDashboardEvent(reason);
                    }
                });
            }
        });

        dashboardPanel.setWidget(board);
    }

    private void startGadgets() {
        IGadgetIterator it = board.getGadgetIterator();
        while (it.hasNext()) {
            it.next().start();
        }
    }

    private void proccessDashboardEvent(BoardEvent.Reason reason) {
        switch (reason) {
        case addGadget:
            break;
        case newLayout:
            break;
        case removeGadget:
            break;
        case repositionGadget:
            break;
        case updateGadget:
            // gadget settings were changed: IMHO not supposed to affect the dashboard metadata and be managed internally by the gadget
            break;
        }

        layoutManager.saveLayout(dashboardMetadata, board);

        onDashboardMetadataChanged();
    }
}
