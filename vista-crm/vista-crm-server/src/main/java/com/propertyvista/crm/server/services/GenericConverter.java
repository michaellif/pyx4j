/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Vector;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

/**
 * Converts Data Base Object (DBO) to Data Transfer Ones (DTO) and vice versa.
 * Applies to SerachCriteria and it's SearchResult also (one direction)
 * Note: it's supposed that DTO extends DBO!..
 */

//TODO move to more generic place?
public class GenericConverter {

    public interface EnhanceDTO<DBO extends IEntity, DTO extends DBO> {

        void enhanceDTO(DBO in, DTO dto);

    }

    public static <DBO extends IEntity, DTO extends DBO> DBO convertDTO2DBO(DTO src, Class<DBO> dstClass) {
        DBO dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }

    public static <DBO extends IEntity, DTO extends DBO> DTO convertDBO2DTO(DBO src, Class<DTO> dstClass) {
        DTO dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }

    public static <DTO extends IEntity> EntitySearchCriteria<DTO> convertDTO2DBO(EntitySearchCriteria<? extends DTO> src, Class<DTO> dstClass) {
        EntitySearchCriteria<DTO> dst = EntitySearchCriteria.create(dstClass);
        dst.setPageNumber(src.getPageNumber());
        dst.setPageSize(src.getPageSize());
        return dst;
    }

    public static <DBO extends IEntity, DTO extends DBO> EntitySearchResult<DTO> convertDBO2DTO(EntitySearchResult<DBO> resultE, Class<DTO> dstClass,
            EnhanceDTO<DBO, DTO> enhanceDTO) {
        EntitySearchResult<DTO> result = new EntitySearchResult<DTO>();
        result.setEncodedCursorReference(resultE.getEncodedCursorReference());
        result.hasMoreData(resultE.hasMoreData());
        Vector<DTO> data = new Vector<DTO>();
        for (DBO entity : resultE.getData()) {
            DTO dto = convertDBO2DTO(entity, dstClass);
            enhanceDTO.enhanceDTO(entity, dto);
            data.add(dto);
        }
        result.setData(data);
        return result;
    }
}
