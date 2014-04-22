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
package com.propertyvista.crm.client.ui.tools.legal.n4.datawidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;

import com.propertyvista.crm.client.ui.tools.common.BulkItemsFolder;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;

public class LegalNoticeCandidateFolder extends BulkItemsFolder<LegalNoticeCandidateDTO> {

    public LegalNoticeCandidateFolder() {
        super(LegalNoticeCandidateDTO.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
    }

    @Override
    protected CForm<LegalNoticeCandidateDTO> createItemForm(IObject<?> member) {
        return new LegalNoticeCandidateForm();
    }
}
