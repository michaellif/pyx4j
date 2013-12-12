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
package com.propertyvista.crm.client.ui.tools.legal.n4;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.BulkOperationToolViewImpl;
import com.propertyvista.crm.client.ui.tools.legal.n4.forms.N4CandidateSearchCriteriaForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;

public class N4GenerationToolViewImpl extends BulkOperationToolViewImpl<N4CandidateSearchCriteriaDTO, LegalNoticeCandidateDTO, LegalNoticeCandidateHolder>
        implements N4GenerationToolView {

    private static final I18n i18n = I18n.get(N4GenerationToolView.class);

    public enum Styles implements IStyleName {

        N4GenerationToolView;

    }

    public N4GenerationToolViewImpl() {
        super(i18n.tr("Create N4 Batch"), new N4CandidateSearchCriteriaForm(), LegalNoticeCandidateHolder.class, new LegalNoticeCandidateFolderHolderForm());
        setAcceptButtonCaption(i18n.tr("Issue N4's"));
        setPageIncrement(20);
        addStyleName(Styles.N4GenerationToolView.name());
    }
}
