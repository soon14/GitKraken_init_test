package cn.rf.hz.web.mapper.gd;

import java.util.Map;



public interface GdLicenseRecognizeMapper<T>
{

	public T getLicenseRecognizePlateNumber(T t);

	public int saveGdLicenseRecognize(Map<String, Object> map);

	public Map<String, Object> findById(Map<String, Object> map);

	public int updateBySelective(Map<String, Object> map);

	public int saveLicenseRecognize(Map<String, Object> map);

	public int licenseRecognizeOut(Map<String, Object> map);

	public int updateLicense(Map<String, Object> map);


}
