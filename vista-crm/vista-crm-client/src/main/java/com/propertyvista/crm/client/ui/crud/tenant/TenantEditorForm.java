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

import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.components.folders.PhoneFolder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorForm extends CrmEntityForm<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantEditorForm.class);

    private final FormFlexPanel detailsContent = new FormFlexPanel();

    private final FormFlexPanel person = new FormFlexPanel();

    private final FormFlexPanel company = new FormFlexPanel();

    private final FormFlexPanel contacts = new FormFlexPanel();

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public TenantEditorForm() {
        this(false);
    }

    public TenantEditorForm(boolean viewMode) {
        super(TenantDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        // Person:
        int row = -1;

        if (isEditable()) {
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().namePrefix()), 5).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().middleName()), 5).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 25).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().maidenName()), 25).build());
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().nameSuffix()), 5).build());
        } else {
            person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Tenant"))
                    .build());
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().sex()), 7).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().birthDate()), 9).build());

        person.setBR(++row, 0, 1);

        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());
        person.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()), 25).build());

        // ------------------------------------------------------------------------------------------------------------

        // Company:
        row = -1;
        company.setWidget(++row, 0, new DecoratorBuilder(inject(proto().company().name()), 25).build());
        company.setWidget(++row, 0, new DecoratorBuilder(inject(proto().company().website()), 25).build());

        company.setH1(++row, 0, 1, proto().company().phones().getMeta().getCaption());
        company.setWidget(++row, 0, inject(proto().company().phones(), new PhoneFolder(isEditable())));

        company.setH1(++row, 0, 1, proto().company().emails().getMeta().getCaption());
        company.setWidget(++row, 0, inject(proto().company().emails(), new EmailFolder(isEditable())));

        contacts.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable(), true)));

        // ------------------------------------------------------------------------------------------------------------

        // form the hole combined content:
        row = -1;
        detailsContent.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenantID()), 20).build());
        detailsContent.setWidget(++row, 0, person);
        detailsContent.setWidget(++row, 0, company);
        detailsContent.setBR(++row, 0, 1);
        if (isEditable()) {
            detailsContent.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease(), new CEntityLabel<Lease>())).build());
        } else {
            detailsContent.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().lease(), new CEntityCrudHyperlink<Lease>(MainActivityMapper.getCrudAppPlace(Lease.class)))).build());
        }

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        if (index < tabPanel.getWidgetCount()) {
            tabPanel.selectTab(index);
        }
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        setVisibility();
    }

    @Override
    public void addValidations() {
        get(proto().person().email()).setMandatory(true);

        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {

            @Override
            public ValidationFailure isValid(CComponent<List<EmergencyContact>, ?> component, List<EmergencyContact> value) {
                if (value == null || getValue() == null) {
                    return null;
                }
                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new ValidationFailure(i18n
                        .tr("Duplicate contacts specified"));
            }

        });

        new PastDateValidation(get(proto().person().birthDate()));

    }

    private void setVisibility() {
        tabPanel.clear();
        person.setVisible(false);
        company.setVisible(false);

        switch (getValue().type().getValue()) {
        case person:
            person.setVisible(true);
            tabPanel.add(new CrmScrollPanel(detailsContent), i18n.tr("Details"));
            tabPanel.add(isEditable() ? new HTML() : ((TenantViewerView) getParentView()).getScreeningListerView().asWidget(), i18n.tr("Screening"));
            tabPanel.setLastTabDisabled(isEditable());
            break;
        case company:
            company.setVisible(true);
            tabPanel.add(new CrmScrollPanel(detailsContent), proto().company().getMeta().getCaption());
            break;
        }

        get(proto().lease()).setVisible(!getValue().lease().isNull());

        get(proto().tenantID()).setViewable(false);
        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), IdAssignmentPolicy.class,
                new DefaultAsyncCallback<IdAssignmentPolicy>() {
                    @Override
                    public void onSuccess(IdAssignmentPolicy result) {
                        IdAssignmentItem targetItem = null;
                        for (IdAssignmentItem item : result.itmes()) {
                            if (item.target().getValue() == IdTarget.tenant) {
                                targetItem = item;
                                break;
                            }
                        }

                        if (targetItem != null) {
                            switch (targetItem.type().getValue()) {
                            case generatedAlphaNumeric:
                            case generatedNumber:
                                get(proto().tenantID()).setViewable(true);
                                break;
                            case userEditable:
                                get(proto().tenantID()).setViewable(false);
                                break;
                            case userCreated:
                                get(proto().tenantID()).setViewable(getValue().getPrimaryKey() != null);
                                break;
                            }
                        }
                    }
                });

        tabPanel.add(new ScrollPanel(contacts), proto().emergencyContacts().getMeta().getCaption());
    }
}