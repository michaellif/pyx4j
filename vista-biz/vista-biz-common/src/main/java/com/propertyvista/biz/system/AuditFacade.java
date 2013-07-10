/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.common.AbstractUser;

public interface AuditFacade {

    public void login();

    public void loginFailed(AbstractUser user);

    public void credentialsUpdated(AbstractUser user);

    public void created(IEntity entity);

    public void updated(IEntity entity, String changes);

    public void read(IEntity entity);

    public void info(String format, Object... args);

    public void record(AuditRecordEventType eventType, IEntity entity, String format, Object... args);

}
