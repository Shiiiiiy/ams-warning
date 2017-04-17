package com.uws.warning.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.SpringBeanLocator;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

public class WarningImportRule implements IRule
{   
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    List<Dic> onlyChildList = dicUtil.getDicInfoList("Y&N");
    List<Dic> childFosterList = dicUtil.getDicInfoList("YES_OR_NO");
    List<Dic> psychologyAssessmentList = dicUtil.getDicInfoList("PSYCHOLOGY_ASSESSMENT");
    List<Dic> problemAssessmentList = dicUtil.getDicInfoList("PROBLEM_ASSESSMENT");
    List<Dic> isHospitalMedicationList = dicUtil.getDicInfoList("IS_HOSPITAL_MEDICATION");
    List<Dic> dangerAssessmentList = dicUtil.getDicInfoList("DANGER_ASSESSMENT");
    List<Dic> concernOpinionList = dicUtil.getDicInfoList("CONCERN_OPINION");
	@Override
    public void format(ExcelData arg0, ExcelColumn arg1, Map arg2)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void operation(ExcelData arg0, ExcelColumn arg1, Map arg2,
            Map<String, ExcelData> arg3, int arg4)
    {
		if("onlyChild".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "I");
			for (Dic dic : this.onlyChildList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("childFoster".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "J");
			for (Dic dic : this.childFosterList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("psychologyAssessment".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "K");
			for (Dic dic : this.psychologyAssessmentList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("problemAssessment".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "L");
			for (Dic dic : this.problemAssessmentList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("isHospitalMedication".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "M");
			for (Dic dic : this.isHospitalMedicationList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("dangerAssessment".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "N");
			for (Dic dic : this.dangerAssessmentList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("collegeOpinion".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "O");
			for (Dic dic : this.concernOpinionList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
		        }
		}
		if("psychologyOpinion".equals(arg1.getName())){
			String untinValue = getString(arg4, arg3, "P");
			for (Dic dic : this.concernOpinionList)
				if (untinValue.equals(dic.getName())) {
					arg0.setValue(dic);
					break;
				}
		}
		if("studentStr".equals(arg1.getName())) {
			String stuId = this.subStr(this.getString(arg4, arg3, "E"));
			BigDecimal bd = new BigDecimal(stuId);
			arg0.setValue(bd.toPlainString());
		}
    }


	@Override
    public void validate(ExcelData arg0, ExcelColumn column, Map arg2)
            throws ExcelException
    {
		String value = arg0.getValue().toString();
		boolean flag = false; boolean insert = false;
		if ("onlyChildText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.onlyChildList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("childFosterText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.childFosterList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("psychologyAssessmentText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.psychologyAssessmentList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("problemAssessmentText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.problemAssessmentList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("isHospitalMedicationText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.isHospitalMedicationList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("dangerAssessmentText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.dangerAssessmentList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("collegeOpinionText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.concernOpinionList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("psychologyOpinionText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.concernOpinionList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		if ("学号".equals(column.getName())) {
			String code = arg0.getValue().toString();
			BigDecimal bd = new BigDecimal(code);
			code = bd.toString();
			if (studentCommonService.queryStudentByStudentNo(code)==null){
				String isText = arg0.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("+ code+ ")与在系统中没有找到匹配学号的学生信息，请修正后重新上传；<br/>");
			}
		}
    }
	
	private String getString(int site, Map eds, String key){
        String s = "";
        String keyName = (new StringBuilder("$")).append(key).append("$").append(site).toString();
        if(eds.get(keyName) != null && ((ExcelData)eds.get(keyName)).getValue() != null)
            s = (new StringBuilder(String.valueOf(s))).append((String)((ExcelData)eds.get(keyName)).getValue()).toString();
        return s.trim();
    }
	
	private String subStr(String str) {
		
		if(str.endsWith(".0")) {
			str = str.replace(".0", "");
		}
		return str.trim();
	}
}
