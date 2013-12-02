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

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid.MoneyInCandidateDataGrid;

public class MoneyInCreateBatchViewImpl extends AbstractPrimePane implements MoneyInCreateBatchView {

    private static final I18n i18n = I18n.get(MoneyInCreateBatchViewImpl.class);

    private MoneyInCreateBatchView.Presenter presenter;

    private final LayoutPanel viewPanel;

    private LayoutPanel searchBar;

    private MoneyInCandidateDataGrid searchCandidateDataGrid;

    public MoneyInCreateBatchViewImpl() {
        initToolBars();
        viewPanel = initViewPanel();

        viewPanel.add(searchBar = initSearchBar());
        viewPanel.setWidgetTopHeight(searchBar, 0, Unit.PX, 100, Unit.PX);
        viewPanel.setWidgetLeftRight(searchBar, 0, Unit.PX, 0, Unit.PX);

        viewPanel.add(searchCandidateDataGrid = new MoneyInCandidateDataGrid());
        viewPanel.setWidgetTopBottom(searchCandidateDataGrid, 101, Unit.PX, 250, Unit.PX);
        viewPanel.setWidgetLeftRight(searchCandidateDataGrid, 0, Unit.PX, 0, Unit.PX);

    }

    @Override
    public void setPresenter(MoneyInCreateBatchView.Presenter presenter) {
        this.presenter = presenter;
        this.searchCandidateDataGrid.setPresenter(presenter);
    }

    private LayoutPanel initViewPanel() {
        LayoutPanel viewPanel = new LayoutPanel();
        setContentPane(viewPanel);
        setSize("100%", "100%");
        return viewPanel;
    }

    private LayoutPanel initSearchBar() {
        LayoutPanel searchBar = new LayoutPanel();

        FlowPanel formHolderPanel = new FlowPanel();
        formHolderPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        formHolderPanel.add(new HTML(i18n.tr("Here will be a form with search criteria")));

        searchBar.add(formHolderPanel);
        searchBar.setWidgetTopBottom(formHolderPanel, 0, Unit.PX, 31, Unit.PX);

        Toolbar searchToolbar = new Toolbar();
        Button searchButton = new Button(i18n.tr("Search"), new Command() {
            @Override
            public void execute() {
                presenter.search();
            }
        });
        searchToolbar.addItem(searchButton);

        SimplePanel searchToolbarHolder = new SimplePanel();
        searchToolbarHolder.setStyleName(DefaultPaneTheme.StyleName.FooterToolbar.name());
        searchToolbarHolder.add(searchToolbar);

        searchBar.add(searchToolbarHolder);
        searchBar.setWidgetBottomHeight(searchToolbarHolder, 0, Unit.PX, 30, Unit.PX);
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
