/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.AbstractAntiBot;

import com.propertyvista.onboarding.RequestMessageIO;

public class OnboardingSecurity {

    private final static Logger log = LoggerFactory.getLogger(OnboardingSecurity.class);

    public static boolean enter(RequestMessageIO requestMessage) {
        try {
            AbstractAntiBot.assertLogin(requestMessage.interfaceEntity().getValue(), null);
        } catch (Throwable e) {
            log.error("", e);
            return false;
        }
        return "rossul".equals(requestMessage.interfaceEntity().getValue()) && "secret".equals(requestMessage.interfaceEntityPassword().getValue());
    }
}
