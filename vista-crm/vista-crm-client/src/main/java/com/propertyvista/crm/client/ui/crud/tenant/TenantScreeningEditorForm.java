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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.validators.CanadianSinValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.domain.ApplicationDocument.DocumentType;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.domain.tenant.income.TenantAsset;
import com.propertyvista.domain.tenant.income.TenantAsset.AssetType;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.tenant.income.TenantIncome;
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

        tabPanel.add(new ScrollPanel(createSecureInformationTab()), i18n.tr("Secure Information"));
        tabPanel.add(new ScrollPanel(createAddressesTab()), i18n.tr("Addresses"));
        tabPanel.add(new ScrollPanel(createlegalQuestionsTab()), i18n.tr(proto().legalQuestions().getMeta().getCaption()));
        tabPanel.add(new ScrollPanel(createIncomesTab()), i18n.tr("Incomes"));
        tabPanel.add(new ScrollPanel(createAssetsTab()), i18n.tr("Assets"));
        tabPanel.add(new ScrollPanel(createGuarantorsTab()), i18n.tr("Guarantors"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
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
        main.setWidth("100%");
        return main;
    }

    private Widget createAddressesTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new CrmHeader2Decorator(proto().currentAddress().getMeta().getCaption()));
        main.add(inject(proto().currentAddress(), createAddressEditor()));

        main.add(new CrmHeader2Decorator(proto().previousAddress().getMeta().getCaption()));
        main.add(inject(proto().previousAddress(), previousAddressHeader = createAddressEditor()));

        main.setWidth("100%");
        return main;
    }

    private Widget createlegalQuestionsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        DecorationData decor = new DecorationData(43d, HasHorizontalAlignment.ALIGN_LEFT, 8);
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForRent()), decor));
        main.add(new VistaLineSeparator(45, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForDamages()), decor));
        main.add(new VistaLineSeparator(45, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().everEvicted()), decor));
        main.add(new VistaLineSeparator(45, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().defaultedOnLease()), decor));
        main.add(new VistaLineSeparator(45, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().convictedOfFelony()), decor));
        main.add(new VistaLineSeparator(45, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().legalTroubles()), decor));
        main.add(new VistaLineSeparator(45, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().filedBankruptcy()), decor));

        main.setWidth("100%");
        return main;
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

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        currentAddressForm.get(currentAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.after(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the past.");
            }
        });

        // ------------------------------------------------------------------------------------------------        

        @SuppressWarnings("unchecked")
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) getRaw(proto().previousAddress()));
        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.before(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The move out date can not be equal or after move out one.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        // ------------------------------------------------------------------------------------------------        

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The move out date can not be before or equal move in one.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        // ------------------------------------------------------------------------------------------------

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());
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
        main.setWidth("100%");
        return main;
    }

    private Widget createAssetsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().assets(), createAssetFolderEditorColumns()));
        main.setWidth("100%");
        return main;
    }

    private Widget createGuarantorsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().guarantors(), createGuarantorFolderEditorColumns()));
        main.setWidth("100%");
        return main;
    }

    private CEntityFolderEditor<TenantIncome> createIncomeFolderEditor() {

        return new CEntityFolderEditor<TenantIncome>(TenantIncome.class) {

            @Override
            protected IFolderEditorDecorator<TenantIncome> createFolderDecorator() {
                return new BoxFolderEditorDecorator<TenantIncome>(CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add an income source"),
                        TenantScreeningEditorForm.this.isEditable());
            }

            @Override
            protected CEntityFolderItemEditor<TenantIncome> createItem() {
                return new TenantFinancialViewIncomeForm(!TenantScreeningEditorForm.this.isEditable());
            }
        };
    }

    private CEntityFolderEditor<TenantAsset> createAssetFolderEditorColumns() {
        return new CEntityFolderEditor<TenantAsset>(TenantAsset.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().percent(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "15em"));
            }

            @Override
            protected IFolderEditorDecorator<TenantAsset> createFolderDecorator() {
                return new TableFolderEditorDecorator<TenantAsset>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add an asset"),
                        TenantScreeningEditorForm.this.isEditable());
            }

            @Override
            protected CEntityFolderItemEditor<TenantAsset> createItem() {
                return new CEntityFolderRowEditor<TenantAsset>(TenantAsset.class, columns) {

                    @Override
                    public IFolderItemEditorDecorator<TenantAsset> createFolderItemDecorator() {
                        return new TableFolderItemEditorDecorator<TenantAsset>(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(),
                                i18n.tr("Remove asset"), TenantScreeningEditorForm.this.isEditable());
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
                                return i18n.tr("Value can not increase 100%");
                            }

                        });

                        get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<TenantAsset.AssetType>() {

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

    private CEntityFolderEditor<TenantGuarantor> createGuarantorFolderEditorColumns() {
        return new CEntityFolderEditor<TenantGuarantor>(TenantGuarantor.class) {

            @Override
            protected IFolderEditorDecorator<TenantGuarantor> createFolderDecorator() {
                return new BoxFolderEditorDecorator<TenantGuarantor>(CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add guarantor"),
                        TenantScreeningEditorForm.this.isEditable());
            }

            @Override
            protected CEntityFolderItemEditor<TenantGuarantor> createItem() {
                return createGuarantorRowEditor();
            }

            private CEntityFolderItemEditor<TenantGuarantor> createGuarantorRowEditor() {
                return new CEntityFolderItemEditor<TenantGuarantor>(TenantGuarantor.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!TenantScreeningEditorForm.this.isEditable());
                        if (TenantScreeningEditorForm.this.isEditable()) {
                            FlowPanel person = DecorationUtils.formFullName(this, proto());
                            person.getElement().getStyle().setFontWeight(FontWeight.BOLDER);
                            person.getElement().getStyle().setFontSize(1.1, Unit.EM);
                            main.add(person);
                        } else {
                            main.add(inject(proto().name().firstName()), 12);
                            main.add(inject(proto().name().middleName()), 12);
                            main.add(inject(proto().name().lastName()), 20);
                        }
                        main.add(inject(proto().homePhone()), 15);
                        main.add(inject(proto().mobilePhone()), 15);
                        main.add(inject(proto().workPhone()), 15);
                        main.add(inject(proto().birthDate()), 8);
                        main.add(inject(proto().email()), 15);
                        main.add(new HTML());
                        return main;
                    }

                    @Override
                    public IFolderItemEditorDecorator<TenantGuarantor> createFolderItemDecorator() {
                        return new BoxFolderItemEditorDecorator<TenantGuarantor>(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(),
                                i18n.tr("Remove guarantor"), TenantScreeningEditorForm.this.isEditable());
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
                                return i18n.tr("Guarantor should be at least 18 years old");
                            }
                        });
                    }
                };
            }

        };
    }
}