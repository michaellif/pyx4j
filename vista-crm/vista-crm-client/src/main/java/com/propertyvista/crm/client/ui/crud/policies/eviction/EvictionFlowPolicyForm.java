/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.eviction;

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.EvictionFlowPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;

public class EvictionFlowPolicyForm extends PolicyDTOTabPanelBasedForm<EvictionFlowPolicyDTO> {

    public static final I18n i18n = I18n.get(EvictionFlowPolicyForm.class);

    public EvictionFlowPolicyForm(IPrimeFormView<EvictionFlowPolicyDTO, ?> view) {
        super(EvictionFlowPolicyDTO.class, view);

        addTab(getEvictionStepsTab(), i18n.tr("Eviction Steps"));
    }

    private IsWidget getEvictionStepsTab() {
        FormPanel tabPanel = new FormPanel(this);

        tabPanel.h1(i18n.tr("Steps that define your Eviction Flow"));
        tabPanel.append(Location.Dual, proto().evictionFlow(), new EvictionStepFolder());

        return tabPanel;
    }

    class EvictionStepFolder extends VistaBoxFolder<EvictionFlowStep> {

        public EvictionStepFolder() {
            super(EvictionFlowStep.class, true);
        }

        @Override
        protected CForm<EvictionFlowStep> createItemForm(IObject<?> member) {
            return new CForm<EvictionFlowStep>(EvictionFlowStep.class) {

                private final CComboBox<EvictionStepType> stepBaseSelector = new CComboBox<EvictionStepType>() {

                    @Override
                    protected void onEditingStart() {
                        Set<EvictionStepType> availableSteps = EnumSet.allOf(EvictionStepType.class);
                        Set<EvictionStepType> dontRemove = EnumSet.of(EvictionStepType.Custom);
                        if (getValue() != null) {
                            dontRemove.add(getValue());
                        }
                        for (EvictionFlowStep step : EvictionStepFolder.this.getValue()) {
                            if (!dontRemove.contains(step.stepType().getValue())) {
                                availableSteps.remove(step.stepType().getValue());
                            }
                        }
                        setOptions(availableSteps);

                        super.onEditingStart();
                    }
                };

                @Override
                protected IsWidget createContent() {
                    FormPanel panel = new FormPanel(this);

                    panel.append(Location.Left, proto().stepType(), stepBaseSelector).decorate();
                    panel.append(Location.Right, proto().name()).decorate();
                    panel.append(Location.Dual, proto().description()).decorate();

                    if (isEditable()) {
                        get(proto().stepType()).addValueChangeHandler(new ValueChangeHandler<EvictionFlowStep.EvictionStepType>() {

                            @Override
                            public void onValueChange(ValueChangeEvent<EvictionStepType> event) {
                                if (EvictionStepType.Custom.equals(event.getValue())) {
                                    get(proto().name()).setEditable(true);
                                    get(proto().name()).clear();
                                } else {
                                    get(proto().name()).setEditable(false);
                                    get(proto().name()).setValue(event.getValue() == null ? "" : event.getValue().toString());
                                }
                            }
                        });

                        get(proto().name()).addComponentValidator(new AbstractComponentValidator<String>() {

                            @Override
                            public AbstractValidationError isValid() {
                                for (EvictionFlowStep step : EvictionStepFolder.this.getValue()) {
                                    if (step.equals(getValue())) {
                                        continue;
                                    }
                                    if (step.name().getValue().equalsIgnoreCase(getCComponent().getValue())) {
                                        return new BasicValidationError(getCComponent(), i18n.tr("Duplicate Step Name"));
                                    }
                                }
                                return null;
                            }
                        });
                    }
                    return panel;
                }
            };
        }
    }
}
