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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.components.editors.MarketingEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.components.media.CrmMediaFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingForm extends CrmEntityForm<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingForm.class);

    public BuildingForm(IFormView<BuildingDTO> view) {
        super(BuildingDTO.class, view);

        Tab tab = null;

        tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

        addTab(createDetailsTab(i18n.tr("Details")));

        tab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFloorplanListerView().asWidget(), i18n.tr("Floorplans"));
        setTabEnabled(tab, !isEditable());

        tab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getUnitListerView().asWidget(), i18n.tr("Units"));
        setTabEnabled(tab, !isEditable());

        FormFlexPanel combinedtab = new FormFlexPanel();
        int row = 0;
        combinedtab.setH4(row++, 0, 2, i18n.tr("Elevators"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getElevatorListerView().asWidget());
        combinedtab.setH4(row++, 0, 2, i18n.tr("Boilers"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBoilerListerView().asWidget());
        combinedtab.setH4(row++, 0, 2, i18n.tr("Roofs"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getRoofListerView().asWidget());

        tab = addTab(combinedtab, i18n.tr("Mechanicals"));
        setTabEnabled(tab, !isEditable());

        combinedtab = new FormFlexPanel();
        row = 0;
        combinedtab.setH4(row++, 0, 2, i18n.tr("Parking"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getParkingListerView().asWidget());
        combinedtab.setH4(row++, 0, 2, i18n.tr("Locker Areas"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getLockerAreaListerView().asWidget());

        tab = addTab(combinedtab, i18n.tr("Add-Ons"));
        setTabEnabled(tab, !isEditable());

        addTab(createFinancialTab(i18n.tr("Financial")));

        addTab(createMarketingTab(i18n.tr("Marketing")));

        combinedtab = new FormFlexPanel();
        row = 0;
        combinedtab.setH4(row++, 0, 2, i18n.tr("Services"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getServiceListerView().asWidget());
        combinedtab.setH4(row++, 0, 2, i18n.tr("Features"));
        combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFeatureListerView().asWidget());
        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            combinedtab.setH4(row++, 0, 2, i18n.tr("Concessions"));
            combinedtab.setWidget(row++, 0, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getConcessionListerView().asWidget());
        }

        if (!VistaFeatures.instance().defaultProductCatalog()) {
            tab = addTab(combinedtab, i18n.tr("Product Catalog"));
            setTabEnabled(tab, !isEditable());
        }

        addTab(createContactTab(i18n.tr("Contacts")));

        tab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBillingCycleListerView().asWidget(), i18n.tr("Billing Cycles"));
        setTabEnabled(tab, !isEditable());

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().externalId()).setVisible(!getValue().externalId().isNull());

        // tweak property code editing UI:
        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.propertyCode, get(proto().propertyCode()), getValue().getPrimaryKey());

            ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), DatesPolicy.class,
                    new DefaultAsyncCallback<DatesPolicy>() {
                        @Override
                        public void onSuccess(DatesPolicy result) {
                            // set build year date picker range: 
                            CComponent<?, ?> comp = get(proto().info().structureBuildYear());
                            if (comp instanceof CMonthYearPicker) {
                                int rangeStart = 1900 + result.yearRangeStart().getValue().getYear();
                                ((CMonthYearPicker) comp).setYearRange(new Range(rangeStart, (1900 + ClientContext.getServerDate().getYear())
                                        + result.yearRangeFutureSpan().getValue() - rangeStart));
                            }
                        }
                    });
        }
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().financial().dateAcquired()));
        new PastDateValidation(get(proto().financial().lastAppraisalDate()));

        get(proto().complex()).addValueChangeHandler(new ValueChangeHandler<Complex>() {
            @Override
            public void onValueChange(ValueChangeEvent<Complex> event) {
                get(proto().complexPrimary()).setEditable(!get(proto().complex()).isValueEmpty());
            }
        });
    }

    private FormFlexPanel createGeneralTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

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
            main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().complex(), new CEntityLabel<Complex>()), 15).build());
        } else {
            main.setWidget(row++, 1,
                    new DecoratorBuilder(inject(proto().complex(), new CEntityCrudHyperlink<Complex>(AppPlaceEntityMapper.resolvePlace(Complex.class))), 15)
                            .build());
        }

        main.setWidget(row++, 1,
                new DecoratorBuilder(inject(proto().floorplans(), new CEntityCollectionCrudHyperlink(AppPlaceEntityMapper.resolvePlaceClass(Floorplan.class))),
                        15).build());

        main.setH1(row++, 0, 2, proto().info().address().getMeta().getCaption());
        main.setWidget(row, 0, inject(proto().info().address(), new AddressStructuredEditor(true, false)));
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.setH1(row++, 0, 2, proto().geoLocation().getMeta().getCaption());
//        if (isEditable()) {
        main.setWidget(row, 0, inject(proto().geoLocation()));
//        } else {
//            main.setWidget(row, 0, new DecoratorBuilder(inject(proto().geoLocation())).customLabel("").useLabelSemicolon(false).build());
//        }
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().structureType()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().structureBuildYear()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().constructionType()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().foundationType()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().floorType()), 15).build());

        row = 0;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().landArea()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().waterSupply()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().centralAir()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().centralHeat()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().hasSprinklers()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().hasFireAlarm()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().hasEarthquakes()), 15).build());

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contacts().website()), 50).build());
            get(proto().contacts().website()).addValueValidator(new EditableValueValidator<String>() {

                @Override
                public ValidationError isValid(CComponent<String, ?> component, String url) {
                    if (url != null) {
                        if (ValidationUtils.isSimpleUrl(url)) {
                            return null;
                        } else {
                            return new ValidationError(component, i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
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

        main.setH1(row++, 0, 2, proto().amenities().getMeta().getCaption());
        main.setWidget(row, 0, inject(proto().amenities(), new BuildingAmenityFolder()));
        main.getFlexCellFormatter().setColSpan(row++, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    private FormFlexPanel createFinancialTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = 0;
        main.setBR(row++, 0, 2);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().financial().dateAcquired()), 9).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().financial().purchasePrice()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().financial().marketPrice()), 10).build());
        main.setBR(row++, 0, 1);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().merchantAccount()), 15).build());

        row = 1;
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().financial().lastAppraisalDate()), 9).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().financial().lastAppraisalValue()), 10).build());
        main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().financial().currency().name()), 10).customLabel(i18n.tr("Currency Name")).build());

        return main;
    }

    private FormFlexPanel createMarketingTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Marketing Summary"));
        main.setWidget(++row, 0, inject(proto().marketing(), new MarketingEditor()));

        main.setH1(++row, 0, 2, proto().contacts().propertyContacts().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().contacts().propertyContacts(), new PropertyContactFolder()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Media"));
        main.setWidget(++row, 0, inject(proto().media(), new CrmMediaFolder(isEditable(), ImageTarget.Building)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }

    private FormFlexPanel createContactTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().contacts().organizationContacts(), new OrganizationContactFolder(isEditable())));

        return main;
    }

    private class PropertyPhoneFolder extends VistaTableFolder<PropertyPhone> {

        public PropertyPhoneFolder() {
            super(PropertyPhone.class, BuildingForm.this.isEditable());
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
            super(PropertyContact.class, BuildingForm.this.isEditable());
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
            super(BuildingAmenity.class, BuildingForm.this.isEditable());
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
