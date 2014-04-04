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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseLegalFacadeImpl implements LeaseLegalFacade {

    private static final I18n i18n = I18n.get(LeaseLegalFacade.class);

    @Override
    public LegalStatus getCurrentLegalStatus(Lease leaseId) {
        EntityQueryCriteria<LegalStatus> criteria = EntityQueryCriteria.create(LegalStatus.class);
        criteria.eq(criteria.proto().lease(), leaseId.getPrimaryKey());
        criteria.desc(criteria.proto().setOn());
        LegalStatus status = Persistence.service().retrieve(criteria);

        if (status != null && isActive(status)) {
            Persistence.ensureRetrieve(status.setBy(), AttachLevel.Attached);
        } else {
            status = EntityFactory.create(LegalStatus.class);
            status.status().setValue(Status.None);
            status.details().setValue(i18n.tr("Computed Automatically"));
        }
        return status;
    }

    @Override
    public List<LegalStatus> getLegalStatusHistory(Lease leaseId) {
        EntityQueryCriteria<LegalStatus> criteria = EntityQueryCriteria.create(LegalStatus.class);
        criteria.eq(criteria.proto().lease(), leaseId.getPrimaryKey());
        criteria.desc(criteria.proto().setOn());
        List<LegalStatus> statusesHistory = Persistence.service().query(criteria);
        for (LegalStatus status : statusesHistory) {
            Persistence.ensureRetrieve(status.setBy(), AttachLevel.Attached);
        }
        if (!statusesHistory.isEmpty() && isActive(statusesHistory.get(0))) {
            // remove the current status
            statusesHistory = statusesHistory.subList(1, statusesHistory.size());
        }
        return statusesHistory;
    }

    @Override
    public void removeLegalStatus(LegalStatus legalStatusId) {
        if (legalStatusId.getPrimaryKey() == null) {
            return;
        }
        EntityQueryCriteria<LegalLetter> lettersCriteria = EntityQueryCriteria.create(LegalLetter.class);
        lettersCriteria.eq(lettersCriteria.proto().status().id(), legalStatusId.getPrimaryKey());
        for (LegalLetter letter : Persistence.service().query(lettersCriteria)) {
            Persistence.service().delete(LegalLetterBlob.class, letter.file().blobKey().getValue());
        }
        Persistence.service().delete(lettersCriteria);
        Persistence.service().delete(LegalStatus.class, legalStatusId.getPrimaryKey());
    }

    @Override
    public void setLegalStatus(Lease leaseId, LegalStatus update, List<LegalLetter> letters) {
        LegalStatus current = getCurrentLegalStatus(leaseId);

        if (current.getPrimaryKey() != null && update.status().getValue() == Status.None && current.status().getValue() == Status.None && letters.isEmpty()) {
            return;
        }
        update.lease().set(leaseId);

        // TODO add validations
        Persistence.service().persist(update);

        if (letters != null) {
            for (LegalLetter letter : letters) {
                letter.lease().set(leaseId);
                letter.status().set(update);
                letter.generatedOn().setValue(update.setOn().getValue());
                Persistence.service().persist(letter);
            }
        }
    }

    private boolean isActive(LegalStatus status) {
        if (!status.expiry().isNull()) {
            return status.expiry().getValue().compareTo(SystemDateManager.getDate()) <= 0;
        } else {
            return true; // TODO check for other status type specific conditions;
        }
    }

}
