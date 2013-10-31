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

import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDTO;
import com.propertyvista.crm.rpc.services.legal.N4DownloadToolService;
import com.propertyvista.domain.legal.N4LegalLetter;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4DownloadToolServiceImpl implements N4DownloadToolService {

    @Override
    public void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4DownloadSettingsDTO settings) {
        EntityQueryCriteria<N4LegalLetter> criteria = EntityQueryCriteria.create(N4LegalLetter.class);
        criteria.eq(criteria.proto().generatedOn(), settings.selectedGeneration().generatedOn());

        List<N4LegalLetter> n4LegalLetters = Persistence.secureQuery(criteria);
        Vector<LegalNoticeCandidateDTO> legalNoticesDto = new Vector<LegalNoticeCandidateDTO>();

        for (N4LegalLetter n4LegalLetter : n4LegalLetters) {
            legalNoticesDto.add(makeLegalNoticeCandidateDTO(n4LegalLetter));
        }

        callback.onSuccess(legalNoticesDto);
    }

    @Override
    public void process(AsyncCallback<String> callback, Vector<N4LegalLetter> accepted) {
        callback.onSuccess(DeferredProcessRegistry.fork(new N4DownloadDeferredProcess(accepted), ThreadPoolNames.IMPORTS));
    }

    @Override
    public void getGenerations(AsyncCallback<Vector<N4GenerationDTO>> callback) {
        EntityQueryCriteria<N4LegalLetter> criteria = EntityQueryCriteria.create(N4LegalLetter.class);
        criteria.asc(criteria.proto().generatedOn());

        N4LegalLetter lastGenerationRepresentative = null;

        ICursorIterator<N4LegalLetter> iterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);

        Vector<N4GenerationDTO> generations = new Vector<N4GenerationDTO>();
        while (iterator.hasNext()) {
            N4LegalLetter n4Letter = iterator.next();
            if (lastGenerationRepresentative == null || n4Letter.generatedOn().getValue().compareTo(lastGenerationRepresentative.generatedOn().getValue()) != 0) {
                N4GenerationDTO generation = EntityFactory.create(N4GenerationDTO.class);
                generation.generatedOn().setValue(n4Letter.generatedOn().getValue());
                generation.groupId().setValue(
                        MessageFormat.format("{0,date,short} {1,time}", n4Letter.generatedOn().getValue(), n4Letter.generatedOn().getValue()));

                generations.add(generation);

                lastGenerationRepresentative = n4Letter;

            }
        }

        callback.onSuccess(generations);

        IOUtils.closeQuietly(iterator);
    }

    private LegalNoticeCandidateDTO makeLegalNoticeCandidateDTO(N4LegalLetter n4LegalLetter) {
        LegalNoticeCandidateDTO legalNotice = EntityFactory.create(LegalNoticeCandidateDTO.class);
        legalNotice.n4LetterId().set(n4LegalLetter.createIdentityStub());
        legalNotice.leaseId().set(n4LegalLetter.lease().createIdentityStub());

        legalNotice.n4Issued().setValue(N4Utils.pastN4sCount(legalNotice.leaseId()));
        legalNotice.amountOwed().setValue(n4LegalLetter.amountOwed().getValue());

        Persistence.service().retrieve(n4LegalLetter.lease());
        legalNotice.leaseIdString().setValue(n4LegalLetter.lease().leaseId().getValue());
        legalNotice.moveIn().setValue(n4LegalLetter.lease().expectedMoveIn().getValue());
        legalNotice.moveOut().setValue(n4LegalLetter.lease().expectedMoveOut().getValue());

        Persistence.service().retrieve(n4LegalLetter.lease().unit());
        legalNotice.unit().setValue(n4LegalLetter.lease().unit().info().number().getValue());
        legalNotice.address().setValue(AddressRetriever.getUnitLegalAddress(n4LegalLetter.lease().unit()).getStringView());

        Persistence.service().retrieve(n4LegalLetter.lease().unit().building());
        legalNotice.building().setValue(n4LegalLetter.lease().unit().building().propertyCode().getValue());

        return legalNotice;
    }
}
