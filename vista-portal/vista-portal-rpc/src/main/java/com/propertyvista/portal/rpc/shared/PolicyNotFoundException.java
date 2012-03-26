/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.policy.framework.Policy;

@SuppressWarnings("serial")
public class PolicyNotFoundException extends UserRuntimeException {

    private static final I18n i18n = I18n.get(PolicyNotFoundException.class);

    protected PolicyNotFoundException() {
    }

    public PolicyNotFoundException(Class<? extends Policy> policyClass, String nodeStringView) {
        super(i18n.tr("Policy ''{0}'' was not found for node ''{1}''", GWTJava5Helper.getSimpleName(policyClass), nodeStringView));
    }
}