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
 * Created on 2011-05-17
 * @author Vlad
 */
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView.IPrimeViewerPresenter;

public interface IPrimeViewerView<E extends IEntity> extends IPrimeFormView<E, IPrimeViewerPresenter> {

    public interface IPrimeViewerPresenter extends IPrimeFormView.IPrimeFormPresenter {

        boolean canEdit();

        void edit();

        void cancel();

        void view(Key entityId);

        void approveFinal();

        void duplicate();
    }

}
