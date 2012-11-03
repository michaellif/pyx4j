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
package com.propertyvista.crm.client.activity.crud.customer.tenant;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.client.visor.notes.NotesParentId;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TenantDTO;

public class TenantViewerActivity extends CrmViewerActivity<TenantDTO> implements TenantViewerView.Presenter {

    private Key notesParentPrimaryKey;

    private Key screeningParentPrimaryKey;

    public TenantViewerActivity(CrudAppPlace place) {
        super(place, CustomerViewFactory.instance(TenantViewerView.class), GWT.<TenantCrudService> create(TenantCrudService.class));
    }

    @Override
    public void goToCreateScreening() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Screening().formNewItemPlace(screeningParentPrimaryKey));
    }

    @Override
    public void goToChangePassword(Key tenantPrincipalPk, String tenantName) {
        if (tenantPrincipalPk != null) {
            AppPlace passwordChangePlace = new CrmSiteMap.PasswordChange();
            passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, tenantPrincipalPk.toString());
            passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_NAME_ARG, tenantName);
            passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.TENANT.toString());
            AppSite.getPlaceController().goTo(passwordChangePlace);
        }
    }

    @Override
    public void onPopulateSuccess(TenantDTO result) {
        super.onPopulateSuccess(result);

        notesParentPrimaryKey = result.getPrimaryKey();
        screeningParentPrimaryKey = result.customer().getPrimaryKey();
    }

    @Override
    protected NotesParentId createNotesParentId() {
        return new NotesParentId(LeaseTermTenant.class, notesParentPrimaryKey);
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.Tenants);
    }
}
