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
package com.propertyvista.crm.client.ui.crud.building.catalog.feature;

import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureForm extends CrmEntityForm<Feature> {

    private static final I18n i18n = I18n.get(FeatureForm.class);

    public FeatureForm(IForm<Feature> view) {
        super(Feature.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().code(), new CEntityCrudHyperlink<ARCode>(AppPlaceEntityMapper.resolvePlace(ARCode.class))), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().name()), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().description()), 55).build());

        content.setH1(++row, 0, 2, i18n.tr("Items"));
        content.setWidget(++row, 0, 2, inject(proto().version().items(), new FeatureItemFolder(this)));

        row = 0;
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().version().mandatory()), 4).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().version().recurring()), 4).build());

        selectTab(addTab(content));
        setTabBarVisible(false);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // revalidate items folder (see FeatureItemFolder.addValidations()) on mandatory changes:  
        get(proto().version().mandatory()).addValueChangeHandler(new RevalidationTrigger<Boolean>(get(proto().version().items())));
    }
}