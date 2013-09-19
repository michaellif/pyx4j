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

import java.util.Arrays;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;

import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public abstract class DocumentTypeSelectorDialog extends EntitySelectorListDialog<IdentificationDocumentType> {

    private static final I18n i18n = I18n.get(DocumentTypeSelectorDialog.class);

    public DocumentTypeSelectorDialog() {
        super(i18n.tr("Select Document Type"), Arrays.<IdentificationDocumentType> asList());
    }

    @Override
    public String defineWidth() {
        return "40em";
    }
}