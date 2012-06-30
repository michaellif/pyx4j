/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.deposit;

import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public class DepositLister extends ListerBase<Deposit> {

    private final static I18n i18n = I18n.get(DepositLister.class);

    public DepositLister() {
        super(Deposit.class, true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().depositDate(), false).build(),
            new MemberColumnDescriptor.Builder(proto().refundDate()).build(),
            new MemberColumnDescriptor.Builder(proto().initialAmount()).build(),
            new MemberColumnDescriptor.Builder(proto().currentAmount()).build()
        );//@formatter:on
    }

    @Override
    protected void onItemNew() {
        ((DepositListerPresenter) getPresenter()).getLeaseBillableItems(new DefaultAsyncCallback<List<BillableItem>>() {
            @Override
            public void onSuccess(List<BillableItem> result) {
                new EntitySelectorListDialog<BillableItem>(i18n.tr("Select Item"), false, result) {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            new SelectEnumDialog<DepositType>(i18n.tr("Select Deposit Type"), EnumSet.allOf(DepositType.class)) {
                                @Override
                                public boolean onClickOk() {
                                    ((DepositListerPresenter) getPresenter()).createDeposit(new DefaultAsyncCallback<Deposit>() {
                                        @Override
                                        public void onSuccess(Deposit result) {
                                            getPresenter().editNew(getItemOpenPlaceClass(), result);
                                        }
                                    }, getSelectedType(), getSelectedItems().get(0));
                                    return true;
                                }
                            }.show();
                            return true;
                        }
                        return false;
                    }
                }.show();
            }
        });
    }
}
