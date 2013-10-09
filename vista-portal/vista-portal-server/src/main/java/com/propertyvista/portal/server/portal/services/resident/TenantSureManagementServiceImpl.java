/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureTextFacade;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.rpc.VistaSystemMaintenanceState;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors.TenantSureOnMaintenanceException;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class TenantSureManagementServiceImpl implements TenantSureManagementService {

    @Override
    public void getStatus(AsyncCallback<TenantSureCertificateSummaryDTO> callback) {

        throw new UserRuntimeException("Failed to retrieve TenantSure status. Probably you don't have active TenantSure insurance.");
    }

    @Override
    public void updatePaymentMethod(AsyncCallback<VoidSerializable> callback, InsurancePaymentMethod paymentMethod) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }
        paymentMethod.tenant().set(TenantAppContext.getCurrentUserTenant());

        ServerSideFactory.create(TenantSureFacade.class).updatePaymentMethod(paymentMethod,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void cancelTenantSure(AsyncCallback<VoidSerializable> callback) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        ServerSideFactory.create(TenantSureFacade.class).scheduleCancelByTenant(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub());
        callback.onSuccess(null);
    }

    @Override
    public void reinstate(AsyncCallback<VoidSerializable> callback) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        ServerSideFactory.create(TenantSureFacade.class).reinstate(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub());
        callback.onSuccess(null);
    }

    @Override
    public void getFaq(AsyncCallback<String> faqHtml) {
        faqHtml.onSuccess(ServerSideFactory.create(TenantSureTextFacade.class).getFaq());
    }

    @Override
    public void getPreAuthorizedPaymentsAgreement(AsyncCallback<String> areementHtml) {
        areementHtml.onSuccess(ServerSideFactory.create(TenantSureTextFacade.class).getPreAuthorizedAgreement());
    }

    @Override
    public void sendCertificate(AsyncCallback<String> callback, String email) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        String sendTo = ServerSideFactory.create(TenantSureFacade.class).sendCertificate(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub(), email);
        callback.onSuccess(sendTo);
    }

}
