package com.pyx4j.tester.client.view.widget;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.pyx4j.gwt.commons.ui.HTML;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.events.HasNativeValueChangeHandlers;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.forms.client.events.NativeValueChangeHandler;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.AsyncOptionLoadingDelegate;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.CFocusComponent;
import com.pyx4j.forms.client.ui.NFocusField;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.IValidator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.view.widget.CAddressStateComboBox.NAddressStateBox;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.TextBox;

public abstract class CAddressStateComboBox<E, OPTION extends IEntity> extends CFocusComponent<E, NAddressStateBox<E, OPTION>>
        implements HasOptionsChangeHandlers<List<OPTION>>, HasNativeValueChangeHandlers<E>, AsyncOptionsReadyCallback<OPTION> {

    private static final I18n i18n = I18n.get(CAddressStateComboBox.class);

    private final List<OPTION> options = new ArrayList<OPTION>();

    private final AsyncOptionLoadingDelegate<OPTION> asyncOptionDelegate;

    private IValidator<E> unavailableValidator;

    public CAddressStateComboBox(Class<OPTION> entityClass) {
        super();

        NAddressStateBox<E, OPTION> nativeComboBox = new NAddressStateBox<E, OPTION>(this);
        nativeComboBox.refreshOptions();
        setNativeComponent(nativeComboBox);

        this.asyncOptionDelegate = new AsyncOptionLoadingDelegate<OPTION>(entityClass, this, null);
        this.unavailableValidator = new AbstractComponentValidator<E>() {
            @Override
            public BasicValidationError isValid() {
                return new BasicValidationError(getCComponent(), i18n.tr("Reference data unavailable"));
            }
        };
        retrieveOptions(null);
    }

    public abstract E convertOption(OPTION o);

    public abstract E parseValue(String value);

    public abstract String formatValue(E value);

    public void setTextMode(boolean textMode) {
        getNativeComponent().setTextMode(textMode);
        reset();
    }

    public EntityQueryCriteria<OPTION> addCriterion(Criterion criterion) {
        return asyncOptionDelegate.addCriterion(criterion);
    }

    public void resetCriteria() {
        asyncOptionDelegate.resetCriteria();
    }

    public boolean isOptionsLoaded() {
        return asyncOptionDelegate.isOptionsLoaded();
    }

    public void retrieveOptions(final AsyncOptionsReadyCallback<OPTION> optionsReadyCallback) {
        if (isViewable()) {
            return;
        } else if (!isOptionsLoaded()) {
            asyncOptionDelegate.retrieveOptions(optionsReadyCallback);
        } else if (optionsReadyCallback != null) {
            optionsReadyCallback.onOptionsReady(getOptions());
        }
    }

    public List<OPTION> getOptions() {
        return options;
    }

    public List<E> getConvertedOptions() {
        List<E> result = new ArrayList<>();
        for (OPTION o : options) {
            result.add(convertOption(o));
        }
        return result;
    }

    public void setOptions(Collection<OPTION> opt) {
        options.clear();
        if (opt != null) {
            options.addAll(opt);
        }
        // in case the options were set synchronously
        asyncOptionDelegate.setOptionsLoaded(true);

        getNativeComponent().refreshOptions();
        OptionsChangeEvent.fire(this, getOptions());
    }

    @Override
    public void onOptionsReady(List<OPTION> opt) {
        if (isOptionsLoaded()) {
            removeComponentValidator(unavailableValidator);
        } else if (!getNativeComponent().isTextMode()) {
            addComponentValidator(unavailableValidator);
        }
        setOptions(opt);
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<OPTION>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addNativeValueChangeHandler(NativeValueChangeHandler<E> handler) {
        return addHandler(handler, NativeValueChangeEvent.getType());
    }

    static class NAddressStateBox<E, OPTION extends IEntity> extends NFocusField<E, IFocusWidget, CAddressStateComboBox<E, OPTION>, HTML> {

        private ComboEditor comboEditor;

        private boolean textMode;

        // from NCombpoBox
        private E value;

        private boolean deferredSetSelectedStarted = false;

        public NAddressStateBox(CAddressStateComboBox<E, OPTION> cComponent) {
            super(cComponent);
            setTextMode(true);
        }

        private ComboEditor ensureEditor() {
            return comboEditor == null ? comboEditor = new ComboEditor() : comboEditor;
        }

        private TextBox<String> getTextEditor() {
            return ensureEditor().textBox;
        }

        private ListBox getComboEditor() {
            return ensureEditor().listBox;
        }

        public void setTextMode(boolean textMode) {
            this.textMode = textMode;
            ensureEditor().setTextMode(textMode);
        }

        public boolean isTextMode() {
            return textMode;
        }

        @Override
        public void setNativeValue(E newValue) {
            String textValue = getCComponent().formatValue(newValue);
            if (textMode) {
                if (isViewable()) {
                    getViewer().setText(textValue);
                } else {
                    if (!textValue.equals(getTextEditor().getValue())) {
                        getTextEditor().setValue(textValue);
                    }
                }
                NativeValueChangeEvent.fire(getCComponent(), newValue);
            } else {
                value = newValue;
                if (isViewable()) {
                    getViewer().setText(textValue);
                } else {
                    if ((value != null) && ((getCComponent().getOptions() == null) || !getCComponent().getOptions().contains(value))) {
                        refreshOptions();
                    } else {
                        setSelectedValue(value);
                    }
                }
            }
        }

        @Override
        public E getNativeValue() throws ParseException {
            if (isViewable()) {
                assert false : "getNativeValue() shouldn't be called in viewable mode";
            } else if (textMode) {
                // trim user input before parsing
                value = getCComponent().parseValue(getTextEditor().getValue().trim());
            } else {
                value = getValueByNativeOptionIndex(getComboEditor().getSelectedIndex());
            }
            return value;
        }

        @Override
        protected IFocusWidget createEditor() {
            return ensureEditor();
        }

        @Override
        protected HTML createViewer() {
            return new HTML();
        }

        public void refreshOptions() {
            getComboEditor().clear();

            if (getCComponent().getOptions() != null) {
                for (E o : getCComponent().getConvertedOptions()) {
                    getComboEditor().addItem(getCComponent().formatValue(o));
                }
                // Clear selection if not found in options
                if ((this.value != null) && (getNativeOptionIndex(this.value) == -1)) {
                    getCComponent().setValue(null, false);
                }
            }
            setSelectedValue(this.value);
        }

        private void setSelectedValue(E value) {
            this.value = value;
            if (!deferredSetSelectedStarted) {
                deferredSetSelectedStarted = true;
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        deferredSetSelectedStarted = false;
                        getComboEditor().setSelectedIndex(getNativeOptionIndex(NAddressStateBox.this.value));
                    }
                });
            }
        }

        private E getValueByNativeOptionIndex(int index) {
            return (getCComponent().getOptions() != null && index >= 0) ? getCComponent().getConvertedOptions().get(index) : null;
        }

        private int getNativeOptionIndex(E opt) {
            return (getCComponent().getOptions() != null && opt != null) ? getCComponent().getConvertedOptions().indexOf(opt) : -1;
        }

        static class ComboEditor extends SimplePanel implements IFocusWidget {
            private StringBox textBox;

            private ListBox listBox;

            private IFocusWidget editor;

            public ComboEditor() {
                this(true);
            }

            public ComboEditor(boolean textMode) {
                ensureTextBox();
                ensureListBox();
                setTextMode(textMode);
            }

            private StringBox ensureTextBox() {
                if (textBox == null) {
                    textBox = new StringBox();
                    textBox.setWidth("100%");
                }
                return textBox;
            }

            private ListBox ensureListBox() {
                if (listBox == null) {
                    listBox = new ListBox();
                    listBox.setWidth("100%");
                }
                return listBox;
            }

            public void setTextMode(boolean textMode) {
                setWidget(editor = textMode ? ensureTextBox() : ensureListBox());
            }

            @Override
            public void setEnabled(boolean enabled) {
                textBox.setEnabled(enabled);
                listBox.setEnabled(enabled);
            }

            @Override
            public boolean isEnabled() {
                return editor.isEnabled();
            }

            @Override
            public void setEditable(boolean editable) {
                textBox.setEditable(editable);
                listBox.setEditable(editable);
            }

            @Override
            public boolean isEditable() {
                return editor.isEditable();
            }

            @Override
            public int getTabIndex() {
                return editor.getTabIndex();
            }

            @Override
            public void setAccessKey(char key) {
                textBox.setAccessKey(key);
                listBox.setAccessKey(key);
            }

            @Override
            public void setFocus(boolean focused) {
                textBox.setFocus(focused);
                listBox.setFocus(focused);
            }

            @Override
            public void setTabIndex(int index) {
                textBox.setTabIndex(index);
                listBox.setTabIndex(index);
            }

            @Override
            public void addStyleName(String styleName) {
                textBox.addStyleName(styleName);
                listBox.addStyleName(styleName);
            }

            @Override
            public void addStyleDependentName(String styleSuffix) {
                textBox.addStyleDependentName(styleSuffix);
                listBox.addStyleDependentName(styleSuffix);
            }

            @Override
            public void removeStyleDependentName(String styleSuffix) {
                textBox.removeStyleDependentName(styleSuffix);
                listBox.removeStyleDependentName(styleSuffix);
            }

            @Override
            public void setDebugId(IDebugId debugId) {
                ensureDebugId(debugId.debugId());
            }

            @Override
            protected void onEnsureDebugId(String baseID) {
                // pass through non-empty debug id
                if (!CommonsStringUtils.isEmpty(baseID)) {
                    ensureDebugId(null);
                    textBox.ensureDebugId(baseID);
                    listBox.ensureDebugId(baseID);
                }
            }

            @Override
            public HandlerRegistration addFocusHandler(FocusHandler handler) {
                final HandlerRegistration hr1 = textBox.addFocusHandler(handler);
                final HandlerRegistration hr2 = listBox.addFocusHandler(handler);
                return new HandlerRegistration() {
                    @Override
                    public void removeHandler() {
                        hr1.removeHandler();
                        hr2.removeHandler();
                    }
                };
            }

            @Override
            public HandlerRegistration addBlurHandler(BlurHandler handler) {
                final HandlerRegistration hr1 = textBox.addBlurHandler(handler);
                final HandlerRegistration hr2 = listBox.addBlurHandler(handler);
                return new HandlerRegistration() {
                    @Override
                    public void removeHandler() {
                        hr1.removeHandler();
                        hr2.removeHandler();
                    }
                };
            }

            @Override
            public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
                final HandlerRegistration hr1 = textBox.addKeyDownHandler(handler);
                final HandlerRegistration hr2 = listBox.addKeyDownHandler(handler);
                return new HandlerRegistration() {
                    @Override
                    public void removeHandler() {
                        hr1.removeHandler();
                        hr2.removeHandler();
                    }
                };
            }

            @Override
            public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
                final HandlerRegistration hr1 = textBox.addKeyUpHandler(handler);
                final HandlerRegistration hr2 = listBox.addKeyUpHandler(handler);
                return new HandlerRegistration() {
                    @Override
                    public void removeHandler() {
                        hr1.removeHandler();
                        hr2.removeHandler();
                    }
                };
            }

            @Override
            public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
                final HandlerRegistration hr1 = textBox.addKeyPressHandler(handler);
                final HandlerRegistration hr2 = listBox.addKeyPressHandler(handler);
                return new HandlerRegistration() {
                    @Override
                    public void removeHandler() {
                        hr1.removeHandler();
                        hr2.removeHandler();
                    }
                };
            }

        }

    }
}
