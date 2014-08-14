/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-21
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@DiscriminatorValue("CommunicationThread")
public interface CommunicationThread extends IEntity {

    @I18n(context = "Communication Thread Status")
    @XmlType(name = "ThreadStatus")
    public enum ThreadStatus {

        Unassigned, New, Open, Resolved, Cancelled, Closed;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ReadOnly
    @Length(78)
    IPrimitive<String> subject();

    @ReadOnly
    IPrimitive<Boolean> allowedReply();

    @NotNull
    IPrimitive<ThreadStatus> status();

    @NotNull
    @Detached
    @MemberColumn(notNull = true)
    MessageCategory topic();

    @NotNull
    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<Message> content();

    @Detached
    @NotNull
    @MemberColumn(notNull = true)
    CommunicationEndpoint owner();

}
