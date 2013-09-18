/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 14, 2012
 * @author michaellif
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

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckWizardService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.domain.pmc.info.BusinessInformation;
import com.propertyvista.domain.pmc.info.PersonalInformation;
import com.propertyvista.domain.pmc.info.PmcAddressSimple;
import com.propertyvista.domain.pmc.info.PmcBusinessInfoDocument;
import com.propertyvista.domain.pmc.info.PmcPersonalInformationDocument;
import com.propertyvista.dto.vista2pmc.CreditCheckSetupDTO;
import com.propertyvista.server.jobs.TaskRunner;

public class CreditCheckWizardServiceImpl implements CreditCheckWizardService {

    @Override
    public void init(AsyncCallback<CreditCheckSetupDTO> callback, InitializationData initializationData) {
        CreditCheckSetupDTO creditCheck = EntityFactory.create(CreditCheckSetupDTO.class);
        Pmc pmc = VistaDeployment.getCurrentPmc();
        creditCheck.businessInformation().companyName().setValue(pmc.name().getValue());
        creditCheck.businessInformation().documents().add(EntityFactory.create(PmcBusinessInfoDocument.class));
        creditCheck.businessInformation().documents().add(EntityFactory.create(PmcBusinessInfoDocument.class));
        callback.onSuccess(creditCheck);
    }

    @Override
    public void save(AsyncCallback<Key> callback, final CreditCheckSetupDTO dto) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        final AbstractEquifaxFee fee = ServerSideFactory.create(Vista2PmcFacade.class).getEquifaxFee();

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                BusinessInformation businessInformation = EntityFactory.create(BusinessInformation.class);
                PersonalInformation personalInformation = EntityFactory.create(PersonalInformation.class);

                for (PmcBusinessInfoDocument document : dto.businessInformation().documents()) {
                    businessInformation.documents().add(document);
                }
                for (PmcPersonalInformationDocument document : dto.personalInformation().documents()) {
                    personalInformation.documents().add(document);
                }

                // solution to CRM country namespace editing in CRM and saving in admin
                businessInformation.businessAddress().set(dto.businessInformation().dto_businessAddress().duplicate(PmcAddressSimple.class));
                personalInformation.personalAddress().set(dto.personalInformation().dto_personalAddress().duplicate(PmcAddressSimple.class));

                Persistence.service().persist(businessInformation);
                Persistence.service().persist(personalInformation);

                PmcPaymentMethod paymentMethod = ServerSideFactory.create(PaymentMethodFacade.class).persistPmcPaymentMethod(dto.creditCardInfo(), pmc);
                Persistence.service().retrieveMember(pmc.equifaxInfo());
                pmc.equifaxInfo().paymentMethod().set(paymentMethod);
                pmc.equifaxInfo().status().setValue(PmcEquifaxStatus.PendingVistaApproval);
                pmc.equifaxInfo().reportType().setValue(dto.creditPricingOption().getValue());

                pmc.equifaxInfo().businessInformation().set(businessInformation);
                pmc.equifaxInfo().personalInformation().set(personalInformation);

                switch (dto.creditPricingOption().getValue()) {
                case FullCreditReport:
                    pmc.equifaxInfo().equifaxSignUpFee().setValue(fee.fullCreditReportSetUpFee().getValue());
                    pmc.equifaxInfo().equifaxPerApplicantCreditCheckFee().setValue(fee.fullCreditReportPerApplicantFee().getValue());
                    break;
                case RecomendationReport:
                    pmc.equifaxInfo().equifaxSignUpFee().setValue(fee.recommendationReportSetUpFee().getValue());
                    pmc.equifaxInfo().equifaxPerApplicantCreditCheckFee().setValue(fee.recommendationReportPerApplicantFee().getValue());
                    break;
                default:
                    throw new IllegalArgumentException();
                }

                Persistence.service().persist(pmc.equifaxInfo());
                return null;
            }
        });

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void obtatinFee(AsyncCallback<AbstractEquifaxFee> callback) {
        callback.onSuccess(ServerSideFactory.create(Vista2PmcFacade.class).getEquifaxFee());
    }

    @Override
    public void retrieve(AsyncCallback<CreditCheckSetupDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        // TODO Auto-generated method stub

    }

    @Override
    public void create(AsyncCallback<Key> callback, CreditCheckSetupDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CreditCheckSetupDTO>> callback, EntityListCriteria<CreditCheckSetupDTO> criteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }
}
