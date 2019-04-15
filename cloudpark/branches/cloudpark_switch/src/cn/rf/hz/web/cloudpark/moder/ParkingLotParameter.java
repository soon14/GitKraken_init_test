package cn.rf.hz.web.cloudpark.moder;
/**
    
* ProjectName：cloudplace-service   
* ClassName：ParkingLotParameter   
* Description：    停车场基础参数类
* CreatTime：2016年10月25日   
* @version    
*
 */
public class ParkingLotParameter {
	//是否允许临时车进场
	private boolean isallowtemporarycarin;
	//车场已满时是否允许车辆进场
	private boolean isallowcarinwhennospaces;
	//是否允许无牌车进场
	private boolean isallowunlicensedcarin;
	//默认收费规则
	private int defaultchargeruleid;
	//是否云端计费
	private boolean iscloudcharge;
	//减免是否叠加
	private boolean isreductionsuperposition;
	//是否多收费规则
	private boolean ismultiplechargerule;
	//是否上传大图
	private boolean isuploadbigimage;
	//是否上传小图
	private boolean isuploadsmallimage;
	//出发相机间隔
	private int cameratriggerinterval;
	//行呗默认规则
	private int xbproductid;
	//距离长期车有效结束时间多少天语音播报提醒
	private int voicealert;
	//是否允许免费长期车自动出场
	private boolean isallowfreelongtermcarautoout;
	//是否允许免费临时车车自动出场
	private boolean isallowfreetemporarycarAutoout;
	//是否场内切换
	private boolean isinparkingswitch;
	//长期车是否收费 
	private boolean islongtermcarcharge;
	//是否场中场
	private boolean isinparkinglot;
	
	public boolean isIsallowtemporarycarin() {
		return isallowtemporarycarin;
	}
	public void setIsallowtemporarycarin(boolean isallowtemporarycarin) {
		this.isallowtemporarycarin = isallowtemporarycarin;
	}
	public boolean isIsallowcarinwhennospaces() {
		return isallowcarinwhennospaces;
	}
	public void setIsallowcarinwhennospaces(boolean isallowcarinwhennospaces) {
		this.isallowcarinwhennospaces = isallowcarinwhennospaces;
	}
	public boolean isIsallowunlicensedcarin() {
		return isallowunlicensedcarin;
	}
	public void setIsallowunlicensedcarin(boolean isallowunlicensedcarin) {
		this.isallowunlicensedcarin = isallowunlicensedcarin;
	}
	public int getDefaultchargeruleid() {
		return defaultchargeruleid;
	}
	public void setDefaultchargeruleid(int defaultchargeruleid) {
		this.defaultchargeruleid = defaultchargeruleid;
	}
	public boolean isIscloudcharge() {
		return iscloudcharge;
	}
	public void setIscloudcharge(boolean iscloudcharge) {
		this.iscloudcharge = iscloudcharge;
	}
	public boolean isIsreductionsuperposition() {
		return isreductionsuperposition;
	}
	public void setIsreductionsuperposition(boolean isreductionsuperposition) {
		this.isreductionsuperposition = isreductionsuperposition;
	}
	public boolean isIsmultiplechargerule() {
		return ismultiplechargerule;
	}
	public void setIsmultiplechargerule(boolean ismultiplechargerule) {
		this.ismultiplechargerule = ismultiplechargerule;
	}
	public boolean isIsuploadbigimage() {
		return isuploadbigimage;
	}
	public void setIsuploadbigimage(boolean isuploadbigimage) {
		this.isuploadbigimage = isuploadbigimage;
	}
	public boolean isIsuploadsmallimage() {
		return isuploadsmallimage;
	}
	public void setIsuploadsmallimage(boolean isuploadsmallimage) {
		this.isuploadsmallimage = isuploadsmallimage;
	}
	public int getCameratriggerinterval() {
		return cameratriggerinterval;
	}
	public void setCameratriggerinterval(int cameratriggerinterval) {
		this.cameratriggerinterval = cameratriggerinterval;
	}
	public int getXbproductid() {
		return xbproductid;
	}
	public void setXbproductid(int xbproductid) {
		this.xbproductid = xbproductid;
	}
	public int getVoicealert() {
		return voicealert;
	}
	public void setVoicealert(int voicealert) {
		this.voicealert = voicealert;
	}
	public boolean isIsallowfreelongtermcarautoout() {
		return isallowfreelongtermcarautoout;
	}
	public void setIsallowfreelongtermcarautoout(boolean isallowfreelongtermcarautoout) {
		this.isallowfreelongtermcarautoout = isallowfreelongtermcarautoout;
	}
	public boolean isIsallowfreetemporarycarAutoout() {
		return isallowfreetemporarycarAutoout;
	}
	public void setIsallowfreetemporarycarAutoout(boolean isallowfreetemporarycarAutoout) {
		this.isallowfreetemporarycarAutoout = isallowfreetemporarycarAutoout;
	}
	public boolean isIsinparkingswitch() {
		return isinparkingswitch;
	}
	public void setIsinparkingswitch(boolean isinparkingswitch) {
		this.isinparkingswitch = isinparkingswitch;
	}
	public boolean isIslongtermcarcharge() {
		return islongtermcarcharge;
	}
	public void setIslongtermcarcharge(boolean islongtermcarcharge) {
		this.islongtermcarcharge = islongtermcarcharge;
	}
	public boolean isIsinparkinglot() {
		return isinparkinglot;
	}
	public void setIsinparkinglot(boolean isinparkinglot) {
		this.isinparkinglot = isinparkinglot;
	}
}
