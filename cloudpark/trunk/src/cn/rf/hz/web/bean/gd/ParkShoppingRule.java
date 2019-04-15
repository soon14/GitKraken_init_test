package cn.rf.hz.web.bean.gd;

/**
 * 
 * 停车场购物减免规则
 * 
 * @author 程依寿
 * 2014年3月11日 下午3:26:32
 */
public class ParkShoppingRule
{
	private Integer id;
	private String name;
	private Integer total;
	private Integer relief;
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

	public Integer getTotal()
	{
		return total;
	}

	public void setTotal(Integer total)
	{
		this.total = total;
	}

	public Integer getRelief()
	{
		return relief;
	}

	public void setRelief(Integer relief)
	{
		this.relief = relief;
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
