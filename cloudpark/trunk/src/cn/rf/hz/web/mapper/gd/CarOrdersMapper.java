package cn.rf.hz.web.mapper.gd;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.bean.gd.OrderForm;
import cn.rf.hz.web.mapper.BaseMapper;
import cn.rf.hz.web.model.BaseModel;

/**
 * CarParks Mapper
 * @author Administrator
 *
 */
public interface CarOrdersMapper<T> extends BaseMapper<T> {
	

	public int update1(Map<String, Object> map);

	public int update2(Map<String, Object> map);

	public int queryOrderFormWhether(Map<String, Object> map);

	public int updateStopParking(Map<String, Object> map);

	public int updateCancelParkingSpace(Map<String, Object> map);

	public Map<String, Object> findCancelParkingSpace(Map<String, Object> map);

	public int admissionScanCodeCount1(Map<String, Object> map);

	public Map<String, Object> admissionScanCodePhoneMap(Map<String, Object> map);

	public int admissionScanCodeSave(OrderForm orderForm);

	public Map<String, Object> queryOrderByUserPhoneId(Map<String, Object> map);

	public Map<String, Object> queryIsItEffective(Map<String, Object> map);

	public Integer queryEarningsDayMap(Map<String, Object> map);

	public Integer queryEarningsEarningsJdMap(Map<String, Object> map);

	public Integer queryEarningsEarningsSdMap(Map<String, Object> map);

	public Integer queryEarningsMonthMap(Map<String, Object> map);

	public Integer queryEarningsEarningsJdMapmon(Map<String, Object> map);

	public Integer queryEarningsEarningsSdMapmon(Map<String, Object> map);

	public Map<String, Object> queryEarningsYearList(Map<String, Object> map);

	public List<Map<String, Object>> queryEarningsYearListjssy(Map<String, Object> map);

	public Map<String, Object> queryEarningsYearListsdsy(Map<String, Object> map);

	public int updateCarParks(Map<String, Object> map);

	public int updateScanCodeAppearancesCount1(Map<String, Object> map);

	public int updateScanCodeAppearancesUpdrow(Map<String, Object> map);

	public Map<String, Object> updateScanCodeAppearancesResultMap(Map<String, Object> map);

	public int saveOrderForm(OrderForm orderForm);

	public int updateOrderFormRowcount(Map<String, Object> map);

	public Map<String, Object> updateOrderFormResultMap(Map<String, Object> map);

	public int deleteOrderFormRowcount(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderFormResultList(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderFormResultList2(Map<String, Object> map);

	public int queryOrderFormResultList3(Map<String, Object> map);

	public int queryOrderFormResultList4(Map<String, Object> map);

	public Map<String, Object> queryOrderForm4PauseOrdersMap(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList1(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList2(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList3(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList4(Map<String, Object> map);

	public int earningsJd1(Map<String, Object> map);

	public int earningsJd2(Map<String, Object> map);

	public int updateNotifyAppearances(Map<String, Object> map);

	public Map<String, Object> updateNotifyAppearancesResultMap(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountExOrderList(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountEnterrecoedList(Map<String, Object> map);

	public Map<String, Object> findOrderformByid(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountExByPlateOrderList(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountEntByPlateerrecoedList(Map<String, Object> map);

	public int updateOrderform(Map<String, Object> map);

	public List<Map<String, Object>> contextInitializedFind(Map<String, Object> map);

	public Object contextInitializedUpdate(Map<String, Object> map);

	public int queryOrderForm4ResuCount1(Map<String, Object> map);

	public int queryOrderForm4ResuCount2(Map<String, Object> map);

	public int queryOrderForm4ResuCount3(Map<String, Object> map);

	public int queryOrderForm4ResuCount4(Map<String, Object> map);

	public int save(Map<String, Object> map);

	public int update(Map<String, Object> map);

	public Map<String, Object> findOrderformInOutInfo(Map<String, Object> map);

	public Map<String, Object> findOrderFormCreateTime(Map<String, Object> map);

	public int saveOrderFormsBox(Map<String, Object> map);

	public Map<String, Object> updateNotifyAppearancesResultMap1(Map<String, Object> map);

	public int updateNotifyAppearances1(Map<String, Object> map);

	public Map<String, Object> updateScanCodeAppearancesResultMap1(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountExOrderBoxList(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountExByPlateOrderListBox(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList1Box(Map<String, Object> map);

	public int queryOrderForm4ResuCount1Box(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList2Box(Map<String, Object> map);

	public int queryOrderForm4ResuCount2Box(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList3Box(Map<String, Object> map);

	public int queryOrderForm4ResuCount3Box(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderForm4ResultList4Box(Map<String, Object> map);

	public int queryOrderForm4ResuCount4Box(Map<String, Object> map);

	public int updatePlayingTime(Map<String, Object> map);

	public int updateAcquiringTime(Map<String, Object> map);

	public int updateStopParkingBox(Map<String, Object> map);

	public int updateCancelParkingSpaceBox(Map<String, Object> map);

	public List<Map<String, Object>> contextInitializedFindBox(Map<String, Object> map);

	public List<Map<String, Object>> queryChargeAmountEnterrecoedListBox(Map<String, Object> map);

	public List<Map<String, Object>> queryOrderFormResultListBox(Map<String, Object> map);

	public int updateOrderFormIsItEffective(Map<String, Object> map);

	public Map<String, Object> updatePlayingTime1(Map<String, Object> map);

	public int updateItEffective(Map<String, Object> map);

}
