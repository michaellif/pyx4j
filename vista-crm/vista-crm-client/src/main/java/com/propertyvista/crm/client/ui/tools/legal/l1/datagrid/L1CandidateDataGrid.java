/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.datagrid;

import java.math.BigDecimal;
import java.util.Arrays;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.datagrid.DataGridScrollFixerHack;
import com.propertyvista.crm.client.ui.tools.common.datagrid.EntityFieldColumn;
import com.propertyvista.crm.client.ui.tools.common.datagrid.MultiSelectorCell;
import com.propertyvista.crm.client.ui.tools.common.datagrid.MultiSelectorState;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGridResources;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel.MultiSelectorCellModelFactory;
import com.propertyvista.crm.client.ui.tools.common.datagrid.VistaDataGridStyles;
import com.propertyvista.crm.client.ui.tools.legal.l1.L1DelinquentLeaseSearchView;
import com.propertyvista.crm.client.ui.tools.legal.l1.L1DelinquentLeaseSearchViewImpl.LeaseIdProvider;
import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;

public class L1CandidateDataGrid extends DataGrid<LegalActionCandidateDTO> {

    private static final I18n i18n = I18n.get(L1CandidateDataGrid.class);

    private static final int PAGE_SIZE = 50;

    private static SelectionPresetModel.MultiSelectorCellModelFactory SELECTION_STATES_FACTORY = new MultiSelectorCellModelFactory(
            Arrays.<Object> asList(L1CandidateSelectionPresets.values()));

    private L1DelinquentLeaseSearchView.Presenter presenter;

    public L1CandidateDataGrid() {
        super(PAGE_SIZE, VistaDataGridResources.getInstance(), LeaseIdProvider.INSTANCE);

        initColumns();
        DataGridScrollFixerHack.apply(this);

    }

    public void setPresenter(L1DelinquentLeaseSearchView.Presenter presenter) {
        this.presenter = presenter;
        this.setSelectionModel(this.presenter.getSelectionModel(), DefaultSelectionEventManager.<LegalActionCandidateDTO> createCheckboxManager(0));
        this.presenter.getDataProvider().addDataDisplay(this);

    }

    private void initColumns() {
        {

            Column<LegalActionCandidateDTO, Boolean> selectionColumn = new Column<LegalActionCandidateDTO, Boolean>(new CheckboxCell(true, false)) {
                @Override
                public Boolean getValue(LegalActionCandidateDTO object) {
                    return getSelectionModel() != null ? getSelectionModel().isSelected(object) : false;
                }
            };

            Header<SelectionPresetModel> selectionColumnHeader = new Header<SelectionPresetModel>(new MultiSelectorCell(SELECTION_STATES_FACTORY)) {
                @Override
                public SelectionPresetModel getValue() {
                    return L1CandidateDataGrid.this.getSelectionState();
                }
            };
            selectionColumnHeader.setUpdater(new ValueUpdater<SelectionPresetModel>() {
                @Override
                public void update(SelectionPresetModel value) {
                    if (L1CandidateDataGrid.this.presenter != null) {
                        L1CandidateDataGrid.this.presenter.updateSelection(value);
                    }
                }
            });

            this.addColumn(selectionColumn, selectionColumnHeader);
            this.setColumnWidth(selectionColumn, 40, Unit.PX);

        }

        LegalActionCandidateDTO proto = EntityFactory.getEntityPrototype(LegalActionCandidateDTO.class);

        defTextColumn(proto.leaseId(), 50, Unit.PX);
        defTextColumn(proto.propertyCode(), 50, Unit.PX);
        defTextColumn(proto.unit(), 50, Unit.PX);
        defTextColumn(proto.streetAddress(), 100, Unit.PX);

        // TODO unite as under a single group header
        {
            Column<LegalActionCandidateDTO, Number> owedAmountColumn = new Column<LegalActionCandidateDTO, Number>(new NumberCell(
                    NumberFormat.getFormat("$#,##0.00"))) {
                @Override
                public BigDecimal getValue(LegalActionCandidateDTO object) {
                    return object.l1FormReview().formData().owedRent().totalRentOwing().getValue();
                }
            };
            owedAmountColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
            defColumn(owedAmountColumn, i18n.tr("Rent Owing"), 50, Unit.PX);
        }
        {
            Column<LegalActionCandidateDTO, Number> owedAmountColumn = new Column<LegalActionCandidateDTO, Number>(new NumberCell(
                    NumberFormat.getFormat("$#,##0.00"))) {
                @Override
                public BigDecimal getValue(LegalActionCandidateDTO object) {
                    return object.l1FormReview().formData().owedNsfCharges().nsfTotalChargeOwed().getValue();
                }
            };
            owedAmountColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
            defColumn(owedAmountColumn, i18n.tr("NSF Owing"), 50, Unit.PX);
        }
        {
            Column<LegalActionCandidateDTO, Number> owedAmountColumn = new Column<LegalActionCandidateDTO, Number>(new NumberCell(
                    NumberFormat.getFormat("$#,##0.00"))) {
                @Override
                public BigDecimal getValue(LegalActionCandidateDTO object) {
                    return object.l1FormReview().formData().owedSummary().total().getValue();
                }
            };
            owedAmountColumn.setCellStyleNames(VistaDataGridStyles.VistaMoneyCell.name());
            defColumn(owedAmountColumn, i18n.tr("Total Owing"), 50, Unit.PX);
        }

        // TODO use cell that renders warnings as tooltips or popup
        Column<LegalActionCandidateDTO, String> reviewRequiredColumn = new Column<LegalActionCandidateDTO, String>(new TextCell()) {
            @Override
            public String getValue(LegalActionCandidateDTO object) {
                return CommonsStringUtils.isEmpty(object.warnings().getValue()) ? "" : "\u2713";
            }
        };

        defColumn(reviewRequiredColumn, i18n.tr("Requires Review?"), 50, Unit.PX);

        Column<LegalActionCandidateDTO, LegalActionCandidateDTO> reviewButtonColumn = new Column<LegalActionCandidateDTO, LegalActionCandidateDTO>(
                new ActionCell<LegalActionCandidateDTO>(i18n.tr("Review"), new Delegate<LegalActionCandidateDTO>() {
                    @Override
                    public void execute(LegalActionCandidateDTO object) {
                        presenter.reviewCandidate(object);
                    }
                })) {

            @Override
            public LegalActionCandidateDTO getValue(LegalActionCandidateDTO object) {
                return object;
            }

        };
        defColumn(reviewButtonColumn, "", 50, Unit.PX);

        Column<LegalActionCandidateDTO, Boolean> isReviewedColumn = new Column<LegalActionCandidateDTO, Boolean>(new CheckboxCell(false, false)) {
            @Override
            public Boolean getValue(LegalActionCandidateDTO object) {
                return object.isReviewed().isBooleanTrue();
            }
        };
        isReviewedColumn.setFieldUpdater(new FieldUpdater<LegalActionCandidateDTO, Boolean>() {
            @Override
            public void update(int index, LegalActionCandidateDTO object, Boolean value) {
                object.isReviewed().setValue(value);
            }
        });
        defColumn(isReviewedColumn, i18n.tr("Reviewed?"), 50, Unit.PX);

    }

    private void defTextColumn(IObject<String> columnField, double columWidth, Unit columnWidthUnit) {
        EntityFieldColumn<LegalActionCandidateDTO, String> column = new EntityFieldColumn<LegalActionCandidateDTO, String>(columnField, new TextCell());
        defColumn(column, columnField.getMeta().getCaption(), columWidth, columnWidthUnit);
    }

    private void defColumn(Column<LegalActionCandidateDTO, ?> column, String headerCaption, double columWidth, Unit columnWidthUnit) {
        this.addColumn(column, new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml(),
                new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml());
        this.setColumnWidth(column, columWidth, columnWidthUnit);
    }

    private SelectionPresetModel getSelectionState() {
        if (getSelectionModel() != null && getSelectionModel() instanceof MultiSelectionModel) {
            MultiSelectionModel<LegalActionCandidateDTO> selectionModel = ((MultiSelectionModel<LegalActionCandidateDTO>) getSelectionModel());
            MultiSelectorState state = selectionModel.getSelectedSet().size() > 0 ? MultiSelectorState.Some : MultiSelectorState.None;
            state = selectionModel.getSelectedSet().size() == getRowCount() ? MultiSelectorState.All : state;

            if (state == MultiSelectorState.All || state == MultiSelectorState.None) {
                return SELECTION_STATES_FACTORY.makeModel(state, null);
            } else {
                Object preset = L1CandidateSelectionPresets.Reviewed;
                state = MultiSelectorState.Preset;
                for (LegalActionCandidateDTO c : selectionModel.getSelectedSet()) {
                    if (!c.isReviewed().isBooleanTrue()) {
                        preset = null;
                        state = MultiSelectorState.Some;
                    }
                }
                return SELECTION_STATES_FACTORY.makeModel(state, preset);
            }
        }
        return SELECTION_STATES_FACTORY.makeNone();
    }
}