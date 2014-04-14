/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;

import com.pyx4j.site.client.ui.prime.IPrimePane;

import com.propertyvista.crm.client.ui.tools.common.view.HasMessages;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;

// TODO This is supposed to be a replacement for clunky N4GenerationToolView
public interface N4CreateBatchView extends IPrimePane, HasMessages {

    interface Presenter extends IPrimePane.Presenter, ProvidesKey<LegalNoticeCandidateDTO> {

        void search();

        void createBatch();

        void sortFoundCandidates(String memberPath, boolean isAscending);

    }

    void setPresenter(Presenter presenter);

    HasData<LegalNoticeCandidateDTO> searchResults();

    N4CandidateSearchCriteriaDTO getSearchCriteria();

    void setProgress(int progress, int maxiumumProgress, String message);

}
