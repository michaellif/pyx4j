/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface RequestIO extends IEntity {

    /**
     * Optional unique identifier for Requests debug
     */
    IPrimitive<String> requestId();

    /**
     * The Internet Protocol (IP) address of the client or last proxy that sent the request.
     */
    @NotNull
    @Length(39)
    IPrimitive<String> requestRemoteAddr();

    IPrimitive<String> remoteSessionId();

    @NotNull
    IPrimitive<String> onboardingAccountId();
}
