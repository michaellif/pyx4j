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
package com.propertyvista.operations.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.admin.NetworkSimulation;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface VistaSystemsSimulationConfigDTO extends IEntity {

    IPrimitive<Boolean> useFundsTransferSimulator();

    IPrimitive<Boolean> useDirectBankingSimulator();

    IPrimitive<Boolean> useCardServiceSimulator();

    IPrimitive<Boolean> useEquifaxSimulator();

    NetworkSimulation yardiInterfaceNetworkSimulation();

    IPrimitive<Boolean> yardiAllTenantsToHaveEmails();
}
