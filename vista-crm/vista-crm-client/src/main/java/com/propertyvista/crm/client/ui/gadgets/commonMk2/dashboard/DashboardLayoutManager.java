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

import java.util.Iterator;

import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

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
    public void restoreLayout(String encodedLayout, Iterator<IGadgetInstance> gadgetsIterator, IBoard board) {
        board.setLayout(boardLayout);
        DashboardColumnLayoutFormat layoutFormat = new DashboardColumnLayoutFormat(encodedLayout);
        while (gadgetsIterator.hasNext()) {
            IGadgetInstance gadget = gadgetsIterator.next();
            board.addGadget(gadget, layoutFormat.getGadgetColumn(gadget.getMetadata().gadgetId().getValue()));
        }
    }

    @Override
    public String switchLayout(String oldEncodedLayout, IBoard board) {
        if (board.getLayout() != boardLayout) {
            // TODO add layout switching algorithm here
        }
        board.setLayout(boardLayout);
        DashboardColumnLayoutFormat.Builder layoutFormatBuilder = new DashboardColumnLayoutFormat.Builder(layoutType);
        IGadgetIterator it = board.getGadgetIterator();
        while (it.hasNext()) {
            IGadget gadget = it.next();
            if (gadget instanceof IGadgetInstance) {
                GadgetMetadata gadgetMetadata = ((IGadgetInstance) gadget).getMetadata();
                String gadgetId = gadgetMetadata.gadgetId().getValue();
                layoutFormatBuilder.bind(gadgetId, it.getColumn());
            }
        }
        return layoutFormatBuilder.build().getSerializedForm();
    }

    @Override
    public Resources getResources() {
        return resources;
    }

}
