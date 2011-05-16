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
package com.propertyvista.portal.domain.ptapp;

import java.io.Serializable;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

@AbstractEntity
@ToStringFormat("{0} {1} {2}")
public interface PotentialTenant extends IEntity, IPerson, IBoundToApplication {

    @Translatable
    public enum Relationship implements Serializable {

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

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    public enum Status implements Serializable {

        Applicant,

        @Translation("Co-applicant")
        CoApplicant,

        Dependant;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Caption(name = "Birth Date")
    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<java.sql.Date> birthDate();

    @ToString(index = 0)
    @NotNull
    IPrimitive<Relationship> relationship();

    @ToString(index = 0)
    @NotNull
    IPrimitive<Status> status();

    //TODO add appropriate description
    @Caption(name = "Take Ownership", description = "Take Ownership of application filling means ...")
    IPrimitive<Boolean> takeOwnership();

    IPrimitive<Double> payment();
}
