/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.boxes.SelectUnitDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorForm extends CrmEntityForm<Showing> {

    private static final I18n i18n = I18n.get(ShowingEditorForm.class);

    public ShowingEditorForm() {
        this(false);
    }

    public ShowingEditorForm(boolean viewMode) {
        super(Showing.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        if (isEditable()) {
            HorizontalPanel unitPanel1 = new HorizontalPanel();
            HorizontalPanel unitPanel2 = new HorizontalPanel();
            unitPanel1.add(new DecoratorBuilder(inject(proto().building(), new CEntityLabel<Building>()), 20).build());
            unitPanel2.add(new DecoratorBuilder(inject(proto().unit(), new CEntityLabel<AptUnit>()), 20).build());
            unitPanel2.add(new AnchorButton(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new SelectUnitDialog() {
                        @Override
                        public boolean onClickOk() {
                            ((ShowingEditorView.Presenter) ((ShowingEditorView) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
                            return true;
                        }
                    }.show();
                }
            }));
            main.setWidget(++row, 0, unitPanel1);
            main.setWidget(++row, 0, unitPanel2);
        } else {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().building(), new CEntityCrudHyperlink<Building>(MainActivityMapper.getCrudAppPlace(Building.class))), 20)
                            .build());
            main.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().unit(), new CEntityCrudHyperlink<AptUnit>(MainActivityMapper.getCrudAppPlace(AptUnit.class))), 20)
                            .build());
        }

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 12).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().result()), 12).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reason()), 12).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }
}
