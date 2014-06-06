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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.PublicMediaURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.GeoLocationEditor;
import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.building.MarketingEditor.MarketingContactEditor;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.MediaUploadBuildingService;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.marketing.MarketingContactEmail;
import com.propertyvista.domain.marketing.MarketingContactPhone;
import com.propertyvista.domain.marketing.MarketingContactUrl;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSSummaryBuilding;
import com.propertyvista.domain.policy.policies.DatesPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.Landlord;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingFinancial;
import com.propertyvista.domain.property.asset.building.BuildingMechanical;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingForm extends CrmEntityForm<BuildingDTO> {

    private static final I18n i18n = I18n.get(BuildingForm.class);

    private final Tab floorplansTab, mechanicalsTab, financialTab, billingCyclesTab;

    private Tab catalogTab = null;

    private FormPanel ilsEmailProfilePanel;

    public BuildingForm(IForm<BuildingDTO> view) {
        super(BuildingDTO.class, view);

        selectTab(addTab(createGeneralTab(), i18n.tr("General")));

        addTab(createDetailsTab(), i18n.tr("Details"));

        setTabEnabled(
                floorplansTab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFloorplanListerView().asWidget(),
                        i18n.tr("Floorplans")), !isEditable());

        setTabEnabled(addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getUnitListerView().asWidget(), i18n.tr("Units")),
                !isEditable());

        setTabEnabled(mechanicalsTab = addTab(createMachanicalsTab(), i18n.tr("Mechanicals")), !isEditable());

        setTabEnabled(addTab(createAddOnsTab(), i18n.tr("Add-Ons")), !isEditable());

        financialTab = addTab(createFinancialTab(), i18n.tr("Financial"));

        addTab(createMarketingTab(), i18n.tr("Marketing"));

        setTabEnabled(catalogTab = addTab(createCatalogTab(), i18n.tr("Product Catalog")), !isEditable());

        addTab(createContactTab(), i18n.tr("Contacts"));

        billingCyclesTab = addTab(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBillingCycleListerView().asWidget(),
                i18n.tr("Billing Cycles"));
        setTabEnabled(billingCyclesTab, !isEditable());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().complex()).setVisible(!getValue().complex().isNull());
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
                            CComponent<?, ?, ?> comp = get(proto().info().structureBuildYear());
                            if (comp instanceof CMonthYearPicker) {
                                int rangeStart = 1900 + result.yearRangeStart().getValue().getYear();
                                ((CMonthYearPicker) comp).setYearRange(new Range(rangeStart, (1900 + ClientContext.getServerDate().getYear())
                                        + result.yearRangeFutureSpan().getValue() - rangeStart));
                            }
                        }
                    });
        }

        floorplansTab.setTabVisible(SecurityController.checkPermission(DataModelPermission.permissionRead(FloorplanDTO.class)));

        mechanicalsTab.setTabVisible(SecurityController.checkPermission(DataModelPermission.permissionRead(BuildingMechanical.class)));

        financialTab.setTabVisible(SecurityController.checkPermission(DataModelPermission.permissionRead(BuildingFinancial.class)));

        billingCyclesTab.setTabVisible(SecurityController.checkPermission(DataModelPermission.permissionRead(BillingCycleDTO.class)));

        if (catalogTab != null) {
            catalogTab.setTabVisible(SecurityController.checkBehavior(VistaCrmBehavior.ProductCatalog) && !getValue().defaultProductCatalog().getValue(false));
        }

        fillMerchantAccountStatus(getValue().merchantAccount());

        ilsEmailProfilePanel.setVisible(getValue() != null && getValue().ilsEmailConfigured().getValue(false));
    }

    @Override
    public void addValidations() {
        get(proto().financial().dateAcquired()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().financial().lastAppraisalDate()).addComponentValidator(new PastDateIncludeTodayValidator());

        get(proto().complex()).addValueChangeHandler(new ValueChangeHandler<Complex>() {
            @Override
            public void onValueChange(ValueChangeEvent<Complex> event) {
                get(proto().complexPrimary()).setEditable(!get(proto().complex()).isValueEmpty());
            }
        });
    }

    private FormPanel createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Building Summary"));
        formPanel.append(Location.Left, proto().propertyCode()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().info().shape()).decorate().componentWidth(90);

        formPanel.append(Location.Left, proto().info().name()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().info().totalStoreys()).decorate().componentWidth(50);

        formPanel.append(Location.Left, proto().info().type()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().info().residentialStoreys()).decorate().componentWidth(50);

        formPanel.append(Location.Left, proto().propertyManager()).decorate();
        formPanel.append(Location.Right, proto().externalId()).decorate().componentWidth(150);

        if (isEditable()) {
            formPanel.append(Location.Left, proto().complex(), new CEntityLabel<Complex>()).decorate().componentWidth(150);
        } else {
            formPanel.append(Location.Left, proto().complex(), new CEntityCrudHyperlink<Complex>(AppPlaceEntityMapper.resolvePlace(Complex.class))).decorate()
                    .componentWidth(150);
        }

        formPanel.append(Location.Left, proto().landlord(), new CEntityCrudHyperlink<Landlord>(AppPlaceEntityMapper.resolvePlace(Landlord.class))).decorate()
                .componentWidth(150);

        if (!VistaFeatures.instance().yardiIntegration()) {
            formPanel.append(Location.Right, proto().defaultProductCatalog()).decorate().componentWidth(50);
        }
        formPanel.append(Location.Right, proto().suspended()).decorate().componentWidth(50);

        formPanel.h1(proto().info().address().getMeta().getCaption());
        InternationalAddressEditor addressEditor = new InternationalAddressEditor();
        addressEditor.setReadonlyCountry(true);
        formPanel.append(Location.Dual, proto().info().address(), addressEditor);
        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().info().address()).setViewable(true);
        }

        formPanel.h1(proto().geoLocation().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().geoLocation(), new GeoLocationEditor()));
        return formPanel;
    }

    private FormPanel createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Information"));

        formPanel.append(Location.Left, proto().info().structureType()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().info().structureBuildYear()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().info().constructionType()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().info().foundationType()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().info().floorType()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().contacts().website()).decorate().componentWidth(200);

        formPanel.append(Location.Right, proto().info().landArea()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().info().waterSupply()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().info().centralAir()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().info().centralHeat()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().info().hasSprinklers()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().info().hasFireAlarm()).decorate().componentWidth(150);

        formPanel.h1(proto().amenities().getMeta().getCaption());

        formPanel.append(Location.Dual, inject(proto().amenities(), new BuildingAmenityFolder()).asWidget());

        formPanel.h1(proto().utilities().getMeta().getCaption());

        formPanel.append(Location.Dual, inject(proto().utilities(), new BuildingUtilityFolder()).asWidget());

        get(proto().contacts().website()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (ValidationUtils.isSimpleUrl(getComponent().getValue())) {
                        return null;
                    } else {
                        return new BasicValidationError(getComponent(), i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
                    }
                }
                return null;
            }
        });
        ((CField<?, ?>) get(proto().contacts().website())).setNavigationCommand(new Command() {
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

        return formPanel;
    }

    private FormPanel createMachanicalsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h4(i18n.tr("Elevators"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getElevatorListerView().asWidget());
        formPanel.h4(i18n.tr("Boilers"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBoilerListerView().asWidget());
        formPanel.h4(i18n.tr("Roofs"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getRoofListerView().asWidget());

        return formPanel;
    }

    private FormPanel createAddOnsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h4(i18n.tr("Parking"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getParkingListerView().asWidget());
        formPanel.h4(i18n.tr("Locker Areas"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getLockerAreaListerView().asWidget());

        return formPanel;
    }

    private FormPanel createFinancialTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().financial().dateAcquired()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().financial().purchasePrice()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().financial().marketPrice()).decorate().componentWidth(100);

        formPanel.append(Location.Right, proto().financial().lastAppraisalDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().financial().lastAppraisalValue()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().financial().currency().name()).decorate().componentWidth(100).customLabel(i18n.tr("Currency Name"));

        formPanel.br();
        formPanel.append(Location.Left, proto().merchantAccount()).decorate().componentWidth(180);

        // tweak:
        get(proto().merchantAccount()).addValueChangeHandler(new ValueChangeHandler<MerchantAccount>() {
            @Override
            public void onValueChange(ValueChangeEvent<MerchantAccount> event) {
                fillMerchantAccountStatus(event.getValue());
            }
        });

        return formPanel;
    }

    private FormPanel createMarketingTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Marketing Summary"));
        formPanel.append(Location.Dual, proto().marketing(), new MarketingEditor(this));

        if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
            formPanel.h1(proto().ilsSummary().getMeta().getCaption());
            formPanel.append(Location.Dual, proto().ilsSummary(), new ILSSummaryFolder());
        }

        formPanel.h1(i18n.tr("Images"));
        CImageSlider<MediaFile> imageSlider = new CImageSlider<MediaFile>(MediaFile.class,
                GWT.<MediaUploadBuildingService> create(MediaUploadBuildingService.class), new PublicMediaURLBuilder()) {
            @Override
            protected FolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CForm<MediaFile> entryForm) {
                FormPanel formPanel = new FormPanel(entryForm);

                formPanel.append(Location.Dual, entryForm.proto().caption()).decorate().labelWidth(100).componentWidth(180);
                formPanel.append(Location.Dual, entryForm.proto().description()).decorate().labelWidth(100).componentWidth(180);
                formPanel.append(Location.Dual, entryForm.proto().visibility()).decorate().labelWidth(100).componentWidth(70);

                return formPanel.asWidget();
            }
        };
        imageSlider.setImageSize(240, 180);
        imageSlider.setOrganizerWidth(550);
        formPanel.append(Location.Dual, inject(proto().media(), imageSlider));

        ilsEmailProfilePanel = createILSEmailProfilePanel();
        if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
            formPanel.h1(i18n.tr("ILS Vendor Profile"));
            formPanel.append(Location.Dual, inject(proto().ilsProfile(), new ILSProfileBuildingFolder()));

            formPanel.append(Location.Dual, ilsEmailProfilePanel);
        }

        return formPanel;
    }

    private FormPanel createCatalogTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h4(i18n.tr("Services"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getServiceListerView().asWidget());
        formPanel.h4(i18n.tr("Features"));
        formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFeatureListerView().asWidget());
        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            formPanel.h4(i18n.tr("Concessions"));
            formPanel.append(Location.Dual, isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getConcessionListerView().asWidget());
        }

        return formPanel;
    }

    private FormPanel createContactTab() {
        FormPanel flexPanel = new FormPanel(this);

        flexPanel.append(Location.Left, proto().contacts().supportPhone()).decorate().componentWidth(200);

        flexPanel.h1(proto().contacts().organizationContacts().getMeta().getCaption());
        flexPanel.append(Location.Dual, proto().contacts().organizationContacts(), new OrganizationContactFolder(isEditable(), this));

        flexPanel.h1(proto().contacts().propertyContacts().getMeta().getCaption());
        flexPanel.append(Location.Dual, proto().contacts().propertyContacts(), new PropertyContactFolder());

        return flexPanel;
    }

    private class PropertyContactFolder extends VistaTableFolder<PropertyContact> {

        public PropertyContactFolder() {
            super(PropertyContact.class, BuildingForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().type(), "9em"));
            columns.add(new FolderColumnDescriptor(proto().name(), "10em"));
            columns.add(new FolderColumnDescriptor(proto().description(), "20em"));
            columns.add(new FolderColumnDescriptor(proto().phone(), "10em"));
            columns.add(new FolderColumnDescriptor(proto().email(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().visibility(), "7em"));
            return columns;
        }
    }

    private class BuildingAmenityFolder extends VistaTableFolder<BuildingAmenity> {

        public BuildingAmenityFolder() {
            super(BuildingAmenity.class, BuildingForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }
    }

    private class BuildingUtilityFolder extends VistaTableFolder<BuildingUtility> {

        public BuildingUtilityFolder() {
            super(BuildingUtility.class, BuildingForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }
    }

    private class ILSSummaryFolder extends VistaBoxFolder<ILSSummaryBuilding> {

        public ILSSummaryFolder() {
            super(ILSSummaryBuilding.class);
        }

        @Override
        protected CForm<ILSSummaryBuilding> createItemForm(IObject<?> member) {
            return new ILSSummaryEditor();
        }

        private class ILSSummaryEditor extends CForm<ILSSummaryBuilding> {
            public ILSSummaryEditor() {
                super(ILSSummaryBuilding.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                CImage frontImage = new CImage(GWT.<MediaUploadBuildingService> create(MediaUploadBuildingService.class), new PublicMediaURLBuilder());
                frontImage.setImageSize(240, 180);

                formPanel.append(Location.Left, proto().frontImage().file(), frontImage).decorate().customLabel("").componentWidth(200);
                formPanel.append(Location.Right, proto().title()).decorate();
                formPanel.append(Location.Right, proto().description()).decorate();

                return formPanel;
            }
        }
    }

    private FormPanel createILSEmailProfilePanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("ILS Email Profile"));
        formPanel.append(Location.Left, proto().ilsEmail().maxAds()).decorate().componentWidth(60);
        formPanel.append(Location.Right, proto().ilsEmail().disabled()).decorate();
        return formPanel;
    }

    private class ILSProfileBuildingFolder extends VistaBoxFolder<ILSProfileBuilding> {
        public ILSProfileBuildingFolder() {
            super(ILSProfileBuilding.class);
        }

        @Override
        protected CForm<ILSProfileBuilding> createItemForm(IObject<?> member) {
            return new ILSProfileBuildingEditor();
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
            });
        }

        private class ILSProfileBuildingEditor extends CForm<ILSProfileBuilding> {
            public ILSProfileBuildingEditor() {
                super(ILSProfileBuilding.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Dual, proto().vendor(), new CEnumLabel()).decorate();
                formPanel.append(Location.Left, proto().maxAds()).decorate().componentWidth(50);
                formPanel.append(Location.Right, proto().disabled()).decorate();

                formPanel.h1(proto().preferredContacts().getMeta().getCaption());
                formPanel.append(Location.Dual,
                        inject(proto().preferredContacts().url(), new MarketingContactEditor<MarketingContactUrl>(MarketingContactUrl.class)));
                formPanel.append(Location.Dual,
                        inject(proto().preferredContacts().email(), new MarketingContactEditor<MarketingContactEmail>(MarketingContactEmail.class)));
                formPanel.append(Location.Dual,
                        inject(proto().preferredContacts().phone(), new MarketingContactEditor<MarketingContactPhone>(MarketingContactPhone.class)));

                return formPanel;
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
}
