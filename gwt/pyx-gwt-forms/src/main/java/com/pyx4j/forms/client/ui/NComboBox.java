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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Timer;
import com.pyx4j.gwt.commons.ui.HTML;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.CComboBox.NotInOptionsPolicy;
import com.pyx4j.widgets.client.ListBox;

/**
 *
 */
public class NComboBox<E> extends NFocusField<E, ListBox, CComboBox<E>, HTML> implements INativeFocusField<E> {

    private E value;

    private E populatedValue = null;

    private boolean firstNativeItemIsNoSelection = false;

    private E notInOptionsValue = null;

    private boolean deferredSetSelectedStarted = false;

    private List<E> shownOptions = new ArrayList<>();

    public NComboBox(final CComboBox<E> comboBox) {
        super(comboBox);
    }

    @Override
    protected ListBox createEditor() {
        return new ListBox();
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        getEditor().setWidth("100%");
        super.onEditorCreate();
        refreshOptions();
        getEditor().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getCComponent().stopEditing();
            }
        });
        setTabIndex(getCComponent().getTabIndex());
        getCComponent().addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyChangeEvent.PropertyName.mandatory) {
                    if (firstNativeItemIsNoSelection = !getCComponent().isMandatory()) {
                        refreshOptions();
                    }
                }

            }
        });
    }

    @Override
    public void setNativeValue(E newValue) {
        this.value = newValue;
        if (isViewable()) {
            getViewer().setText(getCComponent().getItemName(newValue));
        } else {
            if ((this.value != null) && ((getCComponent().getOptions() == null) || !getCComponent().getOptions().contains(this.value))) {
                refreshOptions();
            } else {
                setSelectedValue(this.value);
            }
        }
    }

    public void setPopulatedValue(E newValue) {
        this.populatedValue = newValue;
    }

    public void refreshOption(E opt) {
        int index = getNativeOptionIndex(opt);
        if (index >= 0) {
            getEditor().setItemText(index, getCComponent().getItemName(opt));
        }
    }

    public void removeOption(E opt) {
        int index = getNativeOptionIndex(opt);
        if (index >= 0) {
            getEditor().removeItem(index);
            shownOptions.remove(index);
        }
    }

    // The same as in CComponent
    public boolean isValueEmpty(E v) {
        return v == null || (v instanceof IEntity && ((IEntity) v).isNull());
    }

    public void refreshOptions() {
        if (getEditor() != null) {
            getEditor().clear();
            shownOptions.clear();

            firstNativeItemIsNoSelection = !getCComponent().isMandatory();
            if (firstNativeItemIsNoSelection) {
                shownOptions.add(null);
            }

            if (getCComponent().getOptions() != null) {
                // For Policy.KEEP Show populated value in the list
                if ((!isValueEmpty(this.populatedValue)) && (getCComponent().getPolicy() == NotInOptionsPolicy.KEEP)
                        && (!getCComponent().getOptions().contains(this.populatedValue))) {
                    notInOptionsValue = this.populatedValue;
                    shownOptions.add(notInOptionsValue);
                } else {
                    notInOptionsValue = null;
                }

                for (E o : getCComponent().getOptions()) {
                    shownOptions.add(o);
                }

                // See if value is in list created above.
                if ((this.value != null) && (getCComponent().getPolicy() == NotInOptionsPolicy.DISCARD) && (getNativeOptionIndex(this.value) == -1)) {
                    // Discard selection
                    if (getCComponent().isPopulated()) {
                        getCComponent().setValue(null, false);
                    } else {
                        // can't reset the value, it should be ignored.
                        this.value = null;
                    }
                }

                for (E o : shownOptions) {
                    getEditor().addItem(getCComponent().getItemName(o));
                }
            }
            setSelectedValue(this.value);
        }
    }

    private E getValueByNativeOptionIndex(int index) {
        if (index == -1) {
            return null;
        }
        return shownOptions.get(index);
    }

    private int getNativeOptionIndex(E opt) {
        if (opt == null) {
            if (firstNativeItemIsNoSelection) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return shownOptions.indexOf(opt);
        }
    }

    @Override
    public E getNativeValue() {
        this.value = getValueByNativeOptionIndex(getEditor().getSelectedIndex());
        return this.value;
    }

    /*
     * This is a highly educated hack around a very sophisticated asynchronous implementation of IE <select> element,
     * that likes from time to time to set it's initial selectedIndex value to 0 (first option) instead of -1.
     * The main idea below is to avoid multiple value changes and only apply the last one at the end of the execution
     * loop. And this is where Scheduler#scheduleDeferred() method comes to play.
     * However... for some unknown reason this is still not enough, and only added internal Timer loop seems to finally
     * help... Amen!
     */
    private void setSelectedValue(E value) {
        this.value = value;
        if (!deferredSetSelectedStarted) {
            deferredSetSelectedStarted = true;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    new Timer() {
                        @Override
                        public void run() {
                            getEditor().setSelectedIndex(getNativeOptionIndex(NComboBox.this.value));
                            deferredSetSelectedStarted = false;
                        }
                    }.schedule(0);
                }
            });
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (getCComponent().getOptions() != null) {
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
            shownOptions.clear();
        }
    }

}
