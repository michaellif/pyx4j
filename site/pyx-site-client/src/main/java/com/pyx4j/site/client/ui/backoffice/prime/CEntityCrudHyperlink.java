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
 * Created on Sep 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client.ui.backoffice.prime;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

public class CEntityCrudHyperlink<E extends IEntity> extends CEntityHyperlink<E> {

    public CEntityCrudHyperlink(final CrudAppPlace place) {
        super();
        setNavigationCommand(new Command() {
            @Override
            public void execute() {
                if (getValue().getPrimaryKey() != null) {
                    assert (place != null);
                    AppSite.getPlaceController().goTo(place.formViewerPlace(getValue().getPrimaryKey()));
                }
            }
        });

    }

}
