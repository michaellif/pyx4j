/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;

@Transient
public interface TenantSureCoverageDTO extends IEntity {

    @I18n
    public enum PreviousClaims {

        None(0),

        @Translate("1")
        One(1),

        @Translate("2")
        Two(2),

        @Translate("More")
        MoreThanTwo(-1);

        private final int numericValue;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        PreviousClaims(int numericValue) {
            this.numericValue = numericValue;
        }

        public final int numericValue() {
            if (numericValue >= 0) {
                return this.numericValue;
            } else {
                throw new Error("doesn't have a value");
            }
        }
    }

    @NotNull
    @Caption(name = "Name")
    IPrimitive<String> tenantName();

    @NotNull
    @Editor(type = EditorType.phone)
    @Caption(name = "Phone")
    IPrimitive<String> tenantPhone();

    @Format("#,##0")
    @Editor(type = EditorType.money)
    @NotNull
    IPrimitive<BigDecimal> personalLiabilityCoverage();

    @Format("#,##0")
    @Editor(type = EditorType.money)
    @NotNull
    IPrimitive<BigDecimal> contentsCoverage();

    @Caption(name = "Deductible (per claim)")
    @NotNull
    IPrimitive<BigDecimal> deductible();

    IPrimitive<LogicalDate> inceptionDate();

    // these are statement of fact questions
    @NotNull
    @Caption(name = "Number of previous claims made by tenanats")
    IPrimitive<PreviousClaims> numberOfPreviousClaims();

    @Caption(name = "Is any one of the tenants a smoker?")
    @NotNull
    IPrimitive<Boolean> smoker();

    @Caption
    @NotNull
    IPrimitive<TenantSurePaymentSchedule> paymentSchedule();

}
