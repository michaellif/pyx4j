/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.util.UUID;
import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;
import com.propertyvista.biz.system.OnboardingPaymentFacade;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.onboarding.BankAccountInfo;
import com.propertyvista.onboarding.BankAccountInfoApproval;
import com.propertyvista.server.jobs.TaskRunner;

public class OnboardingMerchantAccountCrudServiceImpl extends AbstractCrudServiceDtoImpl<OnboardingMerchantAccount, OnboardingMerchantAccountDTO> implements
        com.propertyvista.admin.rpc.services.OnboardingMerchantAccountCrudService {

    public OnboardingMerchantAccountCrudServiceImpl() {
        super(OnboardingMerchantAccount.class, OnboardingMerchantAccountDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    public static void setCalulatedFileds(final OnboardingMerchantAccount entity, OnboardingMerchantAccountDTO dto) {
        if (entity.merchantTerminalId().isNull()) {
            dto.status().setValue(MerchantAccount.MerchantAccountStatus.NoElectronicPaymentsAllowed);
        } else {
            MerchantAccount merchantAccount = TaskRunner.runInTargetNamespace(entity.pmc().namespace().getValue(), new Callable<MerchantAccount>() {
                @Override
                public MerchantAccount call() {
                    return Persistence.service().retrieve(MerchantAccount.class, entity.merchantAccountKey().getValue());
                }
            });

            if (merchantAccount == null || merchantAccount.invalid().getValue(Boolean.TRUE)) {
                dto.status().setValue(MerchantAccount.MerchantAccountStatus.Invalid);
            } else {
                dto.status().setValue(MerchantAccount.MerchantAccountStatus.ElectronicPaymentsAllowed);
            }
        }
    }

    @Override
    protected void enhanceRetrieved(OnboardingMerchantAccount entity, OnboardingMerchantAccountDTO dto, RetrieveTraget retrieveTraget) {
        setCalulatedFileds(entity, dto);
    }

    @Override
    protected void enhanceListRetrieved(OnboardingMerchantAccount entity, OnboardingMerchantAccountDTO dto) {
        setCalulatedFileds(entity, dto);
    }

    @Override
    protected void persist(OnboardingMerchantAccount entity, OnboardingMerchantAccountDTO dto) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, dto.pmc().getPrimaryKey());

        BankAccountInfo requestAcc = EntityFactory.create(BankAccountInfo.class);

        requestAcc.onboardingBankAccountId().setValue(dto.onboardingBankAccountId().getValue());
        requestAcc.bankId().setValue(dto.bankId().getValue());
        requestAcc.branchTransitNumber().setValue(dto.branchTransitNumber().getValue());
        requestAcc.accountNumber().setValue(dto.accountNumber().getValue());
        requestAcc.chargeDescription().setValue(dto.chargeDescription().getValue());

        if (requestAcc.onboardingBankAccountId().isNull()) {
            requestAcc.onboardingBankAccountId().setValue(UUID.randomUUID().toString());
        }
        ServerSideFactory.create(OnboardingPaymentFacade.class).updateBankAccountInfo(pmc, requestAcc);

        if (!dto.merchantTerminalId().isNull()) {
            BankAccountInfoApproval account = EntityFactory.create(BankAccountInfoApproval.class);

            account.terminalId().setValue(dto.merchantTerminalId().getValue());
            account.onboardingBankAccountId().setValue(requestAcc.onboardingBankAccountId().getValue());
            account.bankId().setValue(requestAcc.bankId().getValue());
            account.branchTransitNumber().setValue(requestAcc.branchTransitNumber().getValue());
            account.accountNumber().setValue(requestAcc.accountNumber().getValue());

            ServerSideFactory.create(OnboardingPaymentFacade.class).approveBankAccountInfo(pmc, account);
        }

        // Find created item
        {
            EntityQueryCriteria<OnboardingMerchantAccount> criteria = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), pmc));
            criteria.add(PropertyCriterion.eq(criteria.proto().onboardingBankAccountId(), requestAcc.onboardingBankAccountId().getValue()));
            entity.set(Persistence.service().retrieve(criteria));
        }
    }
}
