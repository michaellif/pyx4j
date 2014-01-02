/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.services.admin.PmcPaymentMethodsCrudService;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.server.TaskRunner;

public class PmcPaymentMethodsCrudServiceImpl implements PmcPaymentMethodsCrudService {

    @Override
    public void retrieve(AsyncCallback<PmcPaymentMethodsDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {

        final Pmc pmc = VistaDeployment.getCurrentPmc();

        PmcPaymentMethodsDTO paymentMethodsHolder = EntityFactory.create(PmcPaymentMethodsDTO.class);
        paymentMethodsHolder.paymentMethods().addAll(TaskRunner.runInOperationsNamespace(new Callable<Vector<PmcPaymentMethod>>() {

            @Override
            public Vector<PmcPaymentMethod> call() throws Exception {
                Persistence.service().retrieveMember(pmc.equifaxInfo());
                Persistence.service().retrieve(pmc.equifaxInfo().paymentMethod());

                Vector<PmcPaymentMethod> paymentMethods = retrievePaymentMethods(pmc);
                for (PmcPaymentMethod paymentMethod : paymentMethods) {
                    if (paymentMethod.equals(pmc.equifaxInfo().paymentMethod())) {
                        paymentMethod.selectForEquifaxPayments().setValue(true);
                        break;
                    }
                }
                return paymentMethods;
            }
        }));

        callback.onSuccess(paymentMethodsHolder);
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, final PmcPaymentMethodsDTO paymentMethodsHolder) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        TaskRunner.runInOperationsNamespace(new Callable<VoidSerializable>() {

            @Override
            public VoidSerializable call() throws Exception {
                for (PmcPaymentMethod exisitngPaymentMethod : retrievePaymentMethods(pmc)) {
                    if (!paymentMethodsHolder.paymentMethods().contains(exisitngPaymentMethod)) {
                        exisitngPaymentMethod.isDeleted().setValue(true);
                        Persistence.service().persist(exisitngPaymentMethod);
                    }
                }

                boolean equifaxPaymentSet = false;
                for (PmcPaymentMethod updatedPaymentMethod : paymentMethodsHolder.paymentMethods()) {
                    PmcPaymentMethod pm;
                    if (updatedPaymentMethod.getPrimaryKey() == null) {
                        pm = ServerSideFactory.create(PaymentMethodFacade.class).persistPmcPaymentMethod(
                                updatedPaymentMethod.details().duplicate(CreditCardInfo.class), pmc);
                    } else {
                        pm = ServerSideFactory.create(PaymentMethodFacade.class).persistPmcPaymentMethod(updatedPaymentMethod);
                    }
                    if ((!equifaxPaymentSet) && updatedPaymentMethod.selectForEquifaxPayments().getValue(false)) {
                        setEquifaxPayment(pm);
                        equifaxPaymentSet = true;
                    }
                }

                Persistence.service().commit();
                return null;
            }

            /**
             * update pmc's equifax payment method:
             */
            private void setEquifaxPayment(PmcPaymentMethod paymentMethod) {
                Persistence.service().retrieveMember(pmc.equifaxInfo());
                pmc.equifaxInfo().paymentMethod().set(paymentMethod);
                Persistence.service().persist(pmc.equifaxInfo());
            }

        });

        callback.onSuccess(new Key(-1));
    }

    @Override
    public void init(AsyncCallback<PmcPaymentMethodsDTO> callback, InitializationData initializationData) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void create(AsyncCallback<Key> callback, PmcPaymentMethodsDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<PmcPaymentMethodsDTO>> callback, EntityListCriteria<PmcPaymentMethodsDTO> criteria) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Invalid Operation");
    }

    private Vector<PmcPaymentMethod> retrievePaymentMethods(Pmc pmc) {
        final EntityQueryCriteria<PmcPaymentMethod> criteria = EntityQueryCriteria.create(PmcPaymentMethod.class);
        criteria.eq(criteria.proto().pmc(), pmc);
        criteria.ne(criteria.proto().isDeleted(), true);
        Vector<PmcPaymentMethod> paymentMethods = Persistence.secureQuery(criteria);
        return paymentMethods;
    }
}
