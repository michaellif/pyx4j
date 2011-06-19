/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.pyx4j.entity.shared.IEntity;

public interface IEditorView<E extends IEntity> extends IView<E> {

    public interface Presenter {

        public void populate();

        public void apply();

        public void save();

        public void cancel();
    }

    void setPresenter(Presenter presenter);

    enum EditMode {
        existingItem, newItem
    }

    void setEditMode(EditMode mode);

    public void populate(E value);

    public E getValue();

    void onApplySuccess();

    void onSaveSuccess();

    // returns TRUE in case of processed event and 
    // no need to re-throw the exception further.
    boolean onSaveFail(Throwable caught);
}
