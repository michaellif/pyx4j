/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 7, 2014
 * @author vlads
 */
package com.pyx4j.entity.shared.utils;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;

public interface EntityBinder<BO extends IEntity, TO extends IEntity> {

    public Class<BO> boClass();

    public Class<TO> toClass();

    public TO createTO(BO bo);

    public void copyBOtoTO(BO bo, TO to);

    public BO createBO(TO to);

    public void copyTOtoBO(TO to, BO bo);

    public Path getBoundBOMemberPath(Path toMemberPath);

}
