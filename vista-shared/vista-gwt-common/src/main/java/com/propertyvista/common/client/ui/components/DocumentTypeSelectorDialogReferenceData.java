/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.Collections;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;

import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public abstract class DocumentTypeSelectorDialogReferenceData extends EntitySelectorListDialog<IdentificationDocumentType> {

    private static final I18n i18n = I18n.get(DocumentTypeSelectorDialogReferenceData.class);

    public DocumentTypeSelectorDialogReferenceData() {
        super(i18n.tr("Select Document Type"), Collections.<IdentificationDocumentType> emptyList());
    }

    @Override
    public String defineWidth() {
        return "40em";
    }

    @Override
    public void show() {
        ReferenceDataManager.obtain(EntityListCriteria.create(IdentificationDocumentType.class), new DefaultAsyncCallback<List<IdentificationDocumentType>>() {
            @Override
            public void onSuccess(List<IdentificationDocumentType> result) {
                DocumentTypeSelectorDialogReferenceData.super.setData(result);
                DocumentTypeSelectorDialogReferenceData.super.show();
            }
        }, false);
    }
}