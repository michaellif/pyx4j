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
package com.propertyvista.portal.web.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.LeaseContextChoiceDTO;

public class LeaseContextSelectionViewImpl implements LeaseContextSelectionView {

    private final static I18n i18n = I18n.get(LeaseContextSelectionViewImpl.class);

    private final Panel panel;

    private final LeaseContextChoicesLister lister;

    private Presenter presenter;

    public LeaseContextSelectionViewImpl() {
        int row = -1;

        lister = new LeaseContextChoicesLister();

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        content.setSize("100%", "100%");
        content.setWidget(++row, 0, lister);

        final Button chooseButton = new Button(i18n.tr("Continue"), new Command() {

            @Override
            public void execute() {
                LeaseContextChoiceDTO choice = lister.getSelectedItem();
                if (choice != null) {
                    presenter.setLeaseContext(choice.leaseStub().<Lease> detach());
                }
            }
        });
        chooseButton.setEnabled(false);
        lister.addItemSelectionHandler(new ItemSelectionHandler<LeaseContextChoiceDTO>() {

            @Override
            public void onSelect(LeaseContextChoiceDTO selectedItem) {
                chooseButton.setEnabled(selectedItem != null);
            }

        });

        content.setWidget(++row, 0, chooseButton);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        content.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(0.5, Unit.EM);

        panel = new ScrollPanel(content);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<LeaseContextChoiceDTO> leaseChoices) {
        lister.setDataSource(new LeaseContextChoicesDataSource(leaseChoices));
        lister.obtain(0);
    }

    private static class LeaseContextChoicesLister extends EntityDataTablePanel<LeaseContextChoiceDTO> {

        public LeaseContextChoicesLister() {
            super(LeaseContextChoiceDTO.class, false, false);
            setSelectable(true);
            getDataTablePanel().setFilteringEnabled(false);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().unitView()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().streetNumber()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().address().streetName()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().streetType()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().streetDirection()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().city()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().province().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().country().name()).build()                    
            );//@formatter:on
        }
    }

    private static class LeaseContextChoicesDataSource extends ListerDataSource<LeaseContextChoiceDTO> {

        public LeaseContextChoicesDataSource(List<LeaseContextChoiceDTO> leaseChoices) {
            super(LeaseContextChoiceDTO.class, new InMemeoryListService<LeaseContextChoiceDTO>(leaseChoices));
        }

    }
}
