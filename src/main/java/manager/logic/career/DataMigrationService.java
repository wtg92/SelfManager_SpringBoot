package manager.logic.career;

import com.alibaba.fastjson2.JSON;
import manager.dao.UserDAO;
import manager.dao.career.WorkDAO;
import manager.entity.general.SMGeneralEntity;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.entity.general.career.*;
import manager.logic.UserLogic;
import manager.system.SMDB;
import manager.system.SMPerm;
import manager.util.CommonUtil;
import manager.util.DBUtil;
import manager.util.RefiningUtil;
import manager.util.ZonedTimeUtils;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class DataMigrationService {
    @Resource
    UserLogic uL;

    @Resource
    private SessionFactory sessionFactory;

    Map<String,Object> migration = new ConcurrentHashMap<>();

    /**
     * 我的数据库里 一定给了一个default值吧
     * @param loginId
     */
    public synchronized void doFullMigrateOfV1(long loginId){
        uL.checkPerm(loginId, SMPerm.CREATE_NOTE_BOOK_AND_NOTE);

        migration.put("startTime",System.currentTimeMillis());

        doGeneralMigration(Memo.class,Memo::getCreateTime);
        doGeneralMigration(Plan.class,Plan::getCreateTime);
        doGeneralMigration(PlanDept.class,PlanDept::getCreateTime);
        doGeneralMigration(User.class,User::getCreateTime);
        doGeneralMigration(UserGroup.class,UserGroup::getCreateTime);
        doGeneralMigration(WorkSheet.class,WorkSheet::getCreateTime);

        /**
         * Plan的默认时区
         */
        {
            Map<String,Object> equals = new HashMap<>();
            String filed = SMDB.F_TIMEZONE;
            equals.put(filed,null);
            Class<Plan> cla = Plan.class;
            String identity = CommonUtil.getEntityTableName(cla)+"_"+filed;
            Long start = System.currentTimeMillis();
            List<Plan> data = DBUtil.selectEntitiesByTerms(cla,null,equals,null,null,sessionFactory);
            migration.put(identity+" size:",data.size());
            data.forEach(one->{
                one.setTimezone(RefiningUtil.getDefaultTimeZone());
                DBUtil.updateExistedEntity(one,sessionFactory);
            });
            Long end = System.currentTimeMillis();
            migration.put(identity+" lasting:",(end-start)/1000+" s");
        }

        /**
         * Worksheet 默认时区
         */
        {
            Map<String,Object> equals = new HashMap<>();
            String filed = SMDB.F_TIMEZONE;
            equals.put(filed,null);
            Class<WorkSheet> cla = WorkSheet.class;
            String identity = CommonUtil.getEntityTableName(cla)+"_"+filed;
            Long start = System.currentTimeMillis();
            List<WorkSheet> data = DBUtil.selectEntitiesByTerms(cla,null,equals,null,null,sessionFactory);
            migration.put(identity+" size:",data.size());
            data.forEach(one->{
                one.setTimezone(RefiningUtil.getDefaultTimeZone());
                DBUtil.updateExistedEntity(one,sessionFactory);
            });
            Long end = System.currentTimeMillis();
            migration.put(identity+" lasting:",(end-start)/1000+" s");
        }

        /**
         * Worksheet DateUtc
         */
        {
            Map<String,Object> equals = new HashMap<>();
            String filed = SMDB.F_DATE_UTC;
            equals.put(filed,null);
            Class<WorkSheet> cla = WorkSheet.class;
            String identity = CommonUtil.getEntityTableName(cla)+"_"+filed;
            Long start = System.currentTimeMillis();
            List<WorkSheet> data = DBUtil.selectEntitiesByTerms(cla,null,equals,null,null,sessionFactory);
            migration.put(identity+" size:",data.size());
            data.forEach(one->{
                one.setDateUtc(one.getDate().getTimeInMillis());
                DBUtil.updateExistedEntity(one,sessionFactory);
            });
            Long end = System.currentTimeMillis();
            migration.put(identity+" lasting:",(end-start)/1000+" s");
        }

        migration.put("endTime",System.currentTimeMillis());
    }

    /**
     * 修改createUtc 在更改的时候 自动补充 updateUtc
     */
    private  <T extends SMGeneralEntity>  void  doGeneralMigration(Class<T> cla, Function<T,Calendar> createTimeGetter){
        Map<String,Object> equals = new HashMap<>();
        String field = SMDB.F_CREATE_UTC;
        equals.put(field,null);
        final String identity = CommonUtil.getEntityTableName(cla)+"_"+field;
        Long start = System.currentTimeMillis();
        List<T> data = DBUtil.selectEntitiesByTerms(cla,null,equals,null,null,sessionFactory);
        migration.put(identity+" size:",data.size());
        /**
         * 我现在fix的数据 有一个问题： 如何处理成默认时区
         * 这个意义不大 他是有风险： 数据库存的是东八区 但是我在日本的时候 取出来的是东九区
         * 但那又怎么样呢？ 差一个小时呗
         * 至于worksheet 那也没关系
         */
        data.forEach(one->{
            Calendar cal = createTimeGetter.apply(one);
            one.setCreateUtc(cal.getTimeInMillis());
            DBUtil.updateExistedEntity(one,sessionFactory);
        });
        Long end = System.currentTimeMillis();
        migration.put(identity+" lasting:",(end-start)/1000+" s");
    }

    public Map<String, Object> checkLatestMigration(long loginId) {
        uL.checkPerm(loginId, SMPerm.CREATE_NOTE_BOOK_AND_NOTE);

        return migration;
    }
}
