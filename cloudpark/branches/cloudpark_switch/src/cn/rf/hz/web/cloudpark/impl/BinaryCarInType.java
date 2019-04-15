package cn.rf.hz.web.cloudpark.impl;

public class BinaryCarInType {
	 public static int IsLongBit=1; //二进制数第一位表示长期车
	 public static int IsXBBit=2;   //二进制数第二位表示行呗预约
	
	 /**
	 * @Title: getBinaryCarInType  
	 * @Description: 根据carintype具体数值和二进制位，获取车辆属性（长期车、临时车或者行呗） 
	 * @param state
	 * @param statePos
	 * @return int
	 * @throws
	 */
	 public static int getBinaryCarInType(int state,int statePos){
	        if(state < 0 || statePos < 1 ) return 0;  // 得改成 小于 1
	        int posValue = (int)Math.pow(2,(statePos -1));
	        int result =state & posValue;
	        if(result == 0){
	            return  0;
	        }else{
	            return  1;
	        }
		}
}
