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
 * Created on Nov 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.site.client.NavigationIDs;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.tester.client.TesterSiteMap;
import com.pyx4j.tester.client.activity.FormTesterActivity;
import com.pyx4j.tester.client.view.form.FormTesterView;

public class NavigViewImpl extends SimplePanel implements NavigView {

    private NavigPresenter presenter;

    public NavigViewImpl() {
        setSize("100%", "100%");
        Tree tree = new Tree();

        TreeItem formRoot = new TreeItem("Form");
        AppPlace place = new TesterSiteMap.FormTester();
        place.arg(FormTesterActivity.FORM_TYPE, FormTesterView.TestFormType.FormBasic.name());
        formRoot.addItem(new TreeItem(new NavigItemAnchor(place, FormTesterView.TestFormType.FormBasic.name())));
        formRoot.setState(true);
        tree.addItem(formRoot);

        TreeItem folderRoot = new TreeItem("Folders");
        folderRoot.addItem(new TreeItem(new NavigItemAnchor(new TesterSiteMap.Folder.FolderValidation(), "E")));
        place = new TesterSiteMap.FormTester();
        place.arg(FormTesterActivity.FORM_TYPE, FormTesterView.TestFormType.FolderLayout.name());
        folderRoot.addItem(new TreeItem(new NavigItemAnchor(place, FormTesterView.TestFormType.FolderLayout.name())));

        tree.addItem(folderRoot);

        TreeItem nativeWidgetRoot = new TreeItem("Native Widgets");
        nativeWidgetRoot.addItem(new TreeItem(new NavigItemAnchor(new TesterSiteMap.NativeWidget.NativeWidgetBasic(), "Native Widgets")));
        nativeWidgetRoot.addItem(new TreeItem(new NavigItemAnchor(new TesterSiteMap.NativeWidget.RichTextEditor(), "Rich Text Editor")));
        tree.addItem(nativeWidgetRoot);

        setWidget(tree);
    }

    @Override
    public void setPresenter(final NavigPresenter presenter) {
        this.presenter = presenter;
    }

    class NavigItemAnchor extends SimplePanel {

        private final AppPlace place;

        public NavigItemAnchor(final AppPlace place, String title) {
            this.place = place;
            //Anchor anchor = new Anchor(place.getPlaceId());
            Anchor anchor = new Anchor(title);
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }

            });
            anchor.ensureDebugId(new CompositeDebugId(NavigationIDs.Navigation_Item, place.getPlaceId()).toString());
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
