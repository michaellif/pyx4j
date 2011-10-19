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
package com.propertyvista.crm.client.ui.crud.tenant.screening;

import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
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

        new PastDateValidation(currentAddressForm.get(currentAddressForm.proto().moveInDate()));
        new FutureDateValidation(currentAddressForm.get(currentAddressForm.proto().moveOutDate()));

        // ------------------------------------------------------------------------------------------------        

        @SuppressWarnings("unchecked")
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) getRaw(proto().previousAddress()));

        new PastDateValidation(previousAddressForm.get(previousAddressForm.proto().moveInDate()));

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.before(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Move In Date must be less then Move Out Date");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        // ------------------------------------------------------------------------------------------------        

        new PastDateValidation(previousAddressForm.get(previousAddressForm.proto().moveOutDate()));

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Move Out Date must be greater then Move In Date");
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

// TODO : copy/paste/something_else ;) incomes implementation from ptApp step        
//        main.add(inject(proto().incomes(), createIncomeFolderEditor()));

        return new ScrollPanel(main);
    }

    private Widget createAssetsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().assets(), new PersonalAssetFolder()));
        return new CrmScrollPanel(main);
    }

    private Widget createGuarantorsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().guarantors(), new TenantGuarantorFolder()));
        return new ScrollPanel(main);
    }
}