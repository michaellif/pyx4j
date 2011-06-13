/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jun 13, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.resources.client.CommonResources;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;

/**
 * A panel that represents a tabbed set of pages, each of which contains another
 * widget. Its child widgets are shown as the user selects the various tabs
 * associated with them. The tabs can contain arbitrary text, HTML, or widgets.
 * 
 * <p>
 * This widget will <em>only</em> work in standards mode, which requires that the HTML page in which it is run have an explicit &lt;!DOCTYPE&gt; declaration.
 * </p>
 * 
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.gwt-TabLayoutPanel
 * <dd>the panel itself
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelTabs
 * <dd>the tab bar element
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelTab
 * <dd>an individual tab
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelTabInner
 * <dd>an element nested in each tab (useful for styling)
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelContent
 * <dd>applied to all child content widgets
 * </dl>
 * 
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.TabLayoutPanelExample}
 * 
 * <h3>Use in UiBinder Templates</h3>
 * <p>
 * A TabLayoutPanel element in a {@link com.google.gwt.uibinder.client.UiBinder
 * UiBinder} template must have a <code>barHeight</code> attribute with a double value, and may have a <code>barUnit</code> attribute with a
 * {@link com.google.gwt.dom.client.Style.Unit Style.Unit} value. <code>barUnit</code> defaults to PX.
 * <p>
 * The children of a TabLayoutPanel element are laid out in &lt;g:tab> elements. Each tab can have one widget child and one of two types of header elements. A
 * &lt;g:header> element can hold html, or a &lt;g:customHeader> element can hold a widget. (Note that the tags of the header elements are not capitalized. This
 * is meant to signal that the head is not a runtime object, and so cannot have a <code>ui:field</code> attribute.)
 * <p>
 * For example:
 * 
 * <pre>
 * &lt;g:TabLayoutPanel barUnit='EM' barHeight='3'>
 *  &lt;g:tab>
 *    &lt;g:header size='7'>&lt;b>HTML&lt;/b> header&lt;/g:header>
 *    &lt;g:Label>able&lt;/g:Label>
 *  &lt;/g:tab>
 *  &lt;g:tab>
 *    &lt;g:customHeader size='7'>
 *      &lt;g:Label>Custom header&lt;/g:Label>
 *    &lt;/g:customHeader>
 *    &lt;g:Label>baker&lt;/g:Label>
 *  &lt;/g:tab>
 * &lt;/g:TabLayoutPanel>
 * </pre>
 */
public class TabLayoutPanel extends ResizeComposite implements HasWidgets, ProvidesResize, IndexedPanel.ForIsWidget, AnimatedLayout,
        HasBeforeSelectionHandlers<Integer>, HasSelectionHandlers<Integer> {

    private class Tab extends SimplePanel {
        private Element inner;

        private boolean replacingWidget;

        public Tab(Widget child) {
            super(Document.get().createDivElement());
            getElement().appendChild(inner = Document.get().createDivElement());

            setWidget(child);
            setStyleName(TAB_STYLE);
            getElement().addClassName(CommonResources.getInlineBlockStyle());
        }

        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }

        @Override
        public boolean remove(Widget w) {
            /*
             * Removal of items from the TabBar is delegated to the TabLayoutPanel to
             * ensure consistency.
             */
            int index = tabs.indexOf(this);
            if (replacingWidget || index < 0) {
                /*
                 * The tab contents are being replaced, or this tab is no longer in the
                 * panel, so just remove the widget.
                 */
                return super.remove(w);
            } else {
                // Delegate to the TabLayoutPanel.
                return TabLayoutPanel.this.remove(index);
            }
        }

        public void setSelected(boolean selected) {
            if (selected) {
                addStyleDependentName("selected");
            } else {
                removeStyleDependentName("selected");
            }
        }

        @Override
        public void setWidget(Widget w) {
            replacingWidget = true;
            super.setWidget(w);
            replacingWidget = false;
            maybeShowButtons();
        }

        @Override
        protected com.google.gwt.user.client.Element getContainerElement() {
            return inner.cast();
        }
    }

    /**
     * This extension of DeckLayoutPanel overrides the public mutator methods to
     * 
     * prevent external callers from adding to the state of the DeckPanel.
     * <p>
     * Removal of Widgets is supported so that WidgetCollection.WidgetIterator operates as expected.
     * </p>
     * <p>
     * We ensure that the DeckLayoutPanel cannot become of of sync with its associated TabBar by delegating all mutations to the TabBar to this implementation
     * of DeckLayoutPanel.
     * </p>
     */

    private class TabbedDeckLayoutPanel extends com.google.gwt.user.client.ui.DeckLayoutPanel {

        @Override
        public void add(Widget w) {
            throw new UnsupportedOperationException("Use TabLayoutPanel.add() to alter the DeckLayoutPanel");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Use TabLayoutPanel.clear() to alter the DeckLayoutPanel");
        }

        @Override
        public void insert(Widget w, int beforeIndex) {
            throw new UnsupportedOperationException("Use TabLayoutPanel.insert() to alter the DeckLayoutPanel");
        }

        @Override
        public boolean remove(Widget w) {
            /*
             * Removal of items from the DeckLayoutPanel is delegated to the
             * TabLayoutPanel to ensure consistency.
             */
            return TabLayoutPanel.this.remove(w);
        }

        protected void insertProtected(Widget w, int beforeIndex) {
            super.insert(w, beforeIndex);
        }

        protected void removeProtected(Widget w) {
            super.remove(w);
        }
    }

    private static final String CONTENT_CONTAINER_STYLE = "gwt-TabLayoutPanelContentContainer";

    private static final String CONTENT_STYLE = "gwt-TabLayoutPanelContent";

    private static final String TAB_STYLE = "gwt-TabLayoutPanelTab";

    private static final int QUEUE_NEXT = -2;

    private static final int QUEUE_PREV = -3;

    private static final int BIG_ENOUGH_TO_NOT_WRAP = 16384;

    private final TabbedDeckLayoutPanel deckPanel = new TabbedDeckLayoutPanel();

    private final FlowPanel tabBar = new FlowPanel();

    private final LayoutPanel tabBarAnimator = new LayoutPanel();

    private final LayoutPanel tabNavPanel = new LayoutPanel();

    private final ArrayList<Tab> tabs = new ArrayList<Tab>();

    private final SimplePanel nextButtonPanel = new SimplePanel();

    private final SimplePanel previousButtonPanel = new SimplePanel();

    private int selectedIndex = -1;

    private final Image previous, next, nextDisabled, previousDisabled;

    private int firstVisibleTab = -1, lastVisibleTab = -1;

    private final List<Integer> tabQueue = new ArrayList<Integer>();

    private int tabAnimation;

    private boolean isAnimating = false;

    private int tabmargin = 0;

    /**
     * Creates an empty tab panel.
     * 
     * @param barHeight
     *            the size of the tab bar
     * @param barUnit
     *            the unit in which the tab bar size is specified
     * 
     */

    public TabLayoutPanel(double barHeight, Unit barUnit) {
        this(barHeight, barUnit, ImageFactory.getImages());
    }

    /**
     * Creates an empty tab panel.
     * 
     * @param barHeight
     *            the size of the tab bar
     * @param barUnit
     *            the unit in which the tab bar size is specified
     * @param resource
     *            the ClientBundle to use for some internal images.
     */
    public TabLayoutPanel(double barHeight, Unit barUnit, WidgetsImageBundle resource) {
        LayoutPanel panel = new LayoutPanel();
        initWidget(panel);

        // Add the tab bar to the panel.
        Panel navPanel = getTabNavPanel();
        navPanel.add(tabBarAnimator);
        tabBarAnimator.add(tabBar);
        navPanel.add(previousButtonPanel);
        navPanel.add(nextButtonPanel);
        navPanel.getElement().getStyle().setHeight(barHeight, barUnit);
        setTabAnimiationDuration(0);

        panel.add(navPanel);

        panel.setWidgetLeftRight(navPanel, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetTopHeight(navPanel, 0, Unit.PX, barHeight, barUnit);
        panel.setWidgetVerticalPosition(navPanel, Alignment.END);

        // Add the deck panel to the panel.
        deckPanel.addStyleName(CONTENT_CONTAINER_STYLE);
        panel.add(deckPanel);
        panel.setWidgetLeftRight(deckPanel, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetTopBottom(deckPanel, barHeight, barUnit, 0, Unit.PX);

        // Make the tab bar extremely wide so that tabs themselves never wrap.
        // (Its layout container is overflow:hidden)
        tabBar.getElement().getStyle().setWidth(BIG_ENOUGH_TO_NOT_WRAP, Unit.PX);

        tabBar.setStyleName("gwt-TabLayoutPanelTabs");
        setStyleName("gwt-TabLayoutPanel");
        setTabNavOffset(0);

        previous = new Image(resource.previousTab());
        previous.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                selectPreviousTab();
            }
        });

        next = new Image(resource.nextTab());
        next.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                selectNextTab();
            }
        });
        next.getElement().getStyle().setCursor(Cursor.POINTER);
        previous.getElement().getStyle().setCursor(Cursor.POINTER);

        previousDisabled = new Image(resource.previousTabDisabled());
        nextDisabled = new Image(resource.nextTabDisabled());

        previousButtonPanel.add(previous);
        nextButtonPanel.add(next);
        setNavButtonsVisible(false);
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void add(IsWidget w) {
        add(asWidgetOrNull(w));
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void add(IsWidget w, IsWidget tab) {
        add(asWidgetOrNull(w), asWidgetOrNull(tab));
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void add(IsWidget w, String text) {
        add(asWidgetOrNull(w), text);
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void add(IsWidget w, String text, boolean asHtml) {
        add(asWidgetOrNull(w), text, asHtml);
    }

    @Override
    public void add(Widget w) {
        insert(w, getWidgetCount());
    }

    /**
     * Adds a widget to the panel. If the Widget is already attached, it will be
     * moved to the right-most index.
     * 
     * @param child
     *            the widget to be added
     * @param html
     *            the html to be shown on its tab
     */
    public void add(Widget child, SafeHtml html) {
        add(child, html.asString(), true);
    }

    /**
     * Adds a widget to the panel. If the Widget is already attached, it will be
     * moved to the right-most index.
     * 
     * @param child
     *            the widget to be added
     * @param text
     *            the text to be shown on its tab
     */
    public void add(Widget child, String text) {
        insert(child, text, getWidgetCount());
    }

    /**
     * Adds a widget to the panel. If the Widget is already attached, it will be
     * moved to the right-most index.
     * 
     * @param child
     *            the widget to be added
     * @param text
     *            the text to be shown on its tab
     * @param asHtml
     *            <code>true</code> to treat the specified text as HTML
     */
    public void add(Widget child, String text, boolean asHtml) {
        insert(child, text, asHtml, getWidgetCount());
    }

    /**
     * Adds a widget to the panel. If the Widget is already attached, it will be
     * moved to the right-most index.
     * 
     * @param child
     *            the widget to be added
     * @param tab
     *            the widget to be placed in the associated tab
     */
    public void add(Widget child, Widget tab) {
        insert(child, tab, getWidgetCount());
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public void animate(int duration) {
        animate(duration, null);
    }

    @Override
    public void animate(int duration, AnimationCallback callback) {
        deckPanel.animate(duration, callback);
    }

    @Override
    public void clear() {
        Iterator<Widget> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    @Override
    public void forceLayout() {
        deckPanel.forceLayout();
    }

    /**
     * Get the duration of the animated transition between tabs.
     * 
     * @return the duration in milliseconds
     */
    public int getAnimationDuration() {
        return deckPanel.getAnimationDuration();
    }

    /**
     * Gets the index of the currently-selected tab.
     * 
     * @return the selected index, or <code>-1</code> if none is selected.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Gets the widget in the tab at the given index.
     * 
     * @param index
     *            the index of the tab to be retrieved
     * @return the tab's widget
     */
    public Widget getTabWidget(int index) {
        checkIndex(index);
        return tabs.get(index).getWidget();
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public Widget getTabWidget(IsWidget child) {
        return getTabWidget(asWidgetOrNull(child));
    }

    /**
     * Gets the widget in the tab associated with the given child widget.
     * 
     * @param child
     *            the child whose tab is to be retrieved
     * @return the tab's widget
     */
    public Widget getTabWidget(Widget child) {
        checkChild(child);
        return getTabWidget(getWidgetIndex(child));
    }

    /**
     * Returns the widget at the given index.
     */
    @Override
    public Widget getWidget(int index) {
        return deckPanel.getWidget(index);
    }

    /**
     * Returns the number of tabs and widgets.
     */
    @Override
    public int getWidgetCount() {
        return deckPanel.getWidgetCount();
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    @Override
    public int getWidgetIndex(IsWidget child) {
        return getWidgetIndex(asWidgetOrNull(child));
    }

    /**
     * Returns the index of the given child, or -1 if it is not a child.
     */
    @Override
    public int getWidgetIndex(Widget child) {
        return deckPanel.getWidgetIndex(child);
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void insert(IsWidget child, int beforeIndex) {
        insert(asWidgetOrNull(child), beforeIndex);
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void insert(IsWidget child, IsWidget tab, int beforeIndex) {
        insert(asWidgetOrNull(child), asWidgetOrNull(tab), beforeIndex);
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void insert(IsWidget child, String text, boolean asHtml, int beforeIndex) {
        insert(asWidgetOrNull(child), text, asHtml, beforeIndex);
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void insert(IsWidget child, String text, int beforeIndex) {
        insert(asWidgetOrNull(child), text, beforeIndex);
    }

    /**
     * Inserts a widget into the panel. If the Widget is already attached, it will
     * be moved to the requested index.
     * 
     * @param child
     *            the widget to be added
     * @param beforeIndex
     *            the index before which it will be inserted
     */
    public void insert(Widget child, int beforeIndex) {
        insert(child, "", beforeIndex);
    }

    /**
     * Inserts a widget into the panel. If the Widget is already attached, it will
     * be moved to the requested index.
     * 
     * @param child
     *            the widget to be added
     * @param html
     *            the html to be shown on its tab
     * @param beforeIndex
     *            the index before which it will be inserted
     */
    public void insert(Widget child, SafeHtml html, int beforeIndex) {
        insert(child, html.asString(), true, beforeIndex);
    }

    /**
     * Inserts a widget into the panel. If the Widget is already attached, it will
     * be moved to the requested index.
     * 
     * @param child
     *            the widget to be added
     * @param text
     *            the text to be shown on its tab
     * @param asHtml
     *            <code>true</code> to treat the specified text as HTML
     * @param beforeIndex
     *            the index before which it will be inserted
     */
    public void insert(Widget child, String text, boolean asHtml, int beforeIndex) {
        Widget contents;
        if (asHtml) {
            contents = new HTML(text);
        } else {
            contents = new Label(text);
        }
        insert(child, contents, beforeIndex);
    }

    /**
     * Inserts a widget into the panel. If the Widget is already attached, it will
     * be moved to the requested index.
     * 
     * @param child
     *            the widget to be added
     * @param text
     *            the text to be shown on its tab
     * @param beforeIndex
     *            the index before which it will be inserted
     */
    public void insert(Widget child, String text, int beforeIndex) {
        insert(child, text, false, beforeIndex);
    }

    /**
     * Inserts a widget into the panel. If the Widget is already attached, it will
     * be moved to the requested index.
     * 
     * @param child
     *            the widget to be added
     * @param tab
     *            the widget to be placed in the associated tab
     * @param beforeIndex
     *            the index before which it will be inserted
     */
    public void insert(Widget child, Widget tab, int beforeIndex) {
        insert(child, new Tab(tab), beforeIndex);
    }

    /**
     * Check whether or not transitions slide in vertically or horizontally.
     * Defaults to horizontally.
     * 
     * @return true for vertical transitions, false for horizontal
     */
    public boolean isAnimationVertical() {
        return deckPanel.isAnimationVertical();
    }

    public boolean isNavButtonsVisible() {
        return nextButtonPanel.isVisible() && previousButtonPanel.isVisible();
    }

    @Override
    public Iterator<Widget> iterator() {
        return deckPanel.iterator();
    }

    @Override
    public void onResize() {
        maybeShowButtons();
        super.onResize();
        tabNavPanel.onResize();
        deckPanel.onResize();
        if (lastVisibleTab == tabs.size() - 1 && firstVisibleTab != 0) {

            /*
             * This is ensures that on resize if we're displaying the last tab, that
             * the hidden tabs in the beginning of the list will make themselves
             * visible with the resizing of the resizing of the element containing
             * this TabLayoutPanel.
             */
            int position = getTabEndPosition(lastVisibleTab);
            int width = getOffsetWidth();
            width = width - position;
            width -= getPreviousImage().getOffsetWidth();
            setTabNavOffset(getTabNavOffset() + width);
        } else if (lastVisibleTab == tabs.size() - 1 && firstVisibleTab == 0) {

            // No tabs are display
            setTabNavOffset(0);
        }
    }

    @Override
    public boolean remove(int index) {
        if ((index < 0) || (index >= getWidgetCount())) {
            return false;
        }

        Widget child = getWidget(index);
        tabBar.remove(index);
        deckPanel.removeProtected(child);
        child.removeStyleName(CONTENT_STYLE);

        Tab tab = tabs.remove(index);
        tab.getWidget().removeFromParent();
        maybeShowButtons();

        if (index == selectedIndex) {
            // If the selected tab is being removed, select the first tab (if there
            // is one).
            selectedIndex = -1;
            if (getWidgetCount() > 0) {
                selectTab(0);
            }
        } else if (index < selectedIndex) {
            // If the selectedIndex is greater than the one being removed, it needs
            // to be adjusted.
            --selectedIndex;
        }
        return true;
    }

    @Override
    public boolean remove(Widget w) {
        int index = getWidgetIndex(w);
        if (index == -1) {
            return false;
        }

        return remove(index);
    }

    /**
     * Selects to the next tab. If there isn't a next tab, it does nothing.
     */
    public void selectNextTab() {
        if (!isNavButtonsVisible()) {
            return;
        }
        int index = lastVisibleTab + 1;
        if (index < tabs.size()) {
            addToQueue(QUEUE_NEXT);
        }
    }

    /**
     * Scrolls to the previous tab. If there isn't a previous tab, it does
     * nothing.
     */
    public void selectPreviousTab() {
        if (!isNavButtonsVisible()) {
            return;
        }
        int index = firstVisibleTab - 1;
        if (index >= 0) {
            addToQueue(QUEUE_PREV);
        }
    }

    /**
     * Programmatically selects the specified tab and fires events.
     * 
     * @param index
     *            the index of the tab to be selected
     */
    public void selectTab(int index) {
        selectTab(index, true);
    }

    /**
     * Programmatically selects the specified tab.
     * 
     * @param index
     *            the index of the tab to be selected
     * @param fireEvents
     *            true to fire events, false not to
     */
    public void selectTab(int index, boolean fireEvents) {
        checkIndex(index);
        if (index == selectedIndex) {
            return;
        }

        // Fire the before selection event, giving the recipients a chance to
        // cancel the selection.
        if (fireEvents) {
            BeforeSelectionEvent<Integer> event = BeforeSelectionEvent.fire(this, index);
            if ((event != null) && event.isCanceled()) {
                return;
            }
        }

        // Update the tabs being selected and unselected.
        if (selectedIndex != -1) {
            tabs.get(selectedIndex).setSelected(false);
        }

        deckPanel.showWidget(index);
        tabs.get(index).setSelected(true);
        selectedIndex = index;
        addToQueue(index);

        // Fire the selection event.
        if (fireEvents) {
            SelectionEvent.fire(this, index);
        }
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void selectTab(IsWidget child) {
        selectTab(asWidgetOrNull(child));
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public void selectTab(IsWidget child, boolean fireEvents) {
        selectTab(asWidgetOrNull(child), fireEvents);
    }

    /**
     * Programmatically selects the specified tab and fires events.
     * 
     * @param child
     *            the child whose tab is to be selected
     */
    public void selectTab(Widget child) {
        selectTab(getWidgetIndex(child));
    }

    /**
     * Programmatically selects the specified tab.
     * 
     * @param child
     *            the child whose tab is to be selected
     * @param fireEvents
     *            true to fire events, false not to
     */
    public void selectTab(Widget child, boolean fireEvents) {
        selectTab(getWidgetIndex(child), fireEvents);
    }

    /**
     * Set the duration of the animated transition between tabs.
     * 
     * @param duration
     *            the duration in milliseconds.
     */
    public void setAnimationDuration(int duration) {
        deckPanel.setAnimationDuration(duration);
    }

    /**
     * Set whether or not transitions slide in vertically or horizontally.
     * 
     * @param isVertical
     *            true for vertical transitions, false for horizontal
     */
    public void setAnimationVertical(boolean isVertical) {
        deckPanel.setAnimationVertical(isVertical);
    }

    /**
     * Sets the tab animation duration.
     */
    public void setTabAnimiationDuration(int duration) {
        tabAnimation = duration;
    }

    /**
     * Sets a tab's HTML contents.
     * 
     * @param index
     *            the index of the tab whose HTML is to be set
     * @param html
     *            the tab's new HTML contents
     */
    public void setTabHTML(int index, SafeHtml html) {
        setTabHTML(index, html.asString());
    }

    /**
     * Sets a tab's HTML contents.
     * 
     * Use care when setting an object's HTML; it is an easy way to expose
     * script-based security problems. Consider using {@link #setTabHTML(int, SafeHtml)} or {@link #setTabText(int, String)} whenever possible.
     * 
     * @param index
     *            the index of the tab whose HTML is to be set
     * @param html
     *            the tab's new HTML contents
     */
    public void setTabHTML(int index, String html) {
        checkIndex(index);
        tabs.get(index).setWidget(new HTML(html));
    }

    /**
     * Sets a tab's text contents.
     * 
     * @param index
     *            the index of the tab whose text is to be set
     * @param text
     *            the object's new text
     */
    public void setTabText(int index, String text) {
        checkIndex(index);
        tabs.get(index).setWidget(new Label(text));
    }

    protected void doTabLayout() {
        if (tabAnimation == 0) {
            getTabNavPanel().forceLayout();
        } else {
            getTabNavPanel().animate(tabAnimation);
        }
    }

    protected void ensureTabInView(int tab) {
        assert (tab >= 0 && tab < tabs.size()) : "Index out of bounds";
        if (isTabVisible(tab)) {
            return;
        }

        int positionToMoveTo = 0;
        if (tab < firstVisibleTab) {
            if (tab != 0) {
                positionToMoveTo = getTabNavOffset() - getTabEndPosition(tab - 1) + getPreviousImage().getOffsetWidth();
            } else {
                positionToMoveTo = 0;
            }
        } else {
            int width = getTabBarWidth();
            int tabWidth = getTabEndPosition(tab) - getTabNavOffset();
            positionToMoveTo = width - tabWidth + getPreviousImage().getOffsetWidth();
        }
        setTabNavOffset(positionToMoveTo);
    }

    protected void ensureTabInView(Tab tab) {
        int index = tabs.indexOf(tab);
        if (index == -1) {
            return;
        }
        ensureTabInView(index);
    }

    /**
     * @return the @{Link Image} of the next button in the enabled state
     */
    protected Image getNextActiveImage() {
        return next;
    }

    /**
     * @return the @{Link Image} of the next button in the disabled state
     */
    protected Image getNextDisabledImage() {
        return nextDisabled;
    }

    protected Image getNextImage() {
        return lastVisibleTab == tabs.size() - 1 ? getNextDisabledImage() : getNextActiveImage();
    }

    /**
     * @return the @{Link Image} of the previous button in the enabled state
     */
    protected Image getPreviousActiveImage() {
        return previous;
    }

    /**
     * @return the @{Link Image} of the previous button in the disabled state
     */
    protected Image getPreviousDisabledImage() {
        return previousDisabled;
    }

    protected Image getPreviousImage() {
        return firstVisibleTab == 0 ? getPreviousDisabledImage() : getPreviousActiveImage();
    }

    /**
     * Gets the width of the tab bar.
     * 
     * @return how wide the tab bar is.
     */
    protected int getTabBarWidth() {
        int width = getOffsetWidth();
        width -= getPreviousImage().getOffsetWidth() + getNextImage().getOffsetWidth();

        return width;
    }

    /**
     * Gets the current tab nav margin in pixels. If the tab nav margin is
     * manually set to something other than pixels, it will throw an @{link
     * IllegalArgumentException}
     * 
     * @return the tab nav margin in pixels.
     */
    protected int getTabNavOffset() {
        return tabmargin;
    }

    /**
     * Access to the container of the TabNavigation panel and the container of the
     * previous and next buttons.
     * 
     * @return the panel containing the previous/next buttons and the panel
     *         containing tabs
     */
    protected LayoutPanel getTabNavPanel() {
        return tabNavPanel;
    }

    protected boolean isTabVisible(int tab) {
        if (tab >= firstVisibleTab && tab <= lastVisibleTab) {
            return true;
        }
        return false;
    }

    /**
     * Determines whether or not to display the previous and next buttons.
     * 
     * @return if the buttons are visible or not.
     */
    protected boolean maybeShowButtons() {

        int width = getOffsetWidth();
        if (tabs.size() == 0) {
            return false;
        }

        int tabWidth = 0;
        Tab tab = tabs.get(tabs.size() - 1);

        tabWidth += getTabEndPosition(tab) - getTabNavOffset();

        boolean retValue = false;

        if (!isNavButtonsVisible() && tabWidth > width) {
            setNavButtonsVisible(true);
            retValue = true;
        } else if (isNavButtonsVisible() && tabWidth < width) {
            setNavButtonsVisible(false);
            retValue = false;
        } else {
            retValue = isNavButtonsVisible();
        }
        updateVisibleTabs();
        return retValue;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        /*
         * We have to wait until after the widget is attached before we can figure
         * out what the pixel sizes are.
         */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                maybeShowButtons();
            }
        });
    }

    /**
     * Sets whether or not the Previous and next buttons are visible.
     * 
     * @param visible
     *            whether or not the buttons shoudl be visible
     */
    protected void setNavButtonsVisible(boolean visible) {
        nextButtonPanel.setVisible(visible);
        previousButtonPanel.setVisible(visible);
        if (visible) {
            setTabPanelSizes(getPreviousImage().getWidth(), getNextImage().getWidth());
        } else {
            setTabPanelSizes(0, 0);
        }
    }

    /**
     * Sets the left margin of the tab panel. This is used to allow the scrolling
     * visibility of tabs.
     * 
     * @param margin
     *            is the new margin in pixels.
     */
    protected void setTabNavOffset(int margin) {

        if (margin > 0) {
            margin = 0;
        }
        this.tabmargin = margin;
        if (isRtl()) {
            tabBarAnimator.setWidgetRightWidth(tabBar, margin, Unit.PX, BIG_ENOUGH_TO_NOT_WRAP, Unit.PX);
        } else {
            tabBarAnimator.setWidgetLeftWidth(tabBar, margin, Unit.PX, BIG_ENOUGH_TO_NOT_WRAP, Unit.PX);
        }
        AnimationCallback callback = new AnimationCallback() {

            @Override
            public void onAnimationComplete() {
                isAnimating = false;
                updateVisibleTabs();
                tabBar.getElement().getStyle().clearTop();
                popQueue();
            }

            @Override
            public void onLayout(Layer layer, double progress) {
                /*
                 * Layout panel keeps adding a top element. We want to ensure the tabs
                 * are at the bottom, not the top.
                 */
                tabBar.getElement().getStyle().clearTop();
            }

        };
        tabBarAnimator.animate(tabAnimation, callback);
    }

    private void addToQueue(int tab) {
        tabQueue.add(tab);
        popQueue();
    }

    private void checkChild(Widget child) {
        assert getWidgetIndex(child) >= 0 : "Child is not a part of this panel";
    }

    private void checkIndex(int index) {
        assert (index >= 0) && (index < getWidgetCount()) : "Index out of bounds";
    }

    private int getTabEndPosition(int tab) {
        assert (tab >= 0 && tab < tabs.size()) : "Index out of bounds";
        return getTabEndPosition(tabs.get(tab));
    }

    private int getTabEndPosition(Tab tab) {
        if (isRtl()) {
            return getOffsetWidth() + getAbsoluteLeft() - tab.getAbsoluteLeft();
        } else {
            return tab.getAbsoluteLeft() + tab.getOffsetWidth() - getAbsoluteLeft();
        }
    }

    private void insert(final Widget child, Tab tab, int beforeIndex) {
        assert (beforeIndex >= 0) && (beforeIndex <= getWidgetCount()) : "beforeIndex out of bounds";

        // Check to see if the TabPanel already contains the Widget. If so,
        // remove it and see if we need to shift the position to the left.
        int idx = getWidgetIndex(child);
        if (idx != -1) {
            remove(child);
            if (idx < beforeIndex) {
                beforeIndex--;
            }
        }

        deckPanel.insertProtected(child, beforeIndex);
        tabs.add(beforeIndex, tab);

        tabBar.insert(tab, beforeIndex);
        tab.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectTab(child);
            }
        });

        if (isAttached()) {
            // if it isn't attached, this gets run onAttach();
            maybeShowButtons();
        }
        child.addStyleName(CONTENT_STYLE);

        if (selectedIndex == -1) {
            selectTab(0);
        } else if (selectedIndex >= beforeIndex) {
            // If we inserted before the currently selected tab, its index has just
            // increased.
            selectedIndex++;
        }
    }

    /**
     * Convenience method for {@link LocaleInfo}.getCurrentLocale().isRtl().
     */
    private boolean isRtl() {
        return LocaleInfo.getCurrentLocale().isRTL();
    }

    private void popQueue() {
        if (isAnimating || tabQueue.size() == 0) {
            return;
        }
        isAnimating = true;
        int tab = tabQueue.get(0);
        tabQueue.remove(0);
        if (tab == QUEUE_NEXT) {
            tab = lastVisibleTab + 1;
        } else if (tab == QUEUE_PREV) {
            tab = firstVisibleTab - 1;
        }
        if (tab < 0 || tab >= tabs.size()) {
            // if someone spammed on the next/previous buttons, do this again.
            isAnimating = false;
            popQueue();
            return;
        }
        ensureTabInView(tab);
    }

    /**
     * Updates the size of the tabNavPanel.
     */
    private void setTabPanelSizes(int prev, int next) {
        LayoutPanel panel = getTabNavPanel();
        Unit unit = Unit.PX;
        if (isRtl()) {

            panel.setWidgetRightWidth(previousButtonPanel, 0, unit, prev, unit);
            panel.setWidgetLeftWidth(nextButtonPanel, 0, unit, next, unit);
            panel.setWidgetLeftRight(tabBarAnimator, next, unit, prev, unit);
        } else {
            panel.setWidgetLeftWidth(previousButtonPanel, 0, unit, prev, unit);
            panel.setWidgetRightWidth(nextButtonPanel, 0, unit, next, unit);
            panel.setWidgetLeftRight(tabBarAnimator, prev, unit, next, unit);
        }
        panel.forceLayout();
    }

    /**
     * Changes the images if necessary.
     */
    private void updateImages() {
        previousButtonPanel.clear();
        previousButtonPanel.add(getPreviousImage());

        nextButtonPanel.clear();
        nextButtonPanel.add(getNextImage());
    }

    /**
     * Updates the fistVisibleTab and lastVisibleTab variables and updates images
     * as appropriate.
     */
    private void updateVisibleTabs() {

        if (tabs.size() == 0) {
            return;
        }
        int width = getTabBarWidth();
        int currentPosition = getTabNavOffset();
        boolean foundFirst = false;

        for (int i = 0; i < tabs.size(); i++) {
            if (currentPosition >= 0 && !foundFirst) {
                firstVisibleTab = i;
                foundFirst = true;
            }
            currentPosition = getTabEndPosition(i) - getPreviousImage().getOffsetWidth();
            if (currentPosition <= width) {
                // we're still finding last visible tabs
                lastVisibleTab = i;
            } else {
                // we're outside our visible tab range.
                break;
            }
        }
        updateImages();
    }

}