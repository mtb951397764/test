import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Test {


    public void test1()
    {
        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8a216da8762cb457017722ee06315656";
        String accountToken = "88729e2adfe6456ba3d492a783708eb2";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8a216da8762cb457017722ee070d565d";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = "18611436457";
        String templateId= "1";
        String[] datas = {"333333","1","变量3"};
        String subAppend="1234";  //可选	扩展码，四位数字 0~9999
        String reqId="fafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

    public int arrange(int[] nums,int target,int left,int right)
    {
        if(left>right)
        {
            return -1;
        }
        int middle = (left+right)/2;
        if(target == nums[middle])
        {
            return middle;
        }
        else if (target > nums[middle])
        {
            return arrange(nums,target,middle+1,right);
        }
        else
        {
            return arrange(nums,target,left,middle-1);
        }
    }
    public static int randomNumber()
    {
        Random random = new Random();
        return random.nextInt(50);
    }
    public static void main(String[] args) {
        int[] nums = new int[50];
        for (int i = 0 ; i < nums.length ; i++)
        {
            nums[i]=Test.randomNumber();
        }
        for (int i = 0; i < nums.length-1 ; i++)
        {
            for (int j = i ; j<nums.length-i-1 ; j++)
            {
                if(nums[j]>nums[j+1])
                {
                    int t = nums[j];
                    nums[j] = nums[j+1];
                    nums[j+1] = t;
                }
            }
        }
        System.out.println(nums);
        System.out.println(new Test().arrange(nums,8,0,nums.length-1));
    }
}
