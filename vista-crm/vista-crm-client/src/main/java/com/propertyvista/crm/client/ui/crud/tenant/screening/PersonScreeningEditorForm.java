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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ApplicationDocumentUploaderFolder;
import com.propertyvista.common.client.ui.components.IdUploaderFolder;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.components.folders.PersonalAssetFolder;
import com.propertyvista.common.client.ui.components.folders.PersonalIncomeFolder;
import com.propertyvista.common.client.ui.validators.FutureDateValidation;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.misc.BusinessRules;

public class PersonScreeningEditorForm extends CrmEntityForm<PersonScreening> {

    private static final I18n i18n = I18n.get(PersonScreeningEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    private ApplicationDocumentUploaderFolder fileUpload;

    private Widget guarantorsTabWidget;

    private final FormFlexPanel previousAddress = new FormFlexPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    public PersonScreeningEditorForm() {
        this(false);
    }

    public PersonScreeningEditorForm(boolean viewMode) {
        super(PersonScreening.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createIdentificationDocumentsTab(), i18n.tr("Identification Documents"));
        tabPanel.add(createAddressesTab(), i18n.tr("Addresses"));
        tabPanel.add(createlegalQuestionsTab(), proto().legalQuestions().getMeta().getCaption());
        tabPanel.add(createIncomesTab(), i18n.tr("Incomes"));
        tabPanel.add(createAssetsTab(), i18n.tr("Assets"));
        guarantorsTabWidget = createGuarantorsTab();

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
    protected void onPopulate() {
        super.onPopulate();

        enablePreviousAddress();

        // add/remove guarantors tab depends of screened person:
        if (getValue().screene().getInstanceValueClass().equals(Tenant.class)) {
            if (tabPanel.getWidgetIndex(guarantorsTabWidget) < 0) {
                tabPanel.add(guarantorsTabWidget, i18n.tr("Guarantors"));
            }
        } else if (getValue().screene().getInstanceValueClass().equals(Guarantor.class)) {
            if (tabPanel.getWidgetIndex(guarantorsTabWidget) >= 0) {
                tabPanel.remove(guarantorsTabWidget);
            }
        }
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditor<PriorAddress> currentAddressForm = ((CEntityEditor<PriorAddress>) get(proto().currentAddress()));

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
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) get(proto().previousAddress()));

        new PastDateValidation(previousAddressForm.get(previousAddressForm.proto().moveInDate()));

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (value == null || getValue() == null) {
                    return null;
                }
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.before(date.getValue())) ? null : new ValidationFailure(i18n.tr("Move In Date must be less then Move Out Date"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        // ------------------------------------------------------------------------------------------------        

        new PastDateValidation(previousAddressForm.get(previousAddressForm.proto().moveOutDate()));

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (value == null || getValue() == null) {
                    return null;
                }
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue())) ? null : new ValidationFailure(i18n
                        .tr("Move Out Date must be greater then Move In Date"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));
    }

    private Widget createIdentificationDocumentsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, inject(proto().documents(), fileUpload = new IdUploaderFolder()));

        return new CrmScrollPanel(main);
    }

    private Widget createAddressesTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, proto().currentAddress().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH1(0, 0, 1, proto().previousAddress().getMeta().getCaption());
        previousAddress.setWidget(1, 0, inject(proto().previousAddress(), new PriorAddressEditor()));
        main.setWidget(++row, 0, previousAddress);

        return new CrmScrollPanel(main);
    }

    private Widget createlegalQuestionsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setWidget(row++, 0,
                new DecoratorBuilder(inject(proto().legalQuestions().suedForRent()), 10, 45).labelAlignment(Alignment.left).useLabelSemicolon(false).build());
        main.setHR(row++, 0, 1);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().legalQuestions().suedForDamages()), 10, 45).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(row++, 0, 1);
        main.setWidget(row++, 0,
                new DecoratorBuilder(inject(proto().legalQuestions().everEvicted()), 10, 45).labelAlignment(Alignment.left).useLabelSemicolon(false).build());
        main.setHR(row++, 0, 1);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().legalQuestions().defaultedOnLease()), 10, 45).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(row++, 0, 1);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().legalQuestions().convictedOfFelony()), 10, 45).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(row++, 0, 1);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().legalQuestions().legalTroubles()), 10, 45).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(row++, 0, 1);
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().legalQuestions().filedBankruptcy()), 10, 45).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());

        return new CrmScrollPanel(main);
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().currentAddress().moveInDate().getValue()));
    }

// Financial: ------------------------------------------------------------------------------------------------

    private Widget createIncomesTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().incomes(), new PersonalIncomeFolder(isEditable())));

        return new ScrollPanel(main);
    }

    private Widget createAssetsTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().assets(), new PersonalAssetFolder(isEditable())));

        return new CrmScrollPanel(main);
    }

    private Widget createGuarantorsTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().guarantors(), new PersonGuarantorFolder(isEditable())));

        return new ScrollPanel(main);
    }
}