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
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;

public class RichTextContentEditor extends CForm<HtmlContent> {

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
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        if (selectableLocale) {
            CEntityComboBox<AvailableLocale> locale = new CEntityComboBox<AvailableLocale>(AvailableLocale.class);
            formPanel.append(Location.Left, proto().locale(), locale).decorate().componentWidth(120);
        } else {
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            formPanel.append(Location.Left, proto().locale(), locale).decorate().componentWidth(120);
        }
        if (isEditable()) {
            CRichTextArea editor = new CRichTextArea();
            editor.setImageProvider(new SiteImageResourceProvider());
            formPanel.append(Location.Dual, proto().html(), editor).decorate();
        } else {
            formPanel.append(Location.Dual, proto().html(), new CLabel<String>()).decorate();
        }

        return formPanel;
    }
}
