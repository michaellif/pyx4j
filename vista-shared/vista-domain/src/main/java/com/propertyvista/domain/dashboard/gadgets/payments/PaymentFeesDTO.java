/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.payments;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface PaymentFeesDTO extends IEntity {

    @I18n
    enum PaymentFeeMeasure {

        @Translate("Fee, $ per transaction")
        absolute,

        @Translate("Fee, % per transaction")
        relative;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    // this caption is intentionally left blank :)
    @Caption(name = "")
    IPrimitive<PaymentFeeMeasure> paymentFeeMeasure();

    IPrimitive<BigDecimal> cash();

    IPrimitive<BigDecimal> cheque();

    IPrimitive<BigDecimal> eCheque();

    @Caption(name = "EFT")
    IPrimitive<BigDecimal> eft();

    @Caption(name = "CC")
    IPrimitive<BigDecimal> cc();

    IPrimitive<BigDecimal> interacCaledon();

    IPrimitive<BigDecimal> interacVisa();

}
