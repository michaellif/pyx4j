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
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import java.util.Date;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.view.client.ProvidesKey;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityFieldColumn;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGrid;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInBatchesView;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInBatchesView.Presenter;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchDataGrid extends VistaDataGrid<MoneyInBatchDTO> {

    private static final I18n i18n = I18n.get(MoneyInBatchDataGrid.class);

    private Presenter presenter;

    public MoneyInBatchDataGrid() {
        super(MoneyInBatchDTO.class, false);
        initColumns();
    }

    public void setPresenter(MoneyInBatchesView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ProvidesKey<MoneyInBatchDTO> getKeyProvider() {
        return this.presenter;
    }

    private void initColumns() {//@formatter:off
        defColumn(new EntityFieldColumn<MoneyInBatchDTO, String>(proto().building(), new TextCell()), i18n.tr("Building"), 100, Unit.PX);
        defColumn(new EntityFieldColumn<MoneyInBatchDTO, Date>(proto().bankDepositDate(), new DateCell()), i18n.tr("Deposit Date"), 100, Unit.PX);
        defColumn(new EntityFieldColumn<MoneyInBatchDTO, Number>(proto().depositSlipNumber(), new NumberCell()), i18n.tr("Deposit Slip #"), 100, Unit.PX);
        defColumn(new EntityFieldColumn<MoneyInBatchDTO, Number>(proto().totalReceivedAmount(), new NumberCell()), i18n.tr("Total Received"), 100, Unit.PX);
        defColumn(new EntityFieldColumn<MoneyInBatchDTO, Number>(proto().numberOfReceipts(), new NumberCell()), i18n.tr("Number of Received"), 100, Unit.PX);
        defColumn(new EntityFieldColumn<MoneyInBatchDTO, String>(proto().postingStatus(), new TextCell()), i18n.tr("Posting Status"), 100, Unit.PX);
    }//@formatter:on

    @Override
    protected void onSort(String memberPath, boolean isAscending) {
        this.presenter.sort(memberPath, isAscending);
    }
}
