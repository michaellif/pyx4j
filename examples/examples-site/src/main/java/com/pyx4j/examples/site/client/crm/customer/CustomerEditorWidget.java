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
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.site.client.InlineWidget;

public class CustomerEditorWidget extends VerticalPanel implements InlineWidget {

    private final CustomerEditorPanel editorPanel;

    private final CustomerEditorMapPanel map;

    public CustomerEditorWidget() {
        editorPanel = new CustomerEditorPanel();
        add(editorPanel);

        map = new CustomerEditorMapPanel();
        add(map);
    }

    @Override
    public void populate(Map<String, String> args) {

        final long customerId = Long.parseLong(args.get("entity_id"));

        AsyncCallback<IEntity> callback = new RecoverableAsyncCallback<IEntity>() {

            public void onSuccess(IEntity result) {
                if (result != null) {
                    editorPanel.populateForm((Customer) result);
                    map.populate((Customer) result);
                }
            }

            public void onFailure(Throwable caught) {
            }
        };

        EntityCriteria<Customer> criteria = EntityCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(IEntity.PRIMARY_KEY, customerId));
        RPCManager.execute(EntityServices.Retrieve.class, criteria, callback);

    }

}
