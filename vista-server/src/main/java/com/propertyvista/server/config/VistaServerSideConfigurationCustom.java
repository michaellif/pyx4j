/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaSystemsSimulationConfig;

public class VistaServerSideConfigurationCustom extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.  All defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=Custom
        return this;
    }

    @Override
    public Integer enviromentId() {
        String enviromentId = getConfigProperties().getValue("enviromentId");
        if (CommonsStringUtils.isStringSet(enviromentId)) {
            return Integer.valueOf(enviromentId);
        } else {
            return null;
        }
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return getConfigProperties().getBooleanValue("isDevelopmentBehavior", true);
    }

    @Override
    public boolean allowToBypassRpcServiceManifest() {
        return getConfigProperties().getBooleanValue("allowToBypassRpcServiceManifest", false);
    }

    @Override
    public boolean openIdRequired() {
        return getConfigProperties().getBooleanValue("openIdrequired", true);
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        return getConfigProperties().getValue("ApplicationURLNamespace");
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        if (getConfigProperties().getValue("db.type").equals("PostgreSQL")) {
            return new VistaConfigurationPostgreSQLProperties(getConfigDirectory(), getConfigProperties().getProperties());
        } else {
            return new VistaConfigurationMySQLProperties(getConfigDirectory(), getConfigProperties().getProperties());
        }
    }

    @Override
    public int interfaceSSHDPort() {
        return getConfigProperties().getIntegerValue("interfaceSSHDPort", 0);
    }

    @Override
    public boolean isFundsTransferSimulationConfigurable() {
        return true;
    }

    @Override
    public CaledonFundsTransferConfiguration getCaledonFundsTransferConfiguration() {
        if (VistaSystemsSimulationConfig.getConfiguration().useFundsTransferSimulator().getValue(Boolean.TRUE)) {
            return new CaledonFundsTransferConfigurationSimulatorCustom(this);
        } else {
            return new CaledonFundsTransferConfigurationProdTest(this);
        }
    }

    @Override
    public BmoInterfaceConfiguration getBmoInterfaceConfiguration() {
        if (VistaSystemsSimulationConfig.getConfiguration().useDirectBankingSimulator().getValue(Boolean.TRUE)) {
            return new BmoInterfaceConfigurationSimulatorCustom(this);
        } else {
            return new BmoInterfaceConfigurationProd(this);
        }
    }
}
