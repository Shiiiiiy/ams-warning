package com.uws.warning.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.WarningForwardModel;
import com.uws.warning.dao.IWarningForwardDao;

/**
 * 
* @ClassName: WarningForwardDaoImpl 
* @Description: 预警管理 
* @author 联合永道
* @date 2015-12-30 上午9:47:19 
*
 */
@Repository("com.uws.warning.dao.impl.WarningForwardDaoImpl")
public class WarningForwardDaoImpl extends BaseDaoImpl implements IWarningForwardDao
{

	/**
	 * 描述信息: WarningForward infos paged query  
	 * @param pageNo
	 * @param pageSize
	 * @param warningForward
	 * @return
	 * 2015-12-30 上午10:15:47
	 */
	@Override
    public Page queryWarningForwardPage(int pageNo, int pageSize, WarningForwardModel warningForward)
    {
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer( "from WarningForwardModel where 1=1 ");
		if (null != warningForward) 
		{
			// 学院
			if (null != warningForward.getCollege() && !StringUtils.isEmpty( warningForward.getCollege().getId())) 
			{
				hql.append(" and college = ? ");
				values.add(warningForward.getCollege());
			}
			// 类型
			if (null != warningForward.getWarningType() && !StringUtils.isEmpty(warningForward.getWarningType().getId())) 
			{
				hql.append(" and warningType = ? ");
				values.add(warningForward.getWarningType());
			}
			// 学年
			if (null != warningForward.getYearDic() && !StringUtils.isEmpty(warningForward.getYearDic().getId())) 
			{
				hql.append(" and yearDic = ? ");
				values.add(warningForward.getYearDic());
			}
			// 学期
			if (null != warningForward.getTermDic() && !StringUtils.isEmpty(warningForward.getTermDic().getId())) 
			{
				hql.append(" and termDic = ? ");
				values.add(warningForward.getTermDic());
			}
		}
        //排序
        hql.append(" order by warningDate desc ");
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    }

	/**
	 * 描述信息: 维护列表分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param warningForward
	 * @return
	 * 2015-12-30 上午10:15:47
	 */
	@Override
    public Page queryWarningForwardPage(int pageNo, int pageSize, WarningForwardModel warningForward,Object[] queryDic)
    {
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer( "from WarningForwardModel where 1=1 ");
		if (null != warningForward) 
		{
			// 学院
			if (null != warningForward.getCollege() && !StringUtils.isEmpty( warningForward.getCollege().getId())) 
			{
				hql.append(" and college = :college ");
				values.put("college", warningForward.getCollege());
			}
			// 类型
			if (null != warningForward.getWarningType() && !StringUtils.isEmpty(warningForward.getWarningType().getId())) 
			{
				hql.append(" and warningType = :warningType ");
				values.put("warningType", warningForward.getWarningType());
			}
			// 学年
			if (null != warningForward.getYearDic() && !StringUtils.isEmpty(warningForward.getYearDic().getId())) 
			{
				hql.append(" and yearDic = :yearDic ");
				values.put("yearDic", warningForward.getYearDic());
			}
			// 学期
			if (null != warningForward.getTermDic() && !StringUtils.isEmpty(warningForward.getTermDic().getId())) 
			{
				hql.append(" and termDic = :termDic ");
				values.put("termDic", warningForward.getTermDic());
			}
		}
		//类型过滤
		if(!ArrayUtils.isEmpty(queryDic))
		{
			hql.append(" and warningType in (:queryDic) ");
			values.put("queryDic", queryDic);
		}else{
			hql.append(" and 1=2 ");// 当不存在操作角色的配置了菜单之后 是查询不出数据的
		}
		
        //排序
        hql.append(" order by warningDate desc ");
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
    }
	
	/**
	 * 描述信息: 按照条件查询
	 * @param college
	 * @param year
	 * @param warningType
	 * @return
	 * 2015-12-30 下午4:16:10
	 */
	@Override
	@SuppressWarnings("unchecked")
    public WarningForwardModel queryByConditions(String college, String year,
            String term, String warningType)
    {
		StringBuffer hql = new StringBuffer( "from WarningForwardModel where college.id=? and yearDic.id=? and termDic.id=? and warningType.id=? ");
	    List<WarningForwardModel>  list = this.query(hql.toString(), new Object[]{college, year,term, warningType});
	    return null == list||list.size()==0 ? null : list.get(0);
    }
	
}
