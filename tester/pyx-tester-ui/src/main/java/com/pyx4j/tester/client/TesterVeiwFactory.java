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
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.tester.client.view.CComponentView;
import com.pyx4j.tester.client.view.CComponentViewImpl;
import com.pyx4j.tester.client.view.NavigView;
import com.pyx4j.tester.client.view.NavigViewImpl;
import com.pyx4j.tester.client.view.form.FormBasicView;
import com.pyx4j.tester.client.view.form.FormBasicViewImpl;
import com.pyx4j.tester.client.view.form.FormTesterView;
import com.pyx4j.tester.client.view.form.FormTesterViewImpl;
import com.pyx4j.tester.client.view.form.FormVisibilityView;
import com.pyx4j.tester.client.view.form.FormVisibilityViewImpl;
import com.pyx4j.tester.client.view.form.folder.FolderLayoutView;
import com.pyx4j.tester.client.view.form.folder.FolderLayoutViewImpl;
import com.pyx4j.tester.client.view.form.folder.FolderValidationView;
import com.pyx4j.tester.client.view.form.folder.FolderValidationViewImpl;
import com.pyx4j.tester.client.view.widget.NativeWidgetBasicView;
import com.pyx4j.tester.client.view.widget.NativeWidgetBasicViewImpl;

public class TesterVeiwFactory {

    protected static HashMap<Class<?>, IsWidget> map = new HashMap<Class<?>, IsWidget>();

    public static IsWidget retreive(Class<?> type) {
        if (!map.containsKey(type)) {
            if (FolderLayoutView.class.equals(type)) {
                map.put(type, new FolderLayoutViewImpl());
            } else if (FolderValidationView.class.equals(type)) {
                map.put(type, new FolderValidationViewImpl());
            } else if (NavigView.class.equals(type)) {
                map.put(type, new NavigViewImpl());
            } else if (FormBasicView.class.equals(type)) {
                map.put(type, new FormBasicViewImpl());
            } else if (FormVisibilityView.class.equals(type)) {
                map.put(type, new FormVisibilityViewImpl());
            } else if (CComponentView.class.equals(type)) {
                map.put(type, new CComponentViewImpl());
            } else if (NativeWidgetBasicView.class.equals(type)) {
                map.put(type, new NativeWidgetBasicViewImpl());
            } else if (FormTesterView.class.equals(type)) {
                map.put(type, new FormTesterViewImpl());
            }
        }
        return map.get(type);
    }
}
