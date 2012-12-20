/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-20
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.payment.PaymentMethod;

public interface PaymentFee {

    @I18n
    @XmlType(name = "Fee Type")
    enum Type {
        percentage, monetary;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    PaymentMethod method();

    IPrimitive<BigDecimal> fee();

    IPrimitive<PaymentFee.Type> feeType();
}