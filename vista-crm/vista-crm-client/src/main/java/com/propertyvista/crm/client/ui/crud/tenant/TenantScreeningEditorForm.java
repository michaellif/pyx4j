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
package com.propertyvista.crm.client.ui.crud.tenant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderItemDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.validators.CanadianSinValidator;
import com.propertyvista.common.client.ui.validators.FutureDateValidation;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.domain.media.ApplicationDocument.DocumentType;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.BusinessRules;

public class TenantScreeningEditorForm extends CrmEntityForm<TenantScreening> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    private ApplicationDocumentsFolderUploader fileUpload;

    private CEntityEditor<PriorAddress> previousAddressHeader;

    public TenantScreeningEditorForm() {
        super(TenantScreening.class, new CrmEditorsComponentFactory());
    }

    public TenantScreeningEditorForm(IEditableComponentFactory factory) {
        super(TenantScreening.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createSecureInformationTab(), i18n.tr("Secure Information"));
        tabPanel.add(createAddressesTab(), i18n.tr("Addresses"));
        tabPanel.add(createlegalQuestionsTab(), i18n.tr(proto().legalQuestions().getMeta().getCaption()));
        tabPanel.add(createIncomesTab(), i18n.tr("Incomes"));
        tabPanel.add(createAssetsTab(), i18n.tr("Assets"));
        tabPanel.add(createGuarantorsTab(), i18n.tr("Guarantors"));

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

    @Override
    public void populate(TenantScreening value) {
        super.populate(value);

        enablePreviousAddress();

        get(proto().secureIdentifier()).setEnabled(!value.notCanadianCitizen().isBooleanTrue());
        fileUpload.setVisible(isEditable() && value.notCanadianCitizen().isBooleanTrue());
        if (value != null) {
            fileUpload.setTenantID(((IEntity) value).getPrimaryKey());
        }
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditor<PriorAddress> currentAddressForm = ((CEntityEditor<PriorAddress>) getRaw(proto().currentAddress()));

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        new FutureDateValidation(currentAddressForm.get(currentAddressForm.proto().moveInDate()));
        new PastDateValidation(currentAddressForm.get(currentAddressForm.proto().moveOutDate()));

        // ------------------------------------------------------------------------------------------------        

        @SuppressWarnings("unchecked")
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) getRaw(proto().previousAddress()));

        new FutureDateValidation(previousAddressForm.get(previousAddressForm.proto().moveInDate()));

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.before(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Move In Date must be before than Move Out Date");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        // ------------------------------------------------------------------------------------------------        

        new FutureDateValidation(previousAddressForm.get(previousAddressForm.proto().moveOutDate()));

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Move Out Date must be greater than Move In Date");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        // ------------------------------------------------------------------------------------------------

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());
    }

    private Widget createSecureInformationTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().driversLicense()), 14d, 20);
        main.add(inject(proto().driversLicenseState()), 14d, 17);
        final CEditableComponent<?, ?> sin = inject(proto().secureIdentifier());
        main.add(sin, 14d, 7);

        main.add(inject(proto().notCanadianCitizen()), 14d, 3);

        main.add(inject(proto().documents(), fileUpload = new ApplicationDocumentsFolderUploader(DocumentType.securityInfo)));
        fileUpload.asWidget().getElement().getStyle().setMarginLeft(14, Unit.EM);
        fileUpload.asWidget().getElement().getStyle().setMarginTop(1, Unit.EM);
        fileUpload.asWidget().getElement().getStyle().setMarginBottom(1, Unit.EM);
        fileUpload.setVisible(false); // show it in case on not a Canadian citizen!..

        get(proto().notCanadianCitizen()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    sin.setValue(null);
                }
                sin.asWidget().setEnabled(!event.getValue());
                fileUpload.setVisible(event.getValue());
            }
        });
        return new CrmScrollPanel(main);
    }

    private Widget createAddressesTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new CrmSectionSeparator(proto().currentAddress().getMeta().getCaption()));
        main.add(inject(proto().currentAddress(), createAddressEditor()));

        main.add(new CrmSectionSeparator(proto().previousAddress().getMeta().getCaption()));
        main.add(inject(proto().previousAddress(), previousAddressHeader = createAddressEditor()));

        return new CrmScrollPanel(main);
    }

    private Widget createlegalQuestionsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        double width = (isEditable() ? 50 : 45);
        DecorationData decor = new DecorationData(43d, HasHorizontalAlignment.ALIGN_LEFT, 8);

        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForRent()), decor));
        main.add(new VistaLineSeparator(width, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForDamages()), decor));
        main.add(new VistaLineSeparator(width, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().everEvicted()), decor));
        main.add(new VistaLineSeparator(width, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().defaultedOnLease()), decor));
        main.add(new VistaLineSeparator(width, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().convictedOfFelony()), decor));
        main.add(new VistaLineSeparator(width, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().legalTroubles()), decor));
        main.add(new VistaLineSeparator(width, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().filedBankruptcy()), decor));

        return new CrmScrollPanel(main);
    }

    private CEntityEditor<PriorAddress> createAddressEditor() {
        return new CEntityEditor<PriorAddress>(PriorAddress.class) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!TenantScreeningEditorForm.this.isEditable());
                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!TenantScreeningEditorForm.this.isEditable());
                main.add(split);

                AddressUtils.injectIAddress(split, proto(), this);

                split.getLeftPanel().add(inject(proto().moveInDate()), 8.2);
                split.getLeftPanel().add(inject(proto().moveOutDate()), 8.2);
                split.getLeftPanel().add(inject(proto().phone()), 15);

                CEditableComponent<?, ?> rentedComponent = inject(proto().rented());
                rentedComponent.addValueChangeHandler(new ValueChangeHandler() {
                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        setVizibility(getValue());
                    }
                });

                split.getRightPanel().add(rentedComponent, 15);
                split.getRightPanel().add(inject(proto().payment()), 8);
                split.getRightPanel().add(inject(proto().managerName()), 15);

                return main;
            }

            @Override
            public void populate(PriorAddress value) {
                super.populate(value);
                setVizibility(value);
            }

            private void setVizibility(PriorAddress value) {
                boolean rented = OwnedRented.rented.equals(value.rented().getValue());
                get(proto().payment()).setVisible(rented);
                get(proto().managerName()).setVisible(rented);
            }
        };
    }

    private void enablePreviousAddress() {
        boolean enabled = BusinessRules.infoPageNeedPreviousAddress(getValue().currentAddress().moveInDate().getValue());
        get(proto().previousAddress()).setVisible(enabled);
        previousAddressHeader.setVisible(enabled);
    }

// Financial: ------------------------------------------------------------------------------------------------

    private Widget createIncomesTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().incomes(), createIncomeFolderEditor()));
        return new ScrollPanel(main);
    }

    private Widget createAssetsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().assets(), createAssetFolderEditorColumns()));
        return new CrmScrollPanel(main);
    }

    private Widget createGuarantorsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().guarantors(), createGuarantorFolderEditorColumns()));
        return new ScrollPanel(main);
    }

    private CEntityFolder<PersonalIncome> createIncomeFolderEditor() {
        return new VistaEntityFolder<PersonalIncome>(PersonalIncome.class, i18n.tr("Income source"), isEditable()) {

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return null;
            }

            @Override
            protected IFolderDecorator<PersonalIncome> createDecorator() {
                return new VistaBoxFolderDecorator<PersonalIncome>(this);
            }

            @Override
            protected CEntityFolderBoxEditor<PersonalIncome> createItem() {
                return new TenantFinancialViewIncomeForm(!TenantScreeningEditorForm.this.isEditable());
            }
        };
    }

    private CEntityFolder<PersonalAsset> createAssetFolderEditorColumns() {
        return new VistaEntityFolder<PersonalAsset>(PersonalAsset.class, i18n.tr("Asset"), isEditable()) {
            private final VistaEntityFolder<PersonalAsset> parent = this;

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().percent(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "15em"));
            }

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<PersonalAsset> createItem() {
                return new CEntityFolderRowEditor<PersonalAsset>(PersonalAsset.class, columns) {

                    @Override
                    public IFolderItemDecorator<PersonalAsset> createDecorator() {
                        return new VistaTableFolderItemDecorator<PersonalAsset>(parent);
                    }

                    @Override
                    public void addValidations() {
                        get(proto().percent()).addValueValidator(new EditableValueValidator<Double>() {

                            @Override
                            public boolean isValid(CEditableComponent<Double, ?> component, Double value) {
                                return (value == null) || ((value >= 0) && (value <= 100));
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Double, ?> component, Double value) {
                                return VistaEntityFolder.i18n.tr("Value can not increase 100%");
                            }

                        });

                        get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<PersonalAsset.AssetType>() {

                            @Override
                            public void onValueChange(ValueChangeEvent<AssetType> event) {
                                if (get(proto().percent()).getValue() == null) {
                                    get(proto().percent()).setValue(100d);
                                }
                            }
                        });
                    }
                };
            }
        };
    }

    private CEntityFolder<TenantGuarantor> createGuarantorFolderEditorColumns() {
        return new VistaEntityFolder<TenantGuarantor>(TenantGuarantor.class, i18n.tr("Guarantor"), isEditable()) {
            private final VistaEntityFolder<TenantGuarantor> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return null;
            }

            @Override
            protected IFolderDecorator<TenantGuarantor> createDecorator() {
                return new VistaBoxFolderDecorator<TenantGuarantor>(parent);
            }

            @Override
            protected CEntityFolderBoxEditor<TenantGuarantor> createItem() {
                return new CEntityFolderBoxEditor<TenantGuarantor>(TenantGuarantor.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                        if (parent.isEditable()) {
                            main.add(inject(proto().name().namePrefix()), 4);
                            main.add(inject(proto().name().firstName()), 12);
                            main.add(inject(proto().name().middleName()), 12);
                            main.add(inject(proto().name().lastName()), 20);
                            main.add(inject(proto().name().nameSuffix()), 4);
                        } else {
                            main.add(inject(proto().name(), new CEntityLabel()), 25, "Guarantor");
                            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
                        }
                        main.add(inject(proto().homePhone()), 15);
                        main.add(inject(proto().mobilePhone()), 15);
                        main.add(inject(proto().workPhone()), 15);
                        main.add(inject(proto().birthDate()), 8);
                        main.add(inject(proto().email()), 15);
                        return main;
                    }

                    @Override
                    public IFolderItemDecorator<TenantGuarantor> createDecorator() {
                        return new VistaBoxFolderItemDecorator<TenantGuarantor>(parent);
                    }

                    @Override
                    public void addValidations() {

                        get(proto().email()).setMandatory(true);

                        get(proto().birthDate()).addValueValidator(new EditableValueValidator<Date>() {

                            @Override
                            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                                return ValidationUtils.isOlderThen18(value);
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                                return VistaEntityFolder.i18n.tr("Guarantor should be at least 18 years old");
                            }
                        });
                    }
                };
            }
        };
    }
}