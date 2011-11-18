/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

public enum IncomeSource {

    @Translate("Full time")
    fulltime,

    @Translate("Part time")
    parttime,

    @Translate("Self Employed")
    selfemployed,

    @Translate("Seasonally Employed")
    seasonallyEmployed,

    @Translate("Social Services")
    socialServices,

    @Translate("Pension")
    pension,

    @Translate("Retired")
    retired,

    @Translate("Student")
    student,

    @Translate("Unemployment")
    unemployment,

    @Translate("Ontario Disability Support Program (ODSP)")
    odsp,

    @Translate("Dividends")
    dividends,

    @Translate("Other")
    other;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }

}
