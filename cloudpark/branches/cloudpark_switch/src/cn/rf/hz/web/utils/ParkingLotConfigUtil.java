package cn.rf.hz.web.utils;

import java.util.ResourceBundle;

public class ParkingLotConfigUtil {

	private static ResourceBundle res = ResourceBundle.getBundle("parkinglotconfig");

	public static String GetParkingLotConfig(String param) {
		String value = res.getString(param);
		return value;
	}

	/*H
	 * // 是否上传图片 public static final Integer isuploadimage =
	 * Integer.parseInt(res.getString("isuploadimage")); // 是否允许临时车入场 public
	 * static final Integer isallowtemporarycarin =
	 * Integer.parseInt(res.getString("isallowtemporarycarin")); //
	 * 无车位时是否允许无固定车位长期车入场 public static final Integer isallowcarinwhennospaces =
	 * Integer.parseInt(res.getString("isallowcarinwhennospaces")); // 是否场中场
	 * public static final Integer isinparkinglot =
	 * Integer.parseInt(res.getString("isinparkinglot")); // 是否云端计费 public
	 * static final Integer iscloudcharge =
	 * Integer.parseInt(res.getString("iscloudcharge")); // 云端计费地址 public static
	 * final String cloudchargeurl = res.getString("cloudchargeurl");
	 * 
	 * // 特殊车牌匹配规则1(首字匹配) public static final String specialcarcode1 =
	 * res.getString("specialcarcode1");
	 * 
	 * // 特殊车牌匹配规则2(包含匹配) public static final String specialcarcode2 =
	 * res.getString("specialcarcode2");
	 * 
	 * // 是否减免叠加 public static final Integer isreductionsuperposition =
	 * Integer.parseInt(res.getString("isreductionsuperposition"));
	 * 
	 * // 是否多收费规则 public static final Integer ismultiplechargerule =
	 * Integer.parseInt(res.getString("ismultiplechargerule"));
	 * 
	 * // 是否上传大图 public static final Integer isuploadbigimage =
	 * Integer.parseInt(res.getString("isuploadbigimage"));
	 * 
	 * // 是否上传小图 public static final Integer isuploadsmallimage =
	 * Integer.parseInt(res.getString("isuploadsmallimage"));
	 * 
	 * // 相机触发间隔 public static final Integer cameratriggerinterval =
	 * Integer.parseInt(res.getString("cameratriggerinterval"));
	 * 
	 * // 获取阿里云图片路径接口 public static final String getAliyunImageUrl =
	 * res.getString("getaliyunimageurl");
	 * 
	 * public static final String controllerReportUrl =
	 * res.getString("controllerreporturl");
	 */
}
