/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.PersonScreeningCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.income.PersonalIncome;

public class PersonScreeningCrudServiceImpl extends GenericCrudServiceImpl<PersonScreening> implements PersonScreeningCrudService {

    public PersonScreeningCrudServiceImpl() {
        super(PersonScreening.class);
    }

    @Override
    protected void enhanceRetrieve(PersonScreening entity, boolean fromList) {
        if (!fromList) {
            // load detached entities:
            Persistence.service().retrieve(entity.documents());
            Persistence.service().retrieve(entity.incomes());
            Persistence.service().retrieve(entity.assets());
            Persistence.service().retrieve(entity.guarantors());
        }
    }

    @Override
    protected void persistDBO(PersonScreening dbo) {
        super.persistDBO(dbo);

        int no = 0;
        for (ApplicationDocument applicationDocument : dbo.documents()) {
            applicationDocument.owner().set(dbo);
            applicationDocument.orderInOwner().setValue(no++);
            Persistence.service().merge(applicationDocument);
        }
        for (PersonalIncome income : dbo.incomes()) {
            no = 0;
            for (ApplicationDocument applicationDocument : income.documents()) {
                applicationDocument.orderInOwner().setValue(no++);
                Persistence.service().merge(applicationDocument);
            }
        }
    }
}
