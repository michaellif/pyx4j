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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;
import com.propertyvista.server.TaskRunner;

public class PmcMerchantAccountCrudServiceImpl extends AbstractCrudServiceDtoImpl<PmcMerchantAccountIndex, PmcMerchantAccountDTO> implements
        PmcMerchantAccountCrudService {

    public PmcMerchantAccountCrudServiceImpl() {
        super(PmcMerchantAccountIndex.class, PmcMerchantAccountDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected PmcMerchantAccountDTO init(InitializationData initializationData) {
        PmcMerchantAccountInitializationData initData = (PmcMerchantAccountInitializationData) initializationData;

        PmcMerchantAccountDTO ent = EntityFactory.create(PmcMerchantAccountDTO.class);

        ent.pmc().name().set(Persistence.service().retrieve(Pmc.class, initData.parent().getPrimaryKey()).name());
        ent.merchantAccount().status().setValue(MerchantAccountActivationStatus.PendindAppoval);
        ent.merchantAccount().invalid().setValue(Boolean.FALSE);

        ent.merchantAccount().setup().acceptedEcheck().setValue(true);
        ent.merchantAccount().setup().acceptedDirectBanking().setValue(true);
        ent.merchantAccount().setup().acceptedCreditCard().setValue(true);
        ent.merchantAccount().setup().acceptedCreditCardConvenienceFee().setValue(true);
        ent.merchantAccount().setup().acceptedInterac().setValue(true);

        return ent;
    }

    public static void retriveMerchantAccountFromPmc(final PmcMerchantAccountIndex entity, final PmcMerchantAccountDTO dto) {
        Persistence.ensureRetrieve(dto.pmc(), AttachLevel.Attached);
        if ((dto.pmc().status().getValue() != PmcStatus.Created) && (!entity.merchantAccountKey().isNull())) {
            TaskRunner.runInTargetNamespace(dto.pmc(), new Callable<Void>() {
                @Override
                public Void call() {
                    dto.merchantAccount().set(Persistence.service().retrieve(MerchantAccount.class, entity.merchantAccountKey().getValue()));
                    EntityQueryCriteria<BuildingMerchantAccount> criteria = EntityQueryCriteria.create(BuildingMerchantAccount.class);
                    criteria.eq(criteria.proto().merchantAccount(), dto.merchantAccount());
                    List<BuildingMerchantAccount> buildingMerchantAccounts = Persistence.service().query(criteria);
                    List<Building> assignedBuildings = new ArrayList<Building>();
                    for (BuildingMerchantAccount buildingMerchantAccount : buildingMerchantAccounts) {
                        Building b = EntityFactory.create(Building.class);
                        b.propertyCode().setValue(
                                Persistence.service().retrieve(Building.class, buildingMerchantAccount.building().getPrimaryKey(), AttachLevel.Attached, false)
                                        .propertyCode().getValue());
                        assignedBuildings.add(b);
                    }
                    Collections.sort(assignedBuildings, new Comparator<Building>() {
                        @Override
                        public int compare(Building o1, Building o2) {
                            return o1.propertyCode().compareTo(o2.propertyCode());
                        }
                    });
                    dto.assignedBuildings().addAll(assignedBuildings);
                    return null;
                }
            });
            ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(dto.merchantAccount());
        }
    }

    @Override
    protected void enhanceRetrieved(PmcMerchantAccountIndex bo, PmcMerchantAccountDTO to, RetrieveTarget retrieveTarget) {
        retriveMerchantAccountFromPmc(bo, to);
    }

    @Override
    protected void enhanceListRetrieved(PmcMerchantAccountIndex entity, PmcMerchantAccountDTO dto) {
        retriveMerchantAccountFromPmc(entity, dto);
    }

    @Override
    protected void persist(PmcMerchantAccountIndex bo, PmcMerchantAccountDTO to) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, to.pmc().getPrimaryKey());

        // Copy RpcTransient value
        to.merchantAccount().merchantTerminalId().setValue(to.merchantTerminalId().getValue());

        ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, to.merchantAccount());
        // Find created item
        {
            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), pmc));
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccountKey(), to.merchantAccount().getPrimaryKey()));
            bo.set(Persistence.service().retrieve(criteria));
        }
    }
}
