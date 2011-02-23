/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-23
 * @author antonk
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.shared.IPrimitive;

public interface PaymentInfo extends IAddress {
    public enum PaymentType {
        Visa, Amex, MasterCard, Discover, Echeck, Interac
    }

    Money deposit();

    Money applicationFee();

    IPrimitive<Boolean> preauthorised();

    CreditCardInfo creditCard();

    EcheckInfo echeck();

    InteracInfo interac();

}
