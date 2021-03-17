package com.gms.gms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gms.common.exception.GmsException;
import com.gms.gms.domain.AppliedSubject;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author MrBird
 */
public interface AppliedSubjectService extends IService<AppliedSubject> {

    void submitAppliance(AppliedSubject appliedSubject);

    void auditAppliance(AppliedSubject appliedSubject);


    IPage<AppliedSubject> selectWithCondition(AppliedSubject appliedSubject, String roleName) throws GmsException;

    void addAppliedSubject(AppliedSubject appliedSubject);

    void auditAppliedSubject(AppliedSubject appliedSubject);
}
