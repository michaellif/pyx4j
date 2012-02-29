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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadEditorForm extends CrmEntityForm<Lead> {

    private static final I18n i18n = I18n.get(LeadEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public LeadEditorForm() {
        this(false);
    }

    public LeadEditorForm(boolean viewMode) {
        super(Lead.class, viewMode);
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
                    new FloorplanSelectorDialogDialog() {
                        @Override
                        public boolean onClickOk() {
                            if (!getSelectedItems().isEmpty()) {
                                ((LeadEditorView.Presenter) ((LeadEditorView) getParentView()).getPresenter()).setSelectedFloorplan(getSelectedItems().get(0));
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }.show();
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

    private class FloorplanSelectorDialogDialog extends EntitySelectorDialog<Floorplan> {

        public FloorplanSelectorDialogDialog() {
            super(Floorplan.class, false, new ArrayList<Floorplan>(1), i18n.tr("Building/Floorplan Selection"));
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            return false;
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    // building columns                    
                    new MemberColumnDescriptor.Builder(proto().building().info().address().country(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().province(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().city(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetName(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetNumber(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().type(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().name(), true).build(),

                    new MemberColumnDescriptor.Builder(proto().building().propertyCode(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().complex(), true).build(),

                    new MemberColumnDescriptor.Builder(proto().building().marketing().name(), true).title(i18n.tr("Marketing Name")).build(),

                    new MemberColumnDescriptor.Builder(proto().building().info().shape(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().totalStoreys(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().residentialStoreys(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().structureType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().structureBuildYear(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().constructionType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().foundationType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().floorType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().landArea(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().waterSupply(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().centralAir(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().centralHeat(), false).build(),

                    new MemberColumnDescriptor.Builder(proto().building().info().address(), false).build(),

                    new MemberColumnDescriptor.Builder(proto().building().contacts().website(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().contacts().email(), false).title(i18n.tr("Email")).build(),
                    
                    // floorplan columns
                    new MemberColumnDescriptor.Builder(proto().name(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().marketingName(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().floorCount(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().bedrooms(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().dens(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().bathrooms(), true).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Floorplan> getSelectService() {
            return GWT.<AbstractListService<Floorplan>> create(SelectFloorplanListService.class);
        }
    }
}