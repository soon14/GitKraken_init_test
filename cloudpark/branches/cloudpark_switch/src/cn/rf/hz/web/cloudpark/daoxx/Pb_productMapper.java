package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;
import java.util.Map;

import cn.rf.hz.web.cloudpark.moder.Pb_product;
import cn.rf.hz.web.cloudpark.moder.Pb_productKey;

public interface Pb_productMapper {
	int deleteByPrimaryKey(Pb_productKey key);

	int insert(Pb_product record);

	int insertSelective(Pb_product record);

	Pb_product selectByPrimaryKey(Pb_productKey key);

	int updateByPrimaryKeySelective(Pb_product record);

	int updateByPrimaryKey(Pb_product record);

	List<Pb_product> selectByParkinglotno(String prakinglotno);

	List<Pb_product> selectOverByParkinglotno(String prakinglotno);

	// 获取计费包list
	List<Pb_product> selectByModel(Map<String, Object> map);

	// 获取计费包的list count
	int selectCountByModel(Map<String, Object> map);

	List<Pb_product> selectOverByModel(Map<String, Object> map);

	// 查看该车场下是否有计费包存在
	int selectCountByParkingLotNo(String parkno);

	// 区最大的id
	int selectMaxIdByParkingLotNo(String parkno);

	// selectCountForChargeRule
	int selectCountForChargeRule(Map<String, Object> map);

	/**
	 * 通过productid和parkid查询计费包
	 */
	Pb_product SelectProductByid(Map<String, Object> map);

	List<Pb_product> SelectProductByparkno(Map<String, Object> map);

	List<Pb_product> selectByCondition(Map<String, Object> map);

	List<Pb_product> selectByParkinglotnoAndIslong(Map<String, Object> map);

	String SelectProductByid1(Map<String, Object> map);

	int deleteProductByParkingLotNoAndProductID(Map<String, Object> map);

	int deleteByParkingLotNo(String parkinglotno);

}