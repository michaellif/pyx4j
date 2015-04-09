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
 */
package com.pyx4j.tester.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.tester.client.TesterSiteMap;
import com.pyx4j.tester.client.activity.AddressEditorActivity;
import com.pyx4j.tester.client.activity.FolderLayoutActivity;
import com.pyx4j.tester.client.activity.FolderValidationActivity;
import com.pyx4j.tester.client.activity.FormTesterActivity;
import com.pyx4j.tester.client.activity.ListerActivity;
import com.pyx4j.tester.client.activity.NativeWidgetBasicActivity;
import com.pyx4j.tester.client.activity.RichTextEditorActivity;

public class MainActivityMapper implements AppActivityMapper {

    public MainActivityMapper() {
    }

    @Override
    public void obtainActivity(final AppPlace place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof TesterSiteMap.Folder.FolderLayout) {
                    activity = new FolderLayoutActivity(place);
                } else if (place instanceof TesterSiteMap.Folder.FolderValidation) {
                    activity = new FolderValidationActivity(place);
                } else if (place instanceof TesterSiteMap.NativeWidget.NativeWidgetBasic) {
                    activity = new NativeWidgetBasicActivity(place);
                } else if (place instanceof TesterSiteMap.NativeWidget.RichTextEditor) {
                    activity = new RichTextEditorActivity(place);
                } else if (place instanceof TesterSiteMap.NativeWidget.AddressEditor) {
                    activity = new AddressEditorActivity(place);
                } else if (place instanceof TesterSiteMap.NativeWidget.Lister) {
                    activity = new ListerActivity(place);
                } else if (place instanceof TesterSiteMap.FormTester) {
                    activity = new FormTesterActivity(place);
                }

                callback.onSuccess(activity);
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });

    }

}
