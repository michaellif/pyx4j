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
 */
package com.propertyvista.crm.client.ui.tools.legal.n4;

import java.util.List;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.BulkOperationToolViewImpl;
import com.propertyvista.crm.client.ui.tools.common.LinkDialog;
import com.propertyvista.crm.client.ui.tools.legal.n4.datawidget.LegalNoticeCandidateFolderHolderForm;
import com.propertyvista.crm.client.ui.tools.legal.n4.datawidget.LegalNoticeCandidateHolder;
import com.propertyvista.crm.client.ui.tools.legal.n4.forms.N4DownloadSettingsForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDTO;

public class N4DownloadToolViewImpl extends BulkOperationToolViewImpl<N4DownloadSettingsDTO, LegalNoticeCandidateDTO, LegalNoticeCandidateHolder> implements
        N4DownloadToolView {

    private static final I18n i18n = I18n.get(N4GenerationToolView.class);

    public enum Styles implements IStyleName {

        N4DownloadToolView;

    }

    private static N4DownloadSettingsForm downloadSettingsForm;

    public N4DownloadToolViewImpl() {
        super(i18n.tr("Download Generated N4's"), downloadSettingsForm = new N4DownloadSettingsForm(), LegalNoticeCandidateHolder.class,
                new LegalNoticeCandidateFolderHolderForm());
        setAcceptButtonCaption(i18n.tr("DownloadSelected"));
        setPageIncrement(20);
        addStyleName(Styles.N4DownloadToolView.name());
    }

    @Override
    public void displayN4DownloadLink(final String url) {
        new LinkDialog(i18n.tr("N4's are ready"), i18n.tr("Download N4's"), url) {
            @Override
            public boolean onClickCancel() {
                ((N4DownloadToolView.N4DownloadToolViewPresenter) getPresenter()).cancelDownload(url);
                return false;
            }
        }.show();
    }

    @Override
    public void setGenerations(List<N4GenerationDTO> generations) {
        downloadSettingsForm.setGenerations(generations);
    }

}
