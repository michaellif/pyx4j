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
 */
package com.propertyvista.operations.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.HostConfig;
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

        final AbstractVistaServerSideConfiguration conf = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class);

        b.append("  enviromentId                        : ").append(conf.enviromentId()).append("\n");
        b.append("  isDepoymentHttps                    : ").append(conf.isDepoymentHttps()).append("\n");
        b.append("  isDepoymentApplicationDispatcher    : ").append(conf.isDepoymentApplicationDispatcher()).append("\n");
        b.append("  isDepoymentUseNewDevDomains         : ").append(conf.isDepoymentUseNewDevDomains()).append("\n");
        b.append("  openDBReset                         : ").append(conf.openDBReset()).append("\n");
        b.append("  openIdRequired                      : ").append(conf.openIdRequired()).append("\n");
        b.append("  openIdDomain                        : ").append(conf.openIdDomain()).append("\n");
        b.append("  openIdProviderDomain                : ").append(conf.openIdProviderDomain()).append("\n");
        b.append("  isVistaDemo                         : ").append(conf.isDemoBehavior()).append("\n");
        b.append("  isVistaQa                           : ").append(conf.isVistaQa()).append("\n");
        b.append("  interfaceSSHDPort                   : ").append(conf.interfaceSSHDPort()).append("\n");
        b.append("  rdateServer                         : ").append(conf.rdateServer()).append("\n");
        b.append("\n");

        b.append("  OperationsAlertMailServiceConfig     :\n    ")
                .append(conf.getOperationsAlertMailServiceConfiguration().toString().replaceAll("\n", "\n    ")).append("\n");

        b.append("  TenantSureInterfaceSftpDirectory    : ").append(conf.getTenantSureInterfaceSftpDirectory().getAbsolutePath()).append("\n");
        b.append("  TenantSureConfiguration             :\n    ").append(conf.getTenantSureConfiguration().toString().replaceAll("\n", "\n    ")).append("\n");
        b.append("  TenantSureEmailSender               : ").append(conf.getTenantSureEmailSender()).append("\n");
        b.append("  TenantSureMailServiceConfiguration  :\n    ").append(conf.getTenantSureMailServiceConfiguration().toString().replaceAll("\n", "\n    "))
                .append("\n");
        b.append("\n");

        b.append("  CaledonInterfaceWorkDirectory         : ").append(conf.getCaledonInterfaceWorkDirectory().getAbsolutePath()).append("\n");

        b.append(prn("  CaledonFundsTransferConfiguration     :\n      ", new ToPrint() {
            @Override
            public Object get() {
                return conf.getCaledonFundsTransferConfiguration();
            }
        }));
        b.append("\n");

        b.append(prn("  CaledonCardsConfiguration             :\n      ", new ToPrint() {
            @Override
            public Object get() {
                return conf.getCaledonCardsConfiguration();
            }
        }));
        b.append("\n");

        b.append("  BmoInterfaceWorkDirectory             : ").append(conf.getBmoInterfaceWorkDirectory().getAbsolutePath()).append("\n");

        b.append(prn("  BmoInterfaceConfiguration             :\n      ", new ToPrint() {
            @Override
            public Object get() {
                return conf.getBmoInterfaceConfiguration();
            }
        }));
        b.append("\n");

        b.append(prn(" BankingSimulatorConfiguration            :\n      ", new ToPrint() {
            @Override
            public Object get() {
                return conf.getBankingSimulatorConfiguration();
            }
        }));
        b.append("\n");

        b.append(prn("  EquifaxInterfaceConfiguration         :\n      ", new ToPrint() {
            @Override
            public Object get() {
                return conf.getEquifaxInterfaceConfiguration();
            }
        }));
        b.append("\n");

        b.append("  EncryptedStorageConfiguration         :\n      ").append(conf.getEncryptedStorageConfiguration().toString().replaceAll("\n", "\n      "))
                .append("\n");

        b.append("\nconfig.properties:\n");

        return b.toString();
    }

    private interface ToPrint {

        public Object get();

    }

    private String prn(String prefix, ToPrint toPrint) {
        StringBuilder b = new StringBuilder();
        b.append(prefix);
        try {
            b.append(toPrint.get().toString().replaceAll("\n", "\n      "));
        } catch (Throwable e) {
            b.append(e.getMessage());
        }
        return b.toString();
    }

    @Override
    protected String buildConfigurationText() {
        StringBuilder b = new StringBuilder();
        b.append(super.buildConfigurationText());

        b.append("\nconfig.properties:\n");
        b.append(PropertiesConfiguration.stringView("  ", ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getConfigProperties()
                .getProperties()));

        b.append("\nNetwork Interfaces:\n").append(HostConfig.getNetworkInfo());

        return b.toString();
    }
}
