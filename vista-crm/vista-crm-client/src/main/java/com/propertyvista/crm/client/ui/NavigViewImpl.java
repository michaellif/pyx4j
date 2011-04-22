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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.crm.client.activity.NavigFolder;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class NavigViewImpl extends StackLayoutPanel implements NavigView {

    public static String DEFAULT_STYLE_PREFIX = "vistaCrm_Navig";

    public static enum StyleSuffix implements IStyleSuffix {
        Item, NoBottomMargin
    }

    public static enum StyleDependent implements IStyleDependent {
        hover
    }

    private MainNavigPresenter presenter;

    private List<NavigFolderWidget> lastKnownPlaces;

    public NavigViewImpl() {
        super(Unit.EM);
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");
        lastKnownPlaces = null;
    }

    @Override
    public void setPresenter(final MainNavigPresenter presenter) {
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
                    nw = new NavigFolderWidget(navigFolder.getTitle());
                    for (final AppPlace place : navigFolder.getNavigItems()) {
                        nw.addItem(new NavigItemAnchor(place));
                        add(nw, nw.getStackTitle(), 3);
                    }
                    lastKnownPlaces.add(nw);
                }

            }
            if (nw != null) {
                Widget lastheader = this.getHeaderWidget(nw);
                if (lastheader != null) {//the last stack - remove bottom margin
                    lastheader.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NoBottomMargin);
                }
            }

            //test
/*
 * for (NavigFolderWidget w : lastKnownPlaces) {
 * System.out.println(w.getStackTitle());
 * for (NavigItemAnchor a : w.getItems()) {
 * System.out.println("     " + a.getElement().toString());
 * }
 * }
 */

        } else {
            lastKnownPlaces = new ArrayList<NavigFolderWidget>(10);
            NavigFolderWidget nw = null;
            for (NavigFolder navigFolder : folders) {
                nw = new NavigFolderWidget(navigFolder.getTitle());
                for (final AppPlace place : navigFolder.getNavigItems()) {
                    nw.addItem(new NavigItemAnchor(place));
                    add(nw, nw.getStackTitle(), 3);
                }
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

    class NavigFolderWidget extends ScrollPanel {
        private final List<NavigItemAnchor> items;

        private final String stackTitle;

        private final FlowPanel list;

        public NavigFolderWidget(String title) {
            this.stackTitle = title;
            list = new FlowPanel();
            add(list);
            items = new ArrayList<NavigItemAnchor>(10);
        }

        public void addItem(NavigItemAnchor item) {
            if (item != null) {
                items.add(item);
                list.add(item);
            }

        }

        public void removeItem(NavigItemAnchor item) {
            if (item == null)
                return;
            for (NavigItemAnchor a : items) {
                if (a.equals(item)) {
                    items.remove(item);
                    list.remove(item);
                    break;
                }
            }

        }

        public String getStackTitle() {
            return stackTitle;
        }

        public List<NavigItemAnchor> getItems() {
            return items;
        }

        /**
         * TODO implement better algorithm when NavigFolder is finalized
         */
        @Override
        public boolean equals(Object obj) {
            if (stackTitle == null)
                return false;
            return stackTitle.equals(obj);
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
            if (place == null)
                return false;
            return place.equals(obj);
        }

        @Override
        public int hashCode() {
            if (place == null)
                return 0;
            return place.hashCode();
        }
    }
}
