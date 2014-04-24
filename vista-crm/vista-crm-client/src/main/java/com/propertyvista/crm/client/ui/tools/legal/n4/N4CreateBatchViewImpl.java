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
package com.propertyvista.crm.client.ui.tools.legal.n4;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;

import com.propertyvista.crm.client.ui.tools.common.LinkDialog;
import com.propertyvista.crm.client.ui.tools.common.SimpleProgressWidget;
import com.propertyvista.crm.client.ui.tools.common.view.AbstractPrimePaneWithMessagesPopup;
import com.propertyvista.crm.client.ui.tools.legal.n4.datagrid.LegalNoticeCandidateDataGrid;
import com.propertyvista.crm.client.ui.tools.legal.n4.forms.N4CandidateSearchCriteriaForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;

// TODO this is meant to be a replacement for N4GenenrationToolView
public class N4CreateBatchViewImpl extends AbstractPrimePaneWithMessagesPopup implements N4CreateBatchView {

    private static final I18n i18n = I18n.get(N4CreateBatchViewImpl.class);

    private final LayoutPanel viewPanel;

    private N4CandidateSearchCriteriaForm searchCriteriaForm;

    private LayoutPanel searchBar;

    private final LayoutPanel gridsHolder;

    private com.propertyvista.crm.client.ui.tools.legal.n4.N4CreateBatchView.Presenter presenter;

    private LegalNoticeCandidateDataGrid searchCandidateDataGrid;

    private SimpleProgressWidget progressWidget;

    private Button createBatchButton;

    public N4CreateBatchViewImpl() {
        setCaption(i18n.tr("N4: Create N4's"));

        viewPanel = initViewPanel();

        viewPanel.add(searchBar = initSearchBar());
        viewPanel.setWidgetTopHeight(searchBar, 0, Unit.PX, 100, Unit.PX);
        viewPanel.setWidgetLeftRight(searchBar, 0, Unit.PX, 0, Unit.PX);

        gridsHolder = new LayoutPanel();
        viewPanel.add(gridsHolder);
        viewPanel.setWidgetTopBottom(gridsHolder, 101, Unit.PX, 0, Unit.PX);
        viewPanel.setWidgetLeftRight(gridsHolder, 0, Unit.PX, 0, Unit.PX);

        {
            LayoutPanel foundHolder = new LayoutPanel();
            gridsHolder.add(foundHolder);
            gridsHolder.setWidgetTopBottom(foundHolder, 0, Unit.PX, 51, Unit.PX);
            gridsHolder.setWidgetLeftRight(foundHolder, 0, Unit.PX, 0, Unit.PX);

            foundHolder.add(searchCandidateDataGrid = new LegalNoticeCandidateDataGrid() {
                @Override
                protected void onSort(String memberPath, boolean isAscending) {
                    presenter.sortFoundCandidates(memberPath, isAscending);
                }
            });
            searchCandidateDataGrid.setLoadingIndicator(progressWidget = new SimpleProgressWidget());

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
            gridsHolder.setWidgetBottomHeight(selectedHolder, 0, Unit.PX, 50, Unit.PX);
            gridsHolder.setWidgetLeftRight(selectedHolder, 0, Unit.PX, 0, Unit.PX);

            // selected:
            Widget selectedHeader = initSelectedItemsHeaderPanel();
            selectedHeader.getElement().getStyle().setTextAlign(TextAlign.CENTER);
            selectedHeader.getElement().getStyle().setFontWeight(FontWeight.BOLD);

            selectedHolder.add(selectedHeader);
            selectedHolder.setWidgetTopBottom(selectedHeader, 0, Unit.PX, 0, Unit.PX);
            selectedHolder.setWidgetLeftRight(selectedHeader, 0, Unit.PX, 0, Unit.PX);
        }

    }

    private Widget initSelectedItemsHeaderPanel() {
        createBatchButton = new Button(i18n.tr("Issue N4's"), new Command() {
            @Override
            public void execute() {
                N4CreateBatchViewImpl.this.presenter.createBatch();
            }
        });
        SimplePanel createBatchButtonHolder = new SimplePanel(); // this panel is to apply 'toolbar style' to the button
        createBatchButtonHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        createBatchButtonHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        createBatchButtonHolder.setStyleName(DefaultPaneTheme.StyleName.HeaderToolbar.name());
        createBatchButtonHolder.setWidget(createBatchButton);
        return createBatchButtonHolder;
    }

    @Override
    public void setPresenter(N4CreateBatchView.Presenter presenter) {
        this.presenter = presenter;
        searchCandidateDataGrid.setPresenter(presenter);
    }

    @Override
    public void setProgress(int progress, int maximumProgress, String message) {
        progressWidget.setProgress(progress, maximumProgress, message);
        searchCandidateDataGrid.setRowCount(maximumProgress);
        searchCandidateDataGrid.setVisibleRangeAndClearData(searchCandidateDataGrid.getVisibleRange(), true);
    }

    @Override
    public HasData<LegalNoticeCandidateDTO> searchResults() {
        return searchCandidateDataGrid;
    }

    @Override
    public N4CandidateSearchCriteriaDTO getSearchCriteria() {
        return searchCriteriaForm.getValue();
    }

    @Override
    public void displayN4GenerationReportDownloadLink(final String reportUrl) {
        new LinkDialog(i18n.tr("Some of N4 failed"), i18n.tr("Download Errors"), reportUrl) {
            @Override
            public boolean onClickCancel() {
                presenter.cancelDownload(reportUrl);
                return false;
            }

            @Override
            public void hide(boolean autoClosed) {
                super.hide(autoClosed);
                presenter.search();
            };
        }.show();
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

        searchCriteriaForm = new N4CandidateSearchCriteriaForm() {
            // TODO add on Resize
        };
        searchCriteriaForm.init();
        searchCriteriaForm.populateNew();
        searchCriteriaForm.asWidget().getElement().getStyle().setPadding(5, Unit.PX);
        searchCriteriaForm.asWidget().getElement().getStyle().setOverflow(Overflow.AUTO);

        searchBar.add(searchCriteriaForm);
        searchBar.setWidgetTopBottom(searchCriteriaForm, 0, Unit.PX, 0, Unit.PX);
        searchBar.setWidgetLeftRight(searchCriteriaForm, 0, Unit.PX, 100, Unit.PX);

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

}
