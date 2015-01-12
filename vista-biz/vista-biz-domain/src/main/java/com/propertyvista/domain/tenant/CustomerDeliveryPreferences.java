/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2014
 * @author smolka
 */
package com.propertyvista.domain.tenant;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

public interface CustomerDeliveryPreferences extends IEntity {

    @I18n(context = "DeliveryType")
    @XmlType(name = "DeliveryType")
    public enum DeliveryType implements Serializable {

        @Translate("Daily")
        Daily,

        @Translate("Weekly")
        Weekly,

        @Translate("Real-time")
        Individual,

        @Translate("Do not send")
        DoNotSend;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @Detached
    @JoinColumn
    @ReadOnly
    CustomerPreferences userPreferences();

    IPrimitive<DeliveryType> promotionalDelivery();

    IPrimitive<DeliveryType> informationalDelivery();
}
