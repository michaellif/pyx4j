/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.admin.ConfigInfoServlet;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.domain.security.VistaOperationsBehavior;

@SuppressWarnings("serial")
public class VistaConfigInfoServlet extends ConfigInfoServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SecurityController.assertBehavior(VistaOperationsBehavior.SystemAdmin);
        super.doGet(request, response);
    }

    @Override
    protected String applicationConfigurationText() {
        StringBuilder b = new StringBuilder();

        b.append("LocalHostName            : ").append(SystemConfig.getLocalHostName()).append("\n");

        b.append("\nVista Configuration:\n");

        AbstractVistaServerSideConfiguration conf = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class);

        b.append("  enviromentId                        : ").append(conf.enviromentId()).append("\n");
        b.append("  openDBReset                         : ").append(conf.openDBReset()).append("\n");
        b.append("  openIdRequired                      : ").append(conf.openIdRequired()).append("\n");
        b.append("  openIdDomain                        : ").append(conf.openIdDomain()).append("\n");
        b.append("  openIdProviderDomain                : ").append(conf.openIdProviderDomain()).append("\n");
        b.append("  isVistaDemo                         : ").append(conf.isVistaDemo()).append("\n");
        b.append("  isVistaQa                           : ").append(conf.isVistaQa()).append("\n");
        b.append("  interfaceSSHDPort                   : ").append(conf.interfaceSSHDPort()).append("\n");
        b.append("\n");

        b.append("  TenantSureInterfaceSftpDirectory    : ").append(conf.getTenantSureInterfaceSftpDirectory().getAbsolutePath()).append("\n");
        b.append("  TenantSureConfiguration             :\n    ").append(conf.getTenantSureConfiguration().toString().replaceAll("\n", "\n    ")).append("\n");
        b.append("  TenantSureEmailSender               : ").append(conf.getTenantSureEmailSender()).append("\n");
        b.append("  TenantSureMailServiceConfiguration  :\n    ").append(conf.getTenantSureMailServiceConfiguration().toString().replaceAll("\n", "\n    "))
                .append("\n");
        b.append("\n");

        b.append("  CaledonInterfaceWorkDirectory         : ").append(conf.getCaledonInterfaceWorkDirectory().getAbsolutePath()).append("\n");
        b.append("  CaledonFundsTransferConfiguration     :\n      ")
                .append(conf.getCaledonFundsTransferConfiguration().toString().replaceAll("\n", "\n      ")).append("\n");
        b.append("\n");
        b.append("  BmoInterfaceWorkDirectory             : ").append(conf.getBmoInterfaceWorkDirectory().getAbsolutePath()).append("\n");
        b.append("  BmoInterfaceConfiguration             :\n      ").append(conf.getBmoInterfaceConfiguration().toString().replaceAll("\n", "\n      "))
                .append("\n");
        b.append("\n");

        b.append("  BankingSimulatorConfiguration         :\n    ").append(conf.getBankingSimulatorConfiguration().toString().replaceAll("\n", "\n    "))
                .append("\n");

        return b.toString();
    }
}
