/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{0}: {1}: {2}")
public interface MerchantAccount extends AbstractMerchantAccount {

    @I18n
    enum MerchantAccountStatus {

        ElectronicPaymentsAllowed,

        NoElectronicPaymentsAllowed,

        Invalid;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    /**
     * Calculated base on terminal_id before sending it to GWT
     */
    @Transient
    IPrimitive<MerchantAccountStatus> status();

    IPrimitive<Boolean> invalid();

    @JoinTable(value = BuildingMerchantAccount.class)
    @Detached(level = AttachLevel.Detached)
    ISet<Building> _buildings();
}
