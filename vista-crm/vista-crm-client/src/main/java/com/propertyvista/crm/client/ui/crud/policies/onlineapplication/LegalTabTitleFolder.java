/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.onlineapplication;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.boxes.LocalizedContentFolderBase;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTabTitle;
import com.propertyvista.domain.site.AvailableLocale;

class LegalTabTitleFolder extends LocalizedContentFolderBase<OnlineApplicationLegalTabTitle> {

    public LegalTabTitleFolder(boolean editable) {
        super(OnlineApplicationLegalTabTitle.class, editable);
    }

    @Override
    public IsWidget createEditorContent(CEntityForm<OnlineApplicationLegalTabTitle> editor) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
        locale.setEditable(false);
        main.setWidget(++row, 0, new FormDecoratorBuilder(editor.inject(proto().locale(), locale), 10).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(editor.inject(proto().title()), 35).build());

        return main;
    }

}