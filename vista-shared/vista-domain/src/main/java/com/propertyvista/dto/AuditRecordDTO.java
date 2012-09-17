/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.dto;

import java.util.Date;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.VistaApplication;

@Transient
public interface AuditRecordDTO extends IEntity {

    IPrimitive<String> userName();

    IPrimitive<String> remoteAddr();

    IPrimitive<Date> when();

    IPrimitive<AuditRecordEventType> event();

    IPrimitive<String> pmc();

    IPrimitive<VistaApplication> app();

    IPrimitive<String> details();
}
