/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@DiscriminatorValue("NotificationDelivery")
public interface NotificationDelivery extends SpecialDelivery {

    @I18n(context = "NotificationType")
    @XmlType(name = "NotificationType")
    public enum NotificationType {

        @Translate("Information")
        Information,

        @Translate("Warning")
        Warning,

        @Translate("Note")
        Note;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ReadOnly
    IPrimitive<LogicalDate> dateFrom();

    @ReadOnly
    IPrimitive<NotificationType> notificationType();

    @ReadOnly
    IPrimitive<LogicalDate> dateTo();
}
