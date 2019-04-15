package cn.rf.hz.web.service.gd;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.mapper.gd.DataErrorMapper;

@Service("dataErrorService")
public class DataErrorService
{
	@Autowired
	private DataErrorMapper dataErrorMapper;

	/**
	 * 入库
	 * 异常数据
	 * 
	 * 
	 * @param businessType //业务类型
	 * @param carParkId 
	 * @param oldRecordId
	 * @param errorInfo //错误信息
	 * @param sourceData// 源数据
	 */
	public void save(String businessType, Integer carParkId, Integer oldRecordId, String errorInfo, String sourceData)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessType", businessType);
		map.put("carParkId", carParkId);
		map.put("oldRecordId", oldRecordId);
		map.put("errorInfo", errorInfo);
		map.put("sourceData", sourceData);
		
		this.dataErrorMapper.inserDataError(map);
				
		
	}
}
