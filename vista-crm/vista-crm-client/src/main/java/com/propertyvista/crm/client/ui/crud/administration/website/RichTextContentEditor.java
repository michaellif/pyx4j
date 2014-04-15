/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 5, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;

public class RichTextContentEditor extends CEntityForm<HtmlContent> {

    private static final I18n i18n = I18n.get(RichTextContentEditor.class);

    private boolean selectableLocale;

    public RichTextContentEditor() {
        this(false);
    }

    public RichTextContentEditor(boolean selectableLocale) {
        super(HtmlContent.class);
        this.selectableLocale = selectableLocale;
    }

    public RichTextContentEditor(IEditableComponentFactory factory) {
        super(HtmlContent.class, factory);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        if (selectableLocale) {
            CEntityComboBox<AvailableLocale> locale = new CEntityComboBox<AvailableLocale>(AvailableLocale.class);
            main.setWidget(++row, 0, 2, inject(proto().locale(), locale, new FormDecoratorBuilder(10, true).build()));
        } else {
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, 2, inject(proto().locale(), locale, new FormDecoratorBuilder(10, true).build()));
        }
        if (isEditable()) {
            CRichTextArea editor = new CRichTextArea();
            editor.setImageProvider(new SiteImageResourceProvider());
            main.setWidget(++row, 0, 2, inject(proto().html(), editor, new FormDecoratorBuilder(60, true).build()));
        } else {
            main.setWidget(++row, 0, 2, inject(proto().html(), new CLabel<String>(), new FormDecoratorBuilder(60, true).build()));
        }

        return main;
    }
}
