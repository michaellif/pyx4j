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

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.tools.n4generation.base.BulkOperationToolViewImpl;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationSettingsDTO;

public class N4GenerationToolViewImpl extends BulkOperationToolViewImpl<N4GenerationSettingsDTO, LegalNoticeCandidateDTO, LegalNoticeCandidateHolder> implements
        N4GenerationToolView {

    private static final I18n i18n = I18n.get(N4GenerationToolView.class);

    public enum Styles implements IStyleName {

        N4GenerationToolView;

    }

    public N4GenerationToolViewImpl() {
        super(i18n.tr("N4 Generation Tool"), new N4GenerationSettingsForm(), LegalNoticeCandidateHolder.class, new LegalNoticeCandidateFolderHolderForm());
        setAcceptButtonCaption(i18n.tr("Issue N4's"));
        setPageIncrement(20);
        addStyleName(Styles.N4GenerationToolView.name());
    }

    @Override
    public void displayN4DownloadLink(String url) {
        // TODO
        MessageDialog.info(i18n.tr("DOWNLOAD LINK WILL APPEAR HERE"));
    }

}
