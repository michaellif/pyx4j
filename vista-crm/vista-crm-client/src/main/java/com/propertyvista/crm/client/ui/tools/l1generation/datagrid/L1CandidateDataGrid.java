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
package com.propertyvista.crm.client.ui.tools.l1generation.datagrid;

import java.math.BigDecimal;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.view.client.MultiSelectionModel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.l1generation.L1DelinquentLeaseSearchView;
import com.propertyvista.crm.client.ui.tools.l1generation.L1DelinquentLeaseSearchViewImpl;
import com.propertyvista.crm.client.ui.tools.l1generation.L1DelinquentLeaseSearchViewImpl.EntityFieldColumn;
import com.propertyvista.crm.client.ui.tools.l1generation.L1DelinquentLeaseSearchViewImpl.LeaseIdProvider;
import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;

public class L1CandidateDataGrid extends DataGrid<LegalActionCandidateDTO> {

    private static final I18n i18n = I18n.get(L1CandidateDataGrid.class);

    private static final int PAGE_SIZE = 100;

    private L1DelinquentLeaseSearchView.Presenter presenter;

    public L1CandidateDataGrid() {
        super(PAGE_SIZE, L1CandidateDataGridResources.getInstance(), LeaseIdProvider.INSTANCE);
        initColumns();
    }

    private void setPresenter(L1DelinquentLeaseSearchView.Presenter presenter) {
        this.presenter = presenter;
    }

    private void initColumns() {
        {
            Column<LegalActionCandidateDTO, Boolean> selectionColumn = new Column<LegalActionCandidateDTO, Boolean>(new CheckboxCell(true, false)) {
                @Override
                public Boolean getValue(LegalActionCandidateDTO object) {
                    return getSelectionModel() != null ? getSelectionModel().isSelected(object) : false;
                }
            };
            Header<Boolean> selectAllHeader = new Header<Boolean>(new CheckboxCell(true, true)) {
                {
                    setUpdater(new ValueUpdater<Boolean>() {
                        @Override
                        public void update(Boolean value) {
                            if (L1CandidateDataGrid.this.presenter != null) {
                                presenter.toggleSelectAll(value);
                            }
                        }
                    });
                }

                @Override
                public Boolean getValue() {
                    if (getSelectionModel() != null) {
                        if (getSelectionModel() instanceof MultiSelectionModel) {
                            return ((MultiSelectionModel<LegalActionCandidateDTO>) getSelectionModel()).getSelectedSet().size() == getRowCount();
                        } else {
                            // actually here we have to check every candidate has to be checked for selection
                            // i.e. for (candidate : allCandidates) { if (!getSelectionModel().isSelected(object)) return false }
                            // but whaterver, we use multiselection model
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            };

            this.addColumn(selectionColumn, selectAllHeader);
            this.setColumnWidth(selectionColumn, 40, Unit.PX);
        }

        LegalActionCandidateDTO proto = EntityFactory.getEntityPrototype(LegalActionCandidateDTO.class);
        defTextColumn(proto.leaseId(), 50, Unit.PX);
        defTextColumn(proto.propertyCode(), 50, Unit.PX);
        defTextColumn(proto.unit(), 50, Unit.PX);
        defTextColumn(proto.streetAddress(), 200, Unit.PX);

        {
            Column<LegalActionCandidateDTO, Number> owedAmountColumn = new Column<LegalActionCandidateDTO, Number>(new NumberCell(
                    NumberFormat.getFormat("$#,##0.00"))) {
                @Override
                public BigDecimal getValue(LegalActionCandidateDTO object) {
                    return object.l1FormReview().formData().totalRentOwing().getValue();
                }
            };
            owedAmountColumn.setCellStyleNames(L1CandidateDataGridStyles.L1DataGridMoneyCell.name());
            defColumn(owedAmountColumn, i18n.tr("Total Owed"), 50, Unit.PX);
        }

        {
            Column<LegalActionCandidateDTO, String> reviewRequiredColumn = new Column<LegalActionCandidateDTO, String>(new TextCell()) {
                @Override
                public String getValue(LegalActionCandidateDTO object) {
                    return object.isReviewed().isBooleanTrue() ? "" : "\u2713";
                }
            };
            defColumn(reviewRequiredColumn, i18n.tr("Requires Review?"), 50, Unit.PX);
        }

        {
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
            this.addColumn(reviewButtonColumn, SafeHtmlUtils.fromSafeConstant("<br>"));
            this.setColumnWidth(reviewButtonColumn, 50, Unit.PX);
        }
    }

    private void defTextColumn(IObject<String> columnField, double columWidth, Unit columnWidthUnit) {
        EntityFieldColumn<LegalActionCandidateDTO, String> column = new L1DelinquentLeaseSearchViewImpl.EntityFieldColumn<LegalActionCandidateDTO, String>(
                columnField, new TextCell());
        defColumn(column, columnField.getMeta().getCaption(), columWidth, columnWidthUnit);
    }

    private void defColumn(Column<LegalActionCandidateDTO, ?> column, String headerCaption, double columWidth, Unit columnWidthUnit) {
        this.addColumn(column, new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml(),
                new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml());
        this.setColumnWidth(column, columWidth, columnWidthUnit);
    }
}