/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;

public class VistaTestsServerSideConfiguration extends EssentialsServerSideConfiguration {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        boolean debugTests = false;
        if (debugTests) {
            return new VistaTestsDBConfigurationHSQLFile();
        } else {
            return new VistaTestsDBConfigurationHSQLMemory();
        }
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

}
