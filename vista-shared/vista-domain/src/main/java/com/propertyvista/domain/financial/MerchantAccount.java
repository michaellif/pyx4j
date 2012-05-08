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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public interface MerchantAccount extends IEntity {

    @I18n
    enum MerchantAccountStatus {

        ElectronicPaymentsAllowed,

        NoElectronicPaymentsAllowed;

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

    // aka external id for updates from onboarding
    IPrimitive<String> onboardingBankAccountId();

    @Length(8)
    @RpcTransient
    IPrimitive<String> merchantTerminalId();

    @Length(3)
    @ToString
    IPrimitive<String> bankId();

    @Length(5)
    @ToString
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    @ToString
    IPrimitive<String> accountNumber();

    // filed editable by CRM

    /**
     * Caledon: Description to appear on client's statement. Typically a merchant's business name.
     */
    @Caption(description = "Description to appear on client's statement. Typically a merchant's business name.")
    IPrimitive<String> chargeDescription();
}
