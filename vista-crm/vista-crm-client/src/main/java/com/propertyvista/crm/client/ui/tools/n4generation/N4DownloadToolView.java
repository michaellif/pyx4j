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
package com.propertyvista.crm.client.ui.tools.n4generation;

import java.util.List;

import com.propertyvista.crm.client.ui.tools.common.BulkOperationToolView;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDTO;

public interface N4DownloadToolView extends BulkOperationToolView<N4DownloadSettingsDTO, LegalNoticeCandidateDTO> {

    interface N4DownloadToolViewPresenter extends BulkOperationToolView.Presenter {

        void cancelDownload(String url);

    }

    void displayN4DownloadLink(String url);

    void setGenerations(List<N4GenerationDTO> generations);
}
