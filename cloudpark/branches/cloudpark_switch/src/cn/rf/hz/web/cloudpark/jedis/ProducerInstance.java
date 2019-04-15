package cn.rf.hz.web.cloudpark.jedis;

public class ProducerInstance {
	private static Producer producer=null;
	
	public static synchronized Producer getinstance(){
		if(producer==null){
			producer =new Producer();
		}
		System.out.println(producer);
		System.out.println(producer.hashCode());
		return producer;
	}
}
