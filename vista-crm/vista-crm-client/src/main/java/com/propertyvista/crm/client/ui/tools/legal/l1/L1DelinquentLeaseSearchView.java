/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.legal.l1;

import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel;
import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;

public interface L1DelinquentLeaseSearchView extends IPrimePaneView {

    interface Presenter extends IPrimePaneView.IPrimePanePresenter {

        AbstractDataProvider<LegalActionCandidateDTO> getDataProvider();

        SelectionModel<? super LegalActionCandidateDTO> getSelectionModel();

        void updateSelection(SelectionPresetModel selectedPreset);

        void reviewCandidate(LegalActionCandidateDTO candidate);

        void fillCommonFields();

    }

    void setPresenter(Presenter presenter);

}
