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
 * Created on Jun 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;

public class BaseFolderViewerDecorator<E extends IEntity> extends SimplePanel implements FolderViewerDecorator<E> {

    public BaseFolderViewerDecorator() {
    }

    @Override
    public void setFolder(CEntityFolderViewer<?> w) {
        setWidget(w.getContainer());
    }

}
