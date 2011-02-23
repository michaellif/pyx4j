/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.domain.util;

import com.propertyvista.portal.domain.Currency;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.TenantCharge;

import com.pyx4j.entity.shared.EntityFactory;

public class DomainUtil {

    public static Currency createCurrency() {
        Currency currency = EntityFactory.create(Currency.class);
        currency.name().setValue("CAD");
        return currency;
    }

    public static Money createMoney(double value) {
        Money money = EntityFactory.create(Money.class);
        money.amount().setValue(value);
        money.currency().set(createCurrency());
        return money;
    }

    public static ChargeLine createChargeLine(ChargeType type, double money) {
        return createChargeLine(type, money, true);
    }

    public static ChargeLine createChargeLine(ChargeType type, double money, boolean selected) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.charge().set(createMoney(money));
        cl.type().setValue(type);
        cl.selected().setValue(selected);
        return cl;
    }

    public static TenantCharge createTenantCharge(int percentage, double money) {
        TenantCharge tc = EntityFactory.create(TenantCharge.class);

        tc.charge().set(createMoney(money));
        tc.percentage().setValue(percentage);

        return tc;
    }
}
