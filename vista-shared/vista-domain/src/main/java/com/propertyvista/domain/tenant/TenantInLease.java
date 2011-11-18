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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.tenant.lease.Lease;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface TenantInLease extends IBoundToApplication {

    @I18n
    @XmlType(name = "TenantRelationship")
    public static enum Relationship implements Serializable {

        Spouse,

        Son,

        Daughter,

        Mother,

        Father,

        Grandmother,

        Grandfather,

        Uncle,

        Aunt,

        Other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "TenantStatus")
    public static enum Status implements Serializable {

        Applicant,

        @Translate("Co-Applicant")
        CoApplicant,

        Dependent;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Detached
    @NotNull
    @Indexed
    Lease lease();

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    Tenant tenant();

    @ToString(index = 1)
    @NotNull
    IPrimitive<Relationship> relationship();

    @ToString(index = 2)
    @NotNull
    IPrimitive<Status> status();

    @Caption(name = "Take Ownership", description = "Main Applicant To Complete This Part Of The Application")
    IPrimitive<Boolean> takeOwnership();

    /**
     * Tenant's payment share:
     */
    IPrimitive<Integer> percentage();
}
