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
package com.propertyvista.portal.domain.pt;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface TenantAsset extends IEntity {

    public enum AssetType {

        //TODO i18n

        bankAccounts("Bank Accounts"),

        realEstateProperties("Real Estate Properties"),

        insurancePolicies("Insurance Policies"),

        shares("Shares"),

        unitTrusts("Unit Trusts"),

        businesses("Businesses"),

        cars("Cars"),

        other("Other");

        private final String label;

        AssetType() {
            this.label = name();
        }

        AssetType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
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
