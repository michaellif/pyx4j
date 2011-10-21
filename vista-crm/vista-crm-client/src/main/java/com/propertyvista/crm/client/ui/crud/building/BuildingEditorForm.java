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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.components.media.CrmMediaFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.contact.IAddressFull;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class BuildingEditorForm extends CrmEntityForm<BuildingDTO> {

    private static I18n i18n = I18n.get(BuildingEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public BuildingEditorForm(IFormView<BuildingDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public BuildingEditorForm(IEditableComponentFactory factory, IFormView<BuildingDTO> parentView) {
        super(BuildingDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        // TODO - add this data processing later! :
        //  main.add(inject(proto().media()), 15);

        tabPanel.addDisable(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getDashboardView().asWidget(), i18n.tr("Dashboard"));

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.addDisable(isEditable() ? new HTML() : new ScrollPanel(((BuildingViewerView) getParentView()).getFloorplanListerView().asWidget()),
                i18n.tr("Floorplans"));
        tabPanel.addDisable(isEditable() ? new HTML() : new ScrollPanel(((BuildingViewerView) getParentView()).getUnitListerView().asWidget()),
                i18n.tr("Units"));

        FlowPanel combinedtab = new FlowPanel();
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Elevators:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getElevatorListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Boilers:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBoilerListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Roofs:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getRoofListerView().asWidget());
        tabPanel.addDisable(new ScrollPanel(combinedtab), i18n.tr("Mechanicals"));

        combinedtab = new FlowPanel();
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Parkings:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getParkingListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Locker Areas:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getLockerAreaListerView().asWidget());
        tabPanel.addDisable(new ScrollPanel(combinedtab), i18n.tr("Add-ons"));

        tabPanel.add(createFinancialTab(), i18n.tr("Financial"));
        tabPanel.add(createMarketingTab(), i18n.tr("Marketing"));

        combinedtab = new FlowPanel();
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Services:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getServiceListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Features:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFeatureListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Concessions:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getConcessionListerView().asWidget());
        tabPanel.addDisable(new ScrollPanel(combinedtab), i18n.tr("Service Catalog"));

        tabPanel.add(createContactTab(), i18n.tr("Contacts"));
        tabPanel.addDisable(new CrmScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setDisableMode(isEditable());
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

    private WidgetDecorator buildWidgetDecorator(CComponent<?> component, double componentWidth) {
        return new WidgetDecorator(new Builder(component).componentWidth(componentWidth).readOnlyMode(!isEditable()));
    }

    private Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;

        main.setWidget(row, 0, buildWidgetDecorator(inject(proto().propertyCode()), 12));
        main.setWidget(row++, 1, buildWidgetDecorator(inject(proto().info().shape()), 7));

        main.setWidget(row, 0, buildWidgetDecorator(inject(proto().info().name()), 15));
        main.setWidget(row++, 1, buildWidgetDecorator(inject(proto().info().totalStoreys()), 5));

        main.setWidget(row, 0, buildWidgetDecorator(inject(proto().info().type()), 12));
        main.setWidget(row++, 1, buildWidgetDecorator(inject(proto().info().residentialStoreys()), 5));

        main.setWidget(row, 0, buildWidgetDecorator(inject(proto().propertyManager()), 15));
        main.setWidget(row++, 1, buildWidgetDecorator(inject(proto().complexPrimary()), 15));

        main.setWidget(row++, 1, buildWidgetDecorator(inject(proto().complex()), 15));

        main.setHeader(row++, 0, 2, "");
        main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().geoLocation()), 28));

        main.setHeader(row++, 0, 2, proto().info().address().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().info().address(), new AddressEditor(false, !isEditable())));

        main.setHeader(row++, 0, 2, proto().amenities().getMeta().getCaption());
        main.setWidget(row, 0, inject(proto().amenities(), new BuildingAmenityFolder()));
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    class AddressEditor extends CEntityEditor<Address> {

        private final boolean showUnit;

        private final boolean readOnly;

        public AddressEditor(boolean showUnit, boolean readOnly) {
            super(Address.class);
            this.showUnit = showUnit;
            this.readOnly = readOnly;
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = 0;
            if (showUnit) {
                main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().unitNumber()), 12));
            }

            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().streetNumber()), 5));
            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().streetNumberSuffix()), 5));
            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().streetName()), 15));
            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().streetType()), 10));
            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().streetDirection()), 10));

            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().city()), 15));
            main.setWidget(row++, 0, buildWidgetDecorator(inject(proto().county()), 15));

            // Need local variables to avoid extended casting that make the code unreadable
            CEditableComponent<Province, ?> province = (CEditableComponent<Province, ?>) inject(proto().province());
            main.setWidget(row++, 0, buildWidgetDecorator(province, 17));

            CEditableComponent<Country, ?> country = (CEditableComponent<Country, ?>) inject(proto().country());
            main.setWidget(row++, 0, buildWidgetDecorator(country, 15));

            CEditableComponent<String, ?> postalCode = (CEditableComponent<String, ?>) inject(proto().postalCode());
            main.setWidget(row++, 0, buildWidgetDecorator(postalCode, 7));

            attachFilters(proto(), province, country, postalCode);

            return main;
        }

        private WidgetDecorator buildWidgetDecorator(CComponent<?> component, double componentWidth) {
            return new WidgetDecorator(new Builder(component).componentWidth(componentWidth).readOnlyMode(readOnly));
        }

        private void attachFilters(final IAddressFull proto, CEditableComponent<Province, ?> province, CEditableComponent<Country, ?> country,
                CEditableComponent<String, ?> postalCode) {
            postalCode.addValueValidator(new ZipCodeValueValidator(this, proto.country()));
            country.addValueChangeHandler(new RevalidationTrigger(postalCode));

            // The filter does not use the CEditableComponent<Country, ?> and use Model directly. So it work fine on populate.
            ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
                @Override
                public boolean acceptOption(Province entity) {
                    if (getValue() == null) {
                        return true;
                    } else {
                        Country country = (Country) getValue().getMember(proto.country().getPath());
                        return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                    }
                }
            });
        }

    }

    private Widget createDetailsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().info().structureType()), 15);
        split.getLeftPanel().add(inject(proto().info().structureBuildYear()), 10);
        split.getLeftPanel().add(inject(proto().info().constructionType()), 15);
        split.getLeftPanel().add(inject(proto().info().foundationType()), 15);
        split.getLeftPanel().add(inject(proto().info().floorType()), 15);

        split.getRightPanel().add(inject(proto().info().landArea()), 15);
        split.getRightPanel().add(inject(proto().info().waterSupply()), 15);
        split.getRightPanel().add(inject(proto().info().centralAir()), 15);
        split.getRightPanel().add(inject(proto().info().centralHeat()), 15);

        main.add(new VistaLineSeparator());
        if (isEditable()) {
            main.add(inject(proto().contacts().website()), 50);
        } else {
            main.add(inject(proto().contacts().website(), new CHyperlink(new Command() {
                @Override
                public void execute() {
                    String url = getValue().contacts().website().getValue();
                    if (!ValidationUtils.urlHasProtocol(url)) {
                        url = "http://" + url;
                    }
                    Window.open(url, proto().contacts().website().getMeta().getCaption(), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
                }
            })), 50);
        }

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().financial().dateAcquired()), 8.2);
        split.getLeftPanel().add(inject(proto().financial().purchasePrice()), 10);
        split.getLeftPanel().add(inject(proto().financial().marketPrice()), 10);

        split.getRightPanel().add(inject(proto().financial().lastAppraisalDate()), 8.2);
        split.getRightPanel().add(inject(proto().financial().lastAppraisalValue()), 10);
        split.getRightPanel().add(inject(proto().financial().currency().name()), split.getRightPanel().getDefaultLabelWidth(), 10, i18n.tr("Currency Name"));

        main.add(new CrmSectionSeparator(i18n.tr("Included Utilities/Add-ons:")));
        main.add(inject(proto().serviceCatalog().includedUtilities(), new UtilityFolder(this)));
        main.add(new CrmSectionSeparator(i18n.tr("Excluded Utilities/Add-ons:")));
        main.add(inject(proto().serviceCatalog().externalUtilities(), new UtilityFolder(this)));

        return new CrmScrollPanel(main);
    }

    private Widget createMarketingTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        SubtypeInjectors.injectMarketing(main, proto().marketing(), this);

        main.add(new VistaLineSeparator());

        main.add(inject(proto().contacts().email().address()), main.getDefaultLabelWidth(), 30, i18n.tr("Email Address"));
        SubtypeInjectors.injectPropertyPhones(main, proto().contacts().phones(), this);

        main.add(new CrmSectionSeparator(i18n.tr("Media:")));
        main.add(inject(proto().media(), new CrmMediaFolder(isEditable(), ImageTarget.Building)));

        return new CrmScrollPanel(main);
    }

    private Widget createContactTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().contacts().contacts(), new OrganizationContactFolder()));

        return new CrmScrollPanel(main);
    }

    class BuildingAmenityFolder extends VistaTableFolder<BuildingAmenity> {

        public BuildingAmenityFolder() {
            super(BuildingAmenity.class, BuildingEditorForm.this.isEditable());
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }

    }

    static class OrganizationContactFolder extends VistaBoxFolder<OrganizationContact> {
        public OrganizationContactFolder() {
            super(OrganizationContact.class);
        }

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            if (member instanceof OrganizationContact) {
                return new OrganizationContactEditor();
            } else {
                return super.create(member);
            }
        }

        static class OrganizationContactEditor extends CEntityEditor<OrganizationContact> {

            public OrganizationContactEditor() {
                super(OrganizationContact.class);
            }

            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                main.add(inject(proto().description()), 35);
                //TODO
//                if (parent.isEditable()) {
//                    main.add(inject(proto().person()), 35);
//                } else {
                main.add(inject(proto().person()), 35);
                main.add(inject(proto().person().workPhone()), 10);
                main.add(inject(proto().person().mobilePhone()), 10);
                main.add(inject(proto().person().homePhone()), 10);
                main.add(inject(proto().person().email()), 20);
//                }

                return main;
            }

        }
    }

}
