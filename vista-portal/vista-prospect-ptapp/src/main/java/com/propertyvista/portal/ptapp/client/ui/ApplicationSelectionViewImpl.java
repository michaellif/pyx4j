/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import java.util.Vector;

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
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.ptapp.dto.OnlineApplicationContextDTO;

public class ApplicationSelectionViewImpl implements ApplicationSelectionView {

    private final static I18n i18n = I18n.get(ApplicationSelectionViewImpl.class);

    private Presenter presenter;

    private final TwoColumnFlexFormPanel content;

    private final Panel panel;

    private final ApplicationsLister lister;

    public ApplicationSelectionViewImpl() {
        int row = -1;
        content = new TwoColumnFlexFormPanel();
        content.setSize("100%", "100%");
        lister = new ApplicationsLister();
        lister.setSize("100%", "100%");
        content.setWidget(++row, 0, lister);

        final Button approveSelectionButton = new Button(i18n.tr("Continue"), new Command() {
            @Override
            public void execute() {
                OnlineApplicationContextDTO selectedApplication = lister.getSelectedItem();

                if (selectedApplication != null) {
                    presenter.selectApplication(selectedApplication.onlineApplicationIdStub().<OnlineApplication> detach());
                }
            }
        });
        approveSelectionButton.setEnabled(false);
        lister.addItemSelectionHandler(new ItemSelectionHandler<OnlineApplicationContextDTO>() {
            @Override
            public void onSelect(OnlineApplicationContextDTO selectedItem) {
                approveSelectionButton.setEnabled(selectedItem != null);
            }
        });
        content.setWidget(++row, 0, approveSelectionButton);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        content.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(0.5, Unit.EM);

        panel = new ScrollPanel(content);

    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setApplications(Vector<OnlineApplicationContextDTO> applications) {
        lister.setDataSource(new OnlineApplicationDataSource(applications));
        lister.obtain(0);
    }

    @Override
    public void setPresenter(com.propertyvista.portal.ptapp.client.ui.ApplicationSelectionView.Presenter presenter) {
        this.presenter = presenter;
    }

    private class ApplicationsLister extends EntityDataTablePanel<OnlineApplicationContextDTO> {

        public ApplicationsLister() {
            super(OnlineApplicationContextDTO.class, false, false);
            setSelectable(true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().role()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().streetNumber()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().address().streetName()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().streetType()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().streetDirection()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().city()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().province().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().address().country().name()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().unit()).build()                    
            );//@formatter:on

        }
    }

    private static class OnlineApplicationDataSource extends ListerDataSource<OnlineApplicationContextDTO> {

        public OnlineApplicationDataSource(Vector<OnlineApplicationContextDTO> applications) {
            super(OnlineApplicationContextDTO.class, new InMemeoryListService<OnlineApplicationContextDTO>(applications));
        }

    }

}
