/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.components.editors.MarketingEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.media.CrmMediaFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class BuildingEditorForm extends CrmEntityForm<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    private ValueChangeHandler<DashboardMetadata> dashboardSelectedHandler;

    public BuildingEditorForm() {
        this(false);
    }

    public BuildingEditorForm(boolean viewMode) {
        super(BuildingDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(isEditable() ? new HTML() : createDashboardTab(), i18n.tr("Dashboard"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((BuildingViewerView) getParentView()).getFloorplanListerView().asWidget()),
                i18n.tr("Floorplans"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((BuildingViewerView) getParentView()).getUnitListerView().asWidget()), i18n.tr("Units"));
        tabPanel.setLastTabDisabled(isEditable());

        FormFlexPanel combinedtab = new FormFlexPanel();
        int row = 0;
        combinedtab.setH1(row++, 0, 2, i18n.tr("Elevators"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getElevatorListerView().asWidget());
        combinedtab.setH1(row++, 0, 2, i18n.tr("Boilers"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBoilerListerView().asWidget());
        combinedtab.setH1(row++, 0, 2, i18n.tr("Roofs"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getRoofListerView().asWidget());
        tabPanel.add(new ScrollPanel(combinedtab), i18n.tr("Mechanicals"));
        tabPanel.setLastTabDisabled(isEditable());

        combinedtab = new FormFlexPanel();
        row = 0;
        combinedtab.setH1(row++, 0, 2, i18n.tr("Parking"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getParkingListerView().asWidget());
        combinedtab.setH1(row++, 0, 2, i18n.tr("Locker Areas"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getLockerAreaListerView().asWidget());
        tabPanel.add(new ScrollPanel(combinedtab), i18n.tr("Add-Ons"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.add(createFinancialTab(), i18n.tr("Financial"));
        tabPanel.add(createMarketingTab(), i18n.tr("Marketing"));

        combinedtab = new FormFlexPanel();
        row = 0;
        combinedtab.setH1(row++, 0, 2, i18n.tr("Services"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getServiceListerView().asWidget());
        combinedtab.setH1(row++, 0, 2, i18n.tr("Features"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFeatureListerView().asWidget());
        combinedtab.setH1(row++, 0, 2, i18n.tr("Concessions"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getConcessionListerView().asWidget());
        tabPanel.add(new ScrollPanel(combinedtab), i18n.tr("Product Catalog"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.add(createContactTab(), i18n.tr("Contacts"));
        tabPanel.add(new CrmScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().info().structureBuildYear()));
        new PastDateValidation(get(proto().financial().dateAcquired()));
        new PastDateValidation(get(proto().financial().lastAppraisalDate()));

        get(proto().complex()).addValueChangeHandler(new ValueChangeHandler<Complex>() {
            @Override
            public void onValueChange(ValueChangeEvent<Complex> event) {
                get(proto().complexPrimary()).setEditable(!get(proto().complex()).isValueEmpty());
            }
        });
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createDashboardTab() {
        return ((BuildingViewerView) getParentView()).getDashboardView().asWidget();
    }

    public void setDashboardSelectedHandler(ValueChangeHandler<DashboardMetadata> handler) {
        this.dashboardSelectedHandler = handler;
    }

    private Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Building Summary"));
        main.setWidget(row, 0, new DecoratorBuilder(inject(proto().propertyCode()), 12).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().info().shape()), 7).build());

        main.setWidget(row, 0, new DecoratorBuilder(inject(proto().info().name()), 15).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().info().totalStoreys()), 5).build());

        main.setWidget(row, 0, new DecoratorBuilder(inject(proto().info().type()), 12).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().info().residentialStoreys()), 5).build());

        main.setWidget(row, 0, new DecoratorBuilder(inject(proto().propertyManager()), 20).build());
        main.setBR(row++, 1, 1);

        main.setWidget(row, 0, new DecoratorBuilder(inject(proto().externalId()), 15).build());

        if (isEditable()) {
            main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().complex()), 15).build());
            get(proto().complex()).setViewable(true);
        } else {
            main.setWidget(row++, 1,
                    new DecoratorBuilder(inject(proto().complex(), new CEntityCrudHyperlink<Complex>(MainActivityMapper.getCrudAppPlace(Complex.class))), 15)
                            .build());
        }
        main.setH1(row++, 0, 2, proto().info().address().getMeta().getCaption());
        main.setWidget(row, 0, inject(proto().info().address(), new AddressStructuredEditor(true, false)));
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.setH1(row++, 0, 2, proto().geoLocation().getMeta().getCaption());
        if (isEditable()) {
            main.setWidget(row, 0, inject(proto().geoLocation()));
        } else {
            main.setWidget(row, 0, new DecoratorBuilder(inject(proto().geoLocation())).customLabel("").useLabelSemicolon(false).build());
        }
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.setH1(row++, 0, 2, proto().amenities().getMeta().getCaption());
        main.setWidget(row, 0, inject(proto().amenities(), new BuildingAmenityFolder()));
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new CrmSectionSeparator(""));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().structureType()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().structureBuildYear()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().constructionType()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().foundationType()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().floorType()), 15).build());

        row = -1;
        main.setWidget(++row, 0, new CrmSectionSeparator(""));
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().landArea()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().waterSupply()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().centralAir()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().centralHeat()), 15).build());

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contacts().website()), 50).build());
            get(proto().contacts().website()).addValueValidator(new EditableValueValidator<String>() {

                @Override
                public ValidationFailure isValid(CComponent<String, ?> component, String url) {
                    if (url != null) {
                        if (ValidationUtils.isSimpleUrl(url)) {
                            return null;
                        } else {
                            return new ValidationFailure(i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
                        }
                    }
                    return null;
                }
            });
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contacts().website(), new CHyperlink(new Command() {
                @Override
                public void execute() {
                    String url = getValue().contacts().website().getValue();
                    if (!ValidationUtils.urlHasProtocol(url)) {
                        url = "http://" + url;
                    }
                    if (!ValidationUtils.isCorrectUrl(url)) {
                        throw new Error(i18n.tr("The URL is not in proper format"));
                    }

                    Window.open(url, proto().contacts().website().getMeta().getCaption(), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
                }

            })), 50).build());
        }
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Financial Summary"));
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().financial().dateAcquired()), 9).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().financial().purchasePrice()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().financial().marketPrice()), 10).build());

        row = 1;
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().financial().lastAppraisalDate()), 9).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().financial().lastAppraisalValue()), 10).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().financial().currency().name()), 10).customLabel(i18n.tr("Currency Name")).build());

        main.setH1(++row, 0, 2, i18n.tr("Included Utilities/Add-ons"));
        main.setWidget(++row, 0, inject(proto().productCatalog().includedUtilities(), new UtilityFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Excluded Utilities/Add-ons"));
        main.setWidget(++row, 0, inject(proto().productCatalog().externalUtilities(), new UtilityFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return new CrmScrollPanel(main);
    }

    private Widget createMarketingTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Marketing Summary"));
        main.setWidget(++row, 0, inject(proto().marketing(), new MarketingEditor()));

        main.setH1(++row, 0, 2, proto().contacts().propertyContacts().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().contacts().propertyContacts(), new PropertyContactFolder()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Media"));
        main.setWidget(++row, 0, inject(proto().media(), new CrmMediaFolder(isEditable(), ImageTarget.Building)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return new CrmScrollPanel(main);
    }

    private Widget createContactTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().contacts().organizationContacts(), new OrganizationContactFolder(isEditable())));

        return new CrmScrollPanel(main);
    }

    private class PropertyPhoneFolder extends VistaTableFolder<PropertyPhone> {

        public PropertyPhoneFolder() {
            super(PropertyPhone.class, BuildingEditorForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "7em"));
            columns.add(new EntityFolderColumnDescriptor(proto().number(), "11em"));
            columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
            columns.add(new EntityFolderColumnDescriptor(proto().designation(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().provider(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().visibility(), "7em"));
            return columns;
        }
    }

    private class PropertyContactFolder extends VistaTableFolder<PropertyContact> {

        public PropertyContactFolder() {
            super(PropertyContact.class, BuildingEditorForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
            columns.add(new EntityFolderColumnDescriptor(proto().phone(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().email(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().visibility(), "7em"));
            return columns;
        }
    }

    private class BuildingAmenityFolder extends VistaTableFolder<BuildingAmenity> {

        public BuildingAmenityFolder() {
            super(BuildingAmenity.class, BuildingEditorForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }
    }
}
