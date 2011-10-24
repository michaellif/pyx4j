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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;
import com.pyx4j.widgets.client.ListBox;

/**
 *
 */
public class NativeComboBox<E> extends ListBox implements INativeEditableComponent<E> {

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final CComboBox<E> comboBox;

    private E value;

    private List<E> options;

    private boolean firstNativeItemIsNoSelection = false;

    private E notInOptionsValue = null;

    private boolean deferredSetSelectedStarted = false;

    private boolean enabled = true;

    private boolean editable = true;

    public NativeComboBox(final CComboBox<E> comboBox) {
        super();
        this.comboBox = comboBox;
        addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                CComboBox<E> comboBox = NativeComboBox.this.comboBox;
                comboBox.onEditingStop();
            }
        });
        setTabIndex(comboBox.getTabIndex());

    }

    public void setOptions(List<E> opt) {
        this.options = opt;
        refreshOptions();
    }

    public void refreshOption(E opt) {
        setItemText(getNativeOptionIndex(opt), comboBox.getItemName(opt));
    }

    public void removeOption(E opt) {
        removeItem(getNativeOptionIndex(opt));
    }

    public void refreshOptions() {
        super.clear();

        firstNativeItemIsNoSelection = !comboBox.isMandatory();
        if (firstNativeItemIsNoSelection) {
            super.addItem(comboBox.getItemName(null));
        }

        if ((this.value != null) && ((options == null) || !options.contains(this.value))) {
            switch (comboBox.getPolicy()) {
            case KEEP:
                super.addItem(comboBox.getItemName(this.value));
                notInOptionsValue = this.value;
                break;
            case DISCARD:
                if (options != null) {
                    comboBox.setValue(null);
                }
                notInOptionsValue = null;
                break;

            }
        } else {
            notInOptionsValue = null;
        }

        if (options != null) {
            for (E o : options) {
                super.addItem(comboBox.getItemName(o));
            }
        }
        setSelectedValue(this.value);
    }

    private E getValueByNativeOptionIndex(int index) {
        if (index == -1) {
            return null;
        } else if (index == 0) {
            if (firstNativeItemIsNoSelection) {
                return null;
            } else if (notInOptionsValue != null) {
                return this.value;
            }
        } else if (index == 1) {
            if (firstNativeItemIsNoSelection && (notInOptionsValue != null)) {
                return this.value;
            }
        }

        if (options == null) {
            return null;
        }

        if (firstNativeItemIsNoSelection) {
            index--;
        }
        if (notInOptionsValue != null) {
            index--;
        }
        return options.get(index);
    }

    private int getNativeOptionIndex(E opt) {
        if (opt == null) {
            if (firstNativeItemIsNoSelection) {
                return 0;
            } else {
                return -1;
            }
        } else {
            int index = -1;
            if (options != null) {
                index = options.indexOf(opt);
            }
            if (index != -1) {
                if (firstNativeItemIsNoSelection) {
                    index++;
                }
                if (notInOptionsValue != null) {
                    index++;
                }
            } else if ((notInOptionsValue != null) && (notInOptionsValue.equals(opt))) {
                index = 0;
                if (firstNativeItemIsNoSelection) {
                    index++;
                }
            }
            return index;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && this.isEditable());
        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        super.setEnabled(editable && this.isEnabled());
        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setNativeValue(E value) {
        this.value = value;
        if ((this.value != null) && ((options == null) || !options.contains(this.value))) {
            refreshOptions();
        } else {
            setSelectedValue(this.value);
        }
    }

    @Override
    public E getNativeValue() {
        this.value = getValueByNativeOptionIndex(getSelectedIndex());
        return this.value;
    }

    private void setSelectedValue(E value) {
        this.value = value;
        if (!deferredSetSelectedStarted) {
            deferredSetSelectedStarted = true;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    deferredSetSelectedStarted = false;
                    setSelectedIndex(getNativeOptionIndex(NativeComboBox.this.value));
                }
            });
        }
    }

    @Override
    public CComboBox<E> getCComponent() {
        return comboBox;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
        if (options == null) {
            comboBox.retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    setOptions(opt);
                }
            });
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
        options = null;
        super.clear();
    }

    @Override
    public void setValid(boolean valid) {
        String dependentSuffix = Selector.getDependentName(StyleDependent.invalid);
        if (valid) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub

    }
}
