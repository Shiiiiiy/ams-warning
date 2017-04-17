package com.uws.warning.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;


import java.util.Map;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.PsychologyWarning;
import com.uws.domain.warning.WarningBehaviorModel;
import com.uws.domain.warning.StudyWarningModel;

/**
 * 
* @ClassName: IWarningImportService 
* @Description: 预警导入service
* @author 联合永道
* @date 2016-1-18 上午10:14:48 
*
 */
public interface IWarningImportService extends IBaseService
{
    /**
     * 
     * @Title: IWarningImportService.java 
     * @Package com.uws.warning.service 
     * @Description:预警信息--心理预警导入列表查询
     * @author LiuChen 
     * @date 2016-1-18 下午2:39:18
     */
	public Page queryPsychologyWarningList(int pageNo, int PageSize,PsychologyWarning psychologyWarning,boolean isCollege,String currentOrgId);
	
	
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
	public Page queryBehaviorPage(WarningBehaviorModel behavior,int pageNo,int pageSize);
	
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
	* @return WarningBehaviorModel    
	* @throws 
	*/
	public WarningBehaviorModel getBehaviorById(String id);
	
	/** 
	* @Title: importData 
	* @Description: 导入行为预警信息 
	* @param  @param list    
	* @return void    
	* @throws 
	*/
	public void importData(List<WarningBehaviorModel> list);
	/**
	 * 导入数据
	 * @param paramList
	 */
	public void importPsychologyWarningData(List<PsychologyWarning> paramList,HttpServletRequest request);
	
	public void importData(List<Object[]> paramList, String paramString1, String paramString2)
			throws ExcelException, OfficeXmlFileException, IOException, IllegalAccessException, InstantiationException, ClassNotFoundException, Exception;

	/**
	 * 比较数据
	 * @param list
	 * @return
	 */
	public List<Object[]> comparePsychologyWarningData(List<PsychologyWarning> paramList) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException;
    
	

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
	
	/**
	 * 
	 * @Title: importStudyWarningData
	 * @Description: 学业预警导入
	 * @param filePath
	 * @param excelId
	 * @param initData
	 * @param clazz
	 * @throws Exception
	 * @throws
	 */
	public void importStudyWarningData(String filePath, String excelId, Map initData,Class<StudyWarningModel> clazz) throws Exception;

	/**
	 * 
	 * @Title: deleteStudyWarning
	 * @Description: 学业预警删除，物理删除
	 * @param ids
	 * @throws
	 */
	public void deleteStudyWarning(String[] ids);


	public void deletePsychologyInfo(String[] ids);
	
}
