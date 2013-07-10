/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.domain.security;

import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.security.shared.Behavior;

@RpcBlacklist
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface BehaviorHolder extends IEntity {

    IPrimitiveSet<Behavior> permissions();
}
