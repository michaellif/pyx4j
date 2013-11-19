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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationContextChoiceDTO;

public class ApplicationContextSelectionViewImpl implements ApplicationContextSelectionView {

    private final static I18n i18n = I18n.get(ApplicationContextSelectionViewImpl.class);

    private final Panel panel;

    private final ApplicationContextChoicesLister lister;

    private ApplicationContextSelectionPresenter presenter;

    public ApplicationContextSelectionViewImpl() {
        lister = new ApplicationContextChoicesLister();
        lister.setSize("100%", "100%");
        lister.showColumnSelector(false);

        BasicFlexFormPanel content = new BasicFlexFormPanel();
        content.setSize("100%", "100%");

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Please select the Application you want to manage and click ''Continue'':"));
        content.setWidget(++row, 0, 2, lister);

        final Button chooseButton = new Button(i18n.tr("Continue"), new Command() {
            @Override
            public void execute() {
                OnlineApplicationContextChoiceDTO choice = lister.getSelectedItem();
                if (choice != null) {
                    presenter.setLeaseContext();
                }
            }
        });
        chooseButton.setEnabled(false);
        lister.addItemSelectionHandler(new ItemSelectionHandler<OnlineApplicationContextChoiceDTO>() {
            @Override
            public void onSelect(OnlineApplicationContextChoiceDTO selectedItem) {
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
    public void setPresenter(ApplicationContextSelectionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<OnlineApplicationContextChoiceDTO> leaseChoices) {
        lister.setDataSource(new ApplicationContextChoicesDataSource(leaseChoices));
        lister.obtain(0);
    }

    @Override
    public OnlineApplication getSelectedApplication() {
        return lister.getSelectedItem().onlineApplication().duplicate();
    }

    @Override
    public void showMessage(String message) {
        MessageDialog.info(message);
    }

    private static class ApplicationContextChoicesLister extends EntityDataTablePanel<OnlineApplicationContextChoiceDTO> {

        public ApplicationContextChoicesLister() {
            super(OnlineApplicationContextChoiceDTO.class, false, false);
            setSelectable(true);
            getDataTablePanel().setFilteringEnabled(false);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().leaseApplicationUnitAddress()).build()
            );//@formatter:on
        }
    }

    private static class ApplicationContextChoicesDataSource extends ListerDataSource<OnlineApplicationContextChoiceDTO> {

        public ApplicationContextChoicesDataSource(List<OnlineApplicationContextChoiceDTO> applicationChoices) {
            super(OnlineApplicationContextChoiceDTO.class, new InMemeoryListService<OnlineApplicationContextChoiceDTO>(applicationChoices));
        }

    }
}
