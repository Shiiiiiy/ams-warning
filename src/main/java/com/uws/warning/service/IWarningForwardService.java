package com.uws.warning.service;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.WarningForwardModel;
import com.uws.user.model.User;

/**
 * 
* @ClassName: IWarningForwardService 
* @Description: 预警管理 service 接口 
* @author 联合永道
* @date 2015-12-30 上午9:42:14 
*
 */
public interface IWarningForwardService extends IBaseService
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
	 * @Description:  分页列表查询
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
	 * @Title: findById
	 * @Description: query by primaryKey
	 * @param id
	 * @return
	 * @throws
	 */
	public WarningForwardModel findById(String id);
	
	/**
	 * 
	 * @Title: saveWarningForwardModel
	 * @Description: save info
	 * @param model
	 * @throws
	 */
	public void saveWarningForwardModel(WarningForwardModel model); 
	
	/**
	 * 
	 * @Title: updateWarningForwardModel
	 * @Description: udpate info
	 * @param model
	 * @throws
	 */
	public void updateWarningForwardModel(WarningForwardModel model);
	
	/**
	 * 
	 * @Title: saveOrUpdateWarningForward
	 * @Description: save the sumitforward infos
	 * @param model
	 * @param fileIds
	 * @throws
	 */
	public void saveOrUpdateWarningForward(WarningForwardModel model,String[] fileIds,User currentUser);
	
	/**
	 * 
	 * @Title: deleteById
	 * @Description: delete infos (Physical deletion)
	 * @param id
	 * @throws
	 */
	public void deleteById(String id);
	
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
