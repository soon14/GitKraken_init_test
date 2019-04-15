package cn.rf.hz.web.bean.gd;
/**
 * 
 * 停车场支付宝收款信息
 * 
 * @author 程依寿
 * 2015年6月25日 上午9:39:19
 */
public class AliAccount
{
	private Integer id;
	private String partner;
	private String sellerAccount ;
	private String MD5Key;
	private Integer parkId;
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public String getPartner()
	{
		return partner;
	}
	public void setPartner(String partner)
	{
		this.partner = partner;
	}
	public String getSellerAccount()
	{
		return sellerAccount;
	}
	public void setSellerAccount(String sellerAccount)
	{
		this.sellerAccount = sellerAccount;
	}
	public String getMD5Key()
	{
		return MD5Key;
	}
	public void setMD5Key(String mD5Key)
	{
		MD5Key = mD5Key;
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
