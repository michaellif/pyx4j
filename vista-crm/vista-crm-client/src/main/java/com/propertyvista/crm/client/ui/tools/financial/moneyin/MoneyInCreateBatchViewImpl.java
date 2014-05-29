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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.tools.common.view.AbstractPrimePaneWithMessagesPopup;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid.MoneyInCandidateDataGrid;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.forms.MoneyInCandidateSearchCriteriaForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;

public class MoneyInCreateBatchViewImpl extends AbstractPrimePaneWithMessagesPopup implements MoneyInCreateBatchView {

    private static final I18n i18n = I18n.get(MoneyInCreateBatchViewImpl.class);

    private static final int MAX_SEARCH_FORM_HEIGHT = 250;

    private MoneyInCreateBatchView.Presenter presenter;

    private final LayoutPanel viewPanel;

    private LayoutPanel searchBar;

    private LayoutPanel gridsHolder;

    private MoneyInCandidateDataGrid selectedForProcessingDataGrid;

    private MoneyInCandidateSearchCriteriaForm searchForm;

    private CDatePicker receiptDate;

    public MoneyInCreateBatchViewImpl() {
        setCaption(i18n.tr("Money In: Create Payments Batch"));

        Button searchButton = new Button(i18n.tr("Add Payments..."), new Command() {
            @Override
            public void execute() {
                presenter.addPayments();
            }
        });
        addHeaderToolbarItem(searchButton);

        Button createBatchesButton = new Button(i18n.tr("Create Batches"), new Command() {
            @Override
            public void execute() {
                presenter.createBatch();
            }
        });
        addFooterToolbarItem(createBatchesButton);

        viewPanel = initViewPanel();

        gridsHolder = new LayoutPanel();
        viewPanel.add(gridsHolder);
        viewPanel.setWidgetTopBottom(gridsHolder, 0, Unit.PX, 0, Unit.PX);
        viewPanel.setWidgetLeftRight(gridsHolder, 0, Unit.PX, 0, Unit.PX);

        LayoutPanel selectedHolder = new LayoutPanel();
        gridsHolder.add(selectedHolder);
        gridsHolder.setWidgetTopBottom(selectedHolder, 0, Unit.PX, 0, Unit.PX);
        gridsHolder.setWidgetLeftRight(selectedHolder, 0, Unit.PX, 0, Unit.PX);

        // selected:
        Widget selectedHeader = initSelectedItemsHeaderPanel();
        selectedHeader.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        selectedHeader.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        selectedHolder.add(selectedHeader);
        selectedHolder.setWidgetTopHeight(selectedHeader, 0, Unit.PX, 40, Unit.PX);
        selectedHolder.setWidgetLeftRight(selectedHeader, 0, Unit.PX, 0, Unit.PX);

        selectedHolder.add(selectedForProcessingDataGrid = new MoneyInCandidateDataGrid() {
            @Override
            protected void onSort(String memberPath, boolean isAscending) {
                presenter.sortSelectedCandidates(memberPath, isAscending);
            };

            @Override
            protected String createProcessColumnTitle() {
                return "";
            };

            @Override
            protected Column<MoneyInCandidateDTO, ?> createProcessColumn() {
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
                return processColumn;
            }
        });
        selectedHolder.setWidgetTopBottom(selectedForProcessingDataGrid, 41, Unit.PX, 33, Unit.PX);
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

    @Override
    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
        this.selectedForProcessingDataGrid.setPresenter(presenter);
    }

    @Override
    public HasData<MoneyInCandidateDTO> selectedForProcessing() {
        return selectedForProcessingDataGrid;
    }

    @Override
    public LogicalDate getRecieptDate() {
        return receiptDate.getValue();
    }

    private LayoutPanel initViewPanel() {
        LayoutPanel viewPanel = new LayoutPanel();
        setContentPane(viewPanel);
        setSize("100%", "100%");
        return viewPanel;
    }

    private Widget initSelectedItemsHeaderPanel() {

        final String receiptDateHolderTagId = "receiptDateHolder";

        SafeHtmlBuilder headerPanelBuilder = new SafeHtmlBuilder();
        headerPanelBuilder.appendHtmlConstant("<div>");
        headerPanelBuilder.appendHtmlConstant(i18n.tr("Enter the receipt date {0}, and click ''Create Batch'' to process the following payments:",
                "<span id=\"" + receiptDateHolderTagId + "\"></span>"));
        headerPanelBuilder.appendHtmlConstant("</div>");

        receiptDate = new CDatePicker();
        receiptDate.getNativeComponent().getElement().getStyle().setWidth(100, Unit.PX);
        receiptDate.getNativeComponent().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        receiptDate.getNativeComponent().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        HTMLPanel headerPanel = new HTMLPanel(headerPanelBuilder.toSafeHtml());
        headerPanel.addAndReplaceElement(receiptDate, receiptDateHolderTagId);

        return headerPanel;
    }

    private void resizeSearch() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int height = searchForm.getRequiredHeight();
                if (height <= MAX_SEARCH_FORM_HEIGHT) {
                    viewPanel.setWidgetTopHeight(searchBar, 0, Unit.PX, height, Unit.PX);
                    viewPanel.setWidgetTopBottom(gridsHolder, height + 1, Unit.PX, 0, Unit.PX);
                }
            }
        });
    }

}
