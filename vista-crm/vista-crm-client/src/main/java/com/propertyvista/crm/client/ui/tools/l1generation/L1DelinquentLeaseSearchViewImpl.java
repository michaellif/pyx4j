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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ProvidesKey;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.tools.l1generation.datagrid.L1CandidateDataGrid;
import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;

public class L1DelinquentLeaseSearchViewImpl extends AbstractPrimePane implements L1DelinquentLeaseSearchView {

    static final I18n i18n = I18n.get(L1DelinquentLeaseSearchView.class);

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

    L1DelinquentLeaseSearchView.Presenter presenter;

    private final L1CandidateDataGrid dataGrid;

    private final SimplePager pager;

    public L1DelinquentLeaseSearchViewImpl() {
        addHeaderToolbarItem(new Button(i18n.tr("Search...")));
        addHeaderToolbarItem(new Button(i18n.tr("Fill Out Common Fields..."), new Command() {
            @Override
            public void execute() {
                fillCommonFields();
            }
        }));
        addHeaderToolbarItem(new Button(i18n.tr("Issue L1's")));

        LayoutPanel panel = new LayoutPanel();
        dataGrid = new L1CandidateDataGrid();

        panel.add(dataGrid);
        panel.setWidgetLeftWidth(dataGrid, 0, Unit.PX, 100, Unit.PCT);
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

    protected void reviewCandidate(LegalActionCandidateDTO object) {
        this.presenter.reviewCandidate(object);
    }

    protected void fillCommonFields() {
        this.presenter.fillCommonFields();
    }

}
