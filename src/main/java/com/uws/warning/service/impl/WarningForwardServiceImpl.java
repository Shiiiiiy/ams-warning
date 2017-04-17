package com.uws.warning.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.warning.WarningForwardModel;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.warning.dao.IWarningForwardDao;
import com.uws.warning.service.IWarningForwardService;

/**
 * 
* @ClassName: WarningForwardServiceImpl 
* @Description: 预警管理 service 
* @author 联合永道
* @date 2015-12-30 上午9:44:27 
*
 */
@Service("com.uws.warning.service.impl.WarningForwardServiceImpl")
public class WarningForwardServiceImpl extends BaseServiceImpl implements IWarningForwardService
{
	@Autowired
	private IWarningForwardDao  warningForwardDao;
	private FileUtil fileUtil = FileFactory.getFileUtil();
	
	/**
	 * 描述信息: WarningForward infos paged query 
	 * @param pageNo
	 * @param pageSize
	 * @param warningForward
	 * @return
	 * 2015-12-30 上午10:13:33
	 */
	@Override
    public Page queryWarningForwardPage(int pageNo, int pageSize,WarningForwardModel warningForward)
    {
	    return warningForwardDao.queryWarningForwardPage(pageNo,pageSize,warningForward);
    }

	/**
	 * 描述信息: 维护列表分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param warningForward
	 * @param queryCode
	 * @return
	 * 2016-1-14 下午3:09:36
	 */
	@Override
    public Page queryWarningForwardPage(int pageNo, int pageSize, WarningForwardModel warningForward, Object[] queryCode)
    {
		return warningForwardDao.queryWarningForwardPage(pageNo,pageSize,warningForward,queryCode);
    }
	
	/**
	 * 描述信息: find by primaryKey 
	 * @param id
	 * @return
	 * 2015-12-30 上午10:58:11
	 */
	@Override
    public WarningForwardModel findById(String id)
    {
	    if(!StringUtils.isEmpty(id))
	    	return (WarningForwardModel) warningForwardDao.get(WarningForwardModel.class, id);
	    return null;
    }

	/**
	 * 描述信息: save info
	 * @param model
	 * 2015-12-30 上午11:41:55
	 */
	@Override
    public void saveWarningForwardModel(WarningForwardModel model)
    {
	    if(null != model)
	    	warningForwardDao.save(model);
    }

	/**
	 * 描述信息: update info
	 * @param model
	 * 2015-12-30 上午11:42:06
	 */
	@Override
    public void updateWarningForwardModel(WarningForwardModel model)
    {
	    if(null!=model && !StringUtils.isEmpty(model.getId()))
	    	warningForwardDao.update(model);
    }

	/**
	 * 描述信息: 修改 保存 提交的信息
	 * @param model
	 * @param fileIds
	 * 2015-12-30 上午11:46:43
	 */
	@Override
    public void saveOrUpdateWarningForward(WarningForwardModel model, String[] fileIds,User currentUser)
    {
	    /*
	     * 1、预警信息对象保存
	     */
		if (ArrayUtils.isEmpty(fileIds))
  			fileIds = new String[0];
		String id = model.getId();
	    if(!StringUtils.isEmpty(id))
	    {
	    	model.setUpdateUser(currentUser);
	    	model.setFileNumber(fileIds.length);
	    	this.updateWarningForwardModel(model);
	    }else
	    {
	    	model.setCreator(currentUser);
	    	model.setUpdateUser(currentUser);
	    	model.setFileNumber(fileIds.length);
	    	this.saveWarningForwardModel(model);
	    	id = model.getId();
	    }
	    /*
	     * 2、预警附件对象处理 对应的objectId  为 第一步保存的ID
	     */
	     List<UploadFileRef> list = fileUtil.getFileRefsByObjectId(id);
	     for (UploadFileRef ufr : list) {
	       if (!ArrayUtils.contains(fileIds, ufr.getUploadFile().getId()))
	         fileUtil.deleteFormalFile(ufr);
	    }
	     for (String fileId : fileIds){
	       fileUtil.updateFormalFileTempTag(fileId, id);
	  }
    }

	/**
	 * 描述信息: delete infos (Physical deletion)
	 * @param id
	 * 2015-12-30 下午2:37:26
	 */
	@Override
    public void deleteById(String id)
    {
	    if(!StringUtils.isEmpty(id))
	    	warningForwardDao.deleteById(WarningForwardModel.class, id);
    }

	/**
	 * 描述信息: 按照条件查询
	 * @param college
	 * @param year
	 * @param term
	 * @param warningType
	 * @return
	 * 2015-12-30 下午4:15:45
	 */
	@Override
    public WarningForwardModel queryByConditions(String college, String year,
            String term, String warningType)
    {
	    return warningForwardDao.queryByConditions(college, year, term, warningType);
    }

}
