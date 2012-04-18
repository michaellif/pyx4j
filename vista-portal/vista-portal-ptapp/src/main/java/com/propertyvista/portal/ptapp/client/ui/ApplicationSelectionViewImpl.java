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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.tenant.ptapp.OnlineApplication;

public class ApplicationSelectionViewImpl implements ApplicationSelectionView {

    private final static I18n i18n = I18n.get(ApplicationSelectionViewImpl.class);

    private Presenter presenter;

    private final VerticalPanel panel;

    private final ApplicationsLister lister;

    public ApplicationSelectionViewImpl() {
        panel = new VerticalPanel();
        panel.setSize("100%", "100%");

        lister = new ApplicationsLister();
        panel.add(lister);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setApplications(Vector<OnlineApplication> applications) {
        lister.setDataSource(new OnlineApplicationDataSource(applications));
        lister.obtain(0);
    }

    @Override
    public void setPresenter(com.propertyvista.portal.ptapp.client.ui.ApplicationSelectionView.Presenter presenter) {
        this.presenter = presenter;
    }

    private class ApplicationsLister extends BasicLister<OnlineApplication> {

        public ApplicationsLister() {
            super(OnlineApplication.class, false, false);
            setSelectable(true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().status()).build()
            );//@formatter:on

            addActionItem(new Button(i18n.tr("Choose"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    OnlineApplication selectedApplication = getSelectedItem();
                    if (selectedApplication != null) {
                        presenter.selectApplication(selectedApplication.<OnlineApplication> createIdentityStub());
                    }
                }
            }));

        }
    }

    private static class OnlineApplicationDataSource extends ListerDataSource<OnlineApplication> {

        public OnlineApplicationDataSource(Vector<OnlineApplication> applications) {
            super(OnlineApplication.class, new InMemeoryListService<OnlineApplication>(applications));
        }

    }

}
