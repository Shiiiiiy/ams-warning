package com.uws.warning.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpServletResponse;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.commons.lang.ArrayUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.uws.common.service.IBaseDataService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.PsychologyWarning;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.warning.StudyWarningModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.domain.warning.WarningBehaviorModel;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;
import com.uws.warning.service.IWarningImportService;

/**
 * 
* @ClassName: WarningImportController 
* @Description: 预警信息导入和查询
* @author 联合永道
* @date 2016-1-18 上午10:13:12 
*
 */
@Controller
public class WarningImportController extends BaseController
{
	private Logger log = new LoggerFactory(WarningImportController.class);
	@Autowired
	private IWarningImportService warningImportService;
    //数据字典
	private DicUtil dicUtil = DicFactory.getDicUtil();

	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private ICompService compService;
	@Autowired
	private IBaseDataService baseDataService;
	
	/**
	 * 
	 * @Title: queryWarningInfoByType
	 * @Description: 预警列表查询
	 * @param model
	 * @param request
	 * @param queryType
	 * @return
	 * @throws
	 */
	@RequestMapping("/warning/import{queryType}/opt-query/wangingList")
	public String queryWarningInfoByType(ModelMap model, HttpServletRequest request,@PathVariable String queryType,
			StudyWarningModel studyWarning,PsychologyWarning psychologyWarning,WarningBehaviorModel behavior)
	{
		if("psychology".equalsIgnoreCase(queryType)){//心理
			this.setPsychologyWarningValue(model, request, psychologyWarning);
			return "/warning/import/psychologyWarningList";
			
		}else if("behavior".equalsIgnoreCase(queryType)){//行为
			this.queryBehaviorPage(model, request, behavior);
			return "/warning/import/behaviorWarningList";
			
		}else if("study".equalsIgnoreCase(queryType)){//学业
			
			this.setStudyWarningValue(model, request, studyWarning);
			return "/warning/import/studyWarningList";
		}
		return null;
	}
	
	
	/** 
	* @Title: queryBehaviorPage 
	* @Description: 行为预警列表页
	* @param  @param model
	* @param  @param request
	* @param  @param behavior
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping("/warning/importBEHAVIOR/opt-query/queryBehaviorPage")
	public String queryBehaviorPage(ModelMap model, HttpServletRequest request,WarningBehaviorModel behavior) {
		
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != behavior && null != behavior.getStudent() 
				&& null != behavior.getStudent().getCollege() 
				&& null != behavior.getStudent().getCollege().getId()
				&& behavior.getStudent().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(behavior.getStudent().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != behavior && null != behavior.getStudent() 
				&& null != behavior.getStudent().getClassId() 
				&& null != behavior.getStudent().getMajor() 
				&& null != behavior.getStudent().getMajor().getId() 
				&& behavior.getStudent().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(behavior.getStudent().getMajor().getId());
		}
		Page page = this.warningImportService.queryBehaviorPage(behavior, pageNo, Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("behavior", behavior);
		model.addAttribute("page", page);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		return "warning/import/behaviorWarningList";
	}
	
	/** 
	* @Title: importBehavior 
	* @Description: 行为预警导入
	* @param  @param model
	* @param  @param file
	* @param  @param maxSize
	* @param  @param allowedExt
	* @param  @param request
	* @param  @param session
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/warning/importBEHAVIOR/opt-query/importBehavior.do"})
	public String importBehavior(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session) {
		
		List errorText = new ArrayList();
		String errorTemp = "";
		MultipartFileValidator validator = new MultipartFileValidator();
		if(DataUtil.isNotNull(allowedExt)) {
			validator.setAllowedExtStr(allowedExt.toLowerCase());
		}
		if(DataUtil.isNotNull(maxSize)) {
			validator.setMaxSize(Long.valueOf(maxSize).longValue());
		}else{
			validator.setMaxSize(20971520);
		}
		String returnValue = validator.validate(file);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(!returnValue.equals("")) {
			errorTemp = returnValue;
			errorText.add(errorTemp);
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "warning/import/importWarningBehavior";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<WarningBehaviorModel> list = iu.getDataList(filePath, "importWarningBehavior", null, WarningBehaviorModel.class);        //Excel数据
//				List arrayList = this.managePunishService.compareData(list);                                  //Excel与已有的重复的数据
				List arrayList = new ArrayList();                                  //导入去重验证 暂时注释掉
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.warningImportService.importData(list);;
				}else{
					session.setAttribute("arrayList", arrayList);
					List subList = null;
					if(arrayList.size() >= Page.DEFAULT_PAGE_SIZE) {
						subList = arrayList.subList(0, Page.DEFAULT_PAGE_SIZE);
					}else{
						subList = arrayList;
					}
					Page page = new Page();
					page.setPageSize(Page.DEFAULT_PAGE_SIZE);
					page.setResult(subList);
					page.setStart(0L);
					page.setTotalCount(arrayList.size());
					model.addAttribute("page", page);
				}
			} catch (OfficeXmlFileException e) {
				e.printStackTrace();
				errorTemp = "OfficeXmlFileException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IOException e) {
				e.printStackTrace();
				errorTemp = "IOException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				errorTemp = "IllegalAccessException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ExcelException e) {
				e.printStackTrace();
				errorTemp = e.getMessage();
				errorText.add(errorTemp);
			} catch (InstantiationException e) {
				e.printStackTrace();
				errorTemp = "InstantiationException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				String message = e.getMessage();
				if(DataUtil.isNotNull(message)) {
					errorText.add(message);
				}else{
					errorText.add("模板不正确或者模板内数据异常，请检查后再导入。");
				}
			}
			model.addAttribute("importFlag", Boolean.valueOf(true));
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
			return "warning/import/importWarningBehavior";
		}
	}
	
	/**
	 * 执行导入
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @return
	 * @throws exception 
	 * @throws Exception 
	 */
	@RequestMapping(value="/warning/importPSYCHOLOGY/opt-import/importPsychologyInfo")
	public String importPsychologyInfo(ModelMap model, @RequestParam("file") MultipartFile file, 
		   String maxSize, String allowedExt, HttpServletRequest request, HttpSession session) throws Exception{

	     List errorText = new ArrayList();
	 
	     MultipartFileValidator validator = new MultipartFileValidator();
	     if (org.apache.commons.lang.StringUtils.isNotEmpty(allowedExt)) {
	       validator.setAllowedExtStr(allowedExt.toLowerCase());
	     }
	     if (org.apache.commons.lang.StringUtils.isNotEmpty(maxSize))
	       validator.setMaxSize(Long.valueOf(maxSize).longValue());
	     else {
	       validator.setMaxSize(setMaxSize());
	     }
	     String returnValue = validator.validate(file);
	     if (!returnValue.equals("")) {
	       errorText.add(returnValue);
	       model.addAttribute("errorText", errorText);
	       model.addAttribute("importFlag", Boolean.valueOf(true));
	       return "/warning/import/importPsychologyInfo";
	     }
	 
	     String tempFileId = this.fileUtil.saveSingleFile(true, file);
	     File tempFile = this.fileUtil.getTempRealFile(tempFileId);
	     String filePath = tempFile.getAbsolutePath();
	 
	     session.setAttribute("filePath", filePath);
	     try
	     {
	       ImportUtil iu = new ImportUtil();
	       List list = iu.getDataList(filePath, "importPsychologyInfo", null, PsychologyWarning.class);
	       //比较数据是否重复
	       List arrayList = this.warningImportService.comparePsychologyWarningData(list);
	       if ((arrayList == null) || (arrayList.size() == 0))
	       {
	         this.warningImportService.importPsychologyWarningData(list,request);
	       }
	       else {
	         session.setAttribute("arrayList", arrayList);
	         List subList = null;
	         if (arrayList.size() >= Page.DEFAULT_PAGE_SIZE)
	           subList = arrayList.subList(0, Page.DEFAULT_PAGE_SIZE);
	         else
	           subList = arrayList;
	         Page page = new Page();
	         page.setPageSize(Page.DEFAULT_PAGE_SIZE);
	         page.setResult(subList);
	         page.setStart(0L);
	         page.setTotalCount(arrayList.size());
	         model.addAttribute("page", page);
	       }
	     }
	     catch (ExcelException e) {
	       errorText = e.getMessageList();
	 
	       errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
	       model.addAttribute("errorText", errorText);
	     } catch (InstantiationException e) {
	       e.printStackTrace();
	     } catch (IOException e) {
	       e.printStackTrace();
	     } catch (IllegalAccessException e) {
	       e.printStackTrace();
	     } catch (ClassNotFoundException e) {
	       e.printStackTrace(); } finally {
	     }
	     model.addAttribute("importFlag", Boolean.valueOf(true));
	     return "/warning/import/importPsychologyInfo";
	}
	
		 /**
		 * 设置最大
		 * @return
		 */
		private int setMaxSize(){
			return 20971520;//20M
		}
		
		
	   /**
	    * 重复数据的分页
	    * @param model
	    * @param request
	    * @param session
	    * @param pageNo
	    * @return
	    */
	   @RequestMapping(value={"/warning/importPSYCHOLOGY/opt-query/psychologyPageQuery"}, produces={"text/plain;charset=UTF-8"})
	   @ResponseBody
	   public String comparePageQuery(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", required=true) String pageNo)
	   {
	     List arrayList = (List)session.getAttribute("arrayList");
	     List<Object[]> subList = null;
	     int pageno = Integer.parseInt(pageNo); int length = arrayList.size();
	     if (arrayList.size() >= Page.DEFAULT_PAGE_SIZE * pageno)
	       subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), Page.DEFAULT_PAGE_SIZE * pageno);
	     else
	       subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), length);
	     JSONArray array = new JSONArray();
	     JSONObject obj = null; JSONObject json = new JSONObject();
	     for (Object[] employmentInfoArray : subList) {
	       PsychologyWarning psychologyWarning = (PsychologyWarning)employmentInfoArray[0];
	       PsychologyWarning psychologyWarningExcel = (PsychologyWarning)employmentInfoArray[1];
	       obj = new JSONObject();
	       obj.put("id", psychologyWarning.getId());
	       obj.put("stuNumber", psychologyWarning.getStudent()!=null?psychologyWarning.getStudent().getName():"");
	       obj.put("onlyChild", psychologyWarning.getOnlyChild()!=null?psychologyWarning.getOnlyChild().getName():"");
	       obj.put("childFoster", psychologyWarning.getChildFoster()!=null?psychologyWarning.getChildFoster().getName():"");
	       obj.put("excelStuNumber", psychologyWarningExcel.getChildFoster()!=null?psychologyWarningExcel.getChildFoster().getName():"");
	       obj.put("excelOnlyChild", psychologyWarningExcel.getChildFoster()!=null?psychologyWarningExcel.getChildFoster().getName():"");
	       obj.put("excelChildFoster", psychologyWarningExcel.getChildFoster()!=null?psychologyWarningExcel.getChildFoster().getName():"");
	       array.add(obj);
	     }
	     json.put("result", array);
	     obj = new JSONObject();
	     obj.put("totalPageCount", Integer.valueOf(length % Page.DEFAULT_PAGE_SIZE == 0 ? length / Page.DEFAULT_PAGE_SIZE : length / Page.DEFAULT_PAGE_SIZE + 1));
	     obj.put("previousPageNo", Integer.valueOf(pageno - 1));
	     obj.put("nextPageNo", Integer.valueOf(pageno + 1));
	     obj.put("currentPageNo", Integer.valueOf(pageno));
	     obj.put("pageSize", Integer.valueOf(Page.DEFAULT_PAGE_SIZE));
	     obj.put("totalCount", Integer.valueOf(length));
	     json.put("page", obj);
	     return json.toString();
	   }
	   
	   
	    @ResponseBody
		@RequestMapping("/warning/import/opt-del/deletePsychologyInfo")
		public String deletePsychologyInfo(ModelMap model, HttpServletRequest request,HttpServletResponse response) 
		{
			String[] ids =  request.getParameterValues("psychologyWarningId");
			if (!ArrayUtils.isEmpty(ids)) 
				warningImportService.deletePsychologyInfo(ids);
			return "success";
		}
	
	 
	 
	   /**
		 * 对比导入数据
		 * @param model
		 * @param session
		 * @param compareId
		 * @return
	     * @throws Exception 
		 */
		@RequestMapping({"/warning/importPSYCHOLOGY/opt-query/comparePsychologyInfo"})
		public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) throws Exception{
			List errorText = new ArrayList();
		    String filePath = session.getAttribute("filePath").toString();
		    List arrayList = (List)session.getAttribute("arrayList");
		    try {
		       this.warningImportService.importData(arrayList, filePath, compareId);
		     }
		    catch (ExcelException e) {
		      errorText = e.getMessageList();
		 
		      errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
		      model.addAttribute("errorText", errorText);
		    } catch (OfficeXmlFileException e) {
		       e.printStackTrace();
		    } catch (IOException e) {
		       e.printStackTrace();
		    } catch (IllegalAccessException e) {
		       e.printStackTrace();
		    } catch (InstantiationException e) {
		       e.printStackTrace();
		    } catch (ClassNotFoundException e) {
		       e.printStackTrace(); } finally {
		    }
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "/warning/import/importPsychologyInfo";
		}
	
	
	/**
	 * 
	 * @Title: setStudyWarningValue
	 * @Description: 学业预警查询赋值操作
	 * @param model
	 * @param request
	 * @param studyWarning
	 * @throws
	 */
	private void setStudyWarningValue(ModelMap model, HttpServletRequest request,StudyWarningModel studyWarning)
	{
		String currentOrgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		boolean isCollege = CheckUtils.isCurrentOrgEqCollege(currentOrgId);
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		if(!isCollege)
			collegeList = baseDataService.listBaseAcademy();
		else{
			BaseAcademyModel college = baseDataService.findAcademyById(currentOrgId);
			//college.setId(currentOrgId);
			collegeList.add(college);
			StudentInfoModel student = studyWarning.getStudent();
			if(null != student )
				student.setCollege(college);
			else
			{
				student = new StudentInfoModel();
				student.setCollege(college);
				studyWarning.setStudent(student);
			}
		}
		Page page = warningImportService.pagedQueryStudyWarning(pageNo, Page.DEFAULT_PAGE_SIZE, studyWarning,isCollege,currentOrgId);
		// 下拉列表 专业
		List<BaseMajorModel> majorList = null;
		if (null != studyWarning && null != studyWarning.getStudent() && null != studyWarning.getStudent().getCollege()&& !StringUtils.isEmpty(studyWarning.getStudent().getCollege().getId()) ) {
			majorList = compService.queryMajorByCollage(studyWarning.getStudent().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList = null;
		if (null != studyWarning && null != studyWarning.getStudent() && null != studyWarning.getStudent().getMajor() && !StringUtils.isEmpty(studyWarning.getStudent().getMajor().getId())) {
			classList = compService.queryClassByMajor(studyWarning.getStudent().getMajor().getId());
		}
		model.addAttribute("page", page);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("studyWarning", studyWarning);
	}
	
	
	/**
	 * 
	 * @Title: setStudyWarningValue
	 * @Description: 心理预警查询赋值操作
	 * @param model
	 * @param request
	 * @param studyWarning
	 * @throws
	 */
	private void setPsychologyWarningValue(ModelMap model, HttpServletRequest request,PsychologyWarning psychologyWarning)
	{
		String currentOrgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		boolean isCollege = CheckUtils.isCurrentOrgEqCollege(currentOrgId);
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = new ArrayList<BaseAcademyModel>();
		if(!isCollege)
			collegeList = baseDataService.listBaseAcademy();
		else{
			BaseAcademyModel college = baseDataService.findAcademyById(currentOrgId);
			//college.setId(currentOrgId);
			collegeList.add(college);
			StudentInfoModel student = psychologyWarning.getStudent();
			if(null != student )
				student.setCollege(college);
			else
			{
				student = new StudentInfoModel();
				student.setCollege(college);
				psychologyWarning.setStudent(student);
			}
		}
		Page page = warningImportService.queryPsychologyWarningList(pageNo, Page.DEFAULT_PAGE_SIZE, psychologyWarning,isCollege,currentOrgId);
		// 下拉列表 专业
		List<BaseMajorModel> majorList = null;
		if (null != psychologyWarning && null != psychologyWarning.getStudent() && null != psychologyWarning.getStudent().getCollege()&& !StringUtils.isEmpty(psychologyWarning.getStudent().getCollege().getId()) ) {
			majorList = compService.queryMajorByCollage(psychologyWarning.getStudent().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList = null;
		if (null != psychologyWarning && null != psychologyWarning.getStudent() && null != psychologyWarning.getStudent().getMajor() && !StringUtils.isEmpty(psychologyWarning.getStudent().getMajor().getId())) {
			classList = compService.queryClassByMajor(psychologyWarning.getStudent().getMajor().getId());
		}
		model.addAttribute("page", page);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("psychologyWarning", psychologyWarning);
	}
	
	
	
	/**
	 * 
	 * @Title: importStudyWarning
	 * @Description:学业预警信息导入
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @return
	 * @throws
	 */
	@SuppressWarnings({ "finally", "deprecation" })
    @RequestMapping("/warning/import/opt-import/importStudyWanging")
	public String importStudyWarning(ModelMap model, @RequestParam("file")  MultipartFile file, String maxSize,String allowedExt,HttpServletRequest request)
	{
		List<Object> errorText = new ArrayList<Object>();
		String errorTemp = "";
		try {
			//构建文件验证对象
	    	MultipartFileValidator validator = new MultipartFileValidator();
	    	if(DataUtil.isNotNull(allowedExt)){
	    		validator.setAllowedExtStr(allowedExt.toLowerCase());
	    	}
	    	//设置文件大小
	    	if(DataUtil.isNotNull(maxSize)){
	    		validator.setMaxSize(Long.valueOf(maxSize));//20M
	    	}else{
	    		validator.setMaxSize(1024*1024*20);//20M
	    	}
			//调用验证框架自动验证数据
	        String returnValue=validator.validate(file);                
	        if(!returnValue.equals("")){
				errorTemp = returnValue;       	
				errorText.add(errorTemp);
	        	model.addAttribute("errorText",errorText);
	        	return "warning/import/studyWarningImport";
	        }
	        String tempFileId=fileUtil.saveSingleFile(true, file); 
	        File tempFile=fileUtil.getTempRealFile(tempFileId);
	        warningImportService.importStudyWarningData(tempFile.getAbsolutePath(), "importStudyWarning", null ,StudyWarningModel.class);
		} catch (OfficeXmlFileException e) {
			log.error(e.getMessage());
			errorTemp = "OfficeXmlFileException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (ExcelException e) { 
			log.error(e.getMessage());
			errorTemp = e.getMessage();
			errorText.add(errorTemp);
		} catch (InstantiationException e) {
			log.error(e.getMessage());
			errorTemp = "InstantiationException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (IOException e) {
			log.error(e.getMessage());
			errorTemp = "IOException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			errorTemp = "IllegalAccessException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			errorText.add("模板不正确或者模板内数据异常，请检查后再导入。");
		} finally {
			model.addAttribute("errorText",errorText);
	        return "/warning/import/studyWarningImport";
		}
	}
	
	
	@ResponseBody
	@RequestMapping("/warning/import/opt-del/deleteStudyWarning")
	public String deleteStudyWarning(ModelMap model, HttpServletRequest request,HttpServletResponse response) 
	{
		String[] ids =  request.getParameterValues("studyWarningId");
		if (!ArrayUtils.isEmpty(ids)) 
			warningImportService.deleteStudyWarning(ids);
		return "success";
	}
	
	/** 
	* @Title: deleteWarningBehavior 
	* @Description: 批量删除行为预警
	* @param  @param model
	* @param  @param request
	* @param  @param response
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@ResponseBody
	@RequestMapping("/warning/import/opt-del/deleteWarningBehavior")
	public String deleteWarningBehavior(ModelMap model, HttpServletRequest request,HttpServletResponse response) 
	{
		String[] ids =  request.getParameterValues("behaviorWarningId");
		if (!ArrayUtils.isEmpty(ids)) {
			for(String id : ids) {
				WarningBehaviorModel behavior = this.warningImportService.getBehaviorById(id);
				this.warningImportService.delBehavior(behavior);
			}
		}
		return "success";
	}
	
	
}
