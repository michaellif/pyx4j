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
 * Created on Sep 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.rpc.customization;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface ICustomizationPersistenceService<E extends IEntity> extends IService {

    void list(AsyncCallback<Vector<String>> callback, E proto);

    void save(AsyncCallback<VoidSerializable> callback, String id, E entity, boolean allowOverwrite);

    void load(AsyncCallback<E> callback, String id, E proto);

    void delete(AsyncCallback<VoidSerializable> callback, String id, E proto);

}
