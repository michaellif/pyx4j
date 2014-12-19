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
 */
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView.IPrimeEditorPresenter;

public interface IPrimeEditorView<E extends IEntity> extends IPrimeFormView<E, IPrimeEditorPresenter> {

    public interface IPrimeEditorPresenter extends IPrimeFormView.IPrimeFormPresenter {

        void apply();

        void save();

        void cancel();
    }

    enum EditMode {
        existingItem, newItem
    }

    /**
     * Notifies view about supposed editing mode - view can select appropriate from here.
     * 
     * @param mode
     */
    void setEditMode(EditMode mode);

    public E getValue();

    public boolean isDirty();

    /**
     * @param caught
     * 
     * @return may return TRUE in case of processed event and no need to re-throw the exception further.
     *         FALSE - re-throws the exception (new UnrecoverableClientError(caught)).
     */
    boolean onSaveFail(Throwable caught);
}
