/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.propertyvista.yardi.mock.model.domain.YardiTransaction;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.TransactionBuilder;

public class TransactionBuilderImpl implements TransactionBuilder {

    private final YardiTransaction trans;

    private final LeaseBuilder parent;

    TransactionBuilderImpl(YardiTransaction trans, LeaseBuilder parent) {
        this.trans = trans;
        this.parent = parent;
    }

    @Override
    public TransactionBuilder setAmount(String amount) {
        trans.amount().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public TransactionBuilder setChargeCode(String chargeCode) {
        trans.chargeCode().setValue(chargeCode);
        return this;
    }

    @Override
    public TransactionBuilder setTransactionDate(String date) {
        trans.transactionDate().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public TransactionBuilder setGlAccountNumber(String account) {
        trans.glAccountNumber().setValue(account);
        return this;
    }

    @Override
    public TransactionBuilder setAmountPaid(String amount) {
        trans.amountPaid().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public TransactionBuilder setBalanceDue(String amount) {
        trans.balanceDue().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public TransactionBuilder setDescription(String text) {
        trans.description().setValue(text);
        return this;
    }

    @Override
    public TransactionBuilder setComment(String text) {
        trans.comment().setValue(text);
        return this;
    }

    @Override
    public LeaseBuilder done() {
        return parent;
    }
}
