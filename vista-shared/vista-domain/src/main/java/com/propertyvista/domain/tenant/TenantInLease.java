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
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
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
    public static enum Role implements Serializable {

        Applicant,

        @Translate("Co-Applicant")
        CoApplicant,

        Dependent;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
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
    @MemberColumn(name = "status")
    IPrimitive<Role> role();

    @Caption(name = "Take Ownership", description = "By checking the box TAKE OWNERSHIP you are agreeing that the MAIN APPLICANT will have access to your personal information and that you are present during the Application Process. The MAIN APPLICANT account will be the USERNAME for future communications with the Property Manager. This Box is only recommended for Family Members. Should you wish to have a separate and secure Login Access please leave the check box blank and an e-mail alert with individual username and passwords will be automatically be sent to all Tenants and Guarantors. ")
    IPrimitive<Boolean> takeOwnership();

    /**
     * Tenant's payment share:
     */
    IPrimitive<Integer> percentage();
}
