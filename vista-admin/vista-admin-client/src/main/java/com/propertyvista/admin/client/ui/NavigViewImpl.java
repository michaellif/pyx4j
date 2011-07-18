/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.admin.client.activity.NavigFolder;

public class NavigViewImpl extends StackLayoutPanel implements NavigView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_Navig";

    private final static double HEADER_SIZE = 3;

    public static enum StyleSuffix implements IStyleSuffix {
        Item, NoBottomMargin
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, selected
    }

    private MainNavigPresenter presenter;

    private List<NavigFolderWidget> lastKnownPlaces = null;

    public NavigViewImpl() {
        super(Unit.EM);
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                GlassPanel.show(GlassStyle.Transparent);
            }
        });
        addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                onSelected(event.getSelectedItem());
                GlassPanel.hide();
            }
        });
    }

    @Override
    public void setPresenter(final MainNavigPresenter presenter) {

        if (this.presenter != null) {
            if (presenter.getClass() != this.presenter.getClass()) {
                clearState(); // CRM <-> Settings navigation switch!.. 
            }
        }

        this.presenter = presenter;

        /**
         * TODO when navigation item structure is finalized review this algorithm again
         * 
         * NOTE: the algorithm needs to be thoroughly tested with different data sets
         * To refresh the stack every time uncomment the lines below
         * 
         * this.clear();
         * lastKnownPlaces = null;
         */

        List<NavigFolder> folders = presenter.getNavigFolders();
        List<NavigFolderWidget> obsoleteFolders = new ArrayList<NavigFolderWidget>(5);
        if (lastKnownPlaces != null && lastKnownPlaces.size() > 0) {
            for (NavigFolderWidget nw : lastKnownPlaces) {

                //scrolling through known stacks
                String headerTitle = nw.getStackTitle();
                boolean folderFound = false;

                //matching new folders to the existing ones
                for (NavigFolder navigFolder : folders) {
                    if (navigFolder.getTitle().equals(headerTitle)) {//assume that the the stack is found
                        folderFound = true;

                        List<NavigItemAnchor> obsoleteAnchors = new ArrayList<NavigViewImpl.NavigItemAnchor>(10);

                        //now scrolling through the existing content
                        for (NavigItemAnchor anchor : nw.getItems()) {
                            //matching new content to the existing one
                            boolean itemFound = false;
                            for (AppPlace place : navigFolder.getNavigItems()) {
                                if (anchor.equals(place)) {
                                    itemFound = true;
                                    break;
                                }
                            }
                            if (!itemFound)
                                //existing item is obsolete remove it
                                obsoleteAnchors.add(anchor);
                        }
                        for (NavigItemAnchor oa : obsoleteAnchors)
                            nw.removeItem(oa);

                        //now the other way around - match old content to the new one to find fresh items
                        for (AppPlace place : navigFolder.getNavigItems()) {
                            boolean itemFound = false;
                            for (NavigItemAnchor anchor : nw.getItems()) {
                                if (anchor.equals(place)) {
                                    itemFound = true;
                                    break;
                                }
                            }
                            if (!itemFound)
                                //brand new item
                                nw.addItem(new NavigItemAnchor(place));
                        }
                        break;

                    }

                }
                if (!folderFound)
                    obsoleteFolders.add(nw);
            }
            //remove obsolete stacks
            for (NavigFolderWidget nw : obsoleteFolders) {
                remove(nw);
                lastKnownPlaces.remove(nw);
            }
            /**
             * now the other way around - add fresh folders
             */
            NavigFolderWidget nw = null;
            for (NavigFolder navigFolder : folders) {
                boolean folderFound = false;
                for (NavigFolderWidget widget : lastKnownPlaces) {
                    if (navigFolder.getTitle().equals(widget.getStackTitle())) {
                        folderFound = true;
                        break;
                    }
                }
                if (!folderFound) {
                    nw = new NavigFolderWidget(navigFolder);
                    add(nw, nw.getStackHeaderWidget(), HEADER_SIZE);
                    lastKnownPlaces.add(nw);
                }

            }
            if (nw != null) {
                Widget lastheader = this.getHeaderWidget(nw);
                if (lastheader != null) {//the last stack - remove bottom margin
                    lastheader.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NoBottomMargin);
                }
            }
        } else {
            lastKnownPlaces = new ArrayList<NavigFolderWidget>(10);
            NavigFolderWidget nw = null;
            for (NavigFolder navigFolder : folders) {
                nw = new NavigFolderWidget(navigFolder);
                add(nw, nw.getStackHeaderWidget(), HEADER_SIZE);
                lastKnownPlaces.add(nw);
            }
            if (nw != null) {
                Widget lastheader = this.getHeaderWidget(nw);
                if (lastheader != null) {//the last stack - remove bottom margin
                    lastheader.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NoBottomMargin);
                }
            }
        }
    }

    private void onSelected(int index) {
        for (int i = 0; i < getWidgetCount(); ++i) {
            Widget w = getWidget(i);
            if (w instanceof NavigFolderWidget) {
                ((NavigFolderWidget) w).setSelected(index == i);
            }
        }
    }

    private void clearState() {
        this.clear();
        lastKnownPlaces = null;
    }

    //
    //  Folder/Item classes:
    //

    class NavigFolderWidget extends ScrollPanel {

        private final NavigFolder folder;

        private final List<NavigItemAnchor> items;

        private final FlowPanel list;

        private final StackHeaderWidget stackHeaderWidget;

        public NavigFolderWidget(NavigFolder folder) {
            this.folder = folder;

            add(list = new FlowPanel());
            items = new ArrayList<NavigItemAnchor>(10);
            for (AppPlace place : folder.getNavigItems()) {
                addItem(new NavigItemAnchor(place));
            }

            stackHeaderWidget = new StackHeaderWidget();

        }

        public void addItem(NavigItemAnchor item) {
            if (item != null) {
                items.add(item);
                list.add(item);
            }

        }

        public void removeItem(NavigItemAnchor item) {
            if (item != null) {
                for (NavigItemAnchor a : items) {
                    if (a.equals(item)) {
                        items.remove(item);
                        list.remove(item);
                        break;
                    }
                }
            }
        }

        public List<NavigItemAnchor> getItems() {
            return items;
        }

        // TODO implement better algorithm when NavigFolder is finalized
        @Override
        public boolean equals(Object obj) {
            return (getStackTitle() != null ? getStackTitle().equals(obj) : false);
        }

        //
        //  UI stuff:
        //

        public String getStackTitle() {
            return folder.getTitle();
        }

        public Widget getStackHeaderWidget() {
            return stackHeaderWidget;
        }

        public void setSelected(boolean selected) {
            stackHeaderWidget.setSelected(selected);
        }

        private class StackHeaderWidget extends HorizontalPanel {

            private Image image = null;

            private boolean selected = false;

            private StackHeaderWidget() {

                if (folder.getImageNormal() != null) {
                    image = new Image(folder.getImageNormal());

                    addHandler(new MouseOverHandler() {
                        @Override
                        public void onMouseOver(MouseOverEvent event) {
                            if (!selected) {
                                image.setResource(folder.getImageHover());
                            }
                        }
                    }, MouseOverEvent.getType());
                    addHandler(new MouseOutHandler() {
                        @Override
                        public void onMouseOut(MouseOutEvent event) {
                            if (!selected) {
                                image.setResource(folder.getImageNormal());
                            }
                        }
                    }, MouseOutEvent.getType());

                    image.getElement().getStyle().setMarginTop(0.2, Unit.EM);
                    image.getElement().getStyle().setMarginRight(0.5, Unit.EM);
                    add(image);
                    setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
                }

                Label label = new Label(folder.getTitle());
                add(label);
                setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
                setCellWidth(label, "100%");

                setWidth("100%");
                setHeight("100%");//VS to align header content in the middle
                //  setHeight(HEADER_SIZE + "em");
            }

            private void setSelected(boolean selected) {
                this.selected = selected;

                if (selected) {
                    addStyleDependentName(StyleDependent.selected.name());
                } else {
                    removeStyleDependentName(StyleDependent.selected.name());
                }

                if (image != null) {
                    image.setResource(selected ? folder.getImageActive() : folder.getImageNormal());
                }
            }
        }
    }

    class NavigItemAnchor extends SimplePanel {

        private final AppPlace place;

        public NavigItemAnchor(final AppPlace place) {
            this.place = place;
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Item);
            Anchor anchor = new Anchor(presenter.getNavigLabel(place));
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }
            });
            setWidget(anchor);
        }

        @Override
        public boolean equals(Object obj) {
            return (place != null ? place.equals(obj) : false);
        }

        @Override
        public int hashCode() {
            return (place != null ? place.hashCode() : 0);
        }
    }
}
