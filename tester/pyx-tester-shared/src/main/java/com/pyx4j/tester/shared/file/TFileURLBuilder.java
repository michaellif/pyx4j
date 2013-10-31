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
 * Created on 2012-12-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.shared.file;

import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.gwt.shared.IFileURLBuilder;
import com.pyx4j.tester.domain.TFile;

public abstract class TFileURLBuilder implements IFileURLBuilder<TFile> {

    public static final Dimension THUMBNAIL_SMALL = new Dimension(70, 50);

    //see web.xml for now
    public static String servletMapping = "file-view/";

    private final boolean thumbnail;

    public TFileURLBuilder(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String getUrl(TFile image) {
        if (image.id().isNull()) {
            return servletMapping + (thumbnail ? "t" : "") + "u" + image.accessKey().getStringView() + "/" + image.fileName().getStringView();
        } else {
            return servletMapping + (thumbnail ? "t" : "") + image.id().getStringView() + "/" + image.fileName().getStringView();
        }
    }
}
