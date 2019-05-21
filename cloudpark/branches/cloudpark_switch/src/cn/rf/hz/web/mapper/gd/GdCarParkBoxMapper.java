package cn.rf.hz.web.mapper.gd;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface GdCarParkBoxMapper
{

	int saveGdCarparkInout(Map<String, Object> m1);

	int saveGdCarparkEnterclose(Map<String, Object> m1);

	void delGdCarparkInout(@Param("carParkId")Integer carParkId);

	void delGdCarparkEnterclose(@Param("carParkId")Integer carParkId);

	List<Map<String, Object>> findRegion(Map<String, Object> m);

	Map<String, Object> findRegionByid(Map<String, Object> m);

	List findInOutInfo(Map<String, Object> m);

	int updateInOutInfo(Map<String, Object> m);

	List<Map<String, Object>> queryByList(Map<String, Object> m);

	List<Map<String, Object>> queryByList1(Map<String, Object> m);


}
