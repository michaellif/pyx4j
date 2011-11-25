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
package com.propertyvista.crm.client.ui;

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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.site.client.NavigationIDs;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;

import com.propertyvista.crm.client.activity.NavigFolder;

public class NavigViewImpl extends StackLayoutPanel implements NavigView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_Navig";

    private final static double HEADER_SIZE = 3;

    public static enum StyleSuffix implements IStyleName {
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
    }

    @Override
    public void setNavigFolders(List<NavigFolder> folders) {
/*
 * TODO when navigation item structure is finalized review this algorithm again
 * 
 * NOTE: the algorithm needs to be thoroughly tested with different data sets
 * To refresh the stack every time uncomment the lines below
 * 
 * this.clear();
 * lastKnownPlaces = null;
 */
        List<NavigFolderWidget> obsoleteFolders = new ArrayList<NavigFolderWidget>(10);
        if (lastKnownPlaces != null && lastKnownPlaces.size() > 0) {
            for (NavigFolderWidget navigFolderWidget : lastKnownPlaces) {

                //scrolling through known stacks
                String headerTitle = navigFolderWidget.getStackTitle();
                boolean folderFound = false;

                //matching new folders to the existing ones
                for (NavigFolder navigFolder : folders) {
                    if (navigFolder.getTitle().equals(headerTitle)) {//assume that the the stack is found
                        folderFound = true;

                        List<NavigItemAnchor> obsoleteAnchors = new ArrayList<NavigViewImpl.NavigItemAnchor>(10);

                        // now scrolling through the existing content
                        for (NavigItemAnchor anchor : navigFolderWidget.getItems()) {
                            //matching new content to the existing one
                            boolean itemFound = false;
                            AppPlace foundPlace = null;
                            for (AppPlace place : navigFolder.getNavigItems()) {
                                if (anchor.equals(place)) {
                                    itemFound = true;
                                    foundPlace = place;
                                    break;
                                }
                            }

                            if (itemFound) {
                                anchor.update(foundPlace);
                            } else {
                                //existing item is obsolete remove it
                                obsoleteAnchors.add(anchor);
                            }
                        }

                        for (NavigItemAnchor oa : obsoleteAnchors)
                            navigFolderWidget.removeItem(oa);

                        //now the other way around - match old content to the new one to find fresh items
                        for (AppPlace place : navigFolder.getNavigItems()) {
                            boolean itemFound = false;
                            for (NavigItemAnchor anchor : navigFolderWidget.getItems()) {
                                if (anchor.equals(place)) {
                                    itemFound = true;
                                    break;
                                }
                            }
                            if (!itemFound)
                                //brand new item
                                navigFolderWidget.addItem(new NavigItemAnchor(place));
                        }
                        break;

                    }

                }

                if (!folderFound)
                    obsoleteFolders.add(navigFolderWidget);
            }
            //remove obsolete stacks
            for (NavigFolderWidget nw : obsoleteFolders) {
                remove(nw);
                lastKnownPlaces.remove(nw);
            }
            /**
             * now the other way around - add fresh folders
             */
            NavigFolderWidget navigFolderWidget = null;
            for (NavigFolder navigFolder : folders) {
                boolean folderFound = false;
                for (NavigFolderWidget widget : lastKnownPlaces) {
                    if (navigFolder.getTitle().equals(widget.getStackTitle())) {
                        folderFound = true;
                        navigFolderWidget = widget;
                        break;
                    }
                }
                if (folderFound) {
//                    navigFolderWidget.updateItems(navigFolder);
//                    navigFolderWidget = null; // just update content
                } else {
                    navigFolderWidget = new NavigFolderWidget(navigFolder);
                    add(navigFolderWidget, navigFolderWidget.getStackHeaderWidget(), HEADER_SIZE);
                    lastKnownPlaces.add(navigFolderWidget);
                }
            }
            if (navigFolderWidget != null) {
                Widget lastheader = this.getHeaderWidget(navigFolderWidget);
                if (lastheader != null) {//the last stack - remove bottom margin
                    lastheader.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NoBottomMargin);
                }
            }
        } else {
            lastKnownPlaces = new ArrayList<NavigFolderWidget>(10);
            NavigFolderWidget navigFolderWidget = null;
            for (NavigFolder navigFolder : folders) {
                navigFolderWidget = new NavigFolderWidget(navigFolder);
                add(navigFolderWidget, navigFolderWidget.getStackHeaderWidget(), HEADER_SIZE);
                lastKnownPlaces.add(navigFolderWidget);
            }
            if (navigFolderWidget != null) {
                Widget lastheader = this.getHeaderWidget(navigFolderWidget);
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

        private NavigFolder folder;

        private StackHeaderWidget stackHeaderWidget;

        private final List<NavigItemAnchor> items = new ArrayList<NavigItemAnchor>(10);

        private final FlowPanel list = new FlowPanel();

        public NavigFolderWidget(NavigFolder folder) {
            add(list);
            updateItems(folder);
        }

        public void updateItems(NavigFolder folder) {
            this.folder = folder;

            items.clear();
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
                label.ensureDebugId(new CompositeDebugId(NavigationIDs.Navigation_Folder, folder.getTitle()).toString());
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

        private AppPlace place;

        private final Anchor anchor;

        public NavigItemAnchor(AppPlace place) {
            this.place = place;
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Item);
            anchor = new Anchor(presenter.getNavigLabel(place));
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(NavigItemAnchor.this.place);
                }
            });
            anchor.ensureDebugId(new CompositeDebugId(NavigationIDs.Navigation_Item, presenter.getNavigLabel(place)).toString());
            setWidget(anchor);
        }

        public void update(AppPlace place) {
            this.place = place;
            anchor.setText(presenter.getNavigLabel(place));
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
