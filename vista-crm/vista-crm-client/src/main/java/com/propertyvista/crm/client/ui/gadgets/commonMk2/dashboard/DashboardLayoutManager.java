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
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.List;

import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;
import com.pyx4j.widgets.client.dashboard.Reportboard;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class DashboardLayoutManager implements ILayoutManager {

    private final LayoutType layoutType;

    private final BoardLayout boardLayout;

    private final Resources resources;

    public DashboardLayoutManager(LayoutType layoutType, BoardLayout boardLayout, Resources resources) {
        this.layoutType = layoutType;
        this.boardLayout = boardLayout;
        this.resources = resources;
    }

    @Override
    public String getLayoutName() {
        return this.layoutType.toString();
    }

    @Override
    public boolean canHandle(String encodedLayout) {
        return encodedLayout.startsWith(layoutType.name().toString());
    }

    @Override
    public Resources getResources() {
        return resources;
    }

    @Override
    public IBoard arrange(String encodedLayout, List<IGadgetInstance> gadgets) {
        IBoard board = null;
        if (boardLayout.isRowLayout()) {
            board = new Reportboard();
        } else {
            board = new Dashboard();
        }
        board.setLayout(boardLayout);

        DashboardColumnLayoutFormat dashboardFormat = new DashboardColumnLayoutFormat(encodedLayout);
        ColumnTransformer transformer = null;
        int thisNumOfColumns = boardLayout.columns();
        int thatNumOfColumns = 0;
        switch (dashboardFormat.getLayoutType()) {
        case One:
            thatNumOfColumns = 1;
            break;
        case Two11:
        case Two12:
        case Two21:
            thatNumOfColumns = 2;
            break;
        case Three:
            thatNumOfColumns = 3;
            break;
        case Report:
            thatNumOfColumns = 0;
        }
        if (dashboardFormat.getLayoutType() == LayoutType.Report) {
            transformer = layoutType != LayoutType.Report ? new FromRowLayoutTransformer(thisNumOfColumns) : new IdentityColumnTransformer();
        } else if (layoutType == LayoutType.Report) {
            transformer = new ToRowLayoutTrasformer();
        } else if (thisNumOfColumns < thatNumOfColumns) {
            transformer = new ShrinkColumnTransformer(thisNumOfColumns);
        } else {
            transformer = new IdentityColumnTransformer();
        }

        for (String gadgetId : dashboardFormat.gadgetIds()) {
            board.addGadget(getGadget(gadgetId, gadgets), transformer.transform(dashboardFormat.getGadgetColumn(gadgetId)));
        }
        return board;
    }

    @Override
    public String encodeLayout(IBoard board) {
        DashboardColumnLayoutFormat.Builder builder = new DashboardColumnLayoutFormat.Builder(layoutType);
        IGadgetIterator i = board.getGadgetIterator();
        while (i.hasNext()) {
            IGadget g = i.next();
            if (g instanceof IGadgetInstance) {
                builder.bind(((IGadgetInstance) g).getMetadata().gadgetId().getValue(), i.getColumn());
            }
        }
        return builder.build().getSerializedForm();
    }

    private IGadgetInstance getGadget(String gadgetId, List<IGadgetInstance> gadgets) {
        for (IGadgetInstance g : gadgets) {
            if (g.getMetadata().gadgetId().getValue().equals(gadgetId)) {
                return g;
            }
        }
        throw new Error("gadget with id=" + gadgetId + " was not found");
    }

    private interface ColumnTransformer {

        int transform(int column);

    }

    private static class IdentityColumnTransformer implements ColumnTransformer {

        @Override
        public int transform(int column) {
            return column;
        }

    }

    private static class ShrinkColumnTransformer implements ColumnTransformer {

        private final int numOfColumns;

        protected int counter;

        public ShrinkColumnTransformer(int numOfColumns) {
            this.numOfColumns = numOfColumns;
            this.counter = -1;

        }

        @Override
        public int transform(int column) {
            return column < numOfColumns ? column : ++counter % numOfColumns;
        }

    }

    private static class FromRowLayoutTransformer extends ShrinkColumnTransformer {

        public FromRowLayoutTransformer(int numOfColumns) {
            super(numOfColumns);
        }

        @Override
        public int transform(int column) {
            if (column == -1) {
                counter = -1;
                return 0;
            } else {
                return super.transform(column);
            }
        }
    }

    private static class ToRowLayoutTrasformer implements ColumnTransformer {

        @Override
        public int transform(int column) {
            return column % 2;
        }

    }
}
