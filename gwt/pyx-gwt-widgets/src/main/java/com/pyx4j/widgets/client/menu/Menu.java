/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jun 4, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.menu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.menu.images.MenuImages;

public class Menu extends Widget implements PopupListener, HasAnimation, HasCloseHandlers<PopupPanel> {

    private Menu parentBar;

    private boolean inEventHandler;

    private boolean hidePopupHack;

    private static final String STYLENAME_DEFAULT = "gwt-MenuBar";

    private final ArrayList<IMenuItem> items = new ArrayList<IMenuItem>();

    private Element body;

    private boolean isAnimationEnabled = false;

    private Menu parentMenu;

    private PopupPanel popup;

    private LabelMenuItem selectedItem;

    private Menu shownChildMenu;

    private boolean vertical;

    private boolean autoOpen;

    public Menu() {
        this(false);
    }

    public Menu(boolean vertical) {
        super();
        init(vertical, ImageFactory.getImages());
    }

    public Menu(boolean vertical, MenuImages images) {
        init(vertical, images);
    }

    public Menu(MenuImages images) {
        this(false, images);
    }

    public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler) {
        return super.addHandler(handler, CloseEvent.getType());
    }

    public void addItem(IMenuItem item) {
        insertItem(item, items.size());
    }

    public void addItem(String text, boolean asHTML, Command cmd) {
        addItem(new ActionMenuItem(text, asHTML, cmd));
    }

    public void addItem(String text, boolean asHTML, Menu popup) {
        addItem(new SubMenuItem(text, asHTML, popup));
    }

    public void addItem(String text, Command cmd) {
        addItem(new ActionMenuItem(text, cmd));
    }

    public void addItem(String text, Menu popup) {
        addItem(new SubMenuItem(text, popup));
    }

    public MenuItemSeparator addSeparator() {
        return addSeparator(new MenuItemSeparator());
    }

    public MenuItemSeparator addSeparator(MenuItemSeparator separator) {
        return insertSeparator(separator, items.size());
    }

    /**
     * Removes all menu items from this menu bar.
     */
    public void clearItems() {
        // Deselect the current item
        selectItem(null);

        Element container = getItemContainerElement();
        while (DOM.getChildCount(container) > 0) {
            DOM.removeChild(container, DOM.getChild(container, 0));
        }

        // Set the parent of all items to null
        for (IMenuItem item : items) {
            setItemColSpan(item, 1);
            item.setParentMenu(null);
        }

        items.clear();
    }

    /**
     * Gets whether this menu bar's child menus will open when the mouse is moved over it.
     * 
     */
    public boolean getAutoOpen() {
        return autoOpen;
    }

    public int getItemIndex(IMenuItem item) {
        return items.indexOf(item);
    }

    public int getSeparatorIndex(MenuItemSeparator item) {
        return items.indexOf(item);
    }

    /**
     * Adds a menu item to the bar at a specific index.
     * 
     * @param item
     *            the item to be inserted
     * @param beforeIndex
     *            the index where the item should be inserted
     * @return the {@link ActionMenuItem} object
     * @throws IndexOutOfBoundsException
     *             if <code>beforeIndex</code> is out of range
     */
    public void insertItem(IMenuItem item, int beforeIndex) throws IndexOutOfBoundsException {
        if (item instanceof SubMenuItem) {
            item.setParentMenu(this);
        }

        // Check the bounds
        if (beforeIndex < 0 || beforeIndex > items.size()) {
            throw new IndexOutOfBoundsException();
        }

        // Add to the list of items
        items.add(beforeIndex, item);
        int itemsIndex = 0;
        for (int i = 0; i < beforeIndex; i++) {
            if (items.get(i) instanceof ActionMenuItem) {
                itemsIndex++;
            }
        }
        items.add(itemsIndex, item);

        // Setup the menu item
        addItemElement(beforeIndex, item.getElement());
        item.setParentMenu(this);
    }

    /**
     * Adds a thin line to the {@link Menu} to separate sections of {@link ActionMenuItem}
     * s at the specified index.
     * 
     * @param beforeIndex
     *            the index where the seperator should be inserted
     * @return the {@link MenuItemSeparator} object
     * @throws IndexOutOfBoundsException
     *             if <code>beforeIndex</code> is out of range
     */
    public MenuItemSeparator insertSeparator(int beforeIndex) {
        return insertSeparator(new MenuItemSeparator(), beforeIndex);
    }

    /**
     * Adds a thin line to the {@link Menu} to separate sections of {@link ActionMenuItem}
     * s at the specified index.
     * 
     * @param separator
     *            the {@link MenuItemSeparator} to be inserted
     * @param beforeIndex
     *            the index where the seperator should be inserted
     * @return the {@link MenuItemSeparator} object
     * @throws IndexOutOfBoundsException
     *             if <code>beforeIndex</code> is out of range
     */
    public MenuItemSeparator insertSeparator(MenuItemSeparator separator, int beforeIndex) throws IndexOutOfBoundsException {
        // Check the bounds
        if (beforeIndex < 0 || beforeIndex > items.size()) {
            throw new IndexOutOfBoundsException();
        }

        if (vertical) {
            setItemColSpan(separator, 2);
        }
        addItemElement(beforeIndex, separator.getElement());
        separator.setParentMenu(this);
        items.add(beforeIndex, separator);
        return separator;
    }

    public boolean isAnimationEnabled() {
        return isAnimationEnabled;
    }

    @Override
    public void onBrowserEvent(Event event) {
        try {
            inEventHandler = true;
            switch (DOM.eventGetType(event)) {
            case Event.ONCLICK:
                IMenuItem selected = this.getSelectedItem();
                if ((selected != null) && !(selected instanceof SubMenuItem)) {
                    if (selected.getParentMenu() instanceof Menu) {
                        removeParentMenuSelection(selected.getParentMenu());
                    }
                }
                break;
            case Event.ONFOCUS:
                return;
            }

            IMenuItem item = findItem(DOM.eventGetTarget(event));
            if (item instanceof LabelMenuItem) {
                LabelMenuItem labelMenu = (LabelMenuItem) item;
                switch (DOM.eventGetType(event)) {
                case Event.ONCLICK: {
                    FocusImpl.getFocusImplForPanel().focus(getElement());
                    // Fire an item's command when the user clicks on it.
                    if (item != null) {
                        doItemAction(labelMenu, true);
                    }
                    break;
                }

                case Event.ONMOUSEOVER: {
                    if (item != null) {
                        itemOver(labelMenu, true);
                    }
                    break;
                }

                case Event.ONMOUSEOUT: {
                    if (item != null) {
                        itemOver(null, true);
                    }
                    break;
                }

                case Event.ONFOCUS: {
                    selectFirstItemIfNoneSelected();
                    break;
                }

                case Event.ONKEYDOWN: {
                    int keyCode = DOM.eventGetKeyCode(event);
                    switch (keyCode) {
                    case KeyCodes.KEY_LEFT:
                        moveToPrevItem();
                        eatEvent(event);
                        break;
                    case KeyCodes.KEY_RIGHT:
                        moveToNextItem();
                        eatEvent(event);
                        break;
                    case KeyCodes.KEY_UP:
                        moveUp();
                        eatEvent(event);
                        break;
                    case KeyCodes.KEY_DOWN:
                        moveDown();
                        eatEvent(event);
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        closeAllParents();
                        eatEvent(event);
                        break;
                    case KeyCodes.KEY_ENTER:
                        if (!selectFirstItemIfNoneSelected()) {
                            doItemAction(selectedItem, true);
                            eatEvent(event);
                        }
                        break;
                    } // end switch(keyCode)

                    break;
                } // end case Event.ONKEYDOWN
                } // end switch (DOM.eventGetType(event))
                super.onBrowserEvent(event);

                if (DOM.eventGetType(event) == Event.ONMOUSEOVER) {
                    IMenuItem selected = this.getSelectedItem();
                    if ((selected != null) && !(selected instanceof SubMenuItem)) {
                        try {
                            hidePopupHack = true;
                            super.onDetach();
                        } catch (IllegalStateException ok) {
                        } finally {
                            hidePopupHack = false;
                        }
                    }
                }
            }
        } finally {
            inEventHandler = false;
        }

    }

    private void removeParentMenuSelection(Menu subBar) {
        if (subBar.parentBar != null) {
            removeMenuSelection(subBar.parentBar);
        }
    }

    private void removeMenuSelection(Menu bar) {
        IMenuItem selected = bar.getSelectedItem();
        if (selected != null) {
            int idx = bar.getItemIndex(selected);
            bar.removeItem(selected);
            bar.insertItem(selected, idx);
        }
    }

    @Override
    public boolean isAttached() {
        if (hidePopupHack) {
            return false;
        }
        return super.isAttached();
    }

    /**
     * Closes the menu bar.
     * 
     * @deprecated Use {@link #addCloseHandler(CloseHandler)} instead
     */
    @Deprecated
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
        if (!inEventHandler) {
            removeMenuSelection(this);
        }
        // If the menu popup was auto-closed, close all of its parents as well.
        if (autoClosed) {
            closeAllParents();
        }

        // When the menu popup closes, remember that no item is
        // currently showing a popup menu.
        onHide();
        CloseEvent.fire(Menu.this, sender);
        shownChildMenu = null;
        popup = null;
        if (parentMenu != null && parentMenu.popup != null) {
            parentMenu.popup.setPreviewingAllNativeEvents(true);
        }
    }

    /**
     * Removes the specified menu item from the bar.
     * 
     * @param item
     *            the item to be removed
     */
    public void removeItem(IMenuItem item) {
        // Unselect if the item is currently selected
        if (selectedItem == item) {
            selectItem(null);
        }

        if (removeItemElement(item)) {
            setItemColSpan(item, 1);
            items.remove(item);
            item.setParentMenu(null);
        }
    }

    /**
     * Removes the specified {@link MenuItemSeparator} from the bar.
     * 
     * @param separator
     *            the separator to be removed
     */
    public void removeSeparator(MenuItemSeparator separator) {
        if (removeItemElement(separator)) {
            separator.setParentMenu(null);
        }
    }

    public void setAnimationEnabled(boolean enable) {
        isAnimationEnabled = enable;
    }

    /**
     * Sets whether this menu bar's child menus will open when the mouse is moved over it.
     * 
     * @param autoOpen
     *            <code>true</code> to cause child menus to auto-open
     */
    public void setAutoOpen(boolean autoOpen) {
        this.autoOpen = autoOpen;
    }

    /**
     * Returns a list containing the <code>MenuItem</code> objects in the menu bar. If
     * there are no items in the menu bar, then an empty <code>List</code> object will be
     * returned.
     * 
     * @return a list containing the <code>MenuItem</code> objects in the menu bar
     */
    protected List<IMenuItem> getItems() {
        return this.items;
    }

    /**
     * Returns the <code>MenuItem</code> that is currently selected (highlighted) by the
     * user. If none of the items in the menu are currently selected, then
     * <code>null</code> will be returned.
     * 
     * @return the <code>MenuItem</code> that is currently selected, or <code>null</code>
     *         if no items are currently selected
     */
    protected LabelMenuItem getSelectedItem() {
        return this.selectedItem;
    }

    @Override
    protected void onDetach() {
        // When the menu is detached, make sure to close all of its children.
        if (popup != null) {
            popup.hide();
        }

        super.onDetach();
    }

    /**
     * <b>Affected Elements:</b>
     * <ul>
     * <li>-item# = the {@link ActionMenuItem} at the specified index.</li>
     * </ul>
     * 
     * @see UIObject#onEnsureDebugId(String)
     */
    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        setMenuItemDebugIds(baseID);
    }

    /*
     * Closes all parent menu popups.
     */
    void closeAllParents() {
        Menu curMenu = this;
        while (curMenu.parentMenu != null) {
            curMenu.close();
            curMenu = curMenu.parentMenu;
        }
    }

    /*
     * Performs the action associated with the given menu item. If the item has a popup
     * associated with it, the popup will be shown. If it has a command associated with
     * it, and 'fireCommand' is true, then the command will be fired. Popups associated
     * with other items will be hidden.
     * 
     * @param item the item whose popup is to be shown. @param fireCommand
     * <code>true</code> if the item's command should be fired, <code>false</code>
     * otherwise.
     */
    void doItemAction(final LabelMenuItem item, boolean fireCommand) {
        // Ensure that the item is selected.
        selectItem(item);

        if (item != null) {
            // if the command should be fired and the item has one, fire it
            if (fireCommand && item instanceof ActionMenuItem) {
                // Close this menu and all of its parents.
                closeAllParents();

                // Fire the item's command.
                Command cmd = ((ActionMenuItem) item).getCommand();
                DeferredCommand.addCommand(cmd);

                // hide any open submenus of this item
                if (shownChildMenu != null) {
                    shownChildMenu.onHide();
                    popup.hide();
                    shownChildMenu = null;
                    selectItem(null);
                }
            } else if (item instanceof SubMenuItem) {
                SubMenuItem subMenu = (SubMenuItem) item;
                if (shownChildMenu == null) {
                    // open this submenu
                    openPopup(subMenu);
                } else if (subMenu.getSubMenu() != shownChildMenu) {
                    // close the other submenu and open this one
                    shownChildMenu.onHide();
                    popup.hide();
                    openPopup(subMenu);
                } else if (fireCommand && !autoOpen) {
                    // close this submenu
                    shownChildMenu.onHide();
                    popup.hide();
                    shownChildMenu = null;
                    selectItem(item);
                }
            } else if (autoOpen && shownChildMenu != null) {
                // close submenu
                shownChildMenu.onHide();
                popup.hide();
                shownChildMenu = null;
            }
        }
    }

    void itemOver(LabelMenuItem item, boolean focus) {
        if (item == null) {
            // Don't clear selection if the currently selected item's menu is showing.
            if ((selectedItem instanceof SubMenuItem) && (shownChildMenu == ((SubMenuItem) selectedItem).getSubMenu())) {
                return;
            }
        }

        // Style the item selected when the mouse enters.
        selectItem(item);
        if (focus) {
            focus();
        }

        // If child menus are being shown, or this menu is itself
        // a child menu, automatically show an item's child menu
        // when the mouse enters.
        if (item != null) {
            if ((shownChildMenu != null) || (parentMenu != null) || autoOpen) {
                doItemAction(item, false);
            }
        }
    }

    void selectItem(LabelMenuItem item) {
        if (item == selectedItem) {
            return;
        }

        if (selectedItem != null) {
            selectedItem.setSelectionStyle(false);
            // Set the style of the submenu indicator
            if (vertical) {
                Element tr = DOM.getParent(selectedItem.getElement());
                if (DOM.getChildCount(tr) == 2) {
                    Element td = DOM.getChild(tr, 1);
                    setStyleName(td, "subMenuIcon-selected", false);
                }
            }
        }

        if (item != null) {
            item.setSelectionStyle(true);

            // Set the style of the submenu indicator
            if (vertical) {
                Element tr = DOM.getParent(item.getElement());
                if (DOM.getChildCount(tr) == 2) {
                    Element td = DOM.getChild(tr, 1);
                    setStyleName(td, "subMenuIcon-selected", true);
                }
            }

            Accessibility.setState(getElement(), Accessibility.STATE_ACTIVEDESCENDANT, DOM.getElementAttribute(item.getElement(), "id"));
        }

        selectedItem = item;
    }

    /**
     * Set the IDs of the menu items.
     * 
     * @param baseID
     *            the base ID
     */
    void setMenuItemDebugIds(String baseID) {
        int itemCount = 0;
        for (IMenuItem item : items) {
            item.ensureDebugId(baseID + "-item" + itemCount);
            itemCount++;
        }
    }

    /**
     * Show or hide the icon used for items with a submenu.
     * 
     * @param item
     *            the item with or without a submenu
     */
    //TODO
    //    void updateSubmenuIcon(SubMenuItem item) {
    //        // The submenu icon only applies to vertical menus
    //        if (!vertical) {
    //            return;
    //        }
    //
    //        // Get the index of the MenuItem
    //        int idx = allItems.indexOf(item);
    //        if (idx == -1) {
    //            return;
    //        }
    //
    //        Element container = getItemContainerElement();
    //        Element tr = DOM.getChild(container, idx);
    //        int tdCount = DOM.getChildCount(tr);
    //        MenuBar submenu = item.getSubMenu();
    //        if (submenu == null) {
    //            // Remove the submenu indicator
    //            if (tdCount == 2) {
    //                DOM.removeChild(tr, DOM.getChild(tr, 1));
    //            }
    //            setItemColSpan(item, 2);
    //        } else if (tdCount == 1) {
    //            // Show the submenu indicator
    //            setItemColSpan(item, 1);
    //            Element td = DOM.createTD();
    //            DOM.setElementProperty(td, "vAlign", "middle");
    //            DOM.setInnerHTML(td, images.menuBarSubMenuIcon().getHTML());
    //            setStyleName(td, "subMenuIcon");
    //            DOM.appendChild(tr, td);
    //        }
    //    }
    /**
     * Physically add the td element of a {@link ActionMenuItem} or
     * {@link MenuItemSeparator} to this {@link Menu}.
     * 
     * @param beforeIndex
     *            the index where the seperator should be inserted
     * @param tdElem
     *            the td element to be added
     */
    private void addItemElement(int beforeIndex, Element tdElem) {
        if (vertical) {
            Element tr = DOM.createTR();
            DOM.insertChild(body, tr, beforeIndex);
            DOM.appendChild(tr, tdElem);
        } else {
            Element tr = DOM.getChild(body, 0);
            DOM.insertChild(tr, tdElem, beforeIndex);
        }
    }

    /**
     * Closes this menu (if it is a popup).
     */
    private void close() {
        if (parentMenu != null) {
            parentMenu.popup.hide();
        }
    }

    private void eatEvent(Event event) {
        DOM.eventCancelBubble(event, true);
        DOM.eventPreventDefault(event);
    }

    private IMenuItem findItem(Element hItem) {
        for (IMenuItem item : items) {
            if (DOM.isOrHasChild(item.getElement(), hItem)) {
                return item;
            }
        }
        return null;
    }

    private void focus() {
        FocusImpl.getFocusImplForPanel().focus(getElement());
    }

    private Element getItemContainerElement() {
        if (vertical) {
            return body;
        } else {
            return DOM.getChild(body, 0);
        }
    }

    private void init(boolean vertical, MenuImages images) {

        Element table = DOM.createTable();
        body = DOM.createTBody();
        DOM.appendChild(table, body);

        if (!vertical) {
            Element tr = DOM.createTR();
            DOM.appendChild(body, tr);
        }

        this.vertical = vertical;

        Element outer = FocusImpl.getFocusImplForPanel().createFocusable();
        DOM.appendChild(outer, table);
        setElement(outer);

        Accessibility.setRole(getElement(), Accessibility.ROLE_MENUBAR);

        sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONFOCUS | Event.ONKEYDOWN);

        setStyleName(STYLENAME_DEFAULT);
        if (vertical) {
            addStyleDependentName("vertical");
        } else {
            addStyleDependentName("horizontal");
        }

        // Hide focus outline in Mozilla/Webkit/Opera
        DOM.setStyleAttribute(getElement(), "outline", "0px");

        // Hide focus outline in IE 6/7
        DOM.setElementAttribute(getElement(), "hideFocus", "true");
    }

    private void moveDown() {
        if (selectFirstItemIfNoneSelected()) {
            return;
        }

        if (vertical) {
            selectNextItem();
        } else {
            if (selectedItem instanceof SubMenuItem && (shownChildMenu == null || shownChildMenu.getSelectedItem() == null)) {
                Menu subMenu = ((SubMenuItem) selectedItem).getSubMenu();
                if (!subMenu.getItems().isEmpty()) {
                    if (shownChildMenu == null) {
                        doItemAction(selectedItem, false);
                    }
                    subMenu.focus();
                }
            } else if (parentMenu != null) {
                if (parentMenu.vertical) {
                    parentMenu.selectNextItem();
                } else {
                    parentMenu.moveDown();
                }
            }
        }
    }

    private void moveToNextItem() {
        if (selectFirstItemIfNoneSelected()) {
            return;
        }

        if (!vertical) {
            selectNextItem();
        } else {
            if (selectedItem instanceof SubMenuItem && (shownChildMenu == null || shownChildMenu.getSelectedItem() == null)) {
                Menu subMenu = ((SubMenuItem) selectedItem).getSubMenu();
                if (!subMenu.getItems().isEmpty()) {
                    if (shownChildMenu == null) {
                        doItemAction(selectedItem, false);
                    }
                    subMenu.focus();
                }
            } else if (parentMenu != null) {
                if (!parentMenu.vertical) {
                    parentMenu.selectNextItem();
                } else {
                    parentMenu.moveToNextItem();
                }
            }
        }
    }

    private void moveToPrevItem() {
        if (selectFirstItemIfNoneSelected()) {
            return;
        }

        if (!vertical) {
            selectPrevItem();
        } else {
            if ((parentMenu != null) && (!parentMenu.vertical)) {
                parentMenu.selectPrevItem();
            } else {
                close();
            }
        }
    }

    private void moveUp() {
        if (selectFirstItemIfNoneSelected()) {
            return;
        }

        if ((shownChildMenu == null) && vertical) {
            selectPrevItem();
        } else if ((parentMenu != null) && parentMenu.vertical) {
            parentMenu.selectPrevItem();
        } else {
            close();
        }
    }

    /*
     * This method is called when a menu bar is hidden, so that it can hide any child
     * popups that are currently being shown.
     */
    private void onHide() {
        if (shownChildMenu != null) {
            shownChildMenu.onHide();
            popup.hide();
            focus();
        }
    }

    /*
     * This method is called when a menu bar is shown.
     */
    private void onShow() {
        // clear the selection; a keyboard user can cursor down to the first item
        selectItem(null);
    }

    private void openPopup(final SubMenuItem item) {
        // Only the last popup to be opened should preview all event
        if (parentMenu != null && parentMenu.popup != null) {
            parentMenu.popup.setPreviewingAllNativeEvents(false);
        }

        // Create a new popup for this item, and position it next to
        // the item (below if this is a horizontal menu bar, to the
        // right if it's a vertical bar).
        popup = new PopupPanel(true, false) {
            {
                setWidget(item.getSubMenu());
                setPreviewingAllNativeEvents(true);
                item.getSubMenu().onShow();
            }

            @Override
            protected void onPreviewNativeEvent(NativePreviewEvent event) {
                // Hook the popup panel's event preview. We use this to keep it from
                // auto-hiding when the parent menu is clicked.
                if (!event.isCanceled()) {

                    switch (event.getTypeInt()) {
                    case Event.ONMOUSEDOWN:
                        // If the event target is part of the parent menu, suppress the
                        // event altogether.
                        EventTarget target = event.getNativeEvent().getEventTarget();
                        Element parentMenuElement = item.getParentMenu().getElement();
                        if (parentMenuElement.isOrHasChild(Element.as(target))) {
                            event.cancel();
                            return;
                        }
                        super.onPreviewNativeEvent(event);
                        if (event.isCanceled()) {
                            selectItem(null);
                        }
                        return;
                    }
                }
                super.onPreviewNativeEvent(event);
            }
        };
        //TODO check with version updates if the access to ONE_WAY_CORNER is changed to public
        //popup.setAnimationType(AnimationType.ONE_WAY_CORNER);
        popup.setAnimationEnabled(isAnimationEnabled);
        popup.setStyleName(STYLENAME_DEFAULT + "Popup");
        String primaryStyleName = getStylePrimaryName();
        if (!STYLENAME_DEFAULT.equals(primaryStyleName)) {
            popup.addStyleName(primaryStyleName + "Popup");
        }
        popup.addPopupListener(this);

        shownChildMenu = item.getSubMenu();
        item.getSubMenu().parentMenu = this;

        // Show the popup, ensuring that the menubar's event preview remains on top
        // of the popup's.
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

            public void setPosition(int offsetWidth, int offsetHeight) {

                // depending on the bidi direction position a menu on the left or right
                // of its base item
                if (vertical) {
                    position(popup, item, offsetWidth, offsetHeight);
                } else {
                    popup.setPopupPosition(item.getAbsoluteLeft(), Menu.this.getAbsoluteTop() + Menu.this.getOffsetHeight() - 1);
                }
            }

        });
    }

    protected void position(PopupPanel popup, SubMenuItem item, int offsetWidth, int offsetHeight) {
        popup.setPopupPosition(item.getAbsoluteLeft() + item.getOffsetWidth() + 1, item.getAbsoluteTop());
    }

    /**
     * Removes the specified item from the {@link Menu} and the physical DOM structure.
     * 
     * @param item
     *            the item to be removed
     * @return true if the item was removed
     */
    private boolean removeItemElement(IMenuItem item) {
        int idx = items.indexOf(item);
        if (idx == -1) {
            return false;
        }

        Element container = getItemContainerElement();
        DOM.removeChild(container, DOM.getChild(container, idx));
        items.remove(idx);
        return true;
    }

    /**
     * Selects the first item in the menu if no items are currently selected. This method
     * assumes that the menu has at least 1 item.
     * 
     * @return true if no item was previously selected and the first item in the list was
     *         selected, false otherwise
     */
    private boolean selectFirstItemIfNoneSelected() {
        if (selectedItem == null) {
            IMenuItem nextItem = items.get(0);
            if (nextItem instanceof LabelMenuItem) {
                selectItem((LabelMenuItem) nextItem);
            }
            return true;
        }

        return false;
    }

    private void selectNextItem() {
        if (selectedItem == null) {
            return;
        }

        int index = items.indexOf(selectedItem);
        // We know that selectedItem is set to an item that is contained in the
        // items collection.
        // Therefore, we know that index can never be -1.
        assert (index != -1);

        LabelMenuItem itemToBeSelected = nextSelectableItem();

        selectItem(itemToBeSelected);
        if (shownChildMenu != null) {
            doItemAction(itemToBeSelected, false);
        }
    }

    private void selectPrevItem() {
        if (selectedItem == null) {
            return;
        }

        int index = items.indexOf(selectedItem);
        // We know that selectedItem is set to an item that is contained in the
        // items collection.
        // Therefore, we know that index can never be -1.
        assert (index != -1);

        LabelMenuItem itemToBeSelected = previousSelectableItem();

        selectItem(itemToBeSelected);
        if (shownChildMenu != null) {
            doItemAction(itemToBeSelected, false);
        }
    }

    /**
     * Set the colspan of a {@link ActionMenuItem} or {@link MenuItemSeparator}.
     * 
     * @param item
     *            the {@link ActionMenuItem} or {@link MenuItemSeparator}
     * @param colspan
     *            the colspan
     */
    private void setItemColSpan(IMenuItem item, int colspan) {
        if (item instanceof UIObject) {
            DOM.setElementPropertyInt(((UIObject) item).getElement(), "colSpan", colspan);
        }
    }

    private LabelMenuItem nextSelectableItem() {
        //TODO
        return (LabelMenuItem) items.get(0);
    }

    private LabelMenuItem previousSelectableItem() {
        //TODO
        return (LabelMenuItem) items.get(0);
    }

}
