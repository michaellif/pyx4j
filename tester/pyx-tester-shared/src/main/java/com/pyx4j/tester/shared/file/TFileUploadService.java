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
 * Created on 2012-12-28
 * @author vlads
 */
package com.pyx4j.tester.shared.file;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.tester.domain.TFileBlob;

public interface TFileUploadService extends UploadService<IEntity, TFileBlob> {

}
