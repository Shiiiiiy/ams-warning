package com.uws.warning.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.WarningForwardModel;

/**
 * 
* @ClassName: IWarningForwardDao 
* @Description: 预警管理 DAO 接口
* @author 联合永道
* @date 2015-12-30 上午9:46:21 
*
 */
public interface IWarningForwardDao extends IBaseDao
{
	/**
	 * 
	 * @Title: queryWarningForwardPage
	 * @Description: 分页列表查询
	 * @param pageNo
	 * @param pageSize
	 * @param warningForward
	 * @return
	 * @throws
	 */
	public Page queryWarningForwardPage(int pageNo,int pageSize,WarningForwardModel warningForward);
	
	/**
	 * 
	 * @Title: queryWarningForwardPage
	 * @Description:  维护列表分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param warningForward
	 * @param queryCode
	 * @return
	 * @throws
	 */
	public Page queryWarningForwardPage(int pageNo,int pageSize,WarningForwardModel warningForward,Object[] queryCode);
	
	/**
	 * 
	 * @Title: queryByConditions
	 * @Description: 按照查询条件查询, 组合主键
	 * @param college
	 * @param year
	 * @param term
	 * @param warningType
	 * @return
	 * @throws
	 */
	public WarningForwardModel queryByConditions(String college,String year,String term,String warningType);
	
}
