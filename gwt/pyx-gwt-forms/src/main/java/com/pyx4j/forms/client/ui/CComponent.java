/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 21, 2016
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.ui.CComponentBase.NoteStyle;
import com.pyx4j.forms.client.ui.concerns.EditabilityConcern;
import com.pyx4j.forms.client.ui.concerns.EditabilityConcernAccessAdapterConvertor;
import com.pyx4j.forms.client.ui.concerns.EditabilitySecureConcern;
import com.pyx4j.forms.client.ui.concerns.EnablingConcernAccessAdapterConvertor;
import com.pyx4j.forms.client.ui.concerns.ViewabilityConcern;
import com.pyx4j.forms.client.ui.concerns.ViewabilityConcernAccessAdapterConvertor;
import com.pyx4j.forms.client.ui.concerns.ViewabilitySecureConcern;
import com.pyx4j.forms.client.ui.concerns.VisibilityConcernAccessAdapterConvertor;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.validators.IValidator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.gwt.commons.concerns.EnablingConcern;
import com.pyx4j.gwt.commons.concerns.EnablingSecureConcern;
import com.pyx4j.gwt.commons.concerns.VisibilityConcern;
import com.pyx4j.gwt.commons.concerns.VisibilitySecureConcern;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.Permission;

public interface CComponent<DATA_TYPE> extends IsWidget, HasPropertyChangeHandlers, HasValueChangeHandlers<DATA_TYPE> {

    // -- Data

    public boolean isPopulated();

    public DATA_TYPE getValue();

    public void populate(DATA_TYPE value);

    public void setValue(DATA_TYPE value);

    public void setValue(DATA_TYPE value, boolean fireEvent); // TODO remove

    public void setValue(DATA_TYPE value, boolean fireEvent, boolean populate);

    /**
     * Set value again and refresh its visual presentation
     */
    public void refresh(boolean fireEvent);

    /**
     * Remove all values from this component; in case of entity, preserves ownership relationships.
     */
    public void clear();

    public boolean isValueEmpty();

    public void generateMockData();

    public void setMockValue(DATA_TYPE value);

    public void setMockValueByString(String value);

    // -- Labels

    public String getTitle();

    public void setTitle(String title);

    public String getTooltip();

    public void setTooltip(String tooltip);

    public String getNote();

    public NoteStyle getNoteStyle();

    public void setNote(String note);

    public void setNote(String note, NoteStyle style);

    // -- Visual

    public boolean isVisible();

    public void setVisible(boolean visible);

    public boolean isEditable();

    public void setEditable(boolean editable);

    public void inheritEditable(boolean flag);

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public void inheritEnabled(boolean flag);

    public boolean isViewable();

    public void setViewable(boolean viewable);

    public void inheritViewable(boolean flag);

    public boolean isMandatory();

    public void setMandatory(boolean mandatory);

    // -- Validation

    public void addComponentValidator(IValidator<DATA_TYPE> validator);

    public void setVisited(boolean visited); // internals ?

    public boolean isVisited();

    public boolean isValid();

    public void revalidate();

    public boolean hasAsyncValidation();

    public void setAsyncValidationErrorMessage(String message);

    public ValidationResults getValidationResults();

    public String getMandatoryValidationMessage();

    public boolean isEditingInProgress();

    // --- internals ... move to other interface

    public INativeComponent<DATA_TYPE> getNativeComponent();

    @SuppressWarnings("rawtypes")
    public IDecorator getDecorator();

    public CContainer<?, ?, ?> getParent();

    public void onAdopt(CContainer<?, ?, ?> parent);

    public void addAccessAdapter(IAccessAdapter adapter);

    public void setDebugIdSuffix(IDebugId debugIdSuffix);

    public IDebugId getDebugId();

    public String getDebugInfo();

    public String shortDebugInfo();

    public void onAbandon();

    /**
     * Use only when you abandon component and detaching from Editing model.
     * In all other cases use clear();
     */
    public void reset();

    public void applyAccessibilityRules();

    public void applyVisibilityRules();

    public void applyEnablingRules();

    public void applyEditabilityRules();

    public void applyViewabilityRules();

    // Concerns to AccessAdapter converters to be moved to 'ConcernBuilderPermissionsBase'?

    /**
     * Component will become Visible when function returns true.
     *
     * Multiple concerns:
     * - all concerns of the same type should return true for component to become Visible.
     * - if any of concerns returns false the component will not become Visible.
     */
    default void visible(VisibilityConcern concern, String... debuggingAdapterName) {
        addAccessAdapter(new VisibilityConcernAccessAdapterConvertor(concern));
    }

    /**
     * Permit component to become Visible when one (any) of the Permission is satisfied.
     * Mind other 'visible' concerns.
     *
     * N.B. Permission with InstanceAccess rules not supported in CComponent hierarchy.
     *
     * @param permissions
     *            not null and not empty
     */
    default void visible(Permission... permissions) {
        visible(new VisibilitySecureConcern(permissions), permissions[0].toString());
    }

    /**
     * Permit component to become Visible when ActionId is permitted.
     * Mind other 'visible' concerns.
     *
     */
    default void visible(Class<? extends ActionId> actionId) {
        visible(new VisibilitySecureConcern(actionId), actionId.toString());
    }

    /**
     * Component will become Enabled when function returns true.
     *
     * Multiple concerns:
     * - all concerns of the same type should return true for component to become Enabled.
     * - if any of concerns returns false the component will not become Enabled.
     */
    default void enabled(EnablingConcern concern, String... debuggingAdapterName) {
        addAccessAdapter(new EnablingConcernAccessAdapterConvertor(concern));
    }

    /**
     * Permit component to become Enabled when one (any) of the Permission is satisfied.
     * Mind other 'enabled' concerns.
     *
     * N.B. Permission with InstanceAccess rules not supported in CComponent hierarchy.
     *
     * @param permissions
     *            not null and not empty
     */
    default void enabled(Permission... permissions) {
        enabled(new EnablingSecureConcern(permissions), permissions[0].toString());
    }

    /**
     * Permit component to become Enabled when ActionId is permitted.
     * Mind other 'enabled' concerns.
     *
     */
    default void enabled(Class<? extends ActionId> actionId) {
        enabled(new EnablingSecureConcern(actionId), actionId.toString());
    }

    default void editable(EditabilityConcern concern, String... debuggingAdapterName) {
        addAccessAdapter(new EditabilityConcernAccessAdapterConvertor(concern));
    }

    /**
     * Permit component to become Editable when one (any) of the Permission is satisfied.
     * Mind other 'editable' concerns.
     *
     * N.B. Permission with InstanceAccess rules not supported in CComponent hierarchy.
     *
     * @param permissions
     *            not null and not empty
     */
    default void editable(Permission... permissions) {
        editable(new EditabilitySecureConcern(permissions), permissions[0].toString());
    }

    /**
     * Permit component to become Enabled when ActionId is permitted.
     * Mind other 'editable' concerns.
     *
     */
    default void editable(Class<? extends ActionId> actionId) {
        editable(new EditabilitySecureConcern(actionId), actionId.toString());
    }

    default void viewable(ViewabilityConcern concern, String... debuggingAdapterName) {
        addAccessAdapter(new ViewabilityConcernAccessAdapterConvertor(concern));
    }

    /**
     * Permit component to become Viewable when one (any) of the Permission is satisfied.
     * Mind other 'viewable' concerns.
     *
     * N.B. Permission with InstanceAccess rules not supported in CComponent hierarchy.
     *
     * @param permissions
     *            not null and not empty
     */
    default void viewable(Permission... permissions) {
        viewable(new ViewabilitySecureConcern(permissions), permissions[0].toString());
    }

    /**
     * Permit component to become Viewable when ActionId is permitted.
     * Mind other 'viewable' concerns.
     *
     */
    default void viewable(Class<? extends ActionId> actionId) {
        viewable(new ViewabilitySecureConcern(actionId), actionId.toString());
    }
}
