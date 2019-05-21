package cn.rf.hz.web.model;

/**
 * 
 * @author feixiang
 *
 */
public class BaseModel {

	private Integer page = 1;
	
	private Integer rows =10;
	
	private String sort;
	
	private String order;

	private boolean isStart;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return the isStart
	 */
	public boolean isStart() {
		return isStart;
	}

	/**
	 * @param isStart the isStart to set
	 */
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
}
