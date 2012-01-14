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

import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;
import com.pyx4j.widgets.client.ListBox;

/**
 *
 */
public class NComboBox<E> extends NFocusComponent<E, ListBox, CComboBox<E>> implements INativeFocusComponent<E> {

    private E value;

    private boolean firstNativeItemIsNoSelection = false;

    private E notInOptionsValue = null;

    private boolean deferredSetSelectedStarted = false;

    public NComboBox(final CComboBox<E> comboBox) {
        super(comboBox);
    }

    @Override
    protected ListBox createEditor() {
        return new ListBox();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getCComponent().onEditingStop();
            }
        });
        setTabIndex(getCComponent().getTabIndex());
    }

    @Override
    public void setNativeValue(E newValue) {
        this.value = newValue;
        if (isViewable()) {
            getViewer().setHTML(getCComponent().getItemName(newValue));
        } else {
            if ((this.value != null) && ((getCComponent().getOptions() == null) || !getCComponent().getOptions().contains(this.value))) {
                refreshOptions();
            } else {
                setSelectedValue(this.value);
            }
        }
    }

    public void refreshOption(E opt) {
        getEditor().setItemText(getNativeOptionIndex(opt), getCComponent().getItemName(opt));
    }

    public void removeOption(E opt) {
        getEditor().removeItem(getNativeOptionIndex(opt));
    }

    public void refreshOptions() {
        if (getEditor() != null) {
            getEditor().clear();

            firstNativeItemIsNoSelection = !getCComponent().isMandatory();
            if (firstNativeItemIsNoSelection) {
                getEditor().addItem(getCComponent().getItemName(null));
            }

            if ((this.value != null) && ((getCComponent().getOptions() == null) || !getCComponent().getOptions().contains(this.value))) {
                switch (getCComponent().getPolicy()) {
                case KEEP:
                    getEditor().addItem(getCComponent().getItemName(this.value));
                    notInOptionsValue = this.value;
                    break;
                case DISCARD:
                    if (getCComponent().getOptions() != null) {
                        getCComponent().setValue(null);
                    }
                    notInOptionsValue = null;
                    break;

                }
            } else {
                notInOptionsValue = null;
            }

            if (getCComponent().getOptions() != null) {
                for (E o : getCComponent().getOptions()) {
                    getEditor().addItem(getCComponent().getItemName(o));
                }
            }
            setSelectedValue(this.value);
        }
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

        if (getCComponent().getOptions() == null) {
            return null;
        }

        if (firstNativeItemIsNoSelection) {
            index--;
        }
        if (notInOptionsValue != null) {
            index--;
        }
        return getCComponent().getOptions().get(index);
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
            if (getCComponent().getOptions() != null) {
                index = getCComponent().getOptions().indexOf(opt);
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
    public E getNativeValue() {
        this.value = getValueByNativeOptionIndex(getEditor().getSelectedIndex());
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
                    getEditor().setSelectedIndex(getNativeOptionIndex(NComboBox.this.value));
                }
            });
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (getCComponent().getOptions() == null) {
            getCComponent().retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    refreshOptions();
                }
            });
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        if (getEditor() != null) {
            getEditor().clear();
        }
    }

}
