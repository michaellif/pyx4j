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
package com.pyx4j.forms.client.gwt;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.INativeNativeComboBox;
import com.pyx4j.widgets.client.ListBox;

/**
 *
 */
public class NativeComboBox<E> extends ListBox implements INativeNativeComboBox<E> {

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
                comboBox.setValue(getValueByNativeOptionIndex(getSelectedIndex()));
            }
        });
        setWidth(comboBox.getWidth());
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
        if (index == 0) {
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
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        super.setEnabled(editable && this.isEnabled());
    }

    public boolean isEditable() {
        return editable;
    }

    public void setNativeValue(E value) {
        this.value = value;
        if ((this.value != null) && ((options == null) || !options.contains(this.value))) {
            refreshOptions();
        } else {
            setSelectedValue(this.value);
        }
    }

    private void setSelectedValue(E value) {
        this.value = value;
        if (!deferredSetSelectedStarted) {
            deferredSetSelectedStarted = true;
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    setSelectedIndex(getNativeOptionIndex(NativeComboBox.this.value));
                }
            });
        }
    }

    public CComboBox<E> getCComponent() {
        return comboBox;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
    }
}
