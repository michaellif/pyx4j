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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.view.client.ProvidesKey;

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

    L1DelinquentLeaseSearchView.Presenter presenter;

    private L1CandidateDataGrid dataGrid;

    private SimplePager pager;

    private final LayoutPanel viewPanel;

    public L1DelinquentLeaseSearchViewImpl() {
        viewPanel = new LayoutPanel();
        setContentPane(viewPanel);
        setSize("100%", "100%");

        initToolbar();
        initDataGrid();
    }

    @Override
    public void setPresenter(L1DelinquentLeaseSearchView.Presenter presenter) {
        this.presenter = presenter;
        this.dataGrid.setPresenter(this.presenter);
    }

    protected void reviewCandidate(LegalActionCandidateDTO object) {
        this.presenter.reviewCandidate(object);
    }

    protected void fillCommonFields() {
        this.presenter.fillCommonFields();
    }

    private void initToolbar() {
        addHeaderToolbarItem(new Button(i18n.tr("Search...")));
        addHeaderToolbarItem(new Button(i18n.tr("Fill Out Common Fields..."), new Command() {
            @Override
            public void execute() {
                fillCommonFields();
            }
        }));
        addHeaderToolbarItem(new Button(i18n.tr("Issue L1's")));
    }

    private void initDataGrid() {
        dataGrid = new L1CandidateDataGrid();
        viewPanel.add(dataGrid);
        viewPanel.setWidgetLeftWidth(dataGrid, 0, Unit.PX, 100, Unit.PCT);
        viewPanel.setWidgetTopBottom(dataGrid, 0, Unit.PX, 31, Unit.PX);

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);

        // TODO this method doesn't help to center the pager on IE:
        pager.getElement().getStyle().setProperty("marginLeft", "auto");
        pager.getElement().getStyle().setProperty("marginRight", "auto");
        viewPanel.add(pager);
        viewPanel.setWidgetLeftRight(pager, 0, Unit.PX, 0, Unit.PX);
        viewPanel.setWidgetBottomHeight(pager, 0, Unit.PCT, 30, Unit.PX);
    }

}
