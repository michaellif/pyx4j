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
package com.propertyvista.crm.rpc.services.legal;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.domain.legal.n4.N4LegalLetter;

public interface N4DownloadToolService extends AbstractBulkOperationService<N4DownloadSettingsDTO, LegalNoticeCandidateDTO, Vector<N4LegalLetter>> {

    @Override
    void getItems(AsyncCallback<Vector<LegalNoticeCandidateDTO>> callback, N4DownloadSettingsDTO settings);

    @Override
    void process(AsyncCallback<String> callback, Vector<N4LegalLetter> accepted);

    void getGenerations(AsyncCallback<Vector<N4GenerationDTO>> callback);
}
