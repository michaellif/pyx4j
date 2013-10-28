/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.person.Name;

@Transient
public interface LatestActivitiesDTO extends IEntity {

    @Transient
    @ToStringFormat("{0}, ${1} - {2}")
    public interface InvoicePaymentDTO extends IEntity {
        @Override
        @Indexed
        @ToString(index = 0)
        @Editor(type = EditorType.label)
        IPrimitive<Key> id();

        @ToString(index = 1)
        @NotNull
        @Format("#,##0.00")
        @Editor(type = EditorType.moneylabel)
        IPrimitive<BigDecimal> amount();

        @ToString(index = 2)
        @Editor(type = EditorType.label)
        IPrimitive<PaymentStatus> paymentStatus();

        @ToString(index = 3)
        @Editor(type = EditorType.label)
        IPrimitive<LogicalDate> postDate();

        @ToString(index = 2)
        @Editor(type = EditorType.label)
        Name payer();
    }

    IList<InvoicePaymentDTO> payments();
}
