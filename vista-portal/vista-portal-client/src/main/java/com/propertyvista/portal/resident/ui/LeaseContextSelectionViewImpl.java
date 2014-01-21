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
package com.propertyvista.portal.resident.ui;

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

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.dto.LeaseContextChoiceDTO;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class LeaseContextSelectionViewImpl extends SimplePanel implements LeaseContextSelectionView {

    private final static I18n i18n = I18n.get(LeaseContextSelectionViewImpl.class);

    private final LeaseLister lister;

    private Presenter presenter;

    public LeaseContextSelectionViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        lister = new LeaseLister();
        LeaseContextSelectionGadget gadget = new LeaseContextSelectionGadget(lister);
        setWidget(gadget);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<LeaseContextChoiceDTO> leaseChoices) {
        lister.setDataSource(new LeaseDataSource(leaseChoices));
        lister.obtain(0);
    }

    class LeaseContextSelectionGadget extends AbstractGadget<LeaseContextSelectionViewImpl> {

        LeaseContextSelectionGadget(LeaseLister lister) {
            super(LeaseContextSelectionViewImpl.this, null, i18n.tr("Select a Lease"), ThemeColor.foreground, 0.3);

            lister.setWidth("100%");

            lister.showColumnSelector(false);
            lister.addActionItem(new Label(i18n.tr("Please select the Lease you want to manage and click \"Continue\"")));
            setContent(lister);
            setActionsToolbar(new ApplicationContextSelectionToolbar());
        }

        class ApplicationContextSelectionToolbar extends GadgetToolbar {
            public ApplicationContextSelectionToolbar() {

                final Button continueButton = new Button("Continue", new Command() {

                    @Override
                    public void execute() {
                        LeaseContextChoiceDTO choice = lister.getSelectedItem();
                        if (choice != null) {
                            presenter.setLeaseContext((Lease) choice.leaseId().duplicate());
                        }
                    }
                });

                continueButton.setEnabled(false);
                lister.addItemSelectionHandler(new ItemSelectionHandler<LeaseContextChoiceDTO>() {
                    @Override
                    public void onSelect(LeaseContextChoiceDTO selectedItem) {
                        continueButton.setEnabled(selectedItem != null);
                    }
                });
                continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.foreground, 0.4));
                addItem(continueButton);
            }
        }
    }

    private static class LeaseLister extends EntityDataTablePanel<LeaseContextChoiceDTO> {

        public LeaseLister() {
            super(LeaseContextChoiceDTO.class, false, false);
            setSelectable(true);
            getDataTablePanel().setFilteringEnabled(false);
            setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().leasedUnitAddress()).build());
        }
    }

    private static class LeaseDataSource extends ListerDataSource<LeaseContextChoiceDTO> {

        public LeaseDataSource(List<LeaseContextChoiceDTO> leaseChoices) {
            super(LeaseContextChoiceDTO.class, new InMemeoryListService<LeaseContextChoiceDTO>(leaseChoices));
        }

    }
}
