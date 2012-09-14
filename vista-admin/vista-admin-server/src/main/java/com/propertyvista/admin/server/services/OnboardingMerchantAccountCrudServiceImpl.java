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

import java.util.concurrent.Callable;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.server.jobs.TaskRunner;

public class OnboardingMerchantAccountCrudServiceImpl extends AbstractCrudServiceDtoImpl<OnboardingMerchantAccount, OnboardingMerchantAccountDTO> implements
        com.propertyvista.admin.rpc.services.OnboardingMerchantAccountCrudService {

    public OnboardingMerchantAccountCrudServiceImpl() {
        super(OnboardingMerchantAccount.class, OnboardingMerchantAccountDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
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
    protected void enhanceRetrieved(OnboardingMerchantAccount entity, OnboardingMerchantAccountDTO dto) {
        setCalulatedFileds(entity, dto);
    }

    @Override
    protected void enhanceListRetrieved(OnboardingMerchantAccount entity, OnboardingMerchantAccountDTO dto) {
        setCalulatedFileds(entity, dto);
    }

}
