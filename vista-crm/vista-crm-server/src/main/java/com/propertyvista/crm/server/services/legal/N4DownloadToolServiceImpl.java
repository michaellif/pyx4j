/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.crm.rpc.services.legal.N4DownloadToolService;
import com.propertyvista.domain.legal.N4LegalLetter;

public class N4DownloadToolServiceImpl implements N4DownloadToolService {

    @Override
    public void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4DownloadSettingsDTO settings) {
        // TODO just add filter
        EntityQueryCriteria<N4LegalLetter> criteria = EntityQueryCriteria.create(N4LegalLetter.class);

        List<N4LegalLetter> n4LegalLetters = Persistence.service().query(criteria);
        Vector<LegalNoticeCandidateDTO> legalNoticesDto = new Vector<LegalNoticeCandidateDTO>();
        for (N4LegalLetter n4LegalLetter : n4LegalLetters) {
            LegalNoticeCandidateDTO legalNotice = EntityFactory.create(LegalNoticeCandidateDTO.class);

            legalNotice.n4LetterId().set(n4LegalLetter.createIdentityStub());
            legalNotice.leaseId().set(n4LegalLetter.lease().createIdentityStub());
            legalNoticesDto.add(legalNotice);

            // todo fill rest of the data
        }

        callback.onSuccess(legalNoticesDto);
    }

    @Override
    public void process(AsyncCallback<String> callback, Vector<N4LegalLetter> accepted) {
        callback.onSuccess(DeferredProcessRegistry.fork(new N4DownloadDeferredProcess(accepted), ThreadPoolNames.IMPORTS));
    }

}
