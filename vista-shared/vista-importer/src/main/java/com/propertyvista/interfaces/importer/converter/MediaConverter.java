/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.media.Media;
import com.propertyvista.interfaces.importer.model.MediaIO;

public class MediaConverter extends EntityDtoBinder<Media, MediaIO> {

    public MediaConverter() {
        super(Media.class, MediaIO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.caption(), dboProto.file().caption());
    }

    @Override
    public void copyDBOtoDTO(Media dbo, MediaIO dto) {
        super.copyDBOtoDTO(dbo, dto);
        switch (dbo.type().getValue()) {
        case file:
            dto.uri().setValue("images/" + dbo.file().blobKey().getStringView() + ".png");
            dto.mediaType().setValue(MediaIO.MediaType.file);
            break;
        default:
            throw new Error("TODO");
        }
    }

    @Override
    public void copyDTOtoDBO(MediaIO dto, Media dbo) {
        super.copyDTOtoDBO(dto, dbo);
        //TODO
    }

}
