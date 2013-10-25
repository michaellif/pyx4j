/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.n4;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface N4GenerationQueryDTO extends IEntity {

    @I18n(context = "Delivery Method")
    @XmlType(name = "DeliveryMethod")
    public enum DeliveryMethod {

        Hand, Mail, Courier;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    @NotNull
    Employee agent();

    IList<Lease> targetDelinquentLeases();

    @NotNull
    IPrimitive<LogicalDate> noticeDate();

    IPrimitive<DeliveryMethod> deliveryMethod();

}
