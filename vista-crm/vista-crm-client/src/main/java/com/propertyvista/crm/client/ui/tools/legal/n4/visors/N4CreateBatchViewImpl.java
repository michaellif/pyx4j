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
package com.propertyvista.crm.client.ui.tools.legal.n4.visors;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.HasData;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;

import com.propertyvista.crm.client.ui.tools.common.view.AbstractPrimePaneWithMessagesPopup;
import com.propertyvista.crm.client.ui.tools.legal.n4.N4CreateBatchView;
import com.propertyvista.crm.client.ui.tools.legal.n4.forms.N4CandidateSearchCriteriaForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;

public class N4CreateBatchViewImpl extends AbstractPrimePaneWithMessagesPopup implements N4CreateBatchView {

    private static final I18n i18n = I18n.get(N4CreateBatchViewImpl.class);

    private final LayoutPanel viewPanel;

    private N4CandidateSearchCriteriaForm searchForm;

    private LayoutPanel searchBar;

    private final LayoutPanel gridsHolder;

    private com.propertyvista.crm.client.ui.tools.legal.n4.N4CreateBatchView.Presenter presenter;

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
    }

    @Override
    public void setPresenter(N4CreateBatchView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasData<LegalNoticeCandidateDTO> searchResults() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public N4CandidateSearchCriteriaDTO getSearchCriteria() {
        // TODO Auto-generated method stub
        return null;
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

        searchForm = new N4CandidateSearchCriteriaForm() {
            // TODO add on Resize
        };
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

}
