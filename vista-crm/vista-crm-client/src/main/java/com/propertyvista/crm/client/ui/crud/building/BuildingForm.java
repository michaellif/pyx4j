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
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.shared.IFileURLBuilder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.components.editors.MarketingEditor;
import com.propertyvista.common.client.ui.components.editors.MarketingEditor.MarketingContactEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.MediaUploadBuildingService;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.marketing.MarketingContactEmail;
import com.propertyvista.domain.marketing.MarketingContactPhone;
import com.propertyvista.domain.marketing.MarketingContactUrl;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingForm extends CrmEntityForm<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingForm.class);

    private final Tab financialTab;

    private final Tab billingCyclesTab;

    public BuildingForm(IForm<BuildingDTO> view) {
        super(BuildingDTO.class, view);

        Tab tab = null;

        tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

        addTab(createDetailsTab(i18n.tr("Details")));

        tab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFloorplanListerView().asWidget(), i18n.tr("Floorplans"));
        setTabEnabled(tab, !isEditable());

        tab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getUnitListerView().asWidget(), i18n.tr("Units"));
        setTabEnabled(tab, !isEditable());

        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel();
        flexPanel.setWidth("100%");
        int row = 0;
        flexPanel.setH4(row++, 0, 2, i18n.tr("Elevators"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getElevatorListerView().asWidget());
        flexPanel.setH4(row++, 0, 2, i18n.tr("Boilers"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBoilerListerView().asWidget());
        flexPanel.setH4(row++, 0, 2, i18n.tr("Roofs"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getRoofListerView().asWidget());

        tab = addTab(flexPanel, i18n.tr("Mechanicals"));
        setTabEnabled(tab, !isEditable());

        flexPanel = new TwoColumnFlexFormPanel();
        flexPanel.setWidth("100%");
        row = 0;
        flexPanel.setH4(row++, 0, 2, i18n.tr("Parking"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getParkingListerView().asWidget());
        flexPanel.setH4(row++, 0, 2, i18n.tr("Locker Areas"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getLockerAreaListerView().asWidget());

        tab = addTab(flexPanel, i18n.tr("Add-Ons"));
        setTabEnabled(tab, !isEditable());

        financialTab = addTab(createFinancialTab(i18n.tr("Financial")));

        addTab(createMarketingTab(i18n.tr("Marketing")));

        flexPanel = new TwoColumnFlexFormPanel();
        row = 0;
        flexPanel.setH4(row++, 0, 2, i18n.tr("Services"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getServiceListerView().asWidget());
        flexPanel.setH4(row++, 0, 2, i18n.tr("Features"));
        flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFeatureListerView().asWidget());
        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            flexPanel.setH4(row++, 0, 2, i18n.tr("Concessions"));
            flexPanel.setWidget(row++, 0, 2, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getConcessionListerView().asWidget());
        }

        if (VistaFeatures.instance().productCatalog() && !VistaFeatures.instance().defaultProductCatalog() && !VistaFeatures.instance().yardiIntegration()) {
            tab = addTab(flexPanel, i18n.tr("Product Catalog"));
            setTabEnabled(tab, !isEditable());
        }

        addTab(createContactTab(i18n.tr("Contacts")));

        billingCyclesTab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBillingCycleListerView().asWidget(),
                i18n.tr("Billing Cycles"));
        setTabEnabled(billingCyclesTab, !isEditable());

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().externalId()).setVisible(!getValue().externalId().isNull());
        get(proto().suspended()).setEditable(SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport));

        // tweak property code editing UI:
        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.propertyCode, get(proto().propertyCode()), getValue().getPrimaryKey());

            ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), DatesPolicy.class,
                    new DefaultAsyncCallback<DatesPolicy>() {
                        @Override
                        public void onSuccess(DatesPolicy result) {
                            // set build year date picker range: 
                            CComponent<?> comp = get(proto().info().structureBuildYear());
                            if (comp instanceof CMonthYearPicker) {
                                int rangeStart = 1900 + result.yearRangeStart().getValue().getYear();
                                ((CMonthYearPicker) comp).setYearRange(new Range(rangeStart, (1900 + ClientContext.getServerDate().getYear())
                                        + result.yearRangeFutureSpan().getValue() - rangeStart));
                            }
                        }
                    });
        }

        financialTab.setTabVisible(SecurityController.checkBehavior(VistaCrmBehavior.BuildingFinancial));
        billingCyclesTab.setTabVisible(SecurityController.checkBehavior(VistaCrmBehavior.Billing));

        fillMerchantAccountStatus(getValue().merchantAccount());
    }

    @Override
    public void addValidations() {
        get(proto().financial().dateAcquired()).addValueValidator(new PastDateIncludeTodayValidator());
        get(proto().financial().lastAppraisalDate()).addValueValidator(new PastDateIncludeTodayValidator());

        get(proto().complex()).addValueChangeHandler(new ValueChangeHandler<Complex>() {
            @Override
            public void onValueChange(ValueChangeEvent<Complex> event) {
                get(proto().complexPrimary()).setEditable(!get(proto().complex()).isValueEmpty());
            }
        });
    }

    private TwoColumnFlexFormPanel createGeneralTab(String title) {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);

        int row = 0;
        flexPanel.setH1(row++, 0, 2, i18n.tr("Building Summary"));
        flexPanel.setWidget(row, 0, new FormDecoratorBuilder(inject(proto().propertyCode()), 12).build());

        flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().info().shape()), 7).build());

        flexPanel.setWidget(row, 0, new FormDecoratorBuilder(inject(proto().info().name()), 15).build());
        flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().info().totalStoreys()), 5).build());

        flexPanel.setWidget(row, 0, new FormDecoratorBuilder(inject(proto().info().type()), 12).build());
        flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().info().residentialStoreys()), 5).build());

        flexPanel.setWidget(row, 0, new FormDecoratorBuilder(inject(proto().propertyManager()), 16).build());
        flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().suspended()), 15).build());
        flexPanel.setBR(row++, 1, 1);

        flexPanel.setWidget(row, 0, new FormDecoratorBuilder(inject(proto().externalId()), 15).build());

        if (isEditable()) {
            flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().complex(), new CEntityLabel<Complex>()), 15).build());
        } else {
            flexPanel
                    .setWidget(
                            row++,
                            1,
                            new FormDecoratorBuilder(inject(proto().complex(),
                                    new CEntityCrudHyperlink<Complex>(AppPlaceEntityMapper.resolvePlace(Complex.class))), 15).build());
        }

        flexPanel.setH1(row++, 0, 2, proto().info().address().getMeta().getCaption());
        flexPanel.setWidget(row, 0, inject(proto().info().address(), new AddressStructuredEditor(false)));
        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().info().address()).setViewable(true);
        }
        flexPanel.getFlexCellFormatter().setColSpan(row++, 0, 2);

        flexPanel.setH1(row++, 0, 2, proto().geoLocation().getMeta().getCaption());
//        if (isEditable()) {
        flexPanel.setWidget(row, 0, inject(proto().geoLocation()));
//        } else {
//            main.setWidget(row, 0, new FormDecoratorBuilder(inject(proto().geoLocation())).customLabel("").useLabelSemicolon(false).build());
//        }
        flexPanel.getFlexCellFormatter().setColSpan(row++, 0, 2);

        return flexPanel;
    }

    private TwoColumnFlexFormPanel createDetailsTab(String title) {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);

        int row = -1;
        flexPanel.setH1(++row, 0, 2, i18n.tr("Information"));
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().info().structureType()), 15).build());
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().info().structureBuildYear()), 10).build());
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().info().constructionType()), 15).build());
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().info().foundationType()), 15).build());
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().info().floorType()), 15).build());

        row = 0;
        flexPanel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().info().landArea()), 15).build());
        flexPanel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().info().waterSupply()), 15).build());
        flexPanel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().info().centralAir()), 15).build());
        flexPanel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().info().centralHeat()), 15).build());
        flexPanel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().info().hasSprinklers()), 15).build());
        flexPanel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().info().hasFireAlarm()), 15).build());

        flexPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().contacts().website()), true).build());
        get(proto().contacts().website()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String> component, String url) {
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
        ((CField) get(proto().contacts().website())).setNavigationCommand(new Command() {
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

        });

        flexPanel.setH1(++row, 0, 2, proto().amenities().getMeta().getCaption());
        flexPanel.setWidget(++row, 0, 2, inject(proto().amenities(), new BuildingAmenityFolder()).asWidget());

        flexPanel.setH1(++row, 0, 2, proto().utilities().getMeta().getCaption());
        flexPanel.setWidget(++row, 0, 2, inject(proto().utilities(), new BuildingUtilityFolder()).asWidget());

        return flexPanel;
    }

    private TwoColumnFlexFormPanel createFinancialTab(String title) {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);

        int row = 0;
        flexPanel.setBR(row++, 0, 2);
        flexPanel.setWidget(row++, 0, new FormDecoratorBuilder(inject(proto().financial().dateAcquired()), 9).build());
        flexPanel.setWidget(row++, 0, new FormDecoratorBuilder(inject(proto().financial().purchasePrice()), 10).build());
        flexPanel.setWidget(row++, 0, new FormDecoratorBuilder(inject(proto().financial().marketPrice()), 10).build());
        flexPanel.setBR(row++, 0, 1);
        flexPanel.setWidget(row++, 0, new FormDecoratorBuilder(inject(proto().merchantAccount()), 15).build());

        row = 1;
        flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().financial().lastAppraisalDate()), 9).build());
        flexPanel.setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().financial().lastAppraisalValue()), 10).build());
        flexPanel
                .setWidget(row++, 1, new FormDecoratorBuilder(inject(proto().financial().currency().name()), 10).customLabel(i18n.tr("Currency Name")).build());

        // tweak:
        get(proto().merchantAccount()).addValueChangeHandler(new ValueChangeHandler<MerchantAccount>() {
            @Override
            public void onValueChange(ValueChangeEvent<MerchantAccount> event) {
                fillMerchantAccountStatus(event.getValue());
            }
        });

        return flexPanel;
    }

    private TwoColumnFlexFormPanel createMarketingTab(String title) {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);

        int row = -1;
        flexPanel.setH1(++row, 0, 2, i18n.tr("Marketing Summary"));
        flexPanel.setWidget(++row, 0, 2, inject(proto().marketing(), new MarketingEditor(this)));

        flexPanel.setH1(++row, 0, 2, i18n.tr("Images"));
        CImageSlider<MediaFile> imageSlider = new CImageSlider<MediaFile>(MediaFile.class,
                GWT.<MediaUploadBuildingService> create(MediaUploadBuildingService.class), new MediaImageUrlBuilder()) {
            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<MediaFile> entryForm) {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().caption()), 8, 15, 16).build());
                main.setWidget(++row, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().description()), 8, 15, 16).build());
                main.setWidget(++row, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().visibility()), 8, 7, 16).build());

                return main;
            }
        };
        imageSlider.setImageSize(240, 160);
        flexPanel.setWidget(++row, 0, 2, inject(proto().media(), imageSlider));

        if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
            flexPanel.setH1(++row, 0, 2, i18n.tr("ILS Profile"));
            flexPanel.setWidget(++row, 0, 2, inject(proto().ilsProfile(), new ILSProfileBuildingFolder()));
        }

        return flexPanel;
    }

    private TwoColumnFlexFormPanel createContactTab(String title) {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);
        int row = -1;

        flexPanel.setH1(++row, 0, 2, proto().contacts().organizationContacts().getMeta().getCaption());
        flexPanel.setWidget(++row, 0, 2, inject(proto().contacts().organizationContacts(), new OrganizationContactFolder(isEditable())));

        flexPanel.setH1(++row, 0, 2, proto().contacts().propertyContacts().getMeta().getCaption());
        flexPanel.setWidget(++row, 0, 2, inject(proto().contacts().propertyContacts(), new PropertyContactFolder()));

        return flexPanel;
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

    private class BuildingUtilityFolder extends VistaTableFolder<BuildingUtility> {

        public BuildingUtilityFolder() {
            super(BuildingUtility.class, BuildingForm.this.isEditable());
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

    private class ILSProfileBuildingFolder extends VistaBoxFolder<ILSProfileBuilding> {
        public ILSProfileBuildingFolder() {
            super(ILSProfileBuilding.class);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof ILSProfileBuilding) {
                return new ILSProfileBuildingEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            // get allowed providers
            ((BuildingEditorView.Presenter) getParentView().getPresenter()).getILSVendors(new DefaultAsyncCallback<Vector<ILSVendor>>() {
                @Override
                public void onSuccess(Vector<ILSVendor> vendors) {
                    // clear used providers
                    for (ILSProfileBuilding pr : BuildingForm.this.getValue().ilsProfile()) {
                        vendors.remove(pr.vendor().getValue());
                    }
                    // show selection dialog
                    new SelectEnumDialog<ILSVendor>(i18n.tr("Select ILS Vendor"), vendors) {
                        @Override
                        public boolean onClickOk() {
                            if (getSelectedType() != null) {
                                ILSProfileBuilding item = EntityFactory.create(ILSProfileBuilding.class);
                                item.vendor().setValue(getSelectedType());
                                addItem(item);
                            }
                            return true;
                        }

                        @Override
                        public String getEmptySelectionMessage() {
                            return i18n.tr("No Vendors to choose from.");
                        }
                    }.show();
                }
            }, EntityFactory.createIdentityStub(Building.class, BuildingForm.this.getValue().getPrimaryKey()));
        }

        private class ILSProfileBuildingEditor extends CEntityDecoratableForm<ILSProfileBuilding> {
            public ILSProfileBuildingEditor() {
                super(ILSProfileBuilding.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().vendor(), new CEnumLabel())).build());
                content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().disabled())).build());

                content.setH1(++row, 0, 2, proto().preferredContacts().getMeta().getCaption());
                content.setWidget(++row, 0, 2,
                        inject(proto().preferredContacts().url(), new MarketingContactEditor<MarketingContactUrl>(MarketingContactUrl.class)));
                content.setWidget(++row, 0, 2,
                        inject(proto().preferredContacts().email(), new MarketingContactEditor<MarketingContactEmail>(MarketingContactEmail.class)));
                content.setWidget(++row, 0, 2,
                        inject(proto().preferredContacts().phone(), new MarketingContactEditor<MarketingContactPhone>(MarketingContactPhone.class)));

                return content;
            }
        }
    }

    private void fillMerchantAccountStatus(MerchantAccount value) {
        if (value != null && !value.isNull()) {
            ((BuildingPresenterCommon) getParentView().getPresenter()).retrieveMerchantAccountStatus(new DefaultAsyncCallback<MerchantAccount>() {
                @Override
                public void onSuccess(MerchantAccount result) {
                    get(proto().merchantAccount()).setNote(result.status().getStringView() + ", " + result.paymentsStatus().getStringView());
                }
            }, EntityFactory.createIdentityStub(MerchantAccount.class, value.getPrimaryKey()));
        } else {
            get(proto().merchantAccount()).setNote(null);
        }
    }

    class MediaImageUrlBuilder implements IFileURLBuilder<MediaFile> {
        @Override
        public String getUrl(MediaFile file) {
            return MediaUtils.createMediaImageUrl(file);
        }
    };
}
