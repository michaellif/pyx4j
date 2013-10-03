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
package com.propertyvista.operations.client;

import static com.pyx4j.site.client.AppPlaceEntityMapper.register;

import com.google.gwt.resources.client.ImageResource;

import com.propertyvista.operations.client.resources.OperationsImages;
import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimRecord;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.AuditRecordOperationsDTO;
import com.propertyvista.operations.rpc.dto.PadBatchDTO;
import com.propertyvista.operations.rpc.dto.PadDebitRecordDTO;
import com.propertyvista.operations.rpc.dto.PadFileDTO;
import com.propertyvista.operations.rpc.dto.PadReconciliationDebitRecordDTO;
import com.propertyvista.operations.rpc.dto.PadReconciliationFileDTO;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.dto.TriggerDTO;

public class OperationsEntityMapper {

    private static ImageResource DEFAULT_IMAGE = OperationsImages.INSTANCE.blank();

    public static void init() {
        register(OperationsUser.class, OperationsSiteMap.Administration.AdminUsers.class, DEFAULT_IMAGE);
        register(PmcDTO.class, OperationsSiteMap.Management.PMC.class, DEFAULT_IMAGE);
        register(TriggerDTO.class, OperationsSiteMap.Management.Trigger.class, DEFAULT_IMAGE);
        register(Run.class, OperationsSiteMap.Management.TriggerRun.class, DEFAULT_IMAGE);
        register(RunData.class, OperationsSiteMap.Management.TriggerRunData.class, DEFAULT_IMAGE);

        register(DirectDebitRecord.class, OperationsSiteMap.FundsTransfer.DirectDebitRecord.class, DEFAULT_IMAGE);

        register(PadFileDTO.class, OperationsSiteMap.FundsTransfer.FundsTransferFile.class, DEFAULT_IMAGE);
        register(PadDebitRecordDTO.class, OperationsSiteMap.FundsTransfer.FundsTransferRecord.class, DEFAULT_IMAGE);
        register(PadBatchDTO.class, OperationsSiteMap.FundsTransfer.FundsTransferBatch.class, DEFAULT_IMAGE);

        register(PadReconciliationFileDTO.class, OperationsSiteMap.FundsTransfer.FundsReconciliationFile.class, DEFAULT_IMAGE);
        register(PadReconciliationDebitRecordDTO.class, OperationsSiteMap.FundsTransfer.FundsReconciliationRecord.class, DEFAULT_IMAGE);

        register(PadSimFile.class, OperationsSiteMap.Simulator.PadSimulation.PadSimFile.class, DEFAULT_IMAGE);
        register(PadSimBatch.class, OperationsSiteMap.Simulator.PadSimulation.PadSimBatch.class, DEFAULT_IMAGE);

        register(CardServiceSimulationMerchantAccount.class, OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationMerchantAccount.class,
                DEFAULT_IMAGE);
        register(CardServiceSimulationCard.class, OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationCard.class, DEFAULT_IMAGE);
        register(CardServiceSimulationTransaction.class, OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationTransaction.class,
                DEFAULT_IMAGE);

        register(AuditRecordOperationsDTO.class, OperationsSiteMap.Security.AuditRecord.class, DEFAULT_IMAGE);

        register(DirectDebitSimRecord.class, OperationsSiteMap.Simulator.DirectBankingSimRecord.class, DEFAULT_IMAGE);
        register(DirectDebitSimFile.class, OperationsSiteMap.Simulator.DirectBankingSimFile.class, DEFAULT_IMAGE);
    }
}
