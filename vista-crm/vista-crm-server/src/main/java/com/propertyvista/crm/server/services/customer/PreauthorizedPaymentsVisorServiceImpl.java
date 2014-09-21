/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodTarget;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.crm.server.services.financial.PreauthorizedPaymentsCommons;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsVisorServiceImpl implements PreauthorizedPaymentsVisorService {

    protected Class<? extends IEntity> toClass = PreauthorizedPaymentsDTO.class;

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId) {
        SecurityController.assertPermission(DataModelPermission.permissionRead(toClass));

        PreauthorizedPaymentsDTO to = EntityFactory.create(PreauthorizedPaymentsDTO.class);

        to.tenant().set(tenantId);

        fillTenantInfo(to);
        fillPreauthorizedPayments(to);
        fillAvailablePaymentMethods(to);

        SecurityController.assertPermission(to, DataModelPermission.permissionRead(to.getValueClass()));

        callback.onSuccess(to);
    }

    @Override
    public void create(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId) {
        SecurityController.assertPermission(DataModelPermission.permissionCreate(toClass));
        callback.onSuccess(PreauthorizedPaymentsCommons.createNewPreauthorizedPayment(tenantId));
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO to) {
        SecurityController.assertPermission(to, DataModelPermission.permissionUpdate(toClass));
        PreauthorizedPaymentsCommons.savePreauthorizedPayments(to.preauthorizedPayments(), to.tenant());

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void recollect(AsyncCallback<Vector<AutopayAgreement>> callback, Tenant tenantId) {
        SecurityController.assertPermission(DataModelPermission.permissionRead(toClass));
        callback.onSuccess(new Vector<AutopayAgreement>(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(tenantId)));
    }

    // Internals:

    private void fillTenantInfo(PreauthorizedPaymentsDTO to) {
        Persistence.ensureRetrieve(to.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.tenant().lease(), AttachLevel.Attached);

        to.tenantInfo().name().set(to.tenant().customer().person().name());

        EntityListCriteria<LeaseTermParticipant> criteria = new EntityListCriteria<LeaseTermParticipant>(LeaseTermParticipant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), to.tenant()));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder(), to.tenant().lease().currentTerm()));

        LeaseTermParticipant<?> ltp = Persistence.service().retrieve(criteria);
        if (ltp != null) {
            to.tenantInfo().role().setValue(ltp.role().getValue());
        }

        to.nextPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(to.tenant().lease()));
    }

    private void fillAvailablePaymentMethods(PreauthorizedPaymentsDTO to) {
        Persistence.ensureRetrieve(to.tenant(), AttachLevel.Attached);

        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(to.tenant(),
                PaymentMethodTarget.AutoPaySetup, VistaApplication.crm);

        to.availablePaymentMethods().addAll(methods);
    }

    private void fillPreauthorizedPayments(PreauthorizedPaymentsDTO dto) {
        dto.preauthorizedPayments().addAll(PreauthorizedPaymentsCommons.createPreauthorizedPayments(dto.tenant(), RetrieveTarget.Edit));
    }
}
