/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.rpc.dto;

import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.rpc.admin.NetworkSimulation;

import com.propertyvista.operations.domain.dev.CardServiceSimulatorConfig;
import com.propertyvista.operations.domain.dev.EquifaxSimulatorConfig;
import com.propertyvista.operations.dto.VistaSystemsSimulationConfigDTO;

@Transient
public interface SimulationDTO extends IEntity {

    IPrimitive<Boolean> generalCacheEnabled();

    IPrimitive<Boolean> entityCacheServiceEnabled();

    @ReadOnly
    IPrimitive<String> memcacheStatistics();

    NetworkSimulation networkSimulation();

    IPrimitive<String> devSessionDuration();

    IPrimitive<String> applicationSessionDuration();

    @ReadOnly
    IPrimitive<String> containerSessionTimeout();

    VistaSystemsSimulationConfigDTO systems();

    IPrimitive<Boolean> fundsTransferSimulationConfigurable();

    EquifaxSimulatorConfig equifax();

    // TODO This should be in separate server/separate forms
    @Deprecated
    CardServiceSimulatorConfig cardService();
}
