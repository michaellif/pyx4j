/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction;

import java.util.Set;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComboBox.NotInOptionsPolicy;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;

public class EvictionStatusEditorBase<S extends EvictionCaseStatus> extends CForm<S> {

    public interface EvictionStepSelectionHandler {
        Set<EvictionFlowStep> getAvailableSteps();
    }

    private final EvictionStepSelectionHandler stepSelectionHandler;

    private final CComboBox<EvictionFlowStep> stepSelector = new CComboBox<EvictionFlowStep>( //
            NotInOptionsPolicy.KEEP, //
            new IFormatter<EvictionFlowStep, String>() {

                @Override
                public String format(EvictionFlowStep value) {
                    return value == null || value.name().isNull() ? "" : value.name().getValue();
                }
            } //
    ) {
        @Override
        protected void onEditingStart() {
            setOptions(getAvailableSteps());

            super.onEditingStart();
        }
    };

    EvictionStatusEditorBase(Class<S> entityClass, EvictionStepSelectionHandler stepSelectionHandler) {
        super(entityClass);
        this.stepSelectionHandler = stepSelectionHandler;
    }

    protected CComboBox<EvictionFlowStep> getStepSelector() {
        return stepSelector;
    }

    private Set<EvictionFlowStep> getAvailableSteps() {
        Set<EvictionFlowStep> opts = stepSelectionHandler.getAvailableSteps();
        // include current value, if any
        if (opts != null && getValue() != null && !getValue().evictionStep().name().isNull()) {
            opts.add(getValue().evictionStep());
        }
        return opts;
    }

    /** To be populated by subclasses */
    protected FormPanel getPropertyPanel() {
        return new FormPanel(this);
    }

    @Override
    protected FormPanel createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().evictionStep(), stepSelector).decorate();
        formPanel.append(Location.Dual, getPropertyPanel());

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        stepSelector.setOptions(getAvailableSteps());
    }
}
