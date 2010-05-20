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
 * Created on May 20, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.photoalbum;

public class Photo {

    private final String thumbnailUrl;

    private final String photoUrl;

    private String caption;

    public Photo(String thumbnailUrl, String photoUrl, String caption) {
        super();
        this.thumbnailUrl = thumbnailUrl;
        this.photoUrl = photoUrl;
        this.caption = caption;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}