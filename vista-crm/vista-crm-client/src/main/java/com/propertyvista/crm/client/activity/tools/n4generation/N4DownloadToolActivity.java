/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.n4generation;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.n4generation.N4DownloadToolView;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.crm.rpc.services.legal.N4DownloadToolService;
import com.propertyvista.domain.legal.N4LegalLetter;

public class N4DownloadToolActivity extends AbstractBulkOperationToolActivity<N4DownloadSettingsDTO, LegalNoticeCandidateDTO, Vector<N4LegalLetter>> {

    public N4DownloadToolActivity(AppPlace place) {
        super(place, CrmSite.getViewFactory().instantiate(N4DownloadToolView.class), GWT.<N4DownloadToolService> create(N4DownloadToolService.class));
    }

    @Override
    protected Vector<N4LegalLetter> makeProducedItems(List<LegalNoticeCandidateDTO> selectedItems) {
        Vector<N4LegalLetter> selectedLetters = new Vector<N4LegalLetter>();
        for (LegalNoticeCandidateDTO noticeCandidate : selectedItems) {
            selectedLetters.add(noticeCandidate.n4LetterId());
        }
        return selectedLetters;
    }

    @Override
    protected void onSelectedProccessSuccess(DeferredProcessProgressResponse result) {
        // TODO
        ((N4DownloadToolView) getView()).displayN4DownloadLink("TBD");
    }

}
