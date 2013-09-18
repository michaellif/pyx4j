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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class NativeListSelectionComposite<E> extends FocusPanel implements INativeListBox<E> {

    private static final Logger log = LoggerFactory.getLogger(NativeListSelectionComposite.class);

    private final InnerListBox selectedListBox;

    protected final Button addButton;

    protected final Button removeButton;

    private List<E> optionsItemList;

    private final InnerListBox optionsListBox;

    private boolean enabled = true;

    private boolean editable = true;

    private final INativeListBox<E> implDelegate;

    private boolean ignoreBlur = false;

    protected class InnerListBox extends ListBox implements HasDoubleClickHandlers {

        private final ArrayList<E> itemList;

        InnerListBox(boolean isMultipleSelect) {
            super(isMultipleSelect);
            this.itemList = new ArrayList<E>();
            this.setTitle("Double click or Space to move");
        }

        @Override
        public void clear() {
            super.clear();
            itemList.clear();
        }

        public void addItem(E item) {
            this.itemList.add(item);
            super.addItem(getItemName(item));
        }

        public void insertItem(E item) {
            this.itemList.add(item);
            Comparator<E> comparator = getComparator();
            if (comparator == null) {
                super.addItem(getItemName(item));
            } else {
                Collections.sort(this.itemList, comparator);
                super.insertItem(getItemName(item), this.itemList.indexOf(item));
            }
        }

        @SuppressWarnings("unchecked")
        public List<E> getItems() {
            return (List<E>) itemList.clone();
        }

        public void refreshItem(int index) {
            if (index < 0) {
                return;
            } else {
                this.setItemText(index, getItemName(itemList.get(index)));
            }
        }

        E getItem(int index) {
            return itemList.get(index);
        }

        public boolean contains(E item) {
            return itemList.contains(item);
        }

        void removeItem(E item) {
            int index = itemList.indexOf(item);
            if (index > -1) {
                itemList.remove(index);
                super.removeItem(index);
            }
        }

        @Override
        public void removeItem(int index) {
            itemList.remove(index);
            super.removeItem(index);
        }

        List<E> getSelected() {
            List<E> s = new Vector<E>();
            for (int i = 0; i < getItemCount(); i++) {
                if (isItemSelected(i)) {
                    s.add(getItem(i));
                }
            }
            return s;
        }

        void deSelected() {
            super.setSelectedIndex(-1);
        }

        void setSelected(List<E> items) {
            deSelected();
            for (E item : items) {
                int index = itemList.indexOf(item);
                if (index > -1) {
                    super.setItemSelected(index, true);
                }
            }
        }

        @Override
        public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
            return addDomHandler(handler, DoubleClickEvent.getType());
        }

    }

    public NativeListSelectionComposite(int visibleItems, INativeListBox<E> implDelegate) {
        this.implDelegate = implDelegate;
        selectedListBox = new InnerListBox(true);
        optionsListBox = new InnerListBox(true);
        optionsListBox.addItem("Loading...");

        FlexTable content = new FlexTable();
        setWidget(content);
        Label availableLabel = new Label("Available");
        content.setWidget(0, 0, availableLabel);

        Label selectedLabel = new Label("Selected");
        content.setWidget(0, 3, selectedLabel);

        content.setWidget(1, 0, optionsListBox);
        content.getFlexCellFormatter().setRowSpan(1, 0, 4);

        // ->
        addButton = new Button(ImageFactory.getImages().arrowLightGreyRight());
//        new Image(ImageFactory.getImages().arrowLightGreyRight()), new Image(ImageFactory.getImages().arrowLightBlueRightDown()));
//        addButton.getUpDisabledFace().setImage(new Image(ImageFactory.getImages().arrowGreyRight()));
//        Image imageRightOver = new Image(ImageFactory.getImages().arrowLightBlueRight());
//        addButton.getUpHoveringFace().setImage(imageRightOver);
//        Cursor.setHand(imageRightOver);

        addButton.setCommand(new Command() {
            @Override
            public void execute() {
                slectedAdd();
            }
        });
        content.setWidget(2, 1, addButton);

        optionsListBox.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                slectedAdd();
            }
        });
        optionsListBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.isRightArrow() || (event.getNativeKeyCode() == ' ')) {
                    slectedAdd();
                }
            }
        });

        // <-
        removeButton = new Button(ImageFactory.getImages().arrowLightGreyLeft());
//        new Image(ImageFactory.getImages().arrowLightGreyLeft()), new Image(ImageFactory.getImages().arrowLightBlueLeftDown()));
//        removeButton.getUpDisabledFace().setImage(new Image(ImageFactory.getImages().arrowGreyLeft()));
//        Image imageLeftOver = new Image(ImageFactory.getImages().arrowLightBlueLeft());
//        removeButton.getUpHoveringFace().setImage(imageLeftOver);
//        Cursor.setHand(imageLeftOver);

        removeButton.setCommand(new Command() {
            @Override
            public void execute() {
                slectedRemove();
            }
        });
        content.setWidget(3, 1, removeButton);

        selectedListBox.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                slectedRemove();
            }
        });
        selectedListBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.isLeftArrow() || (event.getNativeKeyCode() == ' ')) {
                    slectedRemove();
                }
            }
        });

        // Layout corrections
        content.getCellFormatter().getElement(1, 0).getStyle().setProperty("paddingRight", "0");
        removeButton.getElement().getStyle().setProperty("padding", "3px");
        addButton.getElement().getStyle().setProperty("padding", "3px");

        content.setWidget(1, 3, selectedListBox);
        content.getFlexCellFormatter().setRowSpan(1, 3, 4);

        // Remove table offset in form
        content.getElement().getStyle().setProperty("borderCollapse", "collapse");
        content.getCellFormatter().getElement(1, 0).getStyle().setProperty("paddingLeft", "0");

        addFocusableGroup(addButton, removeButton, selectedListBox, optionsListBox);

        setVisibleItemCount(visibleItems);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        optionsListBox.ensureDebugId(baseID + "-options");
        selectedListBox.ensureDebugId(baseID + "-selected");
        removeButton.ensureDebugId(baseID + "-removeButton");
        addButton.ensureDebugId(baseID + "-addButton");
    }

    public void setListBoxWidth(String width) {
        optionsListBox.setWidth(width);
        selectedListBox.setWidth(width);
    }

    private void slectedAdd() {
        List<E> selected = optionsListBox.getSelected();
        int count = 0;
        for (E item : selected) {
            optionsListBox.removeItem(item);
            selectedListBox.insertItem(item);
            count++;
        }
        selectedListBox.setSelected(selected);
        log.debug("items added", count);
    }

    private void slectedRemove() {
        List<E> selected = selectedListBox.getSelected();
        int count = 0;
        for (E item : selected) {
            String msg = itemCannotBeRemovedMessage(item);
            if (msg != null) {
                MessageDialog.info(msg);
                continue;
            }
            selectedListBox.removeItem(item);
            optionsListBox.insertItem(item);
            count++;
        }
        optionsListBox.setSelected(selected);
        log.debug("items removed", count);
    }

    @Override
    public void setWidth(String width) {
        try {
            if (width == null) {
                setListBoxWidth("200");
            } else {
                int w = (Integer.valueOf(width) - 40) / 2;
                setListBoxWidth(String.valueOf(w));
            }
        } catch (NumberFormatException ignore) {
            setListBoxWidth("200");
        }
        super.setWidth(width);
    }

    public void setListBoxHeight(String height) {
        optionsListBox.setHeight(height);
        selectedListBox.setHeight(height);
    }

    @Override
    public void setNativeValue(List<E> value) {
        selectedListBox.clear();
        if (value != null) {
            for (E item : value) {
                selectedListBox.addItem(item);
            }
        }
        refreshOptions();
    }

    @Override
    public List<E> getNativeValue() {
        return selectedListBox.getItems();
    }

    private void refreshOptions() {
        setOptions(optionsItemList);
    }

    @Override
    public void setOptions(List<E> options) {
        optionsListBox.clear();
        optionsItemList = options;
        if (options != null) {
            for (E item : options) {
                if (!selectedListBox.contains(item)) {
                    optionsListBox.addItem(item);
                }
            }
        }
    }

    @Override
    public void setVisibleItemCount(int count) {
        optionsListBox.setVisibleItemCount(count);
        selectedListBox.setVisibleItemCount(count);
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        setEnabledComponents(editable && this.isEnabled());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setEnabledComponents(enabled && this.isEditable());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isEditable() {
        return this.editable;
    }

    private void setEnabledComponents(boolean enabled) {
        optionsListBox.setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        selectedListBox.setEnabled(enabled);
    }

    protected void setGroupFocusHandler(GroupFocusHandler focusHandler) {

    }

    @Override
    public String getItemName(E item) {
        return implDelegate.getItemName(item);
    }

    @Override
    public String itemCannotBeRemovedMessage(E item) {
        return implDelegate.itemCannotBeRemovedMessage(item);
    }

    @Override
    public Comparator<E> getComparator() {
        return implDelegate.getComparator();
    }

    @Override
    public void setFocus(boolean focused) {
        if (focused || !ignoreBlur) {
            super.setFocus(focused);
        }
    }

    /*
     * Only fire blur if none of the FocusableGroup components have mouse over
     */
    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (!event.getAssociatedType().equals(BlurEvent.getType()) || !ignoreBlur) {
            super.fireEvent(event);
        }
    }

    /*
     * 1. MouseOver for any component from focusable group will disable Blur event for the parent component.
     * 2. Focus event for any component from focusable group will also set focus on the parent component via
     * setFocus(), while consequent Blur on the parent component loosing focus will be disabled by MouseOver.
     */
    private void addFocusableGroup(HasAllMouseHandlers... group) {
        for (HasAllMouseHandlers child : group) {
            if (child instanceof HasAllFocusHandlers) {
                child.addMouseOverHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent e) {
                        ignoreBlur = true;
                    }
                });
                child.addMouseOutHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        ignoreBlur = false;
                    }
                });
                ((HasAllFocusHandlers) child).addFocusHandler(new FocusHandler() {
                    @Override
                    public void onFocus(FocusEvent event) {
                        setFocus(true);
                    }
                });
            }
        }
    }
}
