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
import java.text.ParseException;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.ProvidesKey;

import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.formatters.MoneyFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityFieldColumn;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectEditCell;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectEditCell.StyleNames;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionCell;
import com.propertyvista.crm.client.ui.tools.common.datagrid.ObjectSelectionState;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGrid;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGridStyles;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView.Presenter;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInLeaseParticipantDTO;

public class MoneyInCandidateDataGrid extends VistaDataGrid<MoneyInCandidateDTO> {

    private static final I18n i18n = I18n.get(MoneyInCandidateDataGrid.class);

    /** This is one way */
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getFormat("$#,##0.00");

    private static final int PAGE_SIZE = 50;

    private Presenter presenter;

    private ObjectEditCell<BigDecimal> amountToPayCell;

    private ObjectEditCell.Style moneyEditCellStyle;

    private ObjectEditCell<String> checkNumberCell;

    public MoneyInCandidateDataGrid() {
        super(MoneyInCandidateDTO.class, false);

        moneyEditCellStyle = new ObjectEditCell.DefaultStyle() {//@formatter:off
            @Override public String objectEditCell() {
                return StyleNames.ObjectEditCell.name() + " " + VistaDataGridStyles.VistaMoneyCell.name();
            };
        };//@formatter:on

        initColumns();
    }

    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ProvidesKey<MoneyInCandidateDTO> getKeyProvider() {
        return this.presenter;
    }

    private void initColumns() {
        initBuildingColumn();
        initUnitColumn();
        initLeaseColumn();
        initTenantsColumn();
        initPayerColumn();
        initTotalUnpaidColumn();
        initAmountToPayColumn();
        initCheckNumberColumn();
        initProcessColumn();
    }

    private Column<?, ?> initBuildingColumn() {
        Column<?, ?> c = defTextColumn(proto().building(), i18n.tr("Building"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().building().getPath().toString());
        return c;
    }

    private Column<?, ?> initUnitColumn() {
        Column<?, ?> c = defTextColumn(proto().unit(), i18n.tr("Unit"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().unit().getPath().toString());
        return c;
    }

    private Column<?, ?> initLeaseColumn() {
        Column<?, ?> c = defTextColumn(proto().leaseId(), i18n.tr("Lease"), 40, Unit.PX);
        c.setSortable(true);
        c.setDataStoreName(proto().leaseId().getPath().toString());
        return c;
    }

    private Column<?, ?> initTenantsColumn() {
        Column<MoneyInCandidateDTO, String> leaseParticipantsColumn = new Column<MoneyInCandidateDTO, String>(new TextCell()) {
            @Override
            public String getValue(MoneyInCandidateDTO object) {
                return renderLeaseParticipants(object);
            }
        };
        defColumn(leaseParticipantsColumn, i18n.tr("Tenants"), 100, Unit.PX);
        leaseParticipantsColumn.setSortable(true);
        leaseParticipantsColumn.setDataStoreName(proto().payerCandidates().getPath().toString());
        return leaseParticipantsColumn;
    }

    private Column<?, ?> initPayerColumn() {
        Column<MoneyInCandidateDTO, ObjectSelectionState<MoneyInLeaseParticipantDTO>> payerSelectionColumn = new Column<MoneyInCandidateDTO, ObjectSelectionState<MoneyInLeaseParticipantDTO>>(
                new ObjectSelectionCell<MoneyInLeaseParticipantDTO>(new PayerOptionFormat())) {
            @Override
            public ObjectSelectionState<MoneyInLeaseParticipantDTO> getValue(MoneyInCandidateDTO object) {
                return new PayerCandidateSelectionState(object);
            }
        };
        payerSelectionColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, ObjectSelectionState<MoneyInLeaseParticipantDTO>>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, ObjectSelectionState<MoneyInLeaseParticipantDTO> value) {
                presenter.setPayer(object, value.getSelectedOption());
            }
        });
        defColumn(payerSelectionColumn, i18n.tr("Payer"), 100, Unit.PX);
        payerSelectionColumn.setSortable(true);
        payerSelectionColumn.setDataStoreName(proto().payment().payerTenantIdStub().getPath().toString());
        return payerSelectionColumn;
    }

    private Column<MoneyInCandidateDTO, Number> initTotalUnpaidColumn() {
        Column<MoneyInCandidateDTO, Number> totalUnpaidColumn = new EntityFieldColumn<MoneyInCandidateDTO, Number>(proto().totalOutstanding(), new NumberCell(
                CURRENCY_FORMAT));
        totalUnpaidColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
        defColumn(totalUnpaidColumn, i18n.tr("Total Unpaid"), 50, Unit.PX);
        totalUnpaidColumn.setSortable(true);
        totalUnpaidColumn.setDataStoreName(proto().totalOutstanding().getPath().toString());
        return totalUnpaidColumn;
    }

    private Column<MoneyInCandidateDTO, BigDecimal> initAmountToPayColumn() {
        amountToPayCell = new ObjectEditCell<BigDecimal>(new MoneyFormat(), moneyEditCellStyle);
        Column<MoneyInCandidateDTO, BigDecimal> amountToPayColumn = new Column<MoneyInCandidateDTO, BigDecimal>(amountToPayCell) {
            @Override
            public BigDecimal getValue(MoneyInCandidateDTO object) {
                Object key = MoneyInCandidateDataGrid.this.presenter.getKey(object);
                amountToPayCell.setViewData(key, MoneyInCandidateDataGrid.this.presenter.getValidationErrors(object, object.payment().payedAmount().getPath()));
                return object.payment().payedAmount().getValue();
            }
        };
        amountToPayColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, BigDecimal>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, BigDecimal amount) {
                presenter.setAmount(object, amount);
            }
        });
        amountToPayColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
        defColumn(amountToPayColumn, i18n.tr("Amount to Pay"), 50, Unit.PX);

        amountToPayColumn.setSortable(true);
        amountToPayColumn.setDataStoreName(proto().payment().payedAmount().getPath().toString());
        return amountToPayColumn;
    }

    private Column<MoneyInCandidateDTO, String> initCheckNumberColumn() {
        IFormat<String> checkFormat = new IFormat<String>() {
            @Override
            public String format(String value) {
                return value == null ? "" : value;
            }

            @Override
            public String parse(String string) throws ParseException {
                if (string == null || !string.trim().matches("[0-9]*")) {
                    throw new ParseException(i18n.tr("Check number may contain only digits"), 0);
                }
                return string;
            }
        };
        checkNumberCell = new ObjectEditCell<String>(checkFormat, moneyEditCellStyle);
        Column<MoneyInCandidateDTO, String> checkNumberColumn = new Column<MoneyInCandidateDTO, String>(checkNumberCell) {
            @Override
            public String getValue(MoneyInCandidateDTO object) {
                checkNumberCell.setViewData(MoneyInCandidateDataGrid.this.presenter.getKey(object),
                        MoneyInCandidateDataGrid.this.presenter.getValidationErrors(object, object.payment().checkNumber().getPath()));
                return object.payment().checkNumber().getValue();
            }
        };
        checkNumberColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, String>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, String checkNumber) {
                presenter.setCheckNumber(object, checkNumber);
            }
        });
        checkNumberColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
        defColumn(checkNumberColumn, i18n.tr("Ref #"), 40, Unit.PX);

        checkNumberColumn.setSortable(true);
        checkNumberColumn.setDataStoreName(proto().payment().checkNumber().getPath().toString());

        return checkNumberColumn;
    }

    private Column<MoneyInCandidateDTO, ?> initProcessColumn() {
        Column<MoneyInCandidateDTO, ?> processColumn = createProcessColumn();
        defColumn(processColumn, createProcessColumnTitle(), 50, Unit.PX);
        processColumn.setSortable(true);
        processColumn.setDataStoreName(proto().processPayment().getPath().toString());
        return processColumn;
    }

    // TODO this method doesn't indicate in any way that it renders 'process' column, maybe rename it to defLastColumn? 
    protected Column<MoneyInCandidateDTO, ?> createProcessColumn() {
        Column<MoneyInCandidateDTO, Boolean> processColumn = new Column<MoneyInCandidateDTO, Boolean>(new CheckboxCell(false, false)) {
            @Override
            public Boolean getValue(MoneyInCandidateDTO object) {
                return object.processPayment().isBooleanTrue();
            }
        };
        processColumn.setFieldUpdater(new FieldUpdater<MoneyInCandidateDTO, Boolean>() {
            @Override
            public void update(int index, MoneyInCandidateDTO object, Boolean value) {
                presenter.setProcessCandidate(object, value);
            }
        });
        return processColumn;
    }

    protected String createProcessColumnTitle() {
        return i18n.tr("Process?");
    }

    private String renderLeaseParticipants(MoneyInCandidateDTO candidate) {
        StringBuilder b = new StringBuilder();
        for (MoneyInLeaseParticipantDTO leaseParticipant : candidate.payerCandidates()) {
            b.append(leaseParticipant.name().getValue());
            b.append(", ");
        }
        return b.toString().trim();
    }

}
