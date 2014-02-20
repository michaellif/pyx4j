/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2014-01-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.util.List;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseLegalFacadeImpl implements LeaseLegalFacade {

    @Override
    public List<LegalStatus> getLegalStatusHistory(Lease leaseId) {
        EntityQueryCriteria<LegalStatus> criteria = EntityQueryCriteria.create(LegalStatus.class);
        criteria.eq(criteria.proto().lease(), leaseId.getPrimaryKey());
        criteria.desc(criteria.proto().setOn());
        List<LegalStatus> statusesHistory = Persistence.service().query(criteria);
        for (LegalStatus status : statusesHistory) {
            Persistence.ensureRetrieve(status.setBy(), AttachLevel.Attached);
        }
        if (statusesHistory.isEmpty()) {
            statusesHistory.add(getCurrentLegalStatus(leaseId));
        }
        return statusesHistory;
    }

    @Override
    public void setLegalStatus(Lease leaseId, Status status, String details, String notes, CrmUser setBy, List<LegalLetter> letters) {
        LegalStatus legalStatus = EntityFactory.create(LegalStatus.class);
        legalStatus.lease().set(leaseId);
        legalStatus.status().setValue(status);
        legalStatus.details().setValue(details);
        legalStatus.notes().setValue(notes);
        legalStatus.setBy().set(setBy);
        legalStatus.setOn().setValue(SystemDateManager.getDate());
        Persistence.service().persist(legalStatus);

        if (letters != null) {
            for (LegalLetter letter : letters) {
                letter.lease().set(leaseId);
                letter.status().set(legalStatus);
                letter.generatedOn().setValue(legalStatus.setOn().getValue());
                Persistence.service().persist(letter);
            }
        }
    }

    @Override
    public LegalStatus getCurrentLegalStatus(Lease leaseId) {
        EntityQueryCriteria<LegalStatus> criteria = EntityQueryCriteria.create(LegalStatus.class);
        criteria.eq(criteria.proto().lease(), leaseId.getPrimaryKey());
        criteria.desc(criteria.proto().setOn());
        LegalStatus status = Persistence.service().retrieve(criteria);
        if (status != null) {
            Persistence.ensureRetrieve(status.setBy(), AttachLevel.Attached);
        } else {
            status = EntityFactory.create(LegalStatus.class);
            status.status().setValue(Status.None);
        }

        return status;
    }

}
