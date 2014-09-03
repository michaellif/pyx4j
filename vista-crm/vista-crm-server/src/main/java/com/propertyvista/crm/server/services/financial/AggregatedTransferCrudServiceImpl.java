/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.PolymorphicEntityBinder;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.crm.rpc.dto.financial.CardsAggregatedTransferDTO;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecord;
import com.propertyvista.server.TaskRunner;

public class AggregatedTransferCrudServiceImpl extends AbstractCrudServiceImpl<AggregatedTransfer> implements AggregatedTransferCrudService {

    private static class Binder extends PolymorphicEntityBinder<AggregatedTransfer, AggregatedTransfer> {

        protected Binder() {
            super(AggregatedTransfer.class, AggregatedTransfer.class);
        }

        @Override
        protected void bind() {
            bind(EftAggregatedTransfer.class, EftAggregatedTransfer.class, new CrudEntityBinder<EftAggregatedTransfer, EftAggregatedTransfer>(
                    EftAggregatedTransfer.class, EftAggregatedTransfer.class) {
                @Override
                protected void bind() {
                    bindCompleteObject();
                }
            });
            bind(CardsAggregatedTransferDTO.class, CardsAggregatedTransfer.class, new CrudEntityBinder<CardsAggregatedTransfer, CardsAggregatedTransferDTO>(
                    CardsAggregatedTransfer.class, CardsAggregatedTransferDTO.class) {
                @Override
                protected void bind() {
                    bindCompleteObject();
                }
            });
        }

    }

    public AggregatedTransferCrudServiceImpl() {
        super(new Binder());
    }

    @Override
    protected void enhanceRetrieved(final AggregatedTransfer bo, final AggregatedTransfer to, RetrieveTarget retrieveTarget) {
        if (to.isAssignableFrom(CardsAggregatedTransferDTO.class)) {
            if (SecurityController.check(VistaCrmBehavior.PropertyVistaSupport)) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        CardsReconciliationRecord reconciliationRecord = Persistence.service().retrieve(CardsReconciliationRecord.class,
                                bo.<CardsAggregatedTransfer> cast().cardsReconciliationRecordKey().getValue());
                        to.<CardsAggregatedTransferDTO> cast().fileMerchantTotal().setValue(reconciliationRecord.fileMerchantTotal().fileName().getValue());
                        to.<CardsAggregatedTransferDTO> cast().fileCardTotal().setValue(reconciliationRecord.fileCardTotal().fileName().getValue());
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public void cancelTransactions(AsyncCallback<VoidSerializable> callback, AggregatedTransfer aggregatedTransferStub) {
        ServerSideFactory.create(PaymentFacade.class).cancelAggregatedTransfer(aggregatedTransferStub);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
