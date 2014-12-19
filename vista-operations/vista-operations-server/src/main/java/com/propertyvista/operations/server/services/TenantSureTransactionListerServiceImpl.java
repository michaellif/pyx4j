/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2014
 * @author VladL
 */
package com.propertyvista.operations.server.services;

import java.util.concurrent.Callable;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.operations.rpc.services.TenantSureTransactionListerService;
import com.propertyvista.server.TaskRunner;

public class TenantSureTransactionListerServiceImpl extends AbstractListServiceImpl<TenantSureTransaction> implements TenantSureTransactionListerService {

    public TenantSureTransactionListerServiceImpl() {
        super(TenantSureTransaction.class);
    }

    @Override
    protected EntitySearchResult<TenantSureTransaction> query(final EntityListCriteria<TenantSureTransaction> criteria) {
        // Hack! insurance member primary key in criteria should be the same as TenantSureSubscribers, so:
        PropertyCriterion criterion = criteria.getCriterion(criteria.proto().insurance());
        if (criterion != null) {
            TenantSureSubscribers tss = Persistence.service().retrieve(TenantSureSubscribers.class, (Key) criterion.getValue());
            Persistence.ensureRetrieve(tss.pmc(), AttachLevel.Attached);
            if (tss != null) {
                return TaskRunner.runInTargetNamespace(tss.pmc(), new Callable<EntitySearchResult<TenantSureTransaction>>() {
                    @Override
                    public EntitySearchResult<TenantSureTransaction> call() {
                        return Persistence.secureQuery(criteria);
                    }
                });
            }
        }
        // 'not found' return:
        return new EntitySearchResult<TenantSureTransaction>();
    }
}
