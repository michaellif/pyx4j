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
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.photoalbum;

import java.util.ArrayList;
import java.util.List;

public class BasicPhotoAlbumModel implements PhotoAlbumModel {

    private final ArrayList<Photo> photoList = new ArrayList<Photo>();

    private PhotoAlbum photoAlbum;

    public BasicPhotoAlbumModel() {
    }

    public void setPhotoAlbum(PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
    }

    public void addPhoto(Photo photo) {
        photoList.add(photo);
        photoAlbum.onPhotoAdded(photo, photoList.size() - 1);
    }

    public void removePhoto(int index) {
        photoList.remove(index);
        photoAlbum.onPhotoRemoved(index);
    }

    public void updateCaption(int index, String caption) {
        photoList.get(index).setCaption(caption);
        photoAlbum.onCaptionUpdated(caption, index);
    }

    @Override
    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void clear() {
        photoList.clear();
        photoAlbum.onClear();

    }

}
