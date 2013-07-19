/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.bmo.simulator;

import org.apache.commons.lang.Validate;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.payment.pad.sim.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.pad.sim.DirectDebitSimFile.DirectDebitSimFileStatus;

public class DirectDebitSimManager {

    public void send(DirectDebitSimFile directDebitSimFileId) {
        DirectDebitSimFile directDebitSimFile = Persistence.service().retrieve(DirectDebitSimFile.class, directDebitSimFileId.getPrimaryKey());
        Validate.isTrue(directDebitSimFile.status().getValue() == DirectDebitSimFileStatus.New);
        directDebitSimFile.status().setValue(DirectDebitSimFileStatus.Sent);
        Persistence.service().persist(directDebitSimFile);

        Persistence.service().retrieveMember(directDebitSimFile.records());
    }
}
