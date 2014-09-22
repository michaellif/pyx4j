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
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.propertyvista.yardi.mock.model.domain.YardiLeaseCharge;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseChargeBuilder;

public class LeaseChargeBuilderImpl implements LeaseChargeBuilder {

    private final YardiLeaseCharge charge;

    private final LeaseBuilder parent;

    LeaseChargeBuilderImpl(YardiLeaseCharge charge, LeaseBuilder parent) {
        this.charge = charge;
        this.parent = parent;
    }

    @Override
    public LeaseChargeBuilder setAmount(String amount) {
        charge.amount().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public LeaseChargeBuilder setChargeCode(String chargeCode) {
        charge.chargeCode().setValue(chargeCode);
        return this;
    }

    @Override
    public LeaseChargeBuilder setFromDate(String date) {
        charge.serviceFromDate().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseChargeBuilder setToDate(String date) {
        charge.serviceToDate().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseChargeBuilder setGlAccountNumber(String account) {
        charge.glAccountNumber().setValue(account);
        return this;
    }

    @Override
    public LeaseChargeBuilder setDescription(String text) {
        charge.description().setValue(text);
        return this;
    }

    @Override
    public LeaseChargeBuilder setComment(String text) {
        charge.comment().setValue(text);
        return this;
    }

    @Override
    public LeaseBuilder done() {
        return parent;
    }
}
