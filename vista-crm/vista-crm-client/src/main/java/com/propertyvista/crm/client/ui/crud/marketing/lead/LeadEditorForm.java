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
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadEditorForm extends CrmEntityForm<Lead> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public LeadEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public LeadEditorForm(IEditableComponentFactory factory) {
        super(Lead.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.addDisable(createAppointmentsTab(), i18n.tr("Appointments"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name(), new CEntityLabel()), 25).customLabel(i18n.tr("Person")).build());
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            get(proto().person().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
            main.setBR(++row, 0, 1);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refSource()), 20).build());

        main.setBR(++row, 0, 1);
        main.setBR(++row, 0, 1);
        if (!isEditable()) {
            main.setBR(++row, 0, 1);
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().comments()), 55).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().agent()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CLabel()), 15).build());

        row = -1;
        if (isEditable()) {
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().building(), new CEntityLabel()), 20).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().floorplan(), new CEntityLabel()), 20).build());

            AnchorButton select = new AnchorButton(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new ShowPopUpBox<SelectFloorplanBox>(new SelectFloorplanBox()) {
                        @Override
                        protected void onClose(SelectFloorplanBox box) {
                            if (box.getSelectedItem() != null) {
                                ((LeadEditorView.Presenter) ((LeadEditorView) getParentView()).getPresenter()).setSelectedFloorplan(box.getSelectedItem());
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

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().leaseTerm()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().moveInDate()), 9).build());

        main.setBR(++row, 1, 1);
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentDate1()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentTime1()), 9).build());

        main.setBR(++row, 1, 1);
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentDate2()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentTime2()), 9).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    private Widget createAppointmentsTab() {
        if (!isEditable()) {
            return new ScrollPanel(((LeadViewerView) getParentView()).getAppointmentsListerView().asWidget());
        }
        return new HTML(); // just stub - not necessary for editing mode!.. 
    }

    //
    //Selection Boxes:

    private class SelectFloorplanBox extends OkCancelBox {

        private Floorplan selectedItem;

        public SelectFloorplanBox() {
            super("Building/Floorplan Selection");
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            ((LeadEditorView) getParentView()).getFloorplanListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Floorplan>() {
                @Override
                public void onSelect(Floorplan selected) {
                    selectedItem = selected;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Building") + ":"));
            vPanel.add(((LeadEditorView) getParentView()).getBuildingListerView().asWidget());
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Floorplan") + ":"));
            vPanel.add(((LeadEditorView) getParentView()).getFloorplanListerView().asWidget());
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