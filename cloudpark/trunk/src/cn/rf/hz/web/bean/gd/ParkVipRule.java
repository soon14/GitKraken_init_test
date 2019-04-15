package cn.rf.hz.web.bean.gd;

/**
 * 停车场VIP减免规则
 * 
 * @author 程依寿
 * 2015年6月25日 下午1:28:39
 */
public class ParkVipRule
{
	/**
	 * 记录ID, 主键
	 */
	private Integer id;
	/**
	 * 规则名称
	 */
	private String name;
	/**
	 * VIP会员等级
	 */
	private String grade;
	/**
	 * 减免时间, 单位为小时
	 */
	private int relief;
	/**
	 * 一天允许使用的次数
	 */
	private Integer allowPerDay;
	/**
	 * 对应停车场ID
	 */
	private Integer parkId;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getGrade()
	{
		return grade;
	}

	public void setGrade(String grade)
	{
		this.grade = grade;
	}

	public int getRelief()
	{
		return relief;
	}

	public void setRelief(int relief)
	{
		this.relief = relief;
	}

	public Integer getAllowPerDay()
	{
		return allowPerDay;
	}

	public void setAllowPerDay(Integer allowPerDay)
	{
		this.allowPerDay = allowPerDay;
	}

	public Integer getParkId()
	{
		return parkId;
	}

	public void setParkId(Integer parkId)
	{
		this.parkId = parkId;
	}

}
