/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 17, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public class Image extends com.google.gwt.user.client.ui.Image implements HasStyle {

    public Image() {
    }

    public Image(ImageResource resource) {
        super(resource);
    }

    public Image(String url) {
        super(url);
    }

    public Image(SafeUri url) {
        super(url);
    }

    public Image(Element element) {
        super(element);
    }

    public Image(String url, int left, int top, int width, int height) {
        super(url, left, top, width, height);
    }

    public Image(SafeUri url, int left, int top, int width, int height) {
        super(url, left, top, width, height);
    }

}
