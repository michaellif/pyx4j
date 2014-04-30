/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content.pages;

import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.site.PageDescriptor;

public class PageForm extends CrmEntityForm<PageDescriptor> {

    private static final I18n i18n = I18n.get(PageForm.class);

    public PageForm(IForm<PageDescriptor> view) {
        super(PageDescriptor.class, view);

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(180);

        formPanel.h1(proto().content().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().content(), new PageContentFolder(this));

        formPanel.h1(proto().childPages().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().childPages(), new PageDescriptorFolder(this));

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().childPages()).setVisible(getValue().type().getValue() == PageDescriptor.Type.staticContent);
    }
}