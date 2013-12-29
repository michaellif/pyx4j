/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import java.util.EnumSet;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.operations.dto.VistaSystemsSimulationConfigDTO;
import com.propertyvista.shared.VistaSystemIdentification;
import com.propertyvista.shared.config.VistaDemo;

public class VistaSystemsSimulationConfig {

    private static VistaSystemsSimulationConfigDTO configuration;

    static {
        configuration = EntityFactory.create(VistaSystemsSimulationConfigDTO.class);
        configuration.useEquifaxSimulator().setValue(VistaDemo.isDemo() || ApplicationMode.isDevelopment());
        configuration.useFundsTransferSimulator().setValue(
                !EnumSet.of(VistaSystemIdentification.production, VistaSystemIdentification.staging).contains(VistaDeployment.getSystemIdentification()));
        configuration.useCardServiceSimulator().setValue(configuration.useFundsTransferSimulator().getValue());
        configuration.useDirectBankingSimulator().setValue(configuration.useFundsTransferSimulator().getValue());
    }

    public static VistaSystemsSimulationConfigDTO getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(VistaSystemsSimulationConfigDTO configuration) {
        VistaSystemsSimulationConfig.configuration = configuration;
    }
}
