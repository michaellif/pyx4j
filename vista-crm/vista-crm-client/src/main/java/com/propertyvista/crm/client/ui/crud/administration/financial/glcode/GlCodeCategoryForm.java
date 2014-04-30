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
package com.propertyvista.crm.client.ui.crud.administration.financial.glcode;

import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.GlCodeCategory;

public class GlCodeCategoryForm extends CrmEntityForm<GlCodeCategory> {

    private static final I18n i18n = I18n.get(GlCodeCategoryForm.class);

    public GlCodeCategoryForm(IForm<GlCodeCategory> view) {
        super(GlCodeCategory.class, view);

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().categoryId()).decorate().componentWidth(90);
        formPanel.append(Location.Left, proto().description()).decorate().componentWidth(250);

        formPanel.h3(proto().glCodes().getMeta().getCaption());
        formPanel.append(Location.Full, proto().glCodes(), new GlCodeFolder(isEditable()));

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}