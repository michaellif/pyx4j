/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.dto;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.common.VistaApplication;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AuditRecordOperationsDTO extends IEntity {

    IPrimitive<String> userName();

    IPrimitive<Key> userKey();

    IPrimitive<String> remoteAddr();

    IPrimitive<String> sessionId();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> when();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> worldTime();

    IPrimitive<AuditRecordEventType> event();

    IPrimitive<String> namespace();

    Pmc pmc();

    IPrimitive<VistaApplication> application();

    IPrimitive<String> targetEntity();

    IPrimitive<String> details();

    IPrimitive<Key> entityId();

    IPrimitive<String> entityClass();

}
