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
package com.propertyvista.crm.client.ui.crud.marketing.inquiry;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.folders.PhoneFolder;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.dto.InquiryDTO;

public class InquiryEditorForm extends CrmEntityForm<InquiryDTO> {

    public InquiryEditorForm() {
        super(InquiryDTO.class, new CrmEditorsComponentFactory());
    }

    public InquiryEditorForm(IEditableComponentFactory factory) {
        super(InquiryDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().lastName()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name(), new CEntityLabel()), 25).customLabel(i18n.tr("Person")).build());
            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            get(proto().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().comments()), 30).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refSource()), 30).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH3(++row, 0, 2, proto().phones().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().phones(), new PhoneFolder(isEditable())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        if (isEditable()) {
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().building(), new CEntityLabel()), 20).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().floorplan(), new CEntityLabel()), 20).build());

            AnchorButton select = new AnchorButton(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new ShowPopUpBox<SelectUnitBox>(new SelectUnitBox()) {
                        @Override
                        protected void onClose(SelectUnitBox box) {
                            if (box.getSelectedItem() != null) {
                                ((InquiryEditorView.Presenter) ((InquiryEditorView) getParentView()).getPresenter()).setSelectedFloorplan(box.getSelectedItem());
                            }
                        }
                    };
                }
            });
            select.asWidget().getElement().getStyle().setMarginLeft(15, Unit.EM);
            main.setWidget(++row, 1, select);
        } else {
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().building()), 20).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().floorplan()), 20).build());
        }

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().leaseTerm()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().movingDate()), 9).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

//
//Selection Boxes:

    private class SelectUnitBox extends OkCancelBox {

        private Floorplan selectedItem;

        public SelectUnitBox() {
            super("Building/Floorplan Selection");
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            ((InquiryEditorView) getParentView()).getFloorplanListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Floorplan>() {
                @Override
                public void onSelect(Floorplan selected) {
                    selectedItem = selected;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Building") + ":"));
            vPanel.add(((InquiryEditorView) getParentView()).getBuildingListerView().asWidget());
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Floorplan") + ":"));
            vPanel.add(((InquiryEditorView) getParentView()).getFloorplanListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("900px", "500px");
        }

        @Override
        protected void onCancel() {
            selectedItem = null;
        }

        protected Floorplan getSelectedItem() {
            return selectedItem;
        }
    }
}
