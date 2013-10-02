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
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalLetter.LegalLetterType;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.N4FormFieldsData;
import com.propertyvista.domain.legal.N4LandlordsData;
import com.propertyvista.domain.legal.N4LeaseData;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.domain.LegalLetterBlob;

public class N4ManagementFacadeImpl implements N4ManagementFacade {

    @Override
    public List<LegalNoticeCandidate> getN4Candidates(BigDecimal minAmountOwed, List<Building> buildingIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void issueN4(List<Lease> delinquentLeases, Employee employee) throws IllegalStateException {
        // TODO 
        LogicalDate terminationDate = new LogicalDate();

        N4LandlordsData n4LandLordsData = null; // TODO

        for (Lease leaseId : delinquentLeases) {
            issueN4ForLease(leaseId, n4LandLordsData, terminationDate);
        }

    }

    @Override
    public Map<Lease, List<LegalLetter>> getN4(List<Lease> leaseIds, LogicalDate generatedCutOffDate) {
        // TODO Auto-generated method stub
        return null;
    }

    private void issueN4ForLease(Lease leaseId, N4LandlordsData n4LandLordsData, LogicalDate terminationDate) {
        N4LeaseData n4LeaseData = ServerSideFactory.create(N4GenerationFacade.class).getN4LeaseData(leaseId, terminationDate);
        N4FormFieldsData n4FormData = ServerSideFactory.create(N4GenerationFacade.class).populateFormData(n4LeaseData, n4LandLordsData);
        byte[] n4LetterBinary = ServerSideFactory.create(N4GenerationFacade.class).generateN4Letter(n4FormData);

        LegalLetterBlob blob = EntityFactory.create(LegalLetterBlob.class);
        blob.content().setValue(n4LetterBinary);
        blob.contentType().setValue("application/pdf");
        Persistence.service().persist(blob);

        LegalLetter n4Letter = EntityFactory.create(LegalLetter.class);
        n4Letter.lease().set(leaseId);
        n4Letter.type().setValue(LegalLetterType.N4);
        n4Letter.generatedOn().setValue(SystemDateManager.getDate());
        n4Letter.blobKey().setValue(blob.getPrimaryKey());
        n4Letter.fileSize().setValue(n4LetterBinary.length);
        Persistence.service().persist(n4Letter);
    }

    private N4LandlordsData make4landlordsData(Employee signingEmployee) {
        N4LandlordsData n4LandlordsData = EntityFactory.create(N4LandlordsData.class);

        return n4LandlordsData;
    }

}
