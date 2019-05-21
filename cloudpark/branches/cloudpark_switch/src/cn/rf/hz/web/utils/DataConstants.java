package cn.rf.hz.web.utils;

import java.util.HashMap;
import java.util.Map;

public class DataConstants {

	public final static String CLOUDPARK_INOUT = "cloudpark_inout";
	public final static String CLOUDPARK_CORRECT = "cloudpark_correct";
	public final static String CLOUDPARK_INOUT_RECORD = "cloudpark_inout_record";
	public final static String CLOUDPARK_CHARGE = "cloudpark_charge";
	public final static String CLOUDPARK_OTHERCHARGE = "cloudpark_othercharge";
	public final static String CLOUDPARK_USERGROUP = "cloudpark_usergroup";
	public final static String CARPARK_USERDEFINENOTE = "carpark_userdefinenote";
	public final static String CLOUDPARK_SWITCH = "cloudpark_switch";
	public final static String CLOUDSHARE_INOUT = "cloudpark_xbreserve";
	public final static String History_Table_OUT = "history_out";// 最后一次出场时间
	public final static String History_Table_IN = "history_in";// 最后一次进场时间
	public final static String History_Table_OUT_ = "history_out_";// 指定车场，最后一次出场时间
	public final static String History_Table_IN_ = "history_in_";// 指定车场，最后一次进场时间
	public final static String KPRE = "k_cache_";
	public final static String SHARE_INOUT = "share_inout";
	public final static String CLOUDPARK_PARKINGLOTPARAM_CACHE_ = "cloudpark_parkinglotparam_cache_"; // redis中车场相关参数缓存

	public final static String CARINDATA = "carindata";
	public final static String CARINDATAERROR = "carindataerror";

	public final static String CARCHARGEDATA = "carchargedata";
	public final static String CARCHARGEDATAERROR = "carchargedataerror";

	public final static String CLOUDPARK_ANOIN = "cloudpark_anoin";// 进场异常
	public final static String CLOUDPARK_ANOOUT = "cloudpark_anoout";// 出场异常
	public final static String CLOUDPARK_OPENGATE = "cloudpark_opengate";// 出场异常

	public final static String CAROUTDATA = "caroutdata";
	public final static String CAROUTDATAERROR = "caroutdataerror";

	public final static String CHANNEL_CACHE_ = "channel_cache_";
	public final static String CLOUDPARK_AREA_CACHE_ = "cloudpark_area_cache_";

	public final static String CLOUDPLACE_USERGROUP = "cloudplace_usergroup";
	public final static String CLOUDPLACE_ABNORMALTAG = "cloudplace_abnormaltag";
	// public final static String SHARE_INOUT_FLOW = "share_inout_flow";//
	// redis中数据存储的数据域
	@SuppressWarnings("serial")
	public final static Map<String, String> DATA_REDIS_LIST_KEY_MAP = new HashMap<String, String>() {// 初始化，数据类型和redis
		// key
		// 对应关系
		{
			put("cloudpark_xbreserve", "xbreserve_list");// 进出场
			put("cloudpark_inout", "inout_list");// 进出场
			put("cloudpark_charge", "charge_list");// 收费
			put("cloudpark_usergroup", "usergroup_list");// 黑白名单，长期用户
			put("carpark_userdefinenote", "userdefinenote_list");// 黑白名单，长期用户
			put("surplus", "surplus_list");// 空余位
			put("cloudpark_switch", "switch_list");// 切换记录
		}
	};

}