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
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.boxes.GlCodeSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.FeatureItemType;

public class FeatureTypeEditorForm extends CrmEntityForm<FeatureItemType> {

    private Widget glCodeSelector;

    public FeatureTypeEditorForm() {
        this(false);
    }

    public FeatureTypeEditorForm(boolean viewMode) {
        super(FeatureItemType.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().featureType()), 25).build());

        HorizontalPanel glCodePanel = new HorizontalPanel();
        glCodePanel.add(new DecoratorBuilder(inject(proto().glCode(), new CEntityLabel<GlCode>()), 25).build());
        if (isEditable()) {
            glCodePanel.add(glCodeSelector = new AnchorButton("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new GlCodeSelectorDialog() {
                        @Override
                        public boolean onClickOk() {
                            if (!getSelectedItems().isEmpty()) {
                                get(FeatureTypeEditorForm.this.proto().glCode()).setValue(getSelectedItems().get(0));
                            }
                            return !getSelectedItems().isEmpty();
                        }
                    }.show();
                }
            }));
            glCodeSelector.getElement().getStyle().setMarginLeft(4, Unit.EM);
        }

        main.setWidget(++row, 0, glCodePanel);

        return new CrmScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        if (glCodeSelector != null) {
            glCodeSelector.setVisible(isEditable());
        }
    }
}