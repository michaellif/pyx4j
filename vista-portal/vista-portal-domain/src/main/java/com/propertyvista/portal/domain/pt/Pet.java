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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Pet extends IEntity {

    public enum PetType {

        //TODO i18n

        dog("Dog"), cat("Cat");

        private final String label;

        PetType() {
            this.label = name();
        }

        PetType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum WeightUnit {

        //TODO i18n

        lb("LB"), kg("KG");

        private final String label;

        WeightUnit() {
            this.label = name();
        }

        WeightUnit(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
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
    @Format("MM/dd/yyyy")
    public IPrimitive<Date> birthDate();

    @EmbeddedEntity
    @Caption(name = "Charge")
    public ChargeLine chargeLine();

}
