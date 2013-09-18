/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.vista2pmc;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.admin.CreditCheckStatusDTO;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckStatusCrudService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.server.jobs.TaskRunner;

public class CreditCheckStatusCrudServiceImpl implements CreditCheckStatusCrudService {

    @Override
    public void retrieve(AsyncCallback<CreditCheckStatusDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {

        final CreditCheckStatusDTO status = EntityFactory.create(CreditCheckStatusDTO.class);
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        final AbstractEquifaxFee fees = ServerSideFactory.create(Vista2PmcFacade.class).getEquifaxFee();
        TaskRunner.runInOperationsNamespace(new Callable<VoidSerializable>() {

            @Override
            public VoidSerializable call() throws Exception {

                Persistence.service().retrieveMember(pmc.equifaxInfo());
                if (!pmc.equifaxInfo().isNull()) {
                    status.status().setValue(pmc.equifaxInfo().status().getValue());
                    status.reportType().setValue(pmc.equifaxInfo().reportType().getValue());
                    switch (status.reportType().getValue()) {
                    case FullCreditReport:
                        status.setupFee().setValue(fees.fullCreditReportSetUpFee().getValue());
                        status.perApplicantFee().setValue(fees.fullCreditReportPerApplicantFee().getValue());
                        break;
                    case RecomendationReport:
                        status.setupFee().setValue(fees.recommendationReportSetUpFee().getValue());
                        status.perApplicantFee().setValue(fees.recommendationReportPerApplicantFee().getValue());
                        break;
                    default:
                        throw new Error("Unknown Report Type");
                    }
                }

                return null;
            }
        });

        if (status.status().getValue() != null) {
            callback.onSuccess(status);
        } else {
            callback.onSuccess(null);
        }

    }

    @Override
    public void init(AsyncCallback<CreditCheckStatusDTO> callback, InitializationData initializationData) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void create(AsyncCallback<Key> callback, CreditCheckStatusDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, CreditCheckStatusDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CreditCheckStatusDTO>> callback, EntityListCriteria<CreditCheckStatusDTO> criteria) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Invalid Operation");
    }
}
