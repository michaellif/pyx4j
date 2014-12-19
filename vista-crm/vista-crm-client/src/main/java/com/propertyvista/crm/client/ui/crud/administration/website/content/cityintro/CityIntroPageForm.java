/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2012
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content.cityintro;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.domain.site.CityIntroPage;

public class CityIntroPageForm extends CrmEntityForm<CityIntroPage> {
    private static final I18n i18n = I18n.get(CityIntroPageForm.class);

    public CityIntroPageForm(IPrimeFormView<CityIntroPage, ?> view) {
        super(CityIntroPage.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().cityName()).decorate();
        formPanel.append(Location.Left, proto().province()).decorate();

        formPanel.h1(i18n.tr("Page Content"));
        formPanel.append(Location.Dual, proto().content(), new RichTextContentFolder(isEditable()));

        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}
