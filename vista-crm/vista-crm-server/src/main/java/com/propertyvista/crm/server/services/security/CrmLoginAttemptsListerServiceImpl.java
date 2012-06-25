/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.security;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.security.AuditRecord;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.account.LoginAttemptDTO;
import com.propertyvista.crm.rpc.dto.account.LoginAttemptDTO.LoginOutcome;
import com.propertyvista.crm.rpc.services.security.CrmLoginAttemptsListerService;
import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.jobs.TaskRunner;

public class CrmLoginAttemptsListerServiceImpl implements CrmLoginAttemptsListerService {

    private static final I18n i18n = I18n.get(CrmLoginAttemptsListerServiceImpl.class);

    private final EntityDtoBinder<AuditRecord, LoginAttemptDTO> entityDtoEinder;

    private final EntityDto2DboCriteriaConverter<AuditRecord, LoginAttemptDTO> criteriaConverter;

    private final Path userKeyPath;

    private final Path dstUserKeyPath;

    public CrmLoginAttemptsListerServiceImpl() {
        entityDtoEinder = new EntityDtoBinder<AuditRecord, LoginAttemptDTO>(AuditRecord.class, LoginAttemptDTO.class) {
            @Override
            protected void bind() {
                bind(dtoProto.time(), dboProto.created());
                bind(dtoProto.remoteAddress(), dboProto.remoteAddr());
            }
        };

        AuditRecord dboProto = EntityFactory.getEntityPrototype(AuditRecord.class);
        LoginAttemptDTO dtoProto = EntityFactory.getEntityPrototype(LoginAttemptDTO.class);

        userKeyPath = dtoProto.userKey().getPath();
        dstUserKeyPath = dboProto.user().getPath();

        final Path outcomePath = dtoProto.outcome().getPath();
        final Path eventPath = dboProto.event().getPath();

        criteriaConverter = new EntityDto2DboCriteriaConverter<AuditRecord, LoginAttemptDTO>(AuditRecord.class, LoginAttemptDTO.class,
                EntityDto2DboCriteriaConverter.makeMapper(entityDtoEinder), new EntityDto2DboCriteriaConverter.PropertyMapper() {
                    @Override
                    public Path getDboMemberPath(Path dtoMemberPath) {
                        if (dtoMemberPath.equals(userKeyPath)) {
                            return dstUserKeyPath;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public Serializable convertValue(Serializable value) {
                        return value;
                    }
                }, new EntityDto2DboCriteriaConverter.PropertyMapper() {

                    @Override
                    public Path getDboMemberPath(Path dtoMemberPath) {
                        if (dtoMemberPath.equals(outcomePath)) {
                            return eventPath;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public Serializable convertValue(Serializable value) {
                        if (value == null) {
                            return null;
                        } else if (value instanceof LoginAttemptDTO.LoginOutcome) {
                            if (value.equals(LoginAttemptDTO.LoginOutcome.success)) {
                                return AuditRecord.EventType.Login;
                            } else if (value.equals(LoginAttemptDTO.LoginOutcome.failed)) {
                                return AuditRecord.EventType.LoginFailed;
                            } else {
                                throw new IllegalArgumentException("don't know how to convert " + value.toString());
                            }
                        } else {
                            throw new IllegalArgumentException("wrong parameter type in criterion, expected '"
                                    + LoginAttemptDTO.LoginOutcome.class.getCanonicalName() + "', but got '" + value.getClass().getCanonicalName() + "'");
                        }
                    }
                });

    }

    @Override
    public void list(final AsyncCallback<EntitySearchResult<LoginAttemptDTO>> callback, final EntityListCriteria<LoginAttemptDTO> dtoCriteria) {
        final Key dtoCriteriaUserKey = extractUserKey(dtoCriteria);

        if (!(VistaContext.getCurrentUserPrimaryKey() != dtoCriteriaUserKey | SecurityController.checkBehavior(VistaCrmBehavior.Organization))) {
            throw new UserRuntimeException(i18n.tr("Access denied"));
        }

        final Pmc currentPmc = VistaDeployment.getCurrentPmc();

        EntitySearchResult<LoginAttemptDTO> result = TaskRunner.runInAdminNamespace(new Callable<EntitySearchResult<LoginAttemptDTO>>() {
            @Override
            public EntitySearchResult<LoginAttemptDTO> call() throws Exception {
                EntityListCriteria<AuditRecord> criteria = EntityListCriteria.create(AuditRecord.class);
                criteria.setPageNumber(dtoCriteria.getPageNumber());
                criteria.setPageSize(dtoCriteria.getPageSize());
                criteria.addAll(criteriaConverter.convertDTOSearchCriteria(dtoCriteria.getFilters()));
                criteria.add(PropertyCriterion.eq(criteria.proto().user(), dtoCriteriaUserKey));
                criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), currentPmc.namespace()));

                criteria.setSorts(criteriaConverter.convertDTOSortingCriteria(dtoCriteria.getSorts()));

                EntitySearchResult<AuditRecord> dboResult = Persistence.secureQuery(criteria);
                EntitySearchResult<LoginAttemptDTO> result = new EntitySearchResult<LoginAttemptDTO>();

                result.setData(new Vector<LoginAttemptDTO>(convertToDto(Persistence.service().query(criteria))));
                result.setTotalRows(dboResult.getTotalRows());
                result.hasMoreData(dboResult.hasMoreData());
                result.setEncodedCursorReference(dboResult.getEncodedCursorReference());
                return result;
            }
        });
        callback.onSuccess(result);

    }

    private Key extractUserKey(EntityListCriteria<LoginAttemptDTO> dtoCriteria) {
        if (dtoCriteria.getFilters() != null) {
            Iterator<Criterion> i = dtoCriteria.getFilters().iterator();
            while (i.hasNext()) {
                Criterion c = i.next();
                if (c instanceof PropertyCriterion) {
                    PropertyCriterion pc = (PropertyCriterion) c;
                    if (pc.getPropertyPath().equals(userKeyPath.toString())) {
                        i.remove();
                        return (Key) pc.getValue();
                    }
                }
            }
        }
        throw new Error("user key criterion was not found");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("delete not implemented");
    }

    private List<LoginAttemptDTO> convertToDto(List<AuditRecord> records) {
        List<LoginAttemptDTO> attempts = new LinkedList<LoginAttemptDTO>();
        for (AuditRecord auditRecord : records) {
            attempts.add(convertToDto(auditRecord));
        }
        return attempts;
    }

    private LoginAttemptDTO convertToDto(AuditRecord record) {
        LoginAttemptDTO dto = entityDtoEinder.createDTO(record);
        LoginAttemptDTO.LoginOutcome outcome = null;
        switch (record.event().getValue()) {
        case Login:
            outcome = LoginOutcome.success;
            break;
        case LoginFailed:
            outcome = LoginOutcome.failed;
            break;
        default:
            throw new IllegalStateException("failed to convert event type '" + record.event().getValue().name() + "' to a valid login outcome");
        }
        dto.outcome().setValue(outcome);
        return dto;
    }
}
