/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;

import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;

public class L1DelinquentLeaseSearchViewImpl extends AbstractPrimePane implements L1DelinquentLeaseSearchView {

    private static final I18n i18n = I18n.get(L1DelinquentLeaseSearchView.class);

    public static class LeaseIdProvider implements ProvidesKey<LegalActionCandidateDTO> {

        public static LeaseIdProvider INSTANCE = new LeaseIdProvider();

        private LeaseIdProvider() {
        }

        @Override
        public Object getKey(LegalActionCandidateDTO item) {
            return item.leaseIdStub().getPrimaryKey();
        }

    }

    public static class EntityFieldColumn<E extends IEntity, DataType> extends Column<E, DataType> {

        private final Path fieldPath;

        public EntityFieldColumn(IObject<DataType> field, Cell<DataType> cell) {
            super(cell);
            this.fieldPath = field.getPath();
        }

        @Override
        public DataType getValue(E object) {
            return (DataType) object.getMember(fieldPath).getValue();
        }

    }

    public class LegalActionCandidateDataGrid extends DataGrid<LegalActionCandidateDTO> {

        public LegalActionCandidateDataGrid() {
            super(LeaseIdProvider.INSTANCE);
            initColumns();
        }

        private void initColumns() {
            {
                Column<LegalActionCandidateDTO, Boolean> selectionColumn = new Column<LegalActionCandidateDTO, Boolean>(new CheckboxCell(true, false)) {
                    @Override
                    public Boolean getValue(LegalActionCandidateDTO object) {
                        return LegalActionCandidateDataGrid.this.getSelectionModel() != null ? LegalActionCandidateDataGrid.this.getSelectionModel()
                                .isSelected(object) : false;
                    }
                };
                Header<Boolean> selectAllHeader = new Header<Boolean>(new CheckboxCell(true, true)) {
                    {
                        setUpdater(new ValueUpdater<Boolean>() {
                            @Override
                            public void update(Boolean value) {
                                L1DelinquentLeaseSearchViewImpl.this.presenter.toggleSelectAll(value);
                            }
                        });
                    }

                    @Override
                    public Boolean getValue() {
                        if (LegalActionCandidateDataGrid.this.getSelectionModel() != null) {
                            // TODO do it via presenter
                            return ((MultiSelectionModel<LegalActionCandidateDTO>) LegalActionCandidateDataGrid.this.getSelectionModel()).getSelectedSet()
                                    .size() == LegalActionCandidateDataGrid.this.getRowCount();
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

            Column<LegalActionCandidateDTO, String> owedAmountColumn = new Column<LegalActionCandidateDTO, String>(new TextCell()) {
                @Override
                public String getValue(LegalActionCandidateDTO object) {
                    return SimpleMessageFormat.format("{0,number,$#,##0.00}", object.owedAmount().getValue());
                }
            };
            defColumn(owedAmountColumn, i18n.tr("Owed Amount"), 100, Unit.PX);
        }

        private void defTextColumn(IObject<String> columnField, double columWidth, Unit columnWidthUnit) {
            EntityFieldColumn<LegalActionCandidateDTO, String> column = new EntityFieldColumn<LegalActionCandidateDTO, String>(columnField, new TextCell());
            defColumn(column, columnField.getMeta().getCaption(), columWidth, columnWidthUnit);
        }

        private void defColumn(Column<LegalActionCandidateDTO, ?> column, String headerCaption, double columWidth, Unit columnWidthUnit) {
            this.addColumn(column, new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml());
            this.setColumnWidth(column, columWidth, columnWidthUnit);
        }
    }

    private L1DelinquentLeaseSearchView.Presenter presenter;

    private final LegalActionCandidateDataGrid dataGrid;

    private final SimplePager pager;

    public L1DelinquentLeaseSearchViewImpl() {
        LayoutPanel panel = new LayoutPanel();

        dataGrid = new LegalActionCandidateDataGrid();

        panel.add(dataGrid);
        panel.setWidgetLeftRight(dataGrid, 0, Unit.PX, 30, Unit.PX);
        panel.setWidgetTopBottom(dataGrid, 0, Unit.PX, 31, Unit.PX);

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.getElement().getStyle().setProperty("marginLeft", "auto");
        pager.getElement().getStyle().setProperty("marginRight", "auto");
        panel.add(pager);
        panel.setWidgetLeftRight(pager, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetBottomHeight(pager, 0, Unit.PCT, 30, Unit.PX);

        setContentPane(panel);
        setSize("100%", "100%");
    }

    @Override
    public void setPresenter(L1DelinquentLeaseSearchView.Presenter presenter) {
        this.presenter = presenter;
        this.dataGrid.setSelectionModel(this.presenter.getSelectionModel(), DefaultSelectionEventManager.<LegalActionCandidateDTO> createCheckboxManager());
        this.presenter.getDataProvider().addDataDisplay(this.dataGrid);

    }

}
