/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.DefaultSelectionEventManager;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityDataGrid;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionCell;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionState;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView.Presenter;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInPayerOptionDTO;

public class MoneyInCandidateDataGrid extends EntityDataGrid<MoneyInCandidateDTO> {

    private static final I18n i18n = I18n.get(MoneyInCandidateDataGrid.class);

    private static final int PAGE_SIZE = 50;

    private Presenter presenter;

    public MoneyInCandidateDataGrid() {
        super(MoneyInCandidateDTO.class);
        setPageSize(PAGE_SIZE);
        initColumns();
    }

    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
        this.setSelectionModel(this.presenter.getSelectionModel(), DefaultSelectionEventManager.<MoneyInCandidateDTO> createDefaultManager());
        this.presenter.getDataProvider().addDataDisplay(this);
    }

    private void initColumns() {
        defTextColumn(proto().building(), 50, Unit.PX);
        defTextColumn(proto().unit(), 50, Unit.PX);
        defTextColumn(proto().leaseId(), 50, Unit.PX);

        Column<MoneyInCandidateDTO, ObjectSelectionState<MoneyInPayerOptionDTO>> payerSelectionColumn = new Column<MoneyInCandidateDTO, ObjectSelectionState<MoneyInPayerOptionDTO>>(
                new ObjectSelectionCell<MoneyInPayerOptionDTO>(new PayerOptionFormat())) {
            @Override
            public ObjectSelectionState<MoneyInPayerOptionDTO> getValue(MoneyInCandidateDTO object) {
                return new PayerCandidateSelectionState(object);
            }
        };
        payerSelectionColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, ObjectSelectionState<MoneyInPayerOptionDTO>>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, ObjectSelectionState<MoneyInPayerOptionDTO> value) {
                presenter.setPayer(object, value.getSelectedOption());
            }
        });
        defColumn(payerSelectionColumn, i18n.tr("Payer"), 50, Unit.PX);

        Column<MoneyInCandidateDTO, String> processColumn = new Column<MoneyInCandidateDTO, String>(new ButtonCell()) {
            @Override
            public String getValue(MoneyInCandidateDTO object) {
                return object.processPayment().isBooleanTrue() ? i18n.tr("Don't Process") : i18n.tr("Process");
            }
        };
        processColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, String>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, String value) {
                presenter.setProcessCandidate(object, !object.processPayment().isBooleanTrue());
            }
        });
        defColumn(processColumn, "", 50, Unit.PX);
    }
}
