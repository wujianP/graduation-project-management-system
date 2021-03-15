package com.gms.gms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gms.gms.domain.AppliedSubject;
import com.gms.gms.domain.Subject;
import com.gms.gms.dao.SubjectMapper;
import com.gms.gms.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MrBird
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public List<Subject> getSubject() {
        return this.baseMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public void deleteSubject(String subId) {
        this.baseMapper.deleteById(subId);
    }

    @Override
    public void giveOpinion(String docId, Boolean isPassed, String feedback) {
        this.baseMapper.giveOpinion(docId,isPassed,feedback);
    }

    @Override
    public  List<Subject> trackSubject(String subId) {
        LambdaQueryWrapper<Subject> map=new LambdaQueryWrapper<>();
        map.eq(Subject::getSubId, subId);
        return this.baseMapper.selectList(map);
    }

    @Override
    public IPage<AppliedSubject> selectWithCondition() {
        Page<AppliedSubject> page = new Page<>(0, 5);//todo 增加分页
        Page<AppliedSubject> returnSubjectPage = this.baseMapper.selectWithCondition(page);
        return returnSubjectPage;
        //todo

    }
}
