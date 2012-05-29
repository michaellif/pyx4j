/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

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
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface BillDTO extends IEntity {

    @I18n
    @XmlType(name = "BillType")
    public enum Type {

        Bill, Payment;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<Type> type();

    @Caption(name = "Date")
    IPrimitive<LogicalDate> fromDate();

    @Caption(name = "Reference #")
    IPrimitive<String> referenceNo();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();
}
