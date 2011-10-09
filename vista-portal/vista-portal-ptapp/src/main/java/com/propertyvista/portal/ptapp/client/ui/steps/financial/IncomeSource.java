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
package com.propertyvista.portal.ptapp.client.ui.steps.financial;

import com.pyx4j.entity.client.ui.flex.editor.IDiscriminator;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSeasonallyEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSocialServices;

public enum IncomeSource implements IDiscriminator<IIncomeInfo> {

    @Translate("Full time")
    fulltime(IncomeInfoEmployer.class),

    @Translate("Part time")
    parttime(IncomeInfoEmployer.class),

    @Translate("Self employed")
    selfemployed(IncomeInfoSelfEmployed.class),

    @Translate("Seasonally Employed")
    seasonallyEmployed(IncomeInfoSeasonallyEmployed.class),

    @Translate("Social Services")
    socialServices(IncomeInfoSocialServices.class),

    @Translate("Pension")
    pension(IncomeInfoEmployer.class),

    @Translate("Retired")
    retired(IncomeInfoEmployer.class),

    @Translate("Student")
    student(IncomeInfoEmployer.class),

    @Translate("Unemployment")
    unemployment(IncomeInfoEmployer.class),

    @Translate("ODSP")
    odsp(IncomeInfoEmployer.class),

    @Translate("Dividends")
    dividends(IncomeInfoEmployer.class),

    @Translate("Other")
    other(IncomeInfoEmployer.class);

    private final Class<? extends IIncomeInfo> type;

    private IncomeSource(Class<? extends IIncomeInfo> type) {
        this.type = type;
    }

    @Override
    public Class<? extends IIncomeInfo> getType() {
        return type;
    }

    @Override
    public String getName() {
        return I18nEnum.tr(this);
    }

    @Override
    public String toString() {
        return getName();
    }

}
