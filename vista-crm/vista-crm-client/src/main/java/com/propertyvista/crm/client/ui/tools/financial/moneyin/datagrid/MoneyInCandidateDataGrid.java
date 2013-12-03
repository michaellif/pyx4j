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

import java.math.BigDecimal;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.Column;

import com.pyx4j.forms.client.ui.formatters.MoneyFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityDataGrid;
import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityFieldColumn;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionCell;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionState;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView.Presenter;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInLeaseParticipantDTO;

public class MoneyInCandidateDataGrid extends EntityDataGrid<MoneyInCandidateDTO> {

    private static final I18n i18n = I18n.get(MoneyInCandidateDataGrid.class);

    /** This is one way */
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getFormat("$#,##0.00");

    private static final MoneyFormat MONEY_FORMAT = new MoneyFormat();

    private static final int PAGE_SIZE = 50;

    private Presenter presenter;

    public MoneyInCandidateDataGrid() {
        super(MoneyInCandidateDTO.class);
        setPageSize(PAGE_SIZE);
        initColumns();
    }

    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
    }

    private void initColumns() {//@formatter:off
        defTextColumn(proto().building(), i18n.tr("Building"), 40, Unit.PX);
        defTextColumn(proto().unit(), i18n.tr("Unit"), 40, Unit.PX);
        defTextColumn(proto().leaseId(), i18n.tr("Lease ID"), 40, Unit.PX);

        Column<MoneyInCandidateDTO, String> leaseParticipantsColumn = new Column<MoneyInCandidateDTO, String>(new TextCell()) {
            @Override public String getValue(MoneyInCandidateDTO object) { return renderLeaseParticipants(object); }
        };
        defColumn(leaseParticipantsColumn, i18n.tr("Tenants"), 100, Unit.PX);

        Column<MoneyInCandidateDTO, ObjectSelectionState<MoneyInLeaseParticipantDTO>> payerSelectionColumn = new Column<MoneyInCandidateDTO, ObjectSelectionState<MoneyInLeaseParticipantDTO>>(
                new ObjectSelectionCell<MoneyInLeaseParticipantDTO>(new PayerOptionFormat())) {
            @Override public ObjectSelectionState<MoneyInLeaseParticipantDTO> getValue(MoneyInCandidateDTO object) { return new PayerCandidateSelectionState(object); }
        };
        payerSelectionColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, ObjectSelectionState<MoneyInLeaseParticipantDTO>>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, ObjectSelectionState<MoneyInLeaseParticipantDTO> value) {
                presenter.setPayer(object, value.getSelectedOption());
            }
        });
        defColumn(payerSelectionColumn, i18n.tr("Payer"), 100, Unit.PX);

        Column<MoneyInCandidateDTO, Number> prepaymentsColumn = new EntityFieldColumn<MoneyInCandidateDTO, Number>(
                proto().prepayments(),
                new NumberCell(CURRENCY_FORMAT)
        );
        defColumn(prepaymentsColumn, i18n.tr("Prepayments"), 50, Unit.PX);

        Column<MoneyInCandidateDTO, Number> totalUnpaidColumn = new EntityFieldColumn<MoneyInCandidateDTO, Number>(
                proto().totalOutstanding(),
                new NumberCell(CURRENCY_FORMAT)
        );
        defColumn(totalUnpaidColumn, i18n.tr("Total Unpaid"), 50, Unit.PX);
        
        Column<MoneyInCandidateDTO, String> amountToPayColumn = new Column<MoneyInCandidateDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(MoneyInCandidateDTO object) {
                BigDecimal value = object.payment().payedAmount().getValue();
                return value != null ? CURRENCY_FORMAT.format(object.payment().payedAmount().getValue()) : "";
            }
        };
        amountToPayColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, String>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, String value) {               
                BigDecimal amount = null;
                try {
                    amount = MONEY_FORMAT.parse(value);
                } catch (Throwable e) {
                    // ignore
                }
                presenter.setAmount(object, amount);
            }
        });
        defColumn(amountToPayColumn, i18n.tr("Amount to Pay"), 50, Unit.PX);

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
    }//@formatter:on

    private String renderLeaseParticipants(MoneyInCandidateDTO candidate) {
        StringBuilder b = new StringBuilder();
        for (MoneyInLeaseParticipantDTO leaseParticipant : candidate.payerCandidates()) {
            b.append(leaseParticipant.name().getValue());
            b.append(", ");
        }
        return b.toString().trim();
    }

}
