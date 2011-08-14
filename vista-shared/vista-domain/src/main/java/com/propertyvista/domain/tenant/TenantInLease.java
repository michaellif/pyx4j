/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.io.Serializable;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

import com.propertyvista.domain.tenant.lease.Lease;

public interface TenantInLease extends IEntity /* TODO IBoundToApplication ? */{

    @Translatable
    public static enum Status implements Serializable {

        Applicant,

        @Translation("Co-applicant")
        CoApplicant,

        Dependant;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public static enum Relationship implements Serializable {

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

    @Detached
    @NotNull
    @Indexed
    Lease lease();

    @ReadOnly
    @NotNull
    Tenant tenant();

    /**
     * Tenant's payment share:
     */
    IPrimitive<Integer> percentage();

    @ToString(index = 0)
    @NotNull
    IPrimitive<TenantInLease.Relationship> relationship();

    @ToString(index = 1)
    @NotNull
    IPrimitive<TenantInLease.Status> status();

    //TODO add appropriate description
    @Caption(name = "Take Ownership", description = "Take Ownership of application filling means ...")
    IPrimitive<Boolean> takeOwnership();

}
