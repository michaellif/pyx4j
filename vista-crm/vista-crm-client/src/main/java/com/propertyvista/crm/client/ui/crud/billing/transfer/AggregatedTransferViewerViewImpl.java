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
 */
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.dto.PaymentRecordDTO;

public class AggregatedTransferViewerViewImpl extends CrmViewerViewImplBase<AggregatedTransfer> implements AggregatedTransferViewerView {

    private static final I18n i18n = I18n.get(AggregatedTransferViewerViewImpl.class);

    private final PaymentRecordLister paymentLister;

    private final PaymentRecordLister returnedPaymentLister;

    private final PaymentRecordLister rejectedBatchPaymentsLister;

    private final MenuItem cancelAction;

    public AggregatedTransferViewerViewImpl() {
        super(true);

        paymentLister = new PaymentRecordLister();
        returnedPaymentLister = new PaymentRecordLister();
        rejectedBatchPaymentsLister = new PaymentRecordLister();

        setForm(new AggregatedTransferForm(this));

        // Actions:
        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Cancel"), i18n.tr("Do you really want to cancel the transaction?"), new Command() {
                    @Override
                    public void execute() {
                        ((AggregatedTransferViewerView.Presenter) getPresenter()).cancelAction();
                    }
                });
            }
        });
        addAction(cancelAction);

        setNotesPermissions(DataModelPermission.permissionRead(AggregatedTransfer.class), DataModelPermission.permissionRead(AggregatedTransfer.class));
    }

    @Override
    public void reset() {
        setActionVisible(cancelAction, false);
        super.reset();
    }

    @Override
    public void populate(AggregatedTransfer value) {
        super.populate(value);

        PaymentRecordDTO proto = EntityFactory.getEntityPrototype(PaymentRecordDTO.class);
        // BO IS Polymorphic.  Also TO class != BO
        AggregatedTransfer typeSafeParent = (AggregatedTransfer) EntityFactory.createIdentityStub(value.getEntityMeta().getBOClass(), value.getPrimaryKey());

        paymentLister.getDataSource().clearPreDefinedFilters();
        paymentLister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(proto.aggregatedTransfer(), typeSafeParent));
        paymentLister.populate();

        returnedPaymentLister.getDataSource().clearPreDefinedFilters();
        returnedPaymentLister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(proto.aggregatedTransferReturn(), typeSafeParent));
        returnedPaymentLister.populate();

        rejectedBatchPaymentsLister.getDataSource().clearPreDefinedFilters();
        rejectedBatchPaymentsLister.getDataSource().addPreDefinedFilter(PropertyCriterion.eq(proto.processing().$().aggregatedTransfer(), typeSafeParent));
        rejectedBatchPaymentsLister.populate();

        setActionVisible(cancelAction, value.fundsTransferType().getValue() == FundsTransferType.PreAuthorizedDebit
                && value.status().getValue() == AggregatedTransferStatus.Rejected && value.status().getValue() != AggregatedTransferStatus.Canceled);
    }

    @Override
    public PaymentRecordLister getPaymentsListerView() {
        return paymentLister;
    }

    @Override
    public PaymentRecordLister getReturnedPaymentsListerView() {
        return returnedPaymentLister;
    }

    @Override
    public PaymentRecordLister getRejectedBatchPaymentsListerView() {
        return rejectedBatchPaymentsLister;
    }
}