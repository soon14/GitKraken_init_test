package cn.rf.hz.web.cloudpark.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.StringUtil;

/**
 * 
 * ProjectName：cloudreport ClassName：DateSharingServiceImp Description： 分时算法
 * CreatTime：2016年5月30日
 * 
 * @version 
 */
@Service("dateSharingServiceImp")
public class DateSharingServiceImp {

	// 获取按月分时的时间段集合
	public List<Map<String, Object>> getDateSharing(String startDateStr, String endDateStr,
			Map<String, Object> mapparam) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			// 获取开始日期的年、月
			Date startDate = DateUtil.StringfomateDate(startDateStr);
			int startYear = DateUtil.convertDateToYear(startDate);
			String startMonthStr = DateUtil.convertDateToMonth(startDate);
			int startMonth = Integer.parseInt(startMonthStr);
			// 获取开始结束日期的年、月
			Date endDate = DateUtil.StringfomateDate(endDateStr);
			int endYear = DateUtil.convertDateToYear(endDate);
			String endMonthStr = DateUtil.convertDateToMonth(endDate);
			int endMonth = Integer.parseInt(endMonthStr);
			// 同年时间段
			if (endYear == startYear) {
				int month = endMonth - startMonth;
				// 跨月时间段
				if (month > 0) {
					// 获取第一个开始时间段
					String firstEndStr = getLastDayMonth(startDateStr);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("startDate", startDateStr);
					map.put("endDate", firstEndStr);
					map.putAll(mapparam);
					list.add(map);

					for (int i = 1; i <= month; i++) {
						int newMonth = startMonth + i;
						if (newMonth == endMonth) {
							// 结束时间段
							String firstDateStr = getFirstDayMonth(endDateStr);
							Map<String, Object> map2 = new HashMap<String, Object>();
							map2.put("startDate", firstDateStr);
							map2.put("endDate", endDateStr);
							map2.putAll(mapparam);
							list.add(map2);
						} else {
							// 中间时间段
							String midfirstDateStr = startYear + "-" + (newMonth < 10 ? ("0" + newMonth) : newMonth)
									+ "-01 00:00:00";
							String midlastDateStr = getLastDayMonth(midfirstDateStr);
							Map<String, Object> map3 = new HashMap<String, Object>();
							map3.put("startDate", midfirstDateStr);
							map3.put("endDate", midlastDateStr);
							map3.putAll(mapparam);
							list.add(map3);
						}
					}
				} else {
					// 同月时间段
					Map<String, Object> map4 = new HashMap<String, Object>();
					map4.put("startDate", startDateStr);
					map4.put("endDate", endDateStr);
					map4.putAll(mapparam);
					list.add(map4);
				}
			} else {
				// 只支持跨一年的时间分段
				// 开始时间分段
				Date firstEnddate = getYearLast(startYear);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
				String firstEnddateStr = strDate(firstEnddate, format);
				List<Map<String, Object>> startlist = getDateSharing(startDateStr, firstEnddateStr, mapparam);
				list.addAll(startlist);
				// 结束时间分段
				Date endfirstdate = getYearFirst(endYear);
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String endfirstdateStr = strDate(endfirstdate, format2);
				List<Map<String, Object>> endlist = getDateSharing(endfirstdateStr, endDateStr, mapparam);
				list.addAll(endlist);
			}
			
			//按照开始时间倒序排序
			Collections.sort(list, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date startDate1 = DateUtil.getDateFromString(o1.get("startDate").toString());
					Date startDate2 = DateUtil.getDateFromString(o2.get("startDate").toString());
					Long startDateLong1 = startDate1.getTime();
					Long startDateLong2 = startDate2.getTime();
					Long sort = startDateLong1 - startDateLong2;
					return sort.intValue();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	/**
	* @Title: getPartitionids  
	* @Description: 根据时间段计算分区值
	* @param parkingLotNo 车场
	* @param startDateStr 开始时间
	* @param endDateStr	  结束时间
	* @return List<Integer>
	* @throws
	 */
	public List<Integer> getPartitionids(String parkingLotNo,String startDateStr, String endDateStr){
		List<Integer> partitionids = new ArrayList<>();
		Map<String,Object>mapparam=new HashMap<>();
		List<Map<String,Object>> list=getDateSharing(startDateStr,endDateStr,mapparam);
		for(Map<String,Object> item:list){
			Integer partitionid=getPartitionid(parkingLotNo, item.get("startDate").toString());
			partitionids.add(partitionid);
		 }
		return partitionids;
	}
	
	/**
	 * 获取进出场表分区值
	 */
	private Integer getPartitionid(String ParkingLotNo, String dateStr) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		// 分区字段开始计算
		String partitionIdTemp = "";
		Date date = DateUtil.getDateFromString(dateStr);
		if (hasresult < 10) {
			partitionIdTemp = DateUtil.getFomartDate(date, "yyyyMM") + "0" + hasresult;
		} else {
			partitionIdTemp = DateUtil.getFomartDate(date, "yyyyMM") + hasresult;
		}
		Integer partitionID = Integer.parseInt(partitionIdTemp);
		// 分区字段计算结束
		return partitionID;
	}
	
	
	/**
	 * 
	 * @Title: strDate @Description: 获取不同格式，字符串时间 @param date @param
	 * format @return String @throws
	 */
	public String strDate(Date date, SimpleDateFormat format) {
		return format.format(date);

	}

	/**
	 * 获取某年第一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public Date getYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public Date getYearLast(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}

	public String getFirstDayMonth(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		Date date = DateUtil.getDateFromString(dateStr);
		// 获取当前月第一天：
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.MONTH, 0);
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String first = format.format(ca.getTime());
		// System.out.println("===============first:" + first);
		return first;

	}

	public String getLastDayMonth(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		Date date = DateUtil.getDateFromString(dateStr);
		// 获取当前月最后一天
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		// System.out.println("===============last:" + last);
		return last;
	}

 
//	 public static void main(String[] args) {
//		 String startDateStr = "2016-08-04 00:00:00";
//		 String endDateStr = "2018-04-04 23:59:59";
//		 DateSharingServiceImp imp=new DateSharingServiceImp();
//		 Map<String,Object>mapparam=new HashMap<String,Object>();
//		 mapparam.put("partitionID", 11);
//		 mapparam.put("parkNo", "11");
//		 // 可选条件
//		 mapparam.put("channelID",1);
//		 mapparam.put("carCode", "浙A12345");
//		 mapparam.put("chargeRuleID", 1);
//		 mapparam.put("userName", "临时车");
//		 List<Map<String,Object>> list=imp.getDateSharing(startDateStr,
//		 endDateStr,mapparam);
//		 //测试
//		 for(Map<String,Object> item:list){
//			 System.out.println(item);
//		 }
//		 List<Integer>partitionidslist=imp.getPartitionids("1010",startDateStr,endDateStr);
//		 for(Integer item:partitionidslist){
//			 System.out.println("===============partitionid:" + item);
//		 }
//	 }

}
