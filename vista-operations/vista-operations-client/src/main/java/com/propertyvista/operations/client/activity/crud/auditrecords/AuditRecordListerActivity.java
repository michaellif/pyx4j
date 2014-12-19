/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 */
package com.propertyvista.operations.client.activity.crud.auditrecords;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.auditrecords.AuditRecordListerView;
import com.propertyvista.operations.rpc.dto.AuditRecordOperationsDTO;

public class AuditRecordListerActivity extends AbstractPrimeListerActivity<AuditRecordOperationsDTO> {

    public AuditRecordListerActivity(AppPlace place) {
        super(AuditRecordOperationsDTO.class, place, OperationsSite.getViewFactory().getView(AuditRecordListerView.class));
    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<AuditRecordOperationsDTO> entityClass, EntityFiltersBuilder<AuditRecordOperationsDTO> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        String val;

        if ((val = place.getFirstArg(filters.proto().pmc().getPath().toString())) != null) {
            filters.eq(filters.proto().pmc(), EntityFactory.createIdentityStub(Pmc.class, new Key(val)));
        }
    }
}
