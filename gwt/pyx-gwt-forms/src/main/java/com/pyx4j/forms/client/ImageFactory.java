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
 * Created on Dec 25, 2009
 * @author Michael
 */
package com.pyx4j.forms.client;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.images.FormImages;

public class ImageFactory {

    public interface FormsImageBundle extends FormImages {
    }

    private static FormsImageBundle bundle;

    public static FormsImageBundle getImages() {
        if (bundle == null) {
            bundle = GWT.create(FormsImageBundle.class);
        }
        return bundle;
    }

    public static void setImageBundle(FormsImageBundle bundle) {
        ImageFactory.bundle = bundle;
    }

}
