/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.view.client.HasData;

import com.propertyvista.crm.client.ui.tools.common.view.AbstractPrimePaneWithMessagesPopup;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid.MoneyInBatchDataGrid;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchesViewImpl extends AbstractPrimePaneWithMessagesPopup implements MoneyInBatchesView {

    private MoneyInBatchesView.Presenter presenter;

    private final MoneyInBatchDataGrid batchesDataGrid;

    public MoneyInBatchesViewImpl() {
        LayoutPanel viewPanel = new LayoutPanel();
        viewPanel.setSize("100%", "100%");
        batchesDataGrid = new MoneyInBatchDataGrid();
        viewPanel.add(batchesDataGrid);
        viewPanel.setWidgetTopBottom(batchesDataGrid, 0, Unit.PX, 0, Unit.PX);
        viewPanel.setWidgetLeftRight(batchesDataGrid, 0, Unit.PX, 0, Unit.PX);

        setContentPane(viewPanel);
        setSize("100%", "100%");
    }

    @Override
    public void setPresenter(MoneyInBatchesView.Presenter presenter) {
        this.presenter = presenter;
        batchesDataGrid.setPresenter(presenter);
    }

    @Override
    public HasData<MoneyInBatchDTO> batches() {
        return batchesDataGrid;
    }

}
