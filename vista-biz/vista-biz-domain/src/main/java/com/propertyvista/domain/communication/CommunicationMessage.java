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

import java.util.Date;

import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface CommunicationMessage extends IEntity {

    CommunicationMessage parent();

    @JoinColumn
    @NotNull
    CommunicationPerson sender();

    @JoinColumn
    @NotNull
    CommunicationPerson destination();

    @NotNull
    IPrimitive<String> topic();

    @NotNull
    IPrimitive<String> content();

    @NotNull
    IPrimitive<Boolean> isHighImportance();

    @NotNull
    IPrimitive<Boolean> isRead();

    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    IPrimitive<Date> created();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

}
