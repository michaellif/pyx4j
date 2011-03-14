/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import java.io.Serializable;
import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
@ToStringFormat("{0} {1} {2}")
public interface PotentialTenant extends IEntity, IPerson, IBoundToApplication {

    public enum Relationship implements Serializable {

        //TODO i18n

        Spouse,

        Son,

        Daughter,

        Mother,

        Father,

        GrandMother,

        GrandFather,

        Uncle,

        Aunt,

        Other;

        private final String label;

        Relationship() {
            this.label = name();
        }

        Relationship(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum Status implements Serializable {

        //TODO i18n

        Applicant,

        CoApplicant("Co-applicant"),

        Dependant;

        private final String label;

        Status() {
            this.label = name();
        }

        Status(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Caption(name = "Birth Date")
    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<Date> birthDate();

    @ToString(index = 0)
    @NotNull
    IPrimitive<Relationship> relationship();

    @ToString(index = 0)
    IPrimitive<Status> status();

    //TODO add appropriate description
    @Caption(name = "Take Ownership", description = "Take Ownership of application filling means ...")
    IPrimitive<Boolean> takeOwnership();

    IPrimitive<Double> payment();
}
