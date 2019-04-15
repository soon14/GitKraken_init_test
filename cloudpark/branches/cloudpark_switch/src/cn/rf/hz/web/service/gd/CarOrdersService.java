package cn.rf.hz.web.service.gd;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.bean.gd.OrderForm;
import cn.rf.hz.web.mapper.gd.CarOrdersMapper;

/**
 * APP 订单
 * 
 * @author 程依寿 2015年9月21日 下午4:10:32
 * @param <T>
 */
@Service("carOrdersService")
public class CarOrdersService<T>
{

	private final static Logger LOG = Logger.getLogger(CarOrdersService.class);
	@Autowired
	private CarOrdersMapper<T> mapper;

	
	public CarOrdersMapper<T> getMapper()
	{
		return mapper;
	}


	public void setMapper(CarOrdersMapper<T> mapper)
	{
		this.mapper = mapper;
	}


	public int update1(Map<String, Object> map)
	{
		return getMapper().update1(map);
	}
	
	public int update2(Map<String, Object> map)
	{
		return getMapper().update2(map);
	}


	public int queryOrderFormWhether(Map<String, Object> map)
	{
		return getMapper().queryOrderFormWhether(map);
	}


	public int updateStopParking(Map<String, Object> map)
	{
		return getMapper().updateStopParking(map);
	}


	public int updateCancelParkingSpace(Map<String, Object> map)
	{
		return getMapper().updateCancelParkingSpace(map);
	}



	public Map<String, Object> findCancelParkingSpace(Map<String, Object> map)
	{
		return getMapper().findCancelParkingSpace(map);
	}


	public int admissionScanCodeCount1(Map<String, Object> map)
	{
		return getMapper().admissionScanCodeCount1(map);
	}


	public Map<String, Object> admissionScanCodePhoneMap(Map<String, Object> map)
	{
		return getMapper().admissionScanCodePhoneMap(map);
	}


	public int admissionScanCodeSave(OrderForm orderForm)
	{
		return getMapper().admissionScanCodeSave(orderForm);
	}


	public Map<String, Object> queryOrderByUserPhoneId(Map<String, Object> map)
	{
		return getMapper().queryOrderByUserPhoneId(map);
	}


	public Map<String, Object> queryIsItEffective(Map<String, Object> map)
	{
		return getMapper().queryIsItEffective(map);
	}


	public Integer queryEarningsDayMap(Map<String, Object> map)
	{
		return getMapper().queryEarningsDayMap(map);
	}


	public Integer queryEarningsEarningsJdMap(Map<String, Object> map)
	{
		return getMapper().queryEarningsEarningsJdMap(map);
	}


	public Integer queryEarningsEarningsSdMap(Map<String, Object> map)
	{
		return getMapper().queryEarningsEarningsSdMap(map);
	}


	public Integer queryEarningsMonthMap(Map<String, Object> map)
	{
		return getMapper().queryEarningsMonthMap(map);
	}


	public Integer queryEarningsEarningsJdMapmon(Map<String, Object> map)
	{
		return getMapper().queryEarningsEarningsJdMapmon(map);
	}


	public Integer queryEarningsEarningsSdMapmon(Map<String, Object> map)
	{
		return getMapper().queryEarningsEarningsSdMapmon(map);
	}


	public Map<String, Object> queryEarningsYearList(Map<String, Object> map)
	{
		return getMapper().queryEarningsYearList(map);
	}


	public List<Map<String, Object>> queryEarningsYearListjssy(Map<String, Object> map)
	{
		return getMapper().queryEarningsYearListjssy(map);
	}


	public Map<String, Object> queryEarningsYearListsdsy(Map<String, Object> map)
	{
		return getMapper().queryEarningsYearListsdsy(map);
	}


	public int updateCarParks(Map<String, Object> map)
	{
		return getMapper().updateCarParks(map);
	}


	public int updateScanCodeAppearancesCount1(Map<String, Object> map)
	{
		return getMapper().updateScanCodeAppearancesCount1(map);
	}


	public int updateScanCodeAppearancesUpdrow(Map<String, Object> map)
	{
		return getMapper().updateScanCodeAppearancesUpdrow(map);
	}


	public Map<String, Object> updateScanCodeAppearancesResultMap(Map<String, Object> map)
	{
		return getMapper().updateScanCodeAppearancesResultMap(map);
	}


	public int saveOrderForm(OrderForm orderForm)
	{
		return getMapper().saveOrderForm(orderForm);
	}


	public int updateOrderFormRowcount(Map<String, Object> map)
	{
		return getMapper().updateOrderFormRowcount(map);
	}


	public Map<String, Object> updateOrderFormResultMap(Map<String, Object> map)
	{
		return getMapper().updateOrderFormResultMap(map);
	}


	public int deleteOrderFormRowcount(Map<String, Object> map)
	{
		return getMapper().deleteOrderFormRowcount(map);
	}


	public List<Map<String, Object>> queryOrderFormResultList(Map<String, Object> map)
	{
		return getMapper().queryOrderFormResultList(map);
	}


	public List<Map<String, Object>> queryOrderFormResultList2(Map<String, Object> map)
	{
		return getMapper().queryOrderFormResultList2(map);
	}


	public int queryOrderFormResultList3(Map<String, Object> map)
	{
		return getMapper().queryOrderFormResultList3(map);
	}


	public int queryOrderFormResultList4(Map<String, Object> map)
	{
		return getMapper().queryOrderFormResultList4(map);
	}


	public Map<String, Object> queryOrderForm4PauseOrdersMap(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4PauseOrdersMap(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList1(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList1(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList2(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList2(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList3(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList3(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList4(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList4(map);
	}


	public int earningsJd1(Map<String, Object> map)
	{
		return getMapper().earningsJd1(map);
	}


	public int earningsJd2(Map<String, Object> map)
	{
		return getMapper().earningsJd2(map);
	}


	public int updateNotifyAppearances(Map<String, Object> map)
	{
		return getMapper().updateNotifyAppearances(map);
	}


	public Map<String, Object> updateNotifyAppearancesResultMap(Map<String, Object> map)
	{
		return getMapper().updateNotifyAppearancesResultMap(map);
	}


	public List<Map<String, Object>> queryChargeAmountExOrderList(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountExOrderList(map);
	}


	public List<Map<String, Object>> queryChargeAmountEnterrecoedList(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountEnterrecoedList(map);
	}


	public Map<String, Object> findOrderformByid(Map<String, Object> map)
	{
		return getMapper().findOrderformByid(map);
	}


	public List<Map<String, Object>> queryChargeAmountExByPlateOrderList(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountExByPlateOrderList(map);
	}


	public List<Map<String, Object>> queryChargeAmountEntByPlateerrecoedList(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountEntByPlateerrecoedList(map);
	}


	public int updateOrderform(Map<String, Object> map)
	{
		return getMapper().updateOrderform(map);
	}


	public List<Map<String, Object>> contextInitializedFind(Map<String, Object> map)
	{
		return getMapper().contextInitializedFind(map);
	}


	public Object contextInitializedUpdate(Map<String, Object> map)
	{
		return getMapper().contextInitializedUpdate(map);
		
	}


	public int queryOrderForm4ResuCount1(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount1(map);
	}


	public int queryOrderForm4ResuCount2(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount2(map);
	}


	public int queryOrderForm4ResuCount3(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount3(map);
	}


	public int queryOrderForm4ResuCount4(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount4(map);
	}


	public int save(Map<String, Object> map)
	{
		return getMapper().save(map);
	}


	public int update(Map<String, Object> map)
	{
		return getMapper().update(map);
	}


	public Map<String, Object> findOrderformInOutInfo(Map<String, Object> map)
	{
		return getMapper().findOrderformInOutInfo(map);
	}


	public Map<String, Object> findOrderFormCreateTime(Map<String, Object> map)
	{
		return getMapper().findOrderFormCreateTime(map);
	}


	public int saveOrderFormsBox(Map<String, Object> map)
	{
		return getMapper().saveOrderFormsBox(map);
	}


	public Map<String, Object> updateNotifyAppearancesResultMap1(Map<String, Object> map)
	{
		return getMapper().updateNotifyAppearancesResultMap1(map);
	}


	public int updateNotifyAppearances1(Map<String, Object> map)
	{
		return getMapper().updateNotifyAppearances1(map);
	}


	public Map<String, Object> updateScanCodeAppearancesResultMap1(Map<String, Object> map)
	{
		return getMapper().updateScanCodeAppearancesResultMap1(map);
	}


	public List<Map<String, Object>> queryChargeAmountExOrderBoxList(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountExOrderBoxList(map);
	}


	public List<Map<String, Object>> queryChargeAmountExByPlateOrderListBox(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountExByPlateOrderListBox(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList1Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList1Box(map);
	}


	public int queryOrderForm4ResuCount1Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount1Box(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList2Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList2Box(map);
	}


	public int queryOrderForm4ResuCount2Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount2Box(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList3Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList3Box(map);
	}


	public int queryOrderForm4ResuCount3Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount3Box(map);
	}


	public List<Map<String, Object>> queryOrderForm4ResultList4Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResultList4Box(map);
	}


	public int queryOrderForm4ResuCount4Box(Map<String, Object> map)
	{
		return getMapper().queryOrderForm4ResuCount4Box(map);
	}


	public int updatePlayingTime(Map<String, Object> map)
	{
		return getMapper().updatePlayingTime(map);
	}


	public int updateAcquiringTime(Map<String, Object> map)
	{
		return getMapper().updateAcquiringTime(map);
	}


	public int updateStopParkingBox(Map<String, Object> map)
	{
		return getMapper().updateStopParkingBox(map);
	}


	public int updateCancelParkingSpaceBox(Map<String, Object> map)
	{
		return getMapper().updateCancelParkingSpaceBox(map);
	}


	public List<Map<String, Object>> contextInitializedFindBox(Map<String, Object> map)
	{
		return getMapper().contextInitializedFindBox(map);
	}


	public List<Map<String, Object>> queryChargeAmountEnterrecoedListBox(Map<String, Object> map)
	{
		return getMapper().queryChargeAmountEnterrecoedListBox(map);
	}


	public List<Map<String, Object>> queryOrderFormResultListBox(Map<String, Object> map)
	{
		return getMapper().queryOrderFormResultListBox(map);
	}


	public int updateOrderFormIsItEffective(Map<String, Object> map)
	{
		return getMapper().updateOrderFormIsItEffective(map);
	}


	public Map<String, Object> updatePlayingTime1(Map<String, Object> map)
	{
		return getMapper().updatePlayingTime1(map);
	}


	public int updateItEffective(Map<String, Object> map)
	{
		return getMapper().updateItEffective(map);
	}




}
