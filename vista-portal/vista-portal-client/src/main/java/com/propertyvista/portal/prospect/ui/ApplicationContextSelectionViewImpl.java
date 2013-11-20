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
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationContextChoiceDTO;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class ApplicationContextSelectionViewImpl extends SimplePanel implements ApplicationContextSelectionView {

    private final static I18n i18n = I18n.get(ApplicationContextSelectionViewImpl.class);

    private final ApplicationLister lister;

    private ApplicationContextSelectionPresenter presenter;

    public ApplicationContextSelectionViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        lister = new ApplicationLister();
        ApplicationContextSelectionGadget gadget = new ApplicationContextSelectionGadget(lister);
        setWidget(gadget);
    }

    @Override
    public void setPresenter(ApplicationContextSelectionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<OnlineApplicationContextChoiceDTO> leaseChoices) {
        lister.setDataSource(new ApplicationDataSource(leaseChoices));
        lister.obtain(0);
    }

    @Override
    public OnlineApplication getSelectedApplication() {
        return lister.getSelectedItem().onlineApplication().duplicate();
    }

    class ApplicationContextSelectionGadget extends AbstractGadget<ApplicationContextSelectionViewImpl> {

        ApplicationContextSelectionGadget(ApplicationLister lister) {
            super(ApplicationContextSelectionViewImpl.this, null, i18n.tr("Select an Application"), ThemeColor.foreground, 0.3);

            lister.setWidth("100%");

            lister.showColumnSelector(false);
            lister.addActionItem(new Label(i18n.tr("Please select the Application you want to manage and click \"Continue\"")));
            setContent(lister);
            setActionsToolbar(new ApplicationContextSelectionToolbar());
        }

        class ApplicationContextSelectionToolbar extends GadgetToolbar {
            public ApplicationContextSelectionToolbar() {

                final Button continueButton = new Button("Continue", new Command() {

                    @Override
                    public void execute() {
                        OnlineApplicationContextChoiceDTO choice = lister.getSelectedItem();
                        if (choice != null) {
                            presenter.setApplicationContext();
                        }
                    }
                });

                continueButton.setEnabled(false);
                lister.addItemSelectionHandler(new ItemSelectionHandler<OnlineApplicationContextChoiceDTO>() {
                    @Override
                    public void onSelect(OnlineApplicationContextChoiceDTO selectedItem) {
                        continueButton.setEnabled(selectedItem != null);
                    }
                });
                continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.foreground, 0.4));
                addItem(continueButton);
            }
        }
    }

    private static class ApplicationLister extends EntityDataTablePanel<OnlineApplicationContextChoiceDTO> {

        public ApplicationLister() {
            super(OnlineApplicationContextChoiceDTO.class, false, false);
            setSelectable(true);
            getDataTablePanel().setFilteringEnabled(false);
            setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().leaseApplicationUnitAddress()).build());
        }
    }

    private static class ApplicationDataSource extends ListerDataSource<OnlineApplicationContextChoiceDTO> {

        public ApplicationDataSource(List<OnlineApplicationContextChoiceDTO> applicationChoices) {
            super(OnlineApplicationContextChoiceDTO.class, new InMemeoryListService<OnlineApplicationContextChoiceDTO>(applicationChoices));
        }

    }
}
