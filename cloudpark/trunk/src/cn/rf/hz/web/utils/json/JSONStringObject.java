package cn.rf.hz.web.utils.json;

import net.sf.json.JSONString;

/**
 * 
 * @author feixiang
 *
 */
public class JSONStringObject implements JSONString{

    private String jsonString = null;
    
    public JSONStringObject(String jsonString){
        this.jsonString = jsonString;
    }

    @Override
    public String toString(){
        return jsonString;
    }

    public String toJSONString(){
        return jsonString;
    }
}
