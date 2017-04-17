package com.uws.warning.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.dao.IStudentCommonDao;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.warning.WarningBehaviorModel;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.domain.warning.PsychologyWarning;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.domain.warning.StudyWarningModel;
import com.uws.user.model.User;
import com.uws.warning.dao.IWarningImportDao;
import com.uws.warning.service.IWarningImportService;

/**
 * 
* @ClassName: WarningImportServiceImpl 
* @Description: 预警导入service
* @author 联合永道
* @date 2016-1-18 上午10:15:26 
*
 */
@Service("com.uws.warning.service.impl.WarningImportServiceImpl")
public class WarningImportServiceImpl extends BaseServiceImpl implements IWarningImportService
{
	@Autowired
	private IWarningImportDao warningImportDao;
	//条数
	private int pageSize = 1000;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IStudentCommonDao commonStudentDao;
	// sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession("/wanring/import/");
	
	/**
	 * 
	 * @Description:心理预警导入列表查询
	 * @author LiuChen  
	 * @date 2016-1-18 下午2:39:55
	 */
	@Override
	public Page queryPsychologyWarningList(int pageNo, int PageSize,PsychologyWarning psychologyWarning,boolean isCollege,String currentOrgId)
	{
	    return this.warningImportDao.queryPsychologyWarningList(pageNo,PageSize,psychologyWarning,isCollege,currentOrgId);
	}
	
	
	@Override
	public void importPsychologyWarningData(List<PsychologyWarning> list,HttpServletRequest request){
		for (PsychologyWarning psychologyWarning : list){
			 String number = psychologyWarning.getStuNumber();
			 BigDecimal bd = new BigDecimal(number);
			 String stuNumber = bd.toString();
			 StudentInfoModel studentInfo = commonStudentDao.queryStudentByStudentNo(stuNumber);
			 psychologyWarning.setStudent(studentInfo);
			 this.warningImportDao.save(psychologyWarning);
		}
	}

	/**
	 * 比较导入的数据是否重复
	 */
	@Override
	public List<Object[]> comparePsychologyWarningData(List<PsychologyWarning> list) throws OfficeXmlFileException, IOException, IllegalAccessException,
		  ExcelException, InstantiationException, ClassNotFoundException {
		 List compareList = new ArrayList();
	     Object[] array = (Object[])null;
	     long count = this.warningImportDao.getPsychologyCount();
	     if (count != 0L) {
	       for (int i = 0; i < count / this.pageSize + 1L; i++) {
	         Page page = this.warningImportDao.pagePsychologyQuery(i + 1, this.pageSize);
	         List<PsychologyWarning> psychologyWarningList = (List)page.getResult();
	         for (PsychologyWarning psychologyWarning : psychologyWarningList) {
	           for (PsychologyWarning psychologyWarningExcel : list){
	        	   BigDecimal bd = new BigDecimal(psychologyWarningExcel.getStuNumber());
		           String str = bd.toString();
	             if ((psychologyWarning.getStudent() !=null && psychologyWarning.getStudent().getStuNumber() !=null && psychologyWarning.getStudent().getStuNumber().equals(str))) {
	            	   String number = psychologyWarningExcel.getStuNumber();
	       			   BigDecimal bds = new BigDecimal(number);
	       			   String stuNumber = bds.toString();
	            	   StudentInfoModel studentInfo = commonStudentDao.queryStudentByStudentNo(stuNumber);
	            	   psychologyWarningExcel.setStudent(studentInfo);
	            	 array = new Object[] {psychologyWarning,psychologyWarningExcel};
	               compareList.add(array);
	            }
	        }
	      }
	     }
	  }
	     return compareList;
	}
    
	/* (非 Javadoc) 
	* <p>Title: queryBehaviorPage</p> 
	* <p>Description: </p> 
	* @param behavior
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.warning.service.IWarningImportService#queryBehaviorPage(com.uws.domain.warning.WarningBehaviorModel, int, int) 
	*/
	@Override
	public Page queryBehaviorPage(WarningBehaviorModel behavior, int pageNo,
			int pageSize) {
		return this.warningImportDao.queryBehaviorPage(behavior, pageNo, pageSize);
	}

    /**
     * 
     * @Description:导入数据对比
     * @author LiuChen  
     * @date 2015-12-9 下午3:57:33
     */
	@Override
	public void importData(List<Object[]> list, String filePath,String compareId) throws Exception {
		 Map map = new HashMap();
	     for (Object[] array : list) {
	    	 PsychologyWarning psychologyWarning = (PsychologyWarning)array[0];
	         map.put(psychologyWarning.getStudent().getStuNumber(), psychologyWarning);
	    }
	     ImportUtil iu = new ImportUtil();
	     List<PsychologyWarning> listPsychologyWarning = iu.getDataList(filePath, "importPsychologyInfo", null, PsychologyWarning.class);
	     for (PsychologyWarning psychologyWarning : listPsychologyWarning) {
	       BigDecimal bds = new BigDecimal(psychologyWarning.getStuNumber());
    	   String str = bds.toString();
	       if (!map.containsKey(str)) {
	    	  StudentInfoModel studentInfoModel = new StudentInfoModel();
	    	  studentInfoModel.setId(str);
	    	  psychologyWarning.setStudent(studentInfoModel);
	         this.warningImportDao.save(psychologyWarning);
	      } else {
	    	  PsychologyWarning psychologyWarningPo = (PsychologyWarning)map.get(str);
	         if ((StringUtils.isBlank(compareId)) || (!compareId.contains(psychologyWarningPo.getId()))) {
	        	 psychologyWarning.setId(psychologyWarningPo.getId());
	        	 StudentInfoModel studentInfo = commonStudentDao.queryStudentByStudentNo(str);
	        	 psychologyWarning.setStudent(studentInfo);
	             this.warningImportDao.update(psychologyWarning);
	        }
	      }
	    }
	  }


	/* (非 Javadoc) 
	* <p>Title: importData</p> 
	* <p>Description: </p> 
	* @param list 
	* @see com.uws.warning.service.IWarningImportService#importData(java.util.List) 
	*/
	@Override
	public void importData(List<WarningBehaviorModel> list) {
		
		for(WarningBehaviorModel behavior : list) {
			StudentInfoModel stu = this.studentCommonService.queryStudentByStudentNo(behavior.getStudentStr());
			behavior.setStudent(stu);
			this.warningImportDao.saveBehavior(behavior);
		}
	}

	/* (非 Javadoc) 
	* <p>Title: saveBehavior</p> 
	* <p>Description: </p> 
	* @param behavior 
	* @see com.uws.warning.service.IWarningImportService#saveBehavior(com.uws.domain.warning.WarningBehaviorModel) 
	*/
	@Override
	public void saveBehavior(WarningBehaviorModel behavior) {
		this.warningImportDao.saveBehavior(behavior);
	}

	/* (非 Javadoc) 
	* <p>Title: delBehavior</p> 
	* <p>Description: </p> 
	* @param behavior 
	* @see com.uws.warning.service.IWarningImportService#delBehavior(com.uws.domain.warning.WarningBehaviorModel) 
	*/
	@Override
	public void delBehavior(WarningBehaviorModel behavior) {
		this.warningImportDao.delBehavior(behavior);
	}


	/**
	 * 描述信息:  学业预警查询
	 * @param pageNo
	 * @param pageSize
	 * @param studyWarning
	 * @param isCollege
	 * @param collegeId
	 * @return
	 * 2016-1-18 下午2:08:26
	 */
	@Override
    public Page pagedQueryStudyWarning(int pageNo, int pageSize,
            StudyWarningModel studyWarning, boolean isCollege, String collegeId)
    {
	    return warningImportDao.pagedQueryStudyWarning(pageNo, pageSize, studyWarning, isCollege, collegeId);
    }

	/**
	 * 描述信息: 学业预警导入
	 * @param filePath
	 * @param excelId
	 * @param initData
	 * @param clazz
	 * @throws Exception
	 * 2016-1-18 下午3:23:53
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void importStudyWarningData(String filePath, String excelId,Map initData, Class<StudyWarningModel> clazz) throws Exception
    {
		ImportUtil iu = new ImportUtil();
		List<StudyWarningModel> warningList = iu.getDataList(filePath, excelId, initData, clazz);
	    User creator = new User();
	    creator.setId(sessionUtil.getCurrentUserId());
	    Date nowDate = new Date();
	    StudentInfoModel student = null;
	    for(StudyWarningModel warning : warningList)
	    {
	    	try{
	    		String code = warning.getStudentNumber();
				BigDecimal bd = new BigDecimal(code);
	    		/*
	    		 * 注意:当前系统 学号的ID是一样的所以可以直接赋值的方式保存,如果不是则用下边的查询方法代替
	    		 * student = commonStudentDao.queryStudentByStudentNo(bd.toString());
	    		 */
				student = new StudentInfoModel();
				student.setId(bd.toString());
		    	warning.setStudent(student);
		    	warning.setWarningDate(nowDate);
		    	warningImportDao.save(warning);
    		}
			catch(Exception e){
				e.printStackTrace();
				throw new ExcelException("学号为 "+warning.getStudentNumber()+" 的学生预警信息上传有问题,请修正后重新上传；<br/>");
			}
	    }
    }

	/**
	 * 描述信息: 学业预警删除，物理删除
	 * @param ids
	 * 2016-1-18 下午4:11:08
	 */
	@Override
	public void deleteStudyWarning(String[] ids)
	{
		if (!ArrayUtils.isEmpty(ids))
		{
			for (String id : ids)
				warningImportDao.deleteById(StudyWarningModel.class, id);
		}
	}
	
	@Override
	public void deletePsychologyInfo(String[] ids)
	{
		if (!ArrayUtils.isEmpty(ids))
		{
			for (String id : ids)
				warningImportDao.deleteById(PsychologyWarning.class, id);
		}
	    
	}

	/* (非 Javadoc) 
	* <p>Title: getBehaviorById</p> 
	* <p>Description: </p> 
	* @param id 
	* @see com.uws.warning.service.IWarningImportService#getBehaviorById(java.lang.String) 
	*/
	@Override
	public WarningBehaviorModel getBehaviorById(String id) {
		return this.warningImportDao.getBehaviorById(id);
	}
}
