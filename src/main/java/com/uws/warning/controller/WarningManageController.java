package com.uws.warning.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ICommonRoleService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.warning.WarningForwardModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;
import com.uws.warning.service.IWarningForwardService;

/**
 * 
* @ClassName: WarningManageController 
* @Description: 预警维护管理控制Controller
* @author 联合永道
* @date 2015-12-29 下午3:05:13 
*
 */
@Controller
public class WarningManageController extends BaseController
{
	@Autowired
	private IWarningForwardService warningForwardService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICommonRoleService commonRoleSerive;
	
	private Logger logger = new LoggerFactory(this.getClass());
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private FileUtil fileUtil = FileFactory.getFileUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession("/warning/manage");
	//日期格式批量转换
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	
	/**
	 * 
	 * @Title: warningForwardManage
	 * @Description: 管理列表方法
	 * @param request
	 * @param response
	 * @param model
	 * @param warningForward
	 * @return
	 * @throws
	 */
	@RequestMapping("/warning/manage/opt-query/warningForwardList")
	public String warningForwardManage(HttpServletRequest request,HttpServletResponse response, ModelMap model,WarningForwardModel warningForward)
	{
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		String currentUserId = sessionUtil.getCurrentUserId();
		List<Dic> warningTypeList = dicUtil.getDicInfoList("WARNING_TYPE");
		/*
		 * 根据数据字典判断当前登录是否有对应的角色
		 * 在数据字典添加和角色维护的时候，数据字典的code一定要和角色对应的code一致，这样才可以保证类型扩展后数据也可以过滤
		 */
		List<Dic> useTypeList = new ArrayList<Dic>();
		for(Dic dic : warningTypeList)
		{
			if(commonRoleSerive.checkUserIsExist(currentUserId, dic.getCode()))
				useTypeList.add(dic);
		}
		Page page = warningForwardService.queryWarningForwardPage(pageNo, Page.DEFAULT_PAGE_SIZE, warningForward,useTypeList.toArray());
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("warningForward", warningForward);
		model.addAttribute("warningTypeList", useTypeList);
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList", dicUtil.getDicInfoList("TERM"));
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("page", page);
		
		return "/warning/warningManageList";
	}

	/**
	 * 
	 * @Title: queryWarningForward
	 * @Description: 查询 列表 方法
	 * @param request
	 * @param response
	 * @param model
	 * @param warningForward
	 * @return
	 * @throws
	 */
	@RequestMapping("/warning/query/opt-query/warningForwardPage")
	public String queryWarningForward(HttpServletRequest request,HttpServletResponse response, ModelMap model,WarningForwardModel warningForward)
	{
		this.queryWarningForwardPage(request, response, model, warningForward);
		return "/warning/warningQueryList";
	}
	
	/**
	 * 
	 * @Title: queryWarningForwardPage
	 * @Description: 列表查询公用方法
	 * @param request
	 * @param response
	 * @param model
	 * @param warningForward
	 * @throws
	 */
	private void queryWarningForwardPage(HttpServletRequest request,HttpServletResponse response, ModelMap model,WarningForwardModel warningForward)
	{
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		boolean bol = CheckUtils.isCurrentOrgEqCollege(orgId);
		if(bol)// 判断是否二级学院，添加相应的查询条件
		{
			BaseAcademyModel college = new BaseAcademyModel();
			college.setId(orgId);
			warningForward.setCollege(college);
		}
		Page page = warningForwardService.queryWarningForwardPage(pageNo, Page.DEFAULT_PAGE_SIZE, warningForward);

		List<Dic> warningTypeList = dicUtil.getDicInfoList("WARNING_TYPE");
		List<Dic> yearList = dicUtil.getDicInfoList("YEAR");
		List<Dic> termList = dicUtil.getDicInfoList("TERM");
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		
		model.addAttribute("warningForward", warningForward);
		model.addAttribute("warningTypeList", warningTypeList);
		model.addAttribute("yearList", yearList);
		model.addAttribute("termList", termList);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("flag", bol);
		model.addAttribute("page", page);
	}
	
	/**
	 * 
	 * @Title: editWarningForward
	 * @Description：上报 编辑
	 * @param request
	 * @param response
	 * @param model
	 * @param warningForward
	 * @return
	 * @throws
	 */
	@RequestMapping(value={"/warning/manage/opt-add/addWarningForward","/warning/manage/opt-edit/editWarningForward"})
	public String editWarningForward(HttpServletRequest request,HttpServletResponse response, ModelMap model,String id)
	{
		WarningForwardModel warningForward = new WarningForwardModel();
		String currentUserId = sessionUtil.getCurrentUserId();
		List<Dic> warningTypeList = dicUtil.getDicInfoList("WARNING_TYPE");
		/*
		 * 根据数据字典判断当前登录是否有对应的角色
		 * 在数据字典添加和角色维护的时候，数据字典的code一定要和角色对应的code一致，这样才可以保证类型扩展后数据也可以过滤
		 */
		List<Dic> useTypeList = new ArrayList<Dic>();
		for(Dic dic : warningTypeList)
		{
			if(commonRoleSerive.checkUserIsExist(currentUserId, dic.getCode()))
				useTypeList.add(dic);
		}
		if(!StringUtils.isEmpty(id))
		{
			logger.debug("编辑预警信息");
			warningForward = warningForwardService.findById(id);
			List<UploadFileRef> fileList = fileUtil.getFileRefsByObjectId(id);
			model.addAttribute("uploadFileRefList", fileList);
		}else{
			logger.debug("新增预警信息，赋值默认的系统时间");
			warningForward.setWarningDate(new Date());
			model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("termList", dicUtil.getDicInfoList("TERM"));
			model.addAttribute("warningTypeList", useTypeList);
			model.addAttribute("collegeList", baseDataService.listBaseAcademy());
		}
		model.addAttribute("warningForward", warningForward);

		if(!StringUtils.isEmpty(id))
			return "/warning/editWarningForward";
		return "/warning/addWarningForward";
	}
	
	/**
	 * 
	 * @Title: saveWarningForward
	 * @Description: save WarningForwardModel infos 
	 * @param request
	 * @param response
	 * @param model
	 * @param id
	 * @param fileIds
	 * @return
	 * @throws
	 */
	@RequestMapping("/warning/manage/opt-submit/saveWarningForward")
	public String saveWarningForward(HttpServletRequest request,HttpServletResponse response, ModelMap model,WarningForwardModel warningForward,String[] fileId)
	{
		if(null!=warningForward)
		{
			User currentUser = new User();
			currentUser.setId(sessionUtil.getCurrentUserId());
			warningForwardService.saveOrUpdateWarningForward(warningForward,fileId,currentUser);
		}
		return "redirect:/warning/manage/opt-query/warningForwardList.do";
	}
	
	/**
	 * 
	 * @Title: saveWarningForward
	 * @Description: 预警记录信息删除  (物理删除)
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("/warning/manage/opt-del/delWarningForward")
	public String saveWarningForward(HttpServletRequest request,HttpServletResponse response,String id)
	{
		/*
		 * 只删除表中的记录,附件记录和对应的文件没有删除
		 */
		if(!StringUtils.isEmpty(id))
			warningForwardService.deleteById(id);
		return "success";
	}
	
	/**
	 * 
	 * @Title: viewWarningForward
	 * @Description:  查看详细信息
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping("/warning/query/view/viewWarningForward")
	public String viewWarningForward(HttpServletRequest request,HttpServletResponse response, String id,ModelMap model)
	{
		WarningForwardModel warningForward = new WarningForwardModel();
		if(!StringUtils.isEmpty(id))
		{
			warningForward = warningForwardService.findById(id);
			List<UploadFileRef> fileList = fileUtil.getFileRefsByObjectId(id);
			model.addAttribute("uploadFileRefList", fileList);
		}
		model.addAttribute("warningForward", warningForward);
		return "/warning/viewWarningForward";
	}
	
	/**
	 * 
	 * @Title: checkWarningForward
	 * @Description: 判断查询
	 * @param request
	 * @param college
	 * @param year
	 * @param term
	 * @param warningType
	 * @return
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("/warning/manage/opt-check/checkWarningForward")
	public String checkWarningForward(HttpServletRequest request,String college,String year,String term,String warningType)
	{
		String result = "";
		if(!StringUtils.isEmpty(college) &&!StringUtils.isEmpty(year) &&!StringUtils.isEmpty(term) &&!StringUtils.isEmpty(warningType))
		{
			WarningForwardModel warningForward = warningForwardService.queryByConditions(college, year, term, warningType);
			if(null != warningForward && !"".equals(warningForward.getId()))
				result = warningForward.getId();
		}
		return result;
	}
	
	
}
