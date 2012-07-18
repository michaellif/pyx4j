/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.shared.config.VistaDemo.VistaDemoConfiguration;

public class VistaDemo {

    public static class VistaDemoConfigurationImpl implements VistaDemoConfiguration {

        @Override
        public boolean isDemo() {
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).isVistaDemo();
        }

    }
}
