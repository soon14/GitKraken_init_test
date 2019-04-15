package cn.rf.hz.web.service.gd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.mapper.gd.CarParksMapper;
import cn.rf.hz.web.model.BaseModel;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.RequestUtil;
import cn.rf.hz.web.utils.URLUtils;

/**
 * 
 * <br>
 * <b>功能：</b>CarParksService<br>
 * <b>作者：</b>feixiang<br>
 */
@Service("carParksService")
public class CarParksService<T> {
	
	private final static Logger LOG= Logger.getLogger(CarParksService.class); 
    
	/**
	 * 定时把停车场数据放入缓存
	 * 
	 */
	public void carParkInfoRedis()
	{
		List<Map<String,Object>> carParkInfo = getMapper().findCarParkInfo();
		if(carParkInfo.size() > 0)
		{
			JedisPoolUtils.del("carParkInfo");
			for(int i = 0; i < carParkInfo.size(); i++)
			{
				Map<String, Object> map = carParkInfo.get(i);
				JedisPoolUtils.hset("carParkInfo", map.get("recordId") + "", JSON.toJSONString(map));
			}
		}
	}
	
	/**
	 * 查询生效停车场详细信息
	 * @param id
	 * @return
	 */
	// 根据ID查询，ID 我们默认是唯一的  
	public Map<String, Object> getCarParkById(Integer carParkId){
    	LOG.info("getCarParkById");
		return getMapper().getCarParkById(carParkId);
	}
    //清除掉指定key的缓存  
    @CacheEvict(value = "carParksServiceCache", key="#carParkId+'getCarParkById'")  
	public int updateEmptyParkingNum(int num, int carParkId){
		return this.getMapper().updateEmptyParkingNum(num,carParkId);
	}
	/**
	 * 根据登录信息查询停车场
	 * 
	 * @param loginName
	 *            登录名
	 * @param loginPass
	 *            登录密码
	 * @param machineCode
	 *            机器码
	 * @return 停车场详细信息
	 */
	public CarParks findByWSLoginInfo(String loginName, String loginPass,
			String machineCode) {
		return getMapper().findByWSLoginInfo(loginName, loginPass, machineCode);
	}
	public int queryByCount(BaseModel model)throws Exception{
		return getMapper().queryByCount(model);
	}

	public T queryById(Object id) throws Exception{
		return getMapper().queryById(id);
	}
	
	
	/**
	 * 查询最近一个月活跃用户数量
	 * @param day
	  * @param topNum 查询默认用户数量，默认15条
	 * @return
	 */
	public List<T> findListByRecentDayAndTopNum( Integer day, Integer topNum,String companyIds) throws Exception{
		return getMapper().findListByRecentDayAndTopNum(day, topNum, companyIds);
	}
	/**
	 * 查询信息用户（停车场）总数
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public int queryInfoListByListByCount(BaseModel model)throws Exception{
		return getMapper().queryInfoListByListByCount(model);
	}
	/**
	 * 查询待续费用户总数
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public int queryrenewListByCount(BaseModel model)throws Exception{
		return getMapper().queryrenewListByCount(model);
	}
	/**
	 * 查询过期用户（停车场）
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public int queryExpiredListByCount(BaseModel model)throws Exception{
		return getMapper().queryExpiredListByCount(model);
	}
	/**
	 * 检查 停车场通讯的登录账号
	 *@param loginName
	 * @return int
	 */
	public T getUserCountByLoginName(String loginName){
		return getMapper().getUserCountByLoginName(loginName);
	}
	
	
	@Autowired
    private CarParksMapper<T> mapper;

		
	public CarParksMapper<T> getMapper() {
		return mapper;
	}

	public List<T> queryAll1(){
		return getMapper().queryAll();
	}
	/**
	 * 查询子停车场
	 * @param model
	 * @return
	 */
	public List<T> getParksByParentId(BaseModel model){
		return getMapper().getParksByParentId(model);
	}
	
	/**
	 * 查询当前登录用户下停车场总数
	 * @param model
	 * @return
	 */
	public int queryByParentIdCount(BaseModel model){
		return getMapper().queryByParentIdCount(model);
	}
	public List<Map<String, Object>> findLatitudeAndLongitude(Map<String, Object> map)
	{
		return getMapper().findLatitudeAndLongitude(map);
	}
	public Map<String, Object> getCarParkScoreById(Map<String, Object> map)
	{
		return getMapper().getCarParkScoreById(map);
	}
	public int findLatitudeAndLongitudeCount(Map<String, Object> map)
	{
		return getMapper().findLatitudeAndLongitudeCount(map);
	}
	public List<Map<String,Object>> queryCarParkId(Map<String, Object> map)
	{
		return getMapper().queryCarParkId(map);
	}
	public Map<String, Object> findParkingEntranceRecordByid(Map<String, Object> map)
	{
		return getMapper().findParkingEntranceRecordByid(map);
	}
	public List<Map<String, Object>> findParkingEntranceRecordList(Map<String, Object> m)
	{
		return getMapper().findParkingEntranceRecordList(m);
	}
	public int updateCarParkTotalNum(Map<String, Object> map1)
	{
		return getMapper().updateCarParkTotalNum(map1);
	}
	public int updateCarParkEmptyNum(Map<String, Object> map1)
	{
		return getMapper().updateCarParkEmptyNum(map1);
	}
	public List<Map<String, Object>> findCarParkInfoName(Map<String, Object> map1)
	{
		return getMapper().findCarParkInfoName(map1);
	}
	public int updateCarParkEmptyNumSubtract(Map<String, Object> map1)
	{
		return getMapper().updateCarParkEmptyNumSubtract(map1);
	}
	public int updateCarParkEmptyNumAdd(Map<String, Object> map1)
	{
		return getMapper().updateCarParkEmptyNumAdd(map1);
	}
	public List<Map<String, Object>> findParkingInfo(Map<String, Object> map1)
	{
		return getMapper().findParkingInfo(map1);
	}
	public int saveGdCarparkEvaluate(Map<String, Object> map)
	{
		return getMapper().saveGdCarparkEvaluate(map);
	}
	public Map<String, Object> saveOrUpdateCarParksScoreCountAll(Map<String, Object> map)
	{
		return getMapper().saveOrUpdateCarParksScoreCountAll(map);
	}
	public int saveOrUpdateCarParksScoreUpdate(Map<String, Object> map)
	{
		return getMapper().saveOrUpdateCarParksScoreUpdate(map);
	}
	public int saveOrUpdateCarParksScoreSave(Map<String, Object> map)
	{
		return getMapper().saveOrUpdateCarParksScoreSave(map);
	}
	public int updateCarParksGateInfo(Map<String, Object> map)
	{
		return getMapper().updateCarParksGateInfo(map);
	}
	
	/**
	 * 添加停车场
	 * 
	 * @param requestBody
	 * @return
	 */
	public Map<String, Object> saveCarPark(String requestBody)
	{
		
		JSONObject map = JSON.parseObject(requestBody);

		int updrow = getMapper().saveCarPark(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (updrow == 1)
			map1.put("errorcode", 0);
		else
			map1.put("errorcode", 1);

		map1.put("recordId", map.get("recordId"));
		
		return map1;
	}
	
	public int delCarPark(int recordId)
	{
		return getMapper().delete(recordId);
	}
	
	
	/**
	 * 添加销售员、停车场信息
	 * 
	 * @param requestBody
	 * @return
	 */
	public Map<String, Object> saveSalesmanCarPark(String requestBody)
	{
		
		JSONObject map = JSON.parseObject(requestBody);
		
		//添加停车场
		Map<String, Object> map2 = saveCarPark(requestBody);
		
		//回返值
		Map<String, Object> map1 = new HashMap<String, Object>();
		if (((Integer)map2.get("errorcode")) == 1)
		{
			// 销售员，施万义持久层
			String userSystem = URLUtils.get("userSystem");
			String result = RequestUtil.requestUrl(userSystem + "/SalesmanInfo/addSalesmanInfo.do?uuid=" + map.get("uuid") + "&carParkId=" + map.get("recordId"));

			Map<String, Integer> m1 = JSON.parseObject(result, Map.class);
			map1.putAll(m1);
		} else
			map1.put("errorcode", 1);

		map1.put("recordId", map.get("recordId"));
		
		return map1;
	}
	
	public int updateShareMode(Map<String, Object> map)
	{
		return getMapper().updateShareMode(map);
	}
	
	
	/**
	 * 更新车位数
	 * 
	 * @param map
	 * @return
	 */
	public String updateCarParkTotalNumAndEmptyNum(int carParkId, int parkSumNum, int parkIdleNum)
	{
		
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId);
		map1.put("parkSumNum", parkSumNum);
		map1.put("parkIdleNum", parkIdleNum);
		
		int update = getMapper().updateCarParkTotalNumAndEmptyNum(map1);
		
		JSONObject jo = new JSONObject();
		if (update == 1)
			jo.put("errorcode", 0);
		else
			jo.put("errorcode", 1);
		
		return jo.toJSONString();
	}
	public Map<String, Object> findCarParkRule(JSONObject jsonData)
	{
		return getMapper().findCarParkRule(jsonData);
	}
	public List<Map<String, Object>> findArea(Map<String, Object> map)
	{
		// TODO Auto-generated method stub
		return getMapper().findArea(map);
	}

	public Map<String, Object> carParkReport(Map<String, Object> map)
	{
		return getMapper().carParkReport(map);
	}

	
	/**
	 * 添加停车场开放段以及车位上限
	 * 
	 * @param requestBody
	 * @return
	 */
	public String saveDisparkTime(String requestBody)
	{
		
		JSONObject json =  JSON.parseObject(requestBody);
		
//		String starDevelopTime = json.getString("starDevelopTime");
//		String endDevelopTime = json.getString("endDevelopTime");
//		Integer shareParkingNum = json.getInteger("shareParkingNum");
		
		Map<String, Object> carParksDesigner = getMapper().getDisparkTime(json);
		int errorcord = 1;
		if(null != carParksDesigner )
		{
			errorcord = getMapper().updateDisparkTime(json);
		}else
			errorcord = getMapper().saveDisparkTime(json);
		
		if(errorcord > 0)
			return "{\"errorcode\":0}";
		else
			return "{\"errorcode\":1}";
		
	}
}
