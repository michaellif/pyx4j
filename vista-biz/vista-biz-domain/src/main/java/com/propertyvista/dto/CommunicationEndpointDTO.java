/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.CommunicationEndpoint;

@Transient
@ToStringFormat("{0} {1}")
public interface CommunicationEndpointDTO extends CommunicationEndpoint {

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    IPrimitive<String> name();

    @NotNull
    @ReadOnly
    @ToString(index = 1)
    IPrimitive<ContactType> type();

    @NotNull
    CommunicationEndpoint endpoint();
}
