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
package com.propertyvista.crm.client.ui.crud.tenant.lead;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorForm extends CrmEntityForm<Showing> {

    public ShowingEditorForm(IFormView<Showing> parent) {
        super(Showing.class, new CrmEditorsComponentFactory());
        setParentView(parent);
    }

    public ShowingEditorForm(IEditableComponentFactory factory) {
        super(Showing.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        HorizontalPanel unitPanel = new HorizontalPanel();
        unitPanel.add(main.createDecorator(inject(proto().unit(), new CEntityLabel()), 15));
        if (isEditable()) {
            unitPanel.add(new Button("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new ShowPopUpBox<SelectUnitBox>(new SelectUnitBox()) {
                        @Override
                        protected void onClose(SelectUnitBox box) {
                            if (box.getSelectedBuilding() != null) {
                                get(proto().building()).setValue(box.getSelectedBuilding());
                            }
                            if (box.getSelectedUnit() != null) {
                                get(proto().unit()).setValue(box.getSelectedUnit());
                            }
                        }
                    };
                }
            }));
        }

        VistaDecoratorsSplitFlowPanel split;
        main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable()));

        split.getLeftPanel().add(main.createDecorator(inject(proto().building(), new CEntityLabel()), 15));
        split.getLeftPanel().add(unitPanel);

        split.getRightPanel().add(inject(proto().status()), 12);
        split.getRightPanel().add(inject(proto().result()), 12);
        split.getRightPanel().add(inject(proto().reason()), 12);

        return new CrmScrollPanel(main);
    }

    //
    // Selection Boxes:
    //
    private class SelectUnitBox extends OkCancelBox {

        private Building selectedBuilding;

        private AptUnit selectedUnit;

        public SelectUnitBox() {
            super(i18n.tr("Unit Selection"));
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            ((ShowingEditorView) getParentView()).getBuildingListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Building>() {
                @Override
                public void onSelect(Building selectedItem) {
                    selectedBuilding = selectedItem;
                    enableOkButton();
                }
            });
            ((ShowingEditorView) getParentView()).getUnitListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<AptUnit>() {
                @Override
                public void onSelect(AptUnit selectedItem) {
                    selectedUnit = selectedItem;
                    enableOkButton();
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
            selectedBuilding = null;
            selectedUnit = null;
        }

        protected Building getSelectedBuilding() {
            return selectedBuilding;
        }

        protected AptUnit getSelectedUnit() {
            return selectedUnit;
        }

        private void enableOkButton() {
            if (selectedBuilding != null && selectedUnit != null) {
                okButton.setEnabled(true);
            }
        }
    }
}
