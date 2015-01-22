/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author michaellif
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.communication.DeliveryHandle.MessageType;

public interface BroadcastTemplate extends MessageTemplate {

    @I18n(context = "Broadcast Template Audience Type")
    @XmlType(name = "Audience Type")
    public enum AudienceType {
        Customer, Tenant, Guarantor, Prospect, Employee;
        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @Length(78)
    @ToString(index = 0)
    IPrimitive<String> name();

    @Override
    @NotNull
    @ToString(index = 1)
    IPrimitive<String> subject();

    IPrimitive<AudienceType> audienceType();

    @NotNull
    IPrimitive<MessageType> messageType();

    @ReadOnly
    IPrimitive<Boolean> allowedReply();

    @NotNull
    @Detached(level = AttachLevel.ToStringMembers)
    @MemberColumn(notNull = true)
    MessageCategory category();

    IPrimitive<Boolean> highImportance();

    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<BroadcastAttachment> attachments();

    //TODO make hierarchy common parent for BroadcastTemplate, CommunicationThread and BroadcastEvent
//    @Owned
//    @Detached
//    @ReadOnly
//    SpecialDelivery specialDelivery();

}
