package com.uws.warning.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.PsychologyWarning;
import com.uws.domain.warning.WarningBehaviorModel;
import com.uws.domain.warning.StudyWarningModel;

/**
 * 
* @ClassName: IWarningImportDao 
* @Description: 预警导入 DAO 接口
* @author 联合永道
* @date 2016-1-18 上午10:16:33 
*
 */
public interface IWarningImportDao extends IBaseDao
{
    /**
     * 
     * @Title: IWarningImportDao.java 
     * @Package com.uws.warning.dao 
     * @Description:心理预警导入列表查询
     * @author LiuChen 
     * @date 2016-1-18 下午2:40:17
     */
	Page queryPsychologyWarningList(int pageNo, int pageSize,PsychologyWarning psychologyWarning,boolean isCollege,String currentOrgId);
    
	/**
	 * 
	 * @Title: IWarningImportDao.java 
	 * @Package com.uws.warning.dao 
	 * @Description: 获取总条数
	 * @author LiuChen 
	 * @date 2016-1-18 下午3:19:54
	 */
	public long getPsychologyCount();
	/**
	 * 
	 * @Title: pagedQueryStudyWarning
	 * @Description: 学业预警查询
	 * @param pageNo
	 * @param pageSize
	 * @param studyWarning
	 * @param isCollege
	 * @param collegeId
	 * @return
	 * @throws
	 */
	public Page pagedQueryStudyWarning(int pageNo,int pageSize ,StudyWarningModel studyWarning,boolean isCollege,String collegeId);

	Page pagePsychologyQuery(int i, int pageSize);

	/** 
	* @Title: queryCollegeAwardPage 
	* @Description: 行为预警列表 
	* @param  @param awardInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryBehaviorPage(WarningBehaviorModel bahavior,int pageNo,int pageSize);
	
	/** 
	* @Title: saveBehavior 
	* @Description: 保存行为预警
	* @param  @param behavior    
	* @return void    
	* @throws 
	*/
	public void saveBehavior(WarningBehaviorModel behavior);
	
	/** 
	* @Title: delBehavior 
	* @Description: 删除行为预警
	* @param  @param behavior    
	* @return void    
	* @throws 
	*/
	public void delBehavior(WarningBehaviorModel behavior);
	
	/** 
	* @Title: getBehaviorById 
	* @Description: 通过ID获取行为预警对象
	* @param  @param id    
	* @return void    
	* @throws 
	*/
	public WarningBehaviorModel getBehaviorById(String id);
}
