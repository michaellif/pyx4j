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

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.financial.Currency;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.tenant.lease.extradata.Pet.WeightUnit;

public class DomainUtil {

    public static Currency createCurrency() {
        Currency currency = EntityFactory.create(Currency.class);
        currency.name().setValue("CAD");
        return currency;
    }

    public static BigDecimal roundMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static String last4Numbers(String value) {
        if (value.length() < 4) {
            return null;
        } else {
            return value.substring(value.length() - 4, value.length());
        }
    }

    // TODO move to format of the object
    public static String obfuscateCreditCardNumber(String value) {
        return "XXXXXXXXXXXX" + DomainUtil.last4Numbers(value);
    }

    // TODO move to format of the object
    public static String obfuscateAccountNumber(String value) {
        return "XXXXXXXX" + DomainUtil.last4Numbers(value);
    }

    public static ChargeLine createChargeLine(String label, BigDecimal money) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.amount().setValue(money);
        cl.label().setValue(label);
        return cl;
    }

    public static ChargeLine createChargeLine(ChargeType type, BigDecimal money) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.amount().setValue(money);
        cl.type().setValue(type);
        cl.label().setValue(type.toString());
        return cl;
    }

    public static ChargeLine createChargeLine(String label, ChargeType type, BigDecimal money) {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.amount().setValue(money);
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

    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a.min(b);
        }
    }

    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a.max(b);
        }
    }

    public static void nvlAddBigDecimal(IPrimitive<BigDecimal> total, IPrimitive<BigDecimal> value) {
        if (value.isNull()) {
            return;
        } else if (total.isNull()) {
            total.setValue(value.getValue());
        } else {
            total.setValue(total.getValue().add(value.getValue()));
        }
    }

    public static void nvlAddLong(IPrimitive<Long> target, IPrimitive<Long> value) {
        if (target.isNull()) {
            target.setValue(value.getValue());
        } else if (!value.isNull()) {
            target.setValue(target.getValue() + value.getValue());
        }
    }

    /**
     * Create min max Pair from Two pairs of the same type
     */
    public static MinMaxPair<BigDecimal> minMaxPair(MinMaxPair<BigDecimal> pairOne, MinMaxPair<BigDecimal> pairTwo) {
        MinMaxPair<BigDecimal> result = new MinMaxPair<BigDecimal>();
        result.setMin(min(pairOne.getMin(), pairTwo.getMin()));
        result.setMax(max(pairOne.getMax(), pairTwo.getMax()));
        return result;
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
