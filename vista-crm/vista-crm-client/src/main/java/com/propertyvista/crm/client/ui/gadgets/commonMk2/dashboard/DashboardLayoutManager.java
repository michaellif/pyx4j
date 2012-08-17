/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.forms.client.ui.NotImplementedException;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class DashboardLayoutManager implements ILayoutManager {

    private final boolean TODO_PENDING_LAYOUT_STORAGE_IN_GADGET_METADATA = false;

    @Override
    public void restoreLayout(DashboardMetadata dashboardMetadata, Iterator<IGadgetInstance> gadgetsIterator, IBoard board) {
        board.setLayout(asBoardLayout(dashboardMetadata.layoutType().getValue()));

        Iterator<Integer> columnsIterator = getColumnsIterator(dashboardMetadata);
        while (gadgetsIterator.hasNext()) {
            if (TODO_PENDING_LAYOUT_STORAGE_IN_GADGET_METADATA) {
                Integer column = 0;
                if (columnsIterator.hasNext()) {
                    column = columnsIterator.next();
                }
            }
            IGadgetInstance gadget = gadgetsIterator.next();
            board.addGadget(gadget, gadget.getMetadata().docking().column().getValue());
        }
    }

    @Override
    public void saveLayout(DashboardMetadata dashboardMetadata, IBoard board) {
        if (dashboardMetadata != null) {
            dashboardMetadata.layoutType().setValue(asDashboardLayoutType(board.getLayout()));
            dashboardMetadata.gadgets().clear();

            IGadgetIterator it = board.getGadgetIterator();
            List<String> columns = new ArrayList<String>();
            while (it.hasNext()) {
                IGadget gadget = it.next();
                ((IGadgetInstance) gadget).getMetadata().docking().column().setValue(it.getColumn());
                if (TODO_PENDING_LAYOUT_STORAGE_IN_GADGET_METADATA) {
                    columns.add(String.valueOf(it.getColumn()));
                }
                if (gadget instanceof IGadgetInstance) {
                    GadgetMetadata gadgetMetadata = ((IGadgetInstance) gadget).getMetadata();
                    dashboardMetadata.gadgets().add(gadgetMetadata);
                }
            }

            StringBuilder encodedLayoutBuilder = new StringBuilder();
            for (int i = 0; i < columns.size(); ++i) {
                encodedLayoutBuilder.append(columns.get(i));
                if (i != (columns.size() - 1)) {
                    encodedLayoutBuilder.append(" ");
                }
            }
            dashboardMetadata.encodedLayout().setValue(encodedLayoutBuilder.toString());
        }
    }

    private static BoardLayout asBoardLayout(LayoutType value) {
        BoardLayout layout = null;
        switch (value) {
        case One:
            layout = BoardLayout.One;
            break;
        case Two11:
            layout = BoardLayout.Two11;
            break;
        case Two12:
            layout = BoardLayout.Two12;
            break;
        case Two21:
            layout = BoardLayout.Two21;
            break;
        case Three:
            layout = BoardLayout.Three;
            break;
        }
        return layout;
    }

    private static LayoutType asDashboardLayoutType(BoardLayout layout) {
        LayoutType layoutType = null;
        switch (layout) {
        case One:
            layoutType = LayoutType.One;
            break;
        case Two11:
            layoutType = LayoutType.Two11;
            break;
        case Two12:
            layoutType = LayoutType.Two12;
            break;
        case Two21:
            layoutType = LayoutType.Two21;
            break;
        case Three:
            layoutType = LayoutType.Three;
            break;
        }
        return layoutType;
    }

    private Iterator<Integer> getColumnsIterator(DashboardMetadata dashboardMetadata) {
        final String[] columns = (dashboardMetadata.encodedLayout().isNull() ? "" : dashboardMetadata.encodedLayout().getValue()).split(" ");

        return new Iterator<Integer>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < columns.length;
            }

            @Override
            public Integer next() {
                return Integer.parseInt(columns[i++]);
            }

            @Override
            public void remove() {
                throw new NotImplementedException();
            }
        };
    }

}
