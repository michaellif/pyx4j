/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-14
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.ChargeType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Pet extends IEntity {
    public enum PetType {
        dog, cat
    }

    public enum WeightUnit {
        lb, kg
    }

    @Caption(name = "Pet type")
    @NotNull
    public IPrimitive<PetType> type();

    @NotNull
    public IPrimitive<String> name();

    @Caption(name = "Colour")
    @NotNull
    public IPrimitive<String> color();

    public IPrimitive<String> breed();

    @NotNull
    public IPrimitive<Integer> weight();

    @Caption(name = "")
    @NotNull
    public IPrimitive<WeightUnit> weightUnit();

    @NotNull
    public IPrimitive<Date> birthDate();

    public IPrimitive<Double> charge();

    public ChargeType chargeType();

    @Transient
    @Caption(name = "Charge")
    public IPrimitive<String> chargeDescription();

}
