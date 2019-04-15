package cn.rf.hz.web.mapper;

import java.util.List;

import cn.rf.hz.web.model.BaseModel;
/**
 * 
 * @author feixiang
 *
 */
public interface BaseMapper<T> {

	
	public void add(T t);
	
	public Integer save(T t);
	
	public int update(T t);
	
	
	public void updateBySelective(T t); 	
	
	public void delete(Object id);
	

	public int queryByCount(BaseModel model);
	
	public List<T> queryByList(BaseModel model);
	
	
	public T queryById(Object id);
	public void updateBySelective1(T t); 
	
}
