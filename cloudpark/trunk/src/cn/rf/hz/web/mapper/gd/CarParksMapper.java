package cn.rf.hz.web.mapper.gd;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.mapper.BaseMapper;
import cn.rf.hz.web.model.BaseModel;

/**
 * CarParks Mapper
 * @author Administrator
 *
 */
public interface CarParksMapper<T> extends BaseMapper<T> {
	

	/**
	 * 查询最近一个月活跃用户数量
	 * @param day
	  * @param topNum 查询默认用户数量，默认15条
	 * @return
	 */
	public List<T> findListByRecentDayAndTopNum(@Param("day")Integer day,@Param("topNum")Integer topNum,@Param("companyIds")String companyIds);
	
	/**
	 * updateEmptyParkingNum
	 * @param num
	  * @param carParkId  
	 * @return
	 */
	public int updateEmptyParkingNum(@Param("parkingEmptyNum")int parkingEmptyNum,@Param("recordId")int recordId);
	
	/**
	 * 根据登录信息查询停车场
	 * @param loginName 登录名
	 * @param loginPass 登录密码
	 * @param machineCode 机器码
	 * @return 停车场详细信息
	 */
	public CarParks findByWSLoginInfo(@Param("loginName")String loginName, @Param("loginPass")String loginPass, @Param("machineCode")String machineCode);
	
	/**
	 * 查询生效停车场详细信息
	 * @param id
	 * @return
	 */
	public T getCarParkById1111(@Param("carParkId")Integer carParkId);
	
	/**
	 * 查询信息用户（停车场）总数
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public int queryInfoListByListByCount(BaseModel model);
	/**
	 * 查询信息用户（停车场）
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public List<T> queryInfoListByList(BaseModel model) ;
	/**
	 * 查询待续费用户总数
	 * @param model
	 * @return
	 */
	public int queryrenewListByCount(BaseModel model);
	/**
	 * 查询待续费用户
	 * @param model
	 * @return
	 */
	public List<T> queryrenewListByList(BaseModel model);
	
	/**
	 * 查询过期用户总数
	 * @param model
	 * @return
	 */
	public int queryExpiredListByCount(BaseModel model);
	/**
	 * 查询过期用户
	 * @param model
	 * @return
	 */
	public List<T> queryExpiredListByList(BaseModel model);
	
	public List<T> queryAll();
	
	/**
	 * 检查 停车场通讯的登录账号
	 *@param loginName
	 * @return
	 */
	public T getUserCountByLoginName(String loginName);
	
	/**
	 * 查询子停车场
	 * @param model
	 * @return
	 */
	public List<T> getParksByParentId(BaseModel model);
	
	
	/**
	 * 查询当前登录用户下停车场总数
	 * @param model
	 * @return
	 */
	public int queryByParentIdCount(BaseModel model);
	/**
	 * 查询当前登录用户下停车场
	 * @param model
	 * @return
	 */
	public List<T> queryByParentIdList(BaseModel model);
	
	public List<T> queryVenueMaps(Map<String, Integer> map);

	public List<Map<String, Object>> findLatitudeAndLongitude(Map<String, Object> map);

	public Map<String, Object> getCarParkScoreById(Map<String, Object> map);

	public int findLatitudeAndLongitudeCount(Map<String, Object> map);

	public List<Map<String,Object>> queryCarParkId(Map<String, Object> map);

	public Map<String, Object> findParkingEntranceRecordByid(Map<String, Object> map);

	public List<Map<String, Object>> findParkingEntranceRecordList(Map<String, Object> m);

	public int updateCarParkTotalNum(Map<String, Object> map1);

	public int updateCarParkEmptyNum(Map<String, Object> map1);

	public List<Map<String, Object>> findCarParkInfoName(Map<String, Object> map1);

	public int updateCarParkEmptyNumSubtract(Map<String, Object> map1);

	public int updateCarParkEmptyNumAdd(Map<String, Object> map1);

	public Map<String, Object> getCarParkById(Integer carParkId);

	public List<Map<String, Object>> findParkingInfo(Map<String, Object> map1);

	public int saveGdCarparkEvaluate(Map<String, Object> map);

	public Map<String, Object> saveOrUpdateCarParksScoreCountAll(Map<String, Object> map);

	public int saveOrUpdateCarParksScoreUpdate(Map<String, Object> map);

	public int saveOrUpdateCarParksScoreSave(Map<String, Object> map);

	public int updateCarParksGateInfo(Map<String, Object> map);

	public int saveCarPark(Map<String, Object> map);

	public int updateShareMode(Map<String, Object> map);

	public int updateCarParkTotalNumAndEmptyNum(Map<String, Object> map);

	public Map<String, Object> findCarParkRule(JSONObject jsonData);

	public List<Map<String, Object>> findArea(Map<String, Object> map);

	public int delete(int recordId);

	public List<Map<String, Object>> findCarParkInfo();

	public Map<String, Object> carParkReport(Map<String, Object> map);

	public Map<String, Object> getDisparkTime(JSONObject json);

	public int updateDisparkTime(JSONObject json);

	public int saveDisparkTime(JSONObject json);
}
