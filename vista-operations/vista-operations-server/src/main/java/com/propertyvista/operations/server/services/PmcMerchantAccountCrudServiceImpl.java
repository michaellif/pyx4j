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
package com.propertyvista.operations.server.services;

import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.operations.rpc.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcMerchantAccountCrudServiceImpl extends AbstractCrudServiceDtoImpl<PmcMerchantAccountIndex, PmcMerchantAccountDTO> implements
        PmcMerchantAccountCrudService {

    public PmcMerchantAccountCrudServiceImpl() {
        super(PmcMerchantAccountIndex.class, PmcMerchantAccountDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    public static void retriveMerchantAccountFromPmc(final PmcMerchantAccountIndex entity, final PmcMerchantAccountDTO dto) {
        Persistence.service().retrieve(dto.pmc());
        TaskRunner.runInTargetNamespace(dto.pmc(), new Callable<Void>() {
            @Override
            public Void call() {
                dto.merchantAccount().set(Persistence.service().retrieve(MerchantAccount.class, entity.merchantAccountKey().getValue()));
                return null;
            }
        });
        ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(dto.merchantAccount());
    }

    @Override
    protected void enhanceRetrieved(PmcMerchantAccountIndex entity, PmcMerchantAccountDTO dto, RetrieveTraget retrieveTraget) {
        retriveMerchantAccountFromPmc(entity, dto);
    }

    @Override
    protected void enhanceListRetrieved(PmcMerchantAccountIndex entity, PmcMerchantAccountDTO dto) {
        retriveMerchantAccountFromPmc(entity, dto);
    }

    @Override
    protected void persist(PmcMerchantAccountIndex entity, PmcMerchantAccountDTO dto) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, dto.pmc().getPrimaryKey());

        // Copy RpcTransient value
        dto.merchantAccount().merchantTerminalId().setValue(dto.merchantTerminalId().getValue());

        ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, dto.merchantAccount());
        // Find created item
        {
            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), pmc));
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccountKey(), dto.merchantAccount().getPrimaryKey()));
            entity.set(Persistence.service().retrieve(criteria));
        }
    }
}
