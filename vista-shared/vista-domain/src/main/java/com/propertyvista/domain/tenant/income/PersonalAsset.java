/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.Money;

public interface PersonalAsset extends IEntity {

    public enum AssetType {

        @Translate("Bank Accounts")
        bankAccounts,

        @Translate("Real Estate Properties")
        realEstateProperties,

        @Translate("Insurance Policies")
        insurancePolicies,

        @Translate("Shares")
        shares,

        @Translate("Unit Trusts")
        unitTrusts,

        @Translate("Businesses")
        businesses,

        @Translate("Cars")
        cars,

        @Translate("Other")
        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    IPrimitive<AssetType> assetType();

    @Caption(name = "% Ownership")
    @MemberColumn(name = "prcnt")
    IPrimitive<Double> percent();

    @Caption(name = "Current Value")
    Money assetValue();
}
