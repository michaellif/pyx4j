/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.prospect.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationContextChoiceDTO;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class ApplicationContextSelectionViewImpl extends SimplePanel implements ApplicationContextSelectionView {

    private final static I18n i18n = I18n.get(ApplicationContextSelectionViewImpl.class);

    private final ApplicationLister lister = new ApplicationLister();

    private final Anchor newApplication = new Anchor("New Application", new Command() {
        @Override
        public void execute() {
            presenter.createNewApplication();
        }
    });

    private ApplicationContextSelectionPresenter presenter;

    public ApplicationContextSelectionViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());
        setWidget(new ApplicationContextSelectionGadget(lister));
    }

    @Override
    public void setPresenter(ApplicationContextSelectionPresenter presenter) {
        this.presenter = presenter;

        newApplication.setVisible(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_BUILDING_ID) != null);
    }

    @Override
    public void populate(List<OnlineApplicationContextChoiceDTO> leaseChoices) {
        lister.setDataSource(new ApplicationDataSource(leaseChoices));
        lister.populate();
    }

    class ApplicationContextSelectionGadget extends AbstractGadget<ApplicationContextSelectionViewImpl> {

        ApplicationContextSelectionGadget(ApplicationLister lister) {
            super(ApplicationContextSelectionViewImpl.this, null, i18n.tr("Select an Application"), ThemeColor.foreground, 0.3);

            lister.getDataTable().setColumnSelectorVisible(false);
            lister.addUpperActionItem(new Label(i18n.tr("Please select the Application you want to manage and click \"Continue\"")));
            setContent(lister);

            setActionsToolbar(new ApplicationContextSelectionToolbar());
        }

        class ApplicationContextSelectionToolbar extends GadgetToolbar {
            public ApplicationContextSelectionToolbar() {

                final Button continueButton = new Button("Continue", new Command() {
                    @Override
                    public void execute() {
                        OnlineApplicationContextChoiceDTO choice = lister.getDataTable().getSelectedItem();
                        if (choice != null) {
                            presenter.setApplicationContext((OnlineApplication) choice.onlineApplication().duplicate());
                        }
                    }
                });

                continueButton.setEnabled(false);
                lister.addItemSelectionHandler(new ItemSelectionHandler() {
                    @Override
                    public void onChange() {
                        continueButton.setEnabled(lister.getDataTable().getSelectedItem() != null);
                    }
                });

                continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.foreground, 0.4));
                addItem(continueButton);

                newApplication.getElement().getStyle().setFontSize(18, Unit.PX);
                addItem(newApplication);
            }
        }
    }

    private static class ApplicationLister extends DataTablePanel<OnlineApplicationContextChoiceDTO> {

        public ApplicationLister() {
            super(OnlineApplicationContextChoiceDTO.class, false, false);
            setFilteringEnabled(false);

            setColumnDescriptors(//
                    new ColumnDescriptor.Builder(proto().leaseAplicationId()).width("100px").build(), //
                    new ColumnDescriptor.Builder(proto().leaseApplicationUnitAddress()).width("200px").build());
            setDataTableModel(new DataTableModel<OnlineApplicationContextChoiceDTO>());
        }
    }

    private static class ApplicationDataSource extends ListerDataSource<OnlineApplicationContextChoiceDTO> {

        public ApplicationDataSource(List<OnlineApplicationContextChoiceDTO> applicationChoices) {
            super(OnlineApplicationContextChoiceDTO.class, new InMemeoryListService<OnlineApplicationContextChoiceDTO>(applicationChoices));
        }
    }
}
