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
package com.propertyvista.domain.util;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.financial.Currency;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.domain.financial.offering.extradata.Pet.WeightUnit;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;

public class DomainUtil {

    public static Currency createCurrency() {
        Currency currency = EntityFactory.create(Currency.class);
        currency.name().setValue("CAD");
        return currency;
    }

    public static double roundMoney(double value) {
        return Math.round(value * 100d) / 100d;
    }

    public static Money createMoney(double value) {
        Money money = EntityFactory.create(Money.class);
        money.amount().setValue(value);
        money.currency().set(createCurrency());
        return money;
    }

    public static Money createMoney(double value, String currency) {
        Money money = EntityFactory.create(Money.class);
        money.amount().setValue(value);
        money.currency().name().setValue(currency);
        return money;
    }

    public static ChargeLine createChargeLine(String label, double money) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.amount().set(createMoney(money));
        cl.label().setValue(label);
        return cl;
    }

    public static ChargeLine createChargeLine(ChargeType type, double money) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.amount().set(createMoney(money));
        cl.type().setValue(type);
        cl.label().setValue(type.toString());
        return cl;
    }

    public static ChargeLine createChargeLine(String label, ChargeType type, double money) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.amount().set(createMoney(money));
        cl.type().setValue(type);
        cl.label().setValue(label);
        return cl;
    }

// weight 

    public static int getWeightKgToUnit(IPrimitive<Integer> weight, IPrimitive<WeightUnit> weightUnit) {
        if (weight.isNull() || weightUnit.isNull()) {
            return 0;
        }
        switch (weightUnit.getValue()) {
        case lb:
            return (int) (1.0 * weight.getValue() / 0.45359237d);
        default:
            return weight.getValue();
        }
    }

    public static int getWeightKg(int value, WeightUnit unit) {
        switch (unit) {
        case lb:
            return (int) (0.45359237d * value);
        default:
            return value;
        }

    }

    public static Integer getWeightKg(IPrimitive<Integer> weight, IPrimitive<WeightUnit> weightUnit) {
        if (weight.isNull() || weightUnit.isNull()) {
            return 0;
        }
        return getWeightKg(weight.getValue(), weightUnit.getValue());
    }

    public static Integer getAreaInSqFeet(IPrimitive<Double> area, IPrimitive<AreaMeasurementUnit> areaUnits) {
        if (area.isNull() || areaUnits.isNull()) {
            return null;
        }
        switch (areaUnits.getValue()) {
        case sqFeet:
            return Double.valueOf(Math.floor(area.getValue())).intValue();
        default:
            return Double.valueOf(Math.floor(area.getValue() * 10.763911)).intValue();
        }
    }

    public static Double min(Double a, Double b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return Math.min(a, b);
        }
    }

    public static Double max(Double a, Double b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return Math.max(a, b);
        }
    }

    public static Integer min(Integer a, Integer b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return Math.min(a, b);
        }
    }

    public static Integer max(Integer a, Integer b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return Math.max(a, b);
        }
    }
}
