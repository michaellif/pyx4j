/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4.datagrid;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityFieldColumn;
import com.propertyvista.crm.client.ui.tools.common.datagrid.MultiSelectorCell;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel.MultiSelectorCellModelFactory;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGrid;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGridStyles;
import com.propertyvista.crm.client.ui.tools.legal.n4.N4CreateBatchView;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;

public class LegalNoticeCandidateDataGrid extends VistaDataGrid<LegalNoticeCandidateDTO> {

    private static final I18n i18n = I18n.get(LegalNoticeCandidateDataGrid.class);

    //@formatter:off
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getFormat("$#,##0.00");
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
    //@formatter:on

    private N4CreateBatchView.Presenter presenter;

    public LegalNoticeCandidateDataGrid() {
        super(LegalNoticeCandidateDTO.class, false);
        initColumns();
    }

    public void setPresenter(N4CreateBatchView.Presenter presenter) {
        this.presenter = presenter;
    }

    private void initColumns() {
        initSelectionColumn();
        initBuildingColumn();
        initUnitColumn();
        // TODO initAddressColumn(); 
        initLeaseColumn();
        initMoveInColumn();
        initMoveOutColumn();
        initAmountOwedColumn();
        initN4IssuedColumn();
    }

    private void initSelectionColumn() {

        Column<LegalNoticeCandidateDTO, Boolean> selectionColumn = new Column<LegalNoticeCandidateDTO, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(LegalNoticeCandidateDTO object) {
                return getSelectionModel() != null ? getSelectionModel().isSelected(object) : false;
            }
        };

        Header<SelectionPresetModel> selectionColumnHeader = new Header<SelectionPresetModel>(new MultiSelectorCell(new MultiSelectorCellModelFactory(
                new ArrayList<Object>()))) {
            @Override
            public SelectionPresetModel getValue() {
                return LegalNoticeCandidateDataGrid.this.presenter.getSelectionState();
            }
        };
        selectionColumnHeader.setUpdater(new ValueUpdater<SelectionPresetModel>() {
            @Override
            public void update(SelectionPresetModel value) {
                if (LegalNoticeCandidateDataGrid.this.presenter != null) {
                    LegalNoticeCandidateDataGrid.this.presenter.updateSelection(value);
                }
            }
        });

        this.addColumn(selectionColumn, selectionColumnHeader);
        this.setColumnWidth(selectionColumn, 40, Unit.PX);

    }

    private void initBuildingColumn() {
        Column<?, ?> c = defTextColumn(proto().building(), i18n.tr("Building"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().building().getPath().toString());
    }

    private void initUnitColumn() {
        Column<?, ?> c = defTextColumn(proto().unit(), i18n.tr("Unit"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().unit().getPath().toString());
    }

    private void initLeaseColumn() {
        Column<?, ?> c = defTextColumn(proto().leaseIdString(), i18n.tr("Lease"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().leaseId().getPath().toString());
    }

    private void initMoveInColumn() {
        Column<LegalNoticeCandidateDTO, Date> totalUnpaidColumn = new EntityFieldColumn<LegalNoticeCandidateDTO, Date>(proto().moveIn(), new DateCell(
                DATE_FORMAT));
        totalUnpaidColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
        defColumn(totalUnpaidColumn, i18n.tr("Move In"), 50, Unit.PX);
        totalUnpaidColumn.setSortable(true);
        totalUnpaidColumn.setDataStoreName(proto().moveIn().getPath().toString());
    }

    private void initMoveOutColumn() {
        Column<LegalNoticeCandidateDTO, Date> totalUnpaidColumn = new EntityFieldColumn<LegalNoticeCandidateDTO, Date>(proto().moveOut(), new DateCell(
                DATE_FORMAT));
        totalUnpaidColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
        defColumn(totalUnpaidColumn, i18n.tr("Move Out"), 50, Unit.PX);
        totalUnpaidColumn.setSortable(true);
        totalUnpaidColumn.setDataStoreName(proto().moveOut().getPath().toString());
    }

    private void initAmountOwedColumn() {
        Column<LegalNoticeCandidateDTO, Number> totalUnpaidColumn = new EntityFieldColumn<LegalNoticeCandidateDTO, Number>(proto().amountOwed(),
                new NumberCell(CURRENCY_FORMAT));
        totalUnpaidColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
        defColumn(totalUnpaidColumn, i18n.tr("Amount Owed"), 50, Unit.PX);
        totalUnpaidColumn.setSortable(true);
        totalUnpaidColumn.setDataStoreName(proto().amountOwed().getPath().toString());
    }

    private void initN4IssuedColumn() {
        Column<?, ?> c = defTextColumn(proto().n4Issued(), i18n.tr("Past N4's"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().n4Issued().getPath().toString());
    }

}
