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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
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
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().building(), new CEntityCrudHyperlink<Building>(MainActivityMapper.getCrudAppPlace(Building.class))), 20)
                            .build());
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit(), new CEntitySelectorHyperlink<AptUnit>() {
            @Override
            protected AppPlace getTargetPlace() {
                return MainActivityMapper.getCrudAppPlace(AptUnit.class).formViewerPlace(getValue().getPrimaryKey());
            }

            @Override
            protected EntitySelectorDialog<AptUnit> getSelectorDialog() {
                return new UnitSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((ShowingEditorView.Presenter) ((ShowingEditorView) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 20).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 12).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().result()), 12).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reason()), 12).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }
}
