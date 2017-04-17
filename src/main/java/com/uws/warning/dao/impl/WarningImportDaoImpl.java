package com.uws.warning.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.PsychologyWarning;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.warning.WarningBehaviorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.warning.StudyWarningModel;
import com.uws.warning.dao.IWarningImportDao;

/**
 * 
* @ClassName: WarningImportDaoImpl 
* @Description: 预警导入 dao 实现
* @author 联合永道
* @date 2016-1-18 上午10:17:25 
*
 */
@Repository("com.uws.warning.dao.impl.WarningImportDaoImpl")
public class WarningImportDaoImpl extends BaseDaoImpl implements IWarningImportDao
{
	@Override
	public Page queryPsychologyWarningList(int pageNo, int pageSize,PsychologyWarning psychologyWarning,boolean isCollege,String collegeId)
	{
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from PsychologyWarning p where 1=1");
	     if (null != psychologyWarning) 
			{
				StudentInfoModel student = psychologyWarning.getStudent();
				if( null!= student)
				{
					// 学院
					if (null != student.getCollege() && !StringUtils.isEmpty(student.getCollege().getId())) 
					{
						hql.append(" and p.student.college = ? ");
						values.add(student.getCollege());
					}
					// 专业
					if (null != student.getMajor() && !StringUtils.isEmpty(student.getMajor().getId())) 
					{
						hql.append(" and p.student.major = ? ");
						values.add(student.getMajor());
					}
					// 班级
					if (null != student.getClassId() && !StringUtils.isEmpty(student.getClassId().getId())) 
					{
						hql.append(" and p.student.classId = ? ");
						values.add(student.getClassId());
					}
					// 学号
					if (!StringUtils.isEmpty(student.getStuNumber())) {
						hql.append(" and p.student.stuNumber like ? ");
						if (HqlEscapeUtil.IsNeedEscape(student.getStuNumber())) {
							values.add("%" + HqlEscapeUtil.escape(student.getStuNumber()) + "%");
							hql.append(HqlEscapeUtil.HQL_ESCAPE);
						} else
							values.add("%" + student.getStuNumber() + "%");

					}
					// 姓名
					if (!StringUtils.isEmpty(student.getName())) {
						hql.append(" and p.student.name like ? ");
						if (HqlEscapeUtil.IsNeedEscape(student.getName())) {
							values.add("%" + HqlEscapeUtil.escape(student.getName()) + "%");
							hql.append(HqlEscapeUtil.HQL_ESCAPE);
						} else
							values.add("%" + student.getName() + "%");
					}
				}
			}
	        
			if(isCollege)//二级学院的查询过滤
			{
				hql.append(" and p.student.college.id = ? ");
				values.add(collegeId);
			}
	            hql.append(" order by p.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/* (非 Javadoc) 
	* <p>Title: queryBehaviorPage</p> 
	* <p>Description: </p> 
	* @param awardInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.warning.dao.IWarningImportDao#queryBehaviorPage(com.uws.domain.warning.WarningBehaviorModel, int, int) 
	*/
	@Override
	public Page queryBehaviorPage(WarningBehaviorModel behavior, int pageNo,
			int pageSize) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from WarningBehaviorModel b where 1=1");
//		学院
		if(DataUtil.isNotNull(behavior.getStudent()) &&
				DataUtil.isNotNull(behavior.getStudent().getCollege()) &&
				DataUtil.isNotNull(behavior.getStudent().getCollege().getId())) {
			
			hql.append(" and b.student.college.id = ?");
			values.add(behavior.getStudent().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(behavior.getStudent()) &&
				DataUtil.isNotNull(behavior.getStudent().getMajor()) &&
				DataUtil.isNotNull(behavior.getStudent().getMajor().getId())) {
			hql.append(" and b.student.major.id = ?");
			values.add(behavior.getStudent().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(behavior.getStudent()) &&
				DataUtil.isNotNull(behavior.getStudent().getClassId()) &&
				DataUtil.isNotNull(behavior.getStudent().getClassId().getId())) {
			hql.append(" and b.student.classId.id = ?");
			values.add(behavior.getStudent().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(behavior.getStudent()) &&
				DataUtil.isNotNull(behavior.getStudent().getName()) ){
			hql.append(" and b.student.name like ?");
			values.add("%" + HqlEscapeUtil.escape(behavior.getStudent().getName()) + "%"); 
		}
//		学号
		if(DataUtil.isNotNull(behavior.getStudent()) &&
				DataUtil.isNotNull(behavior.getStudent().getStuNumber())) {
			hql.append(" and b.student.stuNumber like ?");
			values.add("%" + HqlEscapeUtil.escape(behavior.getStudent().getStuNumber()) + "%");
		}
		hql.append(" order by b.updateTime desc");
		return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: saveBehavior</p> 
	* <p>Description: </p> 
	* @param behavior 
	* @see com.uws.warning.dao.IWarningImportDao#saveBehavior(com.uws.domain.warning.WarningBehaviorModel) 
	*/
	@Override
	public void saveBehavior(WarningBehaviorModel behavior) {
		this.save(behavior);
	}

	/* (非 Javadoc) 
	* <p>Title: delBehavior</p> 
	* <p>Description: </p> 
	* @param behavior 
	* @see com.uws.warning.dao.IWarningImportDao#delBehavior(com.uws.domain.warning.WarningBehaviorModel) 
	*/
	@Override
	public void delBehavior(WarningBehaviorModel behavior) {
		this.delete(behavior);
	}

	/**
	 * 总条数
	 */
	@Override
	public long getPsychologyCount() {
		String sql = " select count(p.id) from PsychologyWarning p where 1=1 ";
		return ((Long)queryUnique(sql, new Object[0])).longValue();
	}
   
	
	@Override
	public Page pagePsychologyQuery(int i, int pageSize) {
		String sql = "select p from PsychologyWarning p where 1=1 ";
		return pagedQuery(sql, i, pageSize, new Object[0]);
	}

	/**
	 * 描述信息: 学业预警查询
	 * @param pageNo
	 * @param pageSize
	 * @param studyWarning
	 * @param isCollege
	 * @param collegeId
	 * @return
	 * 2016-1-18 下午2:11:26
	 */
	@Override
    public Page pagedQueryStudyWarning(int pageNo, int pageSize,
            StudyWarningModel studyWarning, boolean isCollege, String collegeId)
    {
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer( "from StudyWarningModel where 1=1 ");
		if (null != studyWarning) 
		{
			StudentInfoModel student = studyWarning.getStudent();
			if( null!= student)
			{
				// 学院
				if (null != student.getCollege() && !StringUtils.isEmpty( student.getCollege().getId())) 
				{
					hql.append(" and student.college = ? ");
					values.add(student.getCollege());
				}
				// 专业
				if (null != student.getMajor() && !StringUtils.isEmpty( student.getMajor().getId())) 
				{
					hql.append(" and student.major = ? ");
					values.add(student.getMajor());
				}
				// 班级
				if (null != student.getClassId() && !StringUtils.isEmpty( student.getClassId().getId())) 
				{
					hql.append(" and student.classId = ? ");
					values.add(student.getClassId());
				}
				// 学号
				if (!StringUtils.isEmpty(student.getStuNumber())) {
					hql.append(" and student.stuNumber like ? ");
					if (HqlEscapeUtil.IsNeedEscape(student.getStuNumber())) {
						values.add("%" + HqlEscapeUtil.escape(student.getStuNumber()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					} else
						values.add("%" + student.getStuNumber() + "%");

				}
				// 姓名
				if (!StringUtils.isEmpty(student.getName())) {
					hql.append(" and student.name like ? ");
					if (HqlEscapeUtil.IsNeedEscape(student.getName())) {
						values.add("%" + HqlEscapeUtil.escape(student.getName()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					} else
						values.add("%" + student.getName() + "%");
				}
			}
		}
        
		if(isCollege)//二级学院的查询过滤
		{
			hql.append(" and student.college.id = ? ");
			values.add(collegeId);
		}
		//排序
        hql.append(" order by warningDate desc ");
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    }

	/* (非 Javadoc) 
	* <p>Title: getBehaviorById</p> 
	* <p>Description: </p> 
	* @param id 
	* @see com.uws.warning.dao.IWarningImportDao#getBehaviorById(java.lang.String) 
	*/
	@Override
	public WarningBehaviorModel getBehaviorById(String id) {
		
		String hql = " from WarningBehaviorModel b where b.id = ?";
		return (WarningBehaviorModel) this.queryUnique(hql, new Object[]{id});
	}

}
