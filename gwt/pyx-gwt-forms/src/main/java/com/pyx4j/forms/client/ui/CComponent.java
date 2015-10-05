/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author Michael
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponentTheme.StyleDependent;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.AsyncValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.IValidator;
import com.pyx4j.forms.client.validators.MandatoryValidationError;
import com.pyx4j.forms.client.validators.MandatoryValidator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

public abstract class CComponent<SELF_TYPE extends CComponent<SELF_TYPE, DATA_TYPE, WIDGET_TYPE, DECORATOR_TYPE>, DATA_TYPE, WIDGET_TYPE extends INativeComponent<DATA_TYPE>, DECORATOR_TYPE extends IDecorator<? super SELF_TYPE>>
        implements HasHandlers, HasPropertyChangeHandlers, IsWidget, HasValueChangeHandlers<DATA_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(CComponent.class);

    private static final I18n i18n = I18n.get(CComponent.class);

    //VISTA-5729  and VISTA-5736
    private static boolean TODO_ENFORCE_POPULATED = true;

    protected static final String DEV_ATTR = "devAttr";

    public static enum NoteStyle {

        Info(CComponentTheme.StyleDependent.info),

        Warn(CComponentTheme.StyleDependent.warning);

        private NoteStyle(StyleDependent style) {
            this.style = style;
        }

        CComponentTheme.StyleDependent style;

        public CComponentTheme.StyleDependent getStyle() {
            return style;
        }
    }

    private WIDGET_TYPE nativeComponent;

    private EventBus eventBus;

    private HolderPanel holder;

    private String title;

    private String tooltip;

    private String note;

    private NoteStyle noteStyle = NoteStyle.Info;

    private CContainer<?, ?, ?> parent;

    private final Collection<IAccessAdapter> accessAdapters = new ArrayList<IAccessAdapter>();

    private ComponentAccessAdapter componentAccessAdapter;

    private ContainerAccessAdapter containerAccessAdapter;

    private IDebugId debugId;

    private IDebugId debugIdSuffix;

    private String mandatoryValidationMessage = i18n.tr("This field can't be blank");

    private boolean populated = false;

    private DATA_TYPE value = null;

    private List<IValidator<DATA_TYPE>> componentValidators;

    private MandatoryValidator<DATA_TYPE> mandatoryValidator;

    private AsyncValidator<DATA_TYPE> asyncValidator;

    private boolean visited = false;

    private boolean editingInProgress = false;

    private Set<AbstractValidationError> validationErrors = new HashSet<>();

    private DECORATOR_TYPE decorator;

    public CComponent() {
        this(null);
    }

    public CComponent(String title) {
        this.title = title;

        holder = new HolderPanel();
        holder.setStyleName(CComponentTheme.StyleName.ComponentHolder.name());

        componentAccessAdapter = new ComponentAccessAdapter();
        containerAccessAdapter = new ContainerAccessAdapter();
        addAccessAdapter(componentAccessAdapter);
        addAccessAdapter(containerAccessAdapter);

        if (ApplicationMode.isDevelopment()) {
            addPropertyChangeHandler(new PropertyChangeHandler() {

                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (asWidget() != null) {
                        asWidget().getElement().setAttribute(DEV_ATTR, CComponent.this.getDebugInfo());
                    }
                }
            });

            addValueChangeHandler(new ValueChangeHandler<DATA_TYPE>() {

                @Override
                public void onValueChange(ValueChangeEvent<DATA_TYPE> event) {
                    if (asWidget() != null) {
                        asWidget().getElement().setAttribute(DEV_ATTR, CComponent.this.getDebugInfo());
                    }
                }
            });
        }
    }

    @Override
    public Widget asWidget() {
        return holder;
    }

    /**
     * Basic information would be available in server log
     */
    public static String runtimeCrashInfo(CComponent<?, ?, ?, ?> component) {
        if (component == null) {
            return "n/a";
        }
        return component.getClass() + " " + component.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.title);
    }

    public CContainer<?, ?, ?> getParent() {
        return parent;
    }

    public boolean isAttached() {
        return parent != null;
    }

    public void onAdopt(CContainer<?, ?, ?> parent) {
        assert(this.parent == null) : "Component " + this.getClass().getName() + " is already bound to " + this.parent;
        this.parent = parent;
        setContainerAccessRules(true);

        setDebugIdSuffix(debugIdSuffix);
    }

    public void onAbandon() {
        parent = null;
        setContainerAccessRules(false);
        setDebugIdSuffix(null);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.debugId);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (eventBus != null) {
            eventBus.fireEventFromSource(event, this);
        }
    }

    protected EventBus ensureHandlers() {
        return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
    }

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    private void setContainerAccessRules(boolean inherit) {
        if (parent != null) {
            if (inherit) {
                containerAccessAdapter.setContainer(parent);
            } else {
                containerAccessAdapter.setContainer(null);
            }
            applyAccessibilityRules();
        }
    }

    public final boolean isEnabled() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean enabled = adapter.isEnabled();
            if (enabled != null && !enabled) {
                return false;
            }
        }
        return true;
    }

    public final void setEnabled(boolean enabled) {
        boolean before = isEnabled();
        componentAccessAdapter.setEnabled(enabled);
        if (before != isEnabled()) {
            applyEnablingRules();
            revalidate();
        }
    }

    public final void inheritEnabled(boolean flag) {
        containerAccessAdapter.inheritEnabled(flag);
        applyEnablingRules();
    }

    public final boolean isVisible() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean visible = adapter.isVisible();
            if (visible != null && !visible) {
                return false;
            }
        }
        return true;
    }

    public final void setVisible(boolean visible) {
        boolean before = isVisible();
        componentAccessAdapter.setVisible(visible);
        if (before != isVisible()) {
            applyVisibilityRules();
            revalidate();
        }
    }

    public final void inheritVisible(boolean flag) {
        containerAccessAdapter.inheritVisible(flag);
        applyVisibilityRules();
    }

    public final boolean isEditable() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean editable = adapter.isEditable();
            if (editable != null && !editable) {
                return false;
            }
        }
        return true;
    }

    public final void setEditable(boolean editable) {
        boolean before = isEditable();
        componentAccessAdapter.setEditable(editable);
        if (before != isEditable()) {
            applyEditabilityRules();
            revalidate();
        }
    }

    public final void inheritEditable(boolean flag) {
        containerAccessAdapter.inheritEditable(flag);
        applyEditabilityRules();
    }

    public final boolean isViewable() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean viewable = adapter.isViewable();
            if (viewable != null && viewable) {
                return true;
            }
        }
        return false;
    }

    public final void setViewable(boolean viewable) {
        boolean before = isViewable();
        componentAccessAdapter.setViewable(viewable);
        if (before != isViewable()) {
            applyViewabilityRules();
            revalidate();
        }
    }

    public final void inheritViewable(boolean flag) {
        containerAccessAdapter.inheritViewable(flag);
        applyViewabilityRules();
    }

    public boolean isMandatory() {
        return mandatoryValidator != null;
    }

    public void setMandatory(boolean mandatory) {
        if (isMandatory() != mandatory) {
            if (mandatory) {
                addComponentValidator(mandatoryValidator = new MandatoryValidator<DATA_TYPE>());
            } else {
                removeComponentValidator(mandatoryValidator);
                mandatoryValidator = null;
            }
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.mandatory);
            revalidate();
        }
    }

    public boolean isMandatoryConditionMet() {
        for (AbstractValidationError error : validationErrors) {
            if (error instanceof MandatoryValidationError) {
                return false;
            }
        }
        return true;
    }

    public void setMandatoryValidationMessage(String message) {
        mandatoryValidationMessage = message;
    }

    public String getMandatoryValidationMessage() {
        return mandatoryValidationMessage;
    }

    public void setAsyncValidationErrorMessage(String message) {
        if (asyncValidator == null) {
            addComponentValidator(asyncValidator = new AsyncValidator<>());
        }
        if (message == null || message.trim().equals("")) {
            asyncValidator.setValidationError(null);
        } else {
            asyncValidator.setValidationError(new BasicValidationError(this, message));
        }
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.valid);
        revalidate();
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        return addHandler(handler, PropertyChangeEvent.getType());
    }

    public void addAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.add(adapter);
    }

    public void removeAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.remove(adapter);
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.tooltip);
    }

    public String getNote() {
        return note;
    }

    public NoteStyle getNoteStyle() {
        return noteStyle;
    }

    public void setNote(String note, NoteStyle style) {
        this.note = note;
        this.noteStyle = style;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.note);
    }

    public void setNote(String note) {
        setNote(note, NoteStyle.Info);
    }

    public IDebugId getDebugId() {
        return debugId;
    }

    public void setDebugIdSuffix(IDebugId debugIdSuffix) {
        this.debugIdSuffix = debugIdSuffix;
        if (debugIdSuffix != null) {
            if (parent != null) {
                debugId = new CompositeDebugId(parent.getDebugId(), debugIdSuffix);
            } else {
                debugId = debugIdSuffix;
            }
        } else {
            debugId = null;
        }
        setDebugId(debugId);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.debugId);

    }

    protected abstract void setDebugId(IDebugId debugId);

    public void applyVisibilityRules() {

    }

    public void applyEnablingRules() {

    }

    public void applyEditabilityRules() {

    }

    public void applyViewabilityRules() {

    }

    public void applyAccessibilityRules() {
        applyVisibilityRules();
        applyEnablingRules();
        applyViewabilityRules();
        applyEditabilityRules();
    }

    public boolean isValid() {
        return validationErrors.size() == 0;
    }

    public final void revalidate() {
        Set<AbstractValidationError> origValidationErrors = new HashSet<>(validationErrors);
        if (isValidatable() && !isEditingInProgress()) {
            validationErrors = new HashSet<>();

            if (componentValidators != null) {
                for (IValidator<DATA_TYPE> validator : componentValidators) {
                    AbstractValidationError ve = validator.isValid();
                    if (ve != null) {
                        validationErrors.add(ve);
                    }
                }
            }
        } else {
            validationErrors.clear();
        }
        if (!origValidationErrors.equals(validationErrors)) {
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.valid);
        }

    }

    protected abstract boolean isValidatable();

    /**
     * Use only when you abandon component and detaching from Editing model.
     * In all other cases use clear();
     */
    public final void reset() {
        this.value = null;
        this.populated = false;
        setEditorValue(null);
        if (getParent() != null) {
            getParent().updateContainer(this);
        }
        setVisited(false);
        onReset();
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.reset);
    }

    /**
     * Remove all values from this component; in case of entity, preserves ownership relationships.
     */
    public final void clear() {
        setValue(null);
    }

    public final void setValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
        if (!populate) {
            if (TODO_ENFORCE_POPULATED) {
                assert populated : "Value should be populated before usage on component " + this.shortDebugInfo();
            } else if (!populated) {
                log.error("The API will change soon", new Error("Value should be populated before usage on component " + this.shortDebugInfo()));
            }
        }

        //In case of CComponent model represented by IEntity, disable check for equality because value may be the same instance that is returned by getValue()
        if (value instanceof IEntity || populate || !isValuesEqual(this.value, value)) {
            this.value = preprocessValue(value, fireEvent, populate);
            if (populate) {
                populated = true;
            }
            setEditorValue(this.value);
            revalidate();
            if (getParent() != null) {
                getParent().updateContainer(this);
            }
            if (fireEvent) {
                ValueChangeEvent.fire(this, this.value);
            }
        }
        onValuePropagation(this.value, fireEvent, populate);
        onValueSet(populate);

        if (populate) {
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.repopulated);
        }
    }

    public final void setValue(DATA_TYPE value, boolean fireEvent) {
        setValue(value, fireEvent, false);
    }

    /**
     * Attention! Fires onValueChange event
     *
     * @param value
     */
    public final void setValue(DATA_TYPE value) {
        setValue(value, true, false);
    }

    /*
     * Call populate on init of component
     */
    public final void populate(DATA_TYPE value) {
        setValue(value, false, true);
    }

    public final void refresh(boolean fireEvent) {
        this.value = preprocessValue(this.value, fireEvent, false);
        setEditorValue(this.value);
        revalidate();
        if (getParent() != null) {
            getParent().updateContainer(this);
        }
        if (fireEvent) {
            ValueChangeEvent.fire(this, this.value);
        }
        onValuePropagation(this.value, fireEvent, false);
        onValueSet(false);
    }

    protected void onValueSet(boolean populate) {
    }

    protected void onValuePropagation(DATA_TYPE value, boolean fireEvent, boolean populate) {
    }

    protected void onReset() {
    }

    protected DATA_TYPE preprocessValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
        return value;
    }

    public final boolean isPopulated() {
        return populated;
    }

    public final DATA_TYPE getValue() {
        return value;
    }

    public boolean isValueEmpty() {
        return value == null || (value instanceof IEntity && ((IEntity) value).isNull());
    }

    public boolean isValuesEqual(DATA_TYPE value1, DATA_TYPE value2) {
        return value1 == value2;
    }

    public boolean isEditingInProgress() {
        return editingInProgress;
    }

    public final void startEditing() {
        if (isEnabled() && isVisible() && isEditable() && !isViewable()) {
            onEditingStart();
            if (asyncValidator != null) {
                asyncValidator.setValidationError(null);
            }
            editingInProgress = true;
            revalidate();
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editingInProgress);
        }
    }

    public final void stopEditing() {
        if (isEnabled() && isVisible() && isEditable() && !isViewable()) {
            onEditingStop();
            boolean wasEmpty = isValueEmpty();
            boolean wasVisited = isVisited();
            editingInProgress = false;
            visited = true;
            DATA_TYPE editorValue;
            try {
                editorValue = getEditorValue();
            } catch (ParseException e) {
                editorValue = null;
            }

            this.value = editorValue;

            revalidate();
            //Overwrite native value with the value that has been formatted by getNativeValue()
            if (isValid()) {
                setEditorValue(this.value);
            }
            if (getParent() != null) {
                getParent().updateContainer(this);
            }
            ValueChangeEvent.fire(this, this.value);

            if (!wasEmpty || (wasEmpty && !isValueEmpty())) {
                setVisited(true);
            } else {
                setVisited(wasVisited);
            }
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editingCompleted);
        }
    }

    protected void onEditingStart() {
    }

    protected void onEditingStop() {
    }

    public ValidationResults getValidationResults() {
        ValidationResults results = new ValidationResults();
        if (!isValid()) {
            results.appendValidationErrors(validationErrors);
        }
        return results;
    }

    public final boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        if (this.visited != visited) {
            this.visited = visited;
            revalidate();
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.visited);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DATA_TYPE> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addComponentValidator(IValidator<DATA_TYPE> validator) {
        if (componentValidators == null) {
            componentValidators = new Vector<IValidator<DATA_TYPE>>();
        }
        componentValidators.add(validator);
        validator.setComponent(this);
    }

    public boolean removeComponentValidator(IValidator<DATA_TYPE> validator) {
        if (componentValidators != null) {
            return componentValidators.remove(validator);
        } else {
            return false;
        }
    }

    public List<IValidator<DATA_TYPE>> getComponentValidators() {
        return componentValidators;
    }

    @Override
    public String toString() {
        StringBuilder adaptersReport = new StringBuilder();
        for (IAccessAdapter adapter : accessAdapters) {
            adaptersReport.append(adapter.getClass().getName()).append(" ");
            adaptersReport.append("isEnabled ").append(adapter.isEnabled()).append(" ");
            adaptersReport.append("isEditable ").append(adapter.isEditable()).append(" ");
        }

        return "Type:" + this.getClass() + ";\n Title: " + getTitle() + ";\n isMandatory=" + isMandatory() + ";\n isMandatoryConditionMet="
                + isMandatoryConditionMet() + ";\n isEnabled=" + isEnabled() + ";\n isEditable=" + isEditable() + ";\n isVisible=" + isVisible()
                + ";\n isVisited=" + isVisited() + ";\n isValid=" + isValid() + ";\n toolTip=" + getTooltip() + ";\n adapters=[" + adaptersReport.toString()
                + "]";
    }

    public String shortDebugInfo() {
        return GWTJava5Helper.getSimpleName(this.getClass()) + ((debugId != null) ? " " + debugId : "");
    }

    protected String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("type").append("=").append(getClass().getSimpleName()).append(";");
        info.append("title").append("=").append(getTitle()).append(";");
        info.append("mandatory").append("=").append(isMandatory()).append(";");
        info.append("mandatoryCondtitionMet").append("=").append(isMandatoryConditionMet()).append(";");
        info.append("enabled").append("=").append(isEnabled()).append(";");
        info.append("editable").append("=").append(isEditable()).append(";");
        info.append("visible").append("=").append(isVisible()).append(";");
        info.append("visited").append("=").append(isVisited()).append(";");
        info.append("viewable").append("=").append(isViewable()).append(";");
        info.append("valid").append("=").append(isValid()).append(";");
        if (!isValid()) {
            info.append("validationErrors").append("=[");
            for (AbstractValidationError error : validationErrors) {
                info.append(error.getMessage()).append(", ");
            }
            info.substring(0, info.length() - 2);
            info.append("];");
        }
        return info.toString();
    }

    protected void setNativeComponent(WIDGET_TYPE nativeComponent) {
        this.nativeComponent = nativeComponent;
        if (decorator == null) {
            holder.setWidget(getNativeComponent());
        } else {
            holder.setWidget(decorator);
            decorator.setContent(getNativeComponent());
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    decorator.init((SELF_TYPE) CComponent.this);
                }
            });
        }
    }

    public final WIDGET_TYPE getNativeComponent() {
        return nativeComponent;
    }

    protected abstract void setEditorValue(DATA_TYPE value);

    protected abstract DATA_TYPE getEditorValue() throws ParseException;

    public DECORATOR_TYPE getDecorator() {
        return decorator;
    }

    @SuppressWarnings("unchecked")
    public void setDecorator(final DECORATOR_TYPE decorator) {
        this.decorator = decorator;
        if (decorator == null) {
            holder.setWidget(getNativeComponent());
        } else {
            holder.setWidget(decorator);
            decorator.setContent(getNativeComponent());
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    decorator.init((SELF_TYPE) CComponent.this);
                }
            });
        }
    }

    public void generateMockData() {

    }

    public void setMockValue(DATA_TYPE value) {
        if (isVisible() && isEditable() && isEnabled() && !isViewable() && isValueEmpty()) {
            setValue(value);
            setVisited(true);
        }
    }

    public void setMockValueByString(String value) {
        if (this instanceof IAcceptsText && isVisible() && isEditable() && isEnabled() && !isViewable() && isValueEmpty()) {
            ((IAcceptsText) this).setValueByString(value);
            setVisited(true);
        }
    }

    class HolderPanel extends SimplePanel implements RequiresResize, ProvidesResize {

        public HolderPanel() {
        }

        @Override
        public void onResize() {
            if (getWidget() instanceof RequiresResize) {
                ((RequiresResize) getWidget()).onResize();
            }
        }
    }

}
