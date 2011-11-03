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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorForm extends CrmEntityForm<Showing> {

    public ShowingEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public ShowingEditorForm(IEditableComponentFactory factory) {
        super(Showing.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().building(), new CEntityLabel()), 20).build());

        if (isEditable()) {
            HorizontalPanel unitPanel = new HorizontalPanel();
            unitPanel.add(new DecoratorBuilder(inject(proto().unit(), new CEntityLabel()), 20).build());
            unitPanel.add(new Button("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new ShowPopUpBox<SelectUnitBox>(new SelectUnitBox()) {
                        @Override
                        protected void onClose(SelectUnitBox box) {
                            if (box.getSelectedUnit() != null) {
                                ((ShowingEditorView.Presenter) ((ShowingEditorView) getParentView()).getPresenter()).setSelectedUnit(box.getSelectedUnit());
                            }
                        }
                    };
                }
            }));
            main.setWidget(++row, 0, unitPanel);
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit()), 20).build());
        }

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 12).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().result()), 12).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reason()), 12).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    //
    // Selection Boxes:
    //
    private class SelectUnitBox extends OkCancelBox {

        private AptUnit selectedUnit;

        public SelectUnitBox() {
            super("Unit Selection");
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            ((ShowingEditorView) getParentView()).getUnitListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<AptUnit>() {
                @Override
                public void onSelect(AptUnit selectedItem) {
                    selectedUnit = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Building") + ":"));
            vPanel.add(((ShowingEditorView) getParentView()).getBuildingListerView().asWidget());
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Unit") + ":"));
            vPanel.add(((ShowingEditorView) getParentView()).getUnitListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("900px", "500px");
        }

        @Override
        protected void onCancel() {
            selectedUnit = null;
        }

        protected AptUnit getSelectedUnit() {
            return selectedUnit;
        }
    }
}
