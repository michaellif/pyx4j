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
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translation;

public interface Pet extends IEntity {

    public enum PetType {

        @Translation("Dog")
        dog,

        @Translation("Cat")
        cat;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    public enum WeightUnit {

        @Translation("LB")
        lb,

        @Translation("KG")
        kg;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Caption(name = "Pet type")
    @NotNull
    @BusinessEqualValue
    @MemberColumn(name = "tp")
    public IPrimitive<PetType> type();

    @NotNull
    @BusinessEqualValue
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
    @Format("MM/dd/yyyy")
    @BusinessEqualValue
    public IPrimitive<java.sql.Date> birthDate();

    @EmbeddedEntity
    @Caption(name = "Charge")
    public ChargeLine chargeLine();

}
