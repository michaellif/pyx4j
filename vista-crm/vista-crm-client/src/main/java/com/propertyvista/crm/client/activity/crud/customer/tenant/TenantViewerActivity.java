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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantViewerView;
import com.propertyvista.crm.client.visor.maintenance.MaintenanceRequestVisorController;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningCrudService;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;

public class TenantViewerActivity extends CrmViewerActivity<TenantDTO> implements TenantViewerView.Presenter {

    private MaintenanceRequestVisorController maintenanceRequestVisorController;

    private Key currentTenantId;

    private Key currentBuildingId;

    private Customer screeningCustomer;

    private TenantDTO currentValue;

    public TenantViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(TenantViewerView.class), GWT.<TenantCrudService> create(TenantCrudService.class));
    }

    @Override
    public MaintenanceRequestVisorController getMaintenanceRequestVisorController() {
        if (maintenanceRequestVisorController == null) {
            maintenanceRequestVisorController = new MaintenanceRequestVisorController(getView(), currentBuildingId, currentTenantId);
        }
        return maintenanceRequestVisorController;
    }

    @Override
    public void createScreening() {
        CustomerScreeningCrudService.CustomerScreeningInitializationData id = EntityFactory
                .create(CustomerScreeningCrudService.CustomerScreeningInitializationData.class);
        id.screene().set(screeningCustomer);
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Screening().formNewItemPlace(id));
    }

    @Override
    public void createMaintenanceRequest() {
        MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
        id.tenant().set(EntityFactory.createIdentityStub(Tenant.class, getEntityId()));
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.MaintenanceRequest().formNewItemPlace(id));
    }

    @Override
    public void changePassword(Key tenantPrincipalPk, String tenantName) {
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

        currentValue = result;

        currentTenantId = result.getPrimaryKey();
        screeningCustomer = result.customer();
        currentBuildingId = result.lease().unit().building().id().getValue();
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.Tenants);
    }

    @Override
    public void retrievePortalRegistrationInformation() {
        ((TenantCrudService) getService()).getPortalAccessInformation(new DefaultAsyncCallback<TenantPortalAccessInformationDTO>() {
            @Override
            public void onSuccess(TenantPortalAccessInformationDTO result) {
                ((TenantViewerView) getView()).displayPortalRegistrationInformation(result);
            }
        }, EntityFactory.createIdentityStub(Tenant.class, getEntityId()));
    }

    @Override
    public void viewDeletedPaps() {
        AutopayAgreement proto = EntityFactory.getEntityPrototype(AutopayAgreement.class);
        CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AutopayAgreement.class);
        place.formListerPlace();

        place.queryArg(proto.tenant().lease().leaseId().getPath().toString(), currentValue.lease().leaseId().getValue().toString());

        place.queryArg(proto.tenant().participantId().getPath().toString(), currentValue.participantId().getValue().toString());

        place.queryArg(proto.isDeleted().getPath().toString(), Boolean.TRUE.toString());

        AppSite.getPlaceController().goTo(place);
    }
}
