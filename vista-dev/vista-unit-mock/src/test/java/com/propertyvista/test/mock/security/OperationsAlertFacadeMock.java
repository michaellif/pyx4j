/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author vlads
 */
package com.propertyvista.test.mock.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IEntity;

import com.propertyvista.biz.system.OperationsAlertFacade;

public class OperationsAlertFacadeMock implements OperationsAlertFacade {

    private final static Logger log = LoggerFactory.getLogger(OperationsAlertFacadeMock.class);

    @Override
    public void record(IEntity entity, String format, Object... args) {
        log.info("Mock OperationsAlert: {}, Example: {}", SimpleMessageFormat.format(format, args), entity);

    }

    @Override
    public void sendEmailAlert(String subject, String format, Object... args) {
        log.info("Mock OperationsAlert: {} {}", subject, SimpleMessageFormat.format(format, args));
    }

}
