/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@AbstractEntity
public interface SpecialDelivery extends IEntity {

    @I18n(context = "DeliveryMethod")
    @XmlType(name = "DeliveryMethod")
    public enum DeliveryMethod {
        @Translate("IVR")
        IVR,

        @Translate("SMS")
        SMS,

        @Translate("Notification")
        Notification;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @Detached
    @MemberColumn(name = "thrd")
    CommunicationThread thread();
}
