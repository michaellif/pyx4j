/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class VistaTestsConnectionPoolConfiguration extends ConnectionPoolConfiguration {

    public VistaTestsConnectionPoolConfiguration(ConnectionPoolType connectionType) {
        super(connectionType);
        minPoolSize = 1;
        maxPoolSize = 5;
        if (unreturnedConnectionTimeout != 0) {
            switch (connectionType) {
            case BackgroundProcess:
                unreturnedConnectionTimeout = 1 * Consts.MIN2SEC;
                break;
            case TransactionProcessing:
                unreturnedConnectionTimeout = 1 * Consts.MIN2SEC;
                break;
            default:
                break;
            }
        }
    }

}
