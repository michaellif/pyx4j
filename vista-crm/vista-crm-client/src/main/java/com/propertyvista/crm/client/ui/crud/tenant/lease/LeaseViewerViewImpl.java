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

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private final AnchorButton btnconvert;

    private final LeaseViewDelegate delegate;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        btnconvert = new AnchorButton(i18n.tr("Convert to Application"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).convertToApplication();
            }
        });
        btnconvert.addStyleName(btnconvert.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.ActionButton);
        btnconvert.setWordWrap(false);
        addActionButton(btnconvert);

        delegate = new LeaseViewDelegate(true);

        // create/init/set main form here: 
        CrmEntityForm<LeaseDTO> form = new LeaseEditorForm(new CrmViewersComponentFactory(), this);
        form.initialize();
        setForm(form);
    }

    @Override
    public void populate(LeaseDTO value) {
        btnconvert.setVisible(!value.convertedToApplication().isBooleanTrue());
        super.populate(value);
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return delegate.getBuildingListerView();
    }

    @Override
    public IListerView<AptUnit> getUnitListerView() {
        return delegate.getUnitListerView();
    }

    @Override
    public IListerView<Tenant> getTenantListerView() {
        return delegate.getTenantListerView();
    }

    @Override
    public void onApplicationConvertionSuccess(Application result) {
        MessageDialog.info("Information", "Conversion is succeeded!");
        btnconvert.setVisible(false);
    }

    @Override
    public boolean onConvertionFail(Throwable caught) {
        if (caught instanceof Error) {
            MessageDialog.error("Error", caught.getMessage());
        } else {
            MessageDialog.error("Error", "Conversion is failed!");
        }
        return true;
    }
}