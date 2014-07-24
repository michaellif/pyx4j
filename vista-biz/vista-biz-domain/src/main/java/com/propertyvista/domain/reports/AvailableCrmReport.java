/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.reports;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.security.CrmRole;

public interface AvailableCrmReport extends IEntity {

    @I18n
    public enum CrmReportType {

        AutoPayChanges,

        Availability,

        CustomerCreditCheck,

        EFT,

        @Translate("EFT Variance")
        EftVariance,

        ResidentInsurance;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Indexed(uniqueConstraint = true)
    IPrimitive<CrmReportType> reportType();

    @NotNull
    @Detached
    @MemberColumn(name = "rls")
    IList<CrmRole> roles();
}
