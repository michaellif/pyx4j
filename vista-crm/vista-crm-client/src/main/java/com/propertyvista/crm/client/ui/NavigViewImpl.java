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

    public NavigViewImpl() {
        super(Unit.EM);
        setStyleName(DEFAULT_STYLE_PREFIX);
        setHeight("100%");
    }

    @Override
    public void setPresenter(final MainNavigPresenter presenter) {
        this.presenter = presenter;

/*
 * for (Iterator<Widget> it = this.iterator(); it.hasNext();) {
 * Widget w = it.next();
 * System.out.println(w.toString());
 * 
 * }
 */
        //TODO Clean for now. Implement comparison later
        this.clear();

        List<NavigFolder> folders = presenter.getNavigFolders();
        ScrollPanel scroll = null;
        for (NavigFolder navigFolder : folders) {
            scroll = new ScrollPanel();

            FlowPanel list = new FlowPanel();

            for (final AppPlace place : navigFolder.getNavigItems()) {
                SimplePanel line = new SimplePanel();
                //VS to add spacing
                line.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Item);
                Anchor anchor = new Anchor(presenter.getNavigLabel(place));
                anchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.navigTo(place);
                    }
                });
                line.setWidget(anchor);
                list.add(line);
            }

            scroll.setWidget(list);
            this.add(scroll, navigFolder.getTitle(), 3);
        }

        Widget lastheader = this.getHeaderWidget(scroll);

        if (lastheader != null) {//the last stack.Remove bottom margin
            lastheader.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NoBottomMargin);
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
            // TODO Auto-generated method stub
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return super.hashCode();
        }
    }
}
