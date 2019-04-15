package cn.rf.hz.web.cloudpark.exception;

public class ParameterException extends Exception {
	// 用来创建无参数对象
	public ParameterException() {
	}

	// 用来创建指定参数对象
	public ParameterException(String message) {
		super(message); // 调用超类构造器
	}
}
