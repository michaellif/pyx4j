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

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.media.ProofOfAssetDocumentFolder;
import com.propertyvista.domain.tenant.CustomerScreening;

@Caption(name = "Personal Asset(s)")
public interface CustomerScreeningPersonalAsset extends IEntity {

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
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    CustomerScreening.CustomerScreeningV owner();

    @OrderColumn
    IPrimitive<Integer> seq();

    @NotNull
    @ToString(index = 0)
    IPrimitive<AssetType> assetType();

    @NotNull
    @Caption(name = "% Ownership")
    @MemberColumn(name = "prcnt")
    IPrimitive<Double> percent();

    @NotNull
    @Format("#,##0.00")
    @Caption(name = "Current Value")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> assetValue();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<ProofOfAssetDocumentFolder> documents();
}
