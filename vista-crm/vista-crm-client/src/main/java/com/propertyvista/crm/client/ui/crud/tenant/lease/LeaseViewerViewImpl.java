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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private final Button createApplicationButton;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        createApplicationButton = new Button(i18n.tr("Create Application"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).createMasterApplication();
            }
        });
        //TODO
        //createApplicationButton.setWrap(false);
        createApplicationButton.addStyleName(createApplicationButton.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.ActionButton);
        addActionButton(createApplicationButton);

        // set main form here: 
        setForm(new LeaseEditorForm(new CrmViewersComponentFactory()));
    }

    @Override
    public void populate(LeaseDTO value) {
        createApplicationButton.setVisible(Lease.Status.Draft.equals(value.status().getValue()));
        super.populate(value);
    }
}