/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.HasData;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid.MoneyInCandidateDataGrid;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.forms.MoneyInCandidateSearchCriteriaForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateSearchCriteriaDTO;

public class MoneyInCreateBatchViewImpl extends AbstractPrimePane implements MoneyInCreateBatchView {

    private static final I18n i18n = I18n.get(MoneyInCreateBatchViewImpl.class);

    private MoneyInCreateBatchView.Presenter presenter;

    private final LayoutPanel viewPanel;

    private LayoutPanel searchBar;

    private MoneyInCandidateDataGrid searchCandidateDataGrid;

    private MoneyInCandidateDataGrid selectedForProcessingDataGrid;

    private MoneyInCandidateSearchCriteriaForm searchForm;

    public MoneyInCreateBatchViewImpl() {
        initToolBars();

        viewPanel = initViewPanel();

        viewPanel.add(searchBar = initSearchBar());
        viewPanel.setWidgetTopHeight(searchBar, 0, Unit.PX, 100, Unit.PX);
        viewPanel.setWidgetLeftRight(searchBar, 0, Unit.PX, 0, Unit.PX);

        LayoutPanel gridsHolder = new LayoutPanel();
        viewPanel.add(gridsHolder);
        viewPanel.setWidgetTopBottom(gridsHolder, 100, Unit.PX, 0, Unit.PX);
        viewPanel.setWidgetLeftRight(gridsHolder, 0, Unit.PX, 0, Unit.PX);

        {
            LayoutPanel foundHolder = new LayoutPanel();
            gridsHolder.add(foundHolder);
            gridsHolder.setWidgetTopBottom(foundHolder, 0, Unit.PX, 50, Unit.PCT);
            gridsHolder.setWidgetLeftRight(foundHolder, 0, Unit.PX, 0, Unit.PX);

            // found:
            foundHolder.add(searchCandidateDataGrid = new MoneyInCandidateDataGrid());
            foundHolder.setWidgetTopBottom(searchCandidateDataGrid, 0, Unit.PX, 33, Unit.PX);
            foundHolder.setWidgetLeftRight(searchCandidateDataGrid, 0, Unit.PX, 0, Unit.PX);

            SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
            SimplePager searchResultsPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
            searchResultsPager.setDisplay(searchCandidateDataGrid);

            HorizontalPanel searchResultsPagerHolder = new HorizontalPanel();
            searchResultsPagerHolder.setWidth("100%");
            searchResultsPagerHolder.add(searchResultsPager);
            searchResultsPagerHolder.setCellHorizontalAlignment(searchResultsPager, HasHorizontalAlignment.ALIGN_CENTER);

            foundHolder.add(searchResultsPagerHolder);
            foundHolder.setWidgetBottomHeight(searchResultsPagerHolder, 24, Unit.PX, 24, Unit.PX);
            foundHolder.setWidgetLeftRight(searchResultsPagerHolder, 0, Unit.PX, 0, Unit.PX);
        }

        {
            LayoutPanel selectedHolder = new LayoutPanel();
            gridsHolder.add(selectedHolder);
            gridsHolder.setWidgetTopBottom(selectedHolder, 51, Unit.PCT, 0, Unit.PX);
            gridsHolder.setWidgetLeftRight(selectedHolder, 0, Unit.PX, 0, Unit.PX);

            // selected:
            HTML selectedHeader = new HTML(i18n.tr("Click \"Create Batch\" to Process the Following Payments:"));
            selectedHeader.getElement().getStyle().setTextAlign(TextAlign.CENTER);
            selectedHeader.getElement().getStyle().setLineHeight(30, Unit.PX);
            selectedHeader.getElement().getStyle().setFontWeight(FontWeight.BOLD);

            selectedHolder.add(selectedHeader);
            selectedHolder.setWidgetTopHeight(selectedHeader, 0, Unit.PX, 30, Unit.PX);
            selectedHolder.setWidgetLeftRight(selectedHeader, 0, Unit.PX, 0, Unit.PX);

            selectedHolder.add(selectedForProcessingDataGrid = new MoneyInCandidateDataGrid() {
                @Override
                protected void defProcessColumn() {
                    Column<MoneyInCandidateDTO, MoneyInCandidateDTO> processColumn = new Column<MoneyInCandidateDTO, MoneyInCandidateDTO>(
                            new ActionCell<MoneyInCandidateDTO>(i18n.tr("Remove"), new Delegate<MoneyInCandidateDTO>() {
                                @Override
                                public void execute(MoneyInCandidateDTO object) {
                                    presenter.setProcessCandidate(object, false);
                                }

                            })) {

                        @Override
                        public MoneyInCandidateDTO getValue(MoneyInCandidateDTO object) {
                            return object;
                        }

                    };
                    defColumn(processColumn, "", 50, Unit.PX);
                }
            });
            selectedHolder.setWidgetTopBottom(selectedForProcessingDataGrid, 31, Unit.PX, 33, Unit.PX);
            selectedHolder.setWidgetLeftRight(selectedForProcessingDataGrid, 0, Unit.PX, 0, Unit.PX);

            SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
            SimplePager selectedPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
            selectedPager.setDisplay(selectedForProcessingDataGrid);
            HorizontalPanel selectedPagerHolder = new HorizontalPanel();
            selectedPagerHolder.setWidth("100%");
            selectedPagerHolder.add(selectedPager);
            selectedPagerHolder.setCellHorizontalAlignment(selectedPager, HasHorizontalAlignment.ALIGN_CENTER);

            selectedHolder.add(selectedPagerHolder);
            selectedHolder.setWidgetBottomHeight(selectedPagerHolder, 24, Unit.PX, 24, Unit.PX);
            selectedHolder.setWidgetLeftRight(selectedPagerHolder, 0, Unit.PX, 0, Unit.PX);
        }

    }

    @Override
    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
        this.searchCandidateDataGrid.setPresenter(presenter);
        this.selectedForProcessingDataGrid.setPresenter(presenter);
    }

    @Override
    public MoneyInCandidateSearchCriteriaDTO getSearchCriteria() {
        return this.searchForm.getValue();
    }

    @Override
    public HasData<MoneyInCandidateDTO> searchResults() {
        return this.searchCandidateDataGrid;
    }

    @Override
    public HasData<MoneyInCandidateDTO> selectedForProcessing() {
        return selectedForProcessingDataGrid;
    }

    private LayoutPanel initViewPanel() {
        LayoutPanel viewPanel = new LayoutPanel();
        setContentPane(viewPanel);
        setSize("100%", "100%");
        return viewPanel;
    }

    private LayoutPanel initSearchBar() {
        LayoutPanel searchBar = new LayoutPanel();
        searchBar.setWidth("100%");
        searchBar.setHeight("100%");

        searchForm = new MoneyInCandidateSearchCriteriaForm();
        searchForm.initContent();
        searchForm.populateNew();
        searchForm.asWidget().getElement().getStyle().setPadding(5, Unit.PX);
        searchForm.asWidget().getElement().getStyle().setOverflow(Overflow.AUTO);

        searchBar.add(searchForm);
        searchBar.setWidgetTopBottom(searchForm, 0, Unit.PX, 0, Unit.PX);
        searchBar.setWidgetLeftRight(searchForm, 0, Unit.PX, 100, Unit.PX);

        Toolbar searchToolbar = new Toolbar();
        searchToolbar.getElement().getStyle().setHeight(100, Unit.PX);
        searchToolbar.getElement().getStyle().setProperty("display", "table-cell");
        searchToolbar.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        Button searchButton = new Button(i18n.tr("Search"), new Command() {
            @Override
            public void execute() {
                presenter.search();
            }
        });
        searchToolbar.addItem(searchButton);

        SimplePanel searchToolbarHolder = new SimplePanel();
        searchToolbarHolder.setStyleName(DefaultPaneTheme.StyleName.HeaderToolbar.name());
        searchToolbarHolder.add(searchToolbar);

        searchBar.add(searchToolbarHolder);
        searchBar.setWidgetTopBottom(searchToolbarHolder, 0, Unit.PX, 0, Unit.PX);
        searchBar.setWidgetRightWidth(searchToolbarHolder, 0, Unit.PX, 100, Unit.PX);
        return searchBar;
    }

    private void initToolBars() {
        addFooterToolbarItem(new Button(i18n.tr("Create Batch"), new Command() {
            @Override
            public void execute() {
                presenter.createBatch();
            }
        }));
    }

}
