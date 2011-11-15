/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.application;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.MasterApplicationDTO;

public class MasterApplicationViewerViewImpl extends CrmViewerViewImplBase<MasterApplicationDTO> implements MasterApplicationViewerView {

    private final IListerView<ApplicationDTO> applicationLister;

    private final CHyperlink approveAction;

    private final CHyperlink moreInfoAction;

    private final CHyperlink declineAction;

    public MasterApplicationViewerViewImpl() {
        super(CrmSiteMap.Tenants.MasterApplication.class, true);

        applicationLister = new ListerInternalViewImplBase<ApplicationDTO>(new ApplicationLister());

        // Add actions:
        approveAction = new CHyperlink(new Command() {
            @Override
            public void execute() {
                ((MasterApplicationViewerView.Presenter) presenter).approve();
            }
        });
        approveAction.setValue(i18n.tr("Approve"));
        addToolbarItem(approveAction.asWidget());

        moreInfoAction = new CHyperlink(new Command() {
            @Override
            public void execute() {
                ((MasterApplicationViewerView.Presenter) presenter).moreInfo();
            }
        });
        moreInfoAction.setValue(i18n.tr("More Info"));
        addToolbarItem(moreInfoAction.asWidget());

        declineAction = new CHyperlink(new Command() {
            @Override
            public void execute() {
                ((MasterApplicationViewerView.Presenter) presenter).decline();
            }
        });
        declineAction.setValue(i18n.tr("Decline"));
        addToolbarItem(declineAction.asWidget());

        //set main form here: 
        setForm(new MasterApplicationEditorForm(new CrmViewersComponentFactory()));
    }

    @Override
    public IListerView<ApplicationDTO> getApplicationsView() {
        return applicationLister;
    }

}