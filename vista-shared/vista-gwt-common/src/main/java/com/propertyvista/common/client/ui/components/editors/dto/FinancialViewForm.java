/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.dto;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.PersonalAssetFolder;
import com.propertyvista.common.client.ui.components.folders.PersonalIncomeFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.dto.TenantFinancialDTO;

public class FinancialViewForm extends CEntityDecoratableForm<TenantFinancialDTO> {

    static I18n i18n = I18n.get(FinancialViewForm.class);

    public FinancialViewForm() {
        super(TenantFinancialDTO.class, new VistaEditorsComponentFactory());
    }

    public FinancialViewForm(boolean viewMode) {
        this();

        if (viewMode) {
            setEditable(false);
            setViewable(true);
        }
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        VerticalPanel adjust = new VerticalPanel();
        Image info = new Image(VistaImages.INSTANCE.formTooltipHoverInfo().getSafeUri());

        int row = -1;
        if (!isEditable()) {
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Person"))
                    .build());
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }

        main.setH1(++row, 0, 1, proto().incomes().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().incomes(), new PersonalIncomeFolder(isEditable())));
        main.setWidget(++row, 0, new HTML());

        info.setTitle("A Guarantor Is An Individual That Will Guarantee The Term Of The Lease. The Guarantor Cannot Be An Occupant Of the Suite And Is There To Assist The Applicant In The Approval Process. The Guarantor(s) Will Receive A Seperate Email With Instructions To Complete The Applications. Reminder: Only Completed Applications Will Be Processed.");
        adjust.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        adjust.add(info);
        adjust.setCellHeight(info, "26");

        main.setH1(++row, 0, 1, proto().assets().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().assets(), new PersonalAssetFolder(isEditable())));
        main.setWidget(++row, 0, new HTML());

        return main;
    }

    @Override
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<TenantFinancialDTO>() {

            @Override
            public ValidationError isValid(CComponent<TenantFinancialDTO> component, TenantFinancialDTO value) {
                return (value.assets().size() > 0) || (value.incomes().size() > 0) ? null : new ValidationError(component, i18n
                        .tr("At least one source of income or one asset is required"));
            }
        });
    }
}
