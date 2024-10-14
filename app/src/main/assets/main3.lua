require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "ThomeLua"
import "main4"
import "toast"
import "android.graphics.drawable.ColorDrawable"
activity.setTheme(R.AndLua5)
local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xffffffff
 else
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色4=0xff757575
  颜色2=0xFFF2F1F6
end
layout3={
  LinearLayout;
  layout_height="fill";
  layout_width="fill";
  backgroundColor=颜色2,
  orientation="vertical";
  {
    RelativeLayout;
    layout_height="fill";
    layout_width="fill";
    {
      LinearLayout;
      layout_height="56dp";
      backgroundColor=颜色1;
      layout_width="fill";
      {
        ImageView;
        layout_gravity="center";
        src="res/ThomeLua8.png";
        ColorFilter="0xFF03A9F4";
        layout_width="25dp";
        id="mImageView2";
        layout_marginLeft="20dp";
      };
      {
        TextView;
        layout_gravity="center";
        text="中文手册";
        textSize="18sp";
        layout_marginLeft="30dp";
        textColor="0xFF03A9F4";
      };
    };
    {

      LinearLayout;
      gravity="center";
      layout_height="90%h";
      orientation="vertical";
      layout_alignParentBottom="true";
      layout_width="fill";
      {
        ListView;
        layout_gravity="end";
        layout_width="fill";
        layout_height="fill";
        id="中文列表";
        DividerHeight=0;
        layout_marginTop="5dp";
      };
    };
  };
}

activity.setContentView(loadlayout(layout3))

隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

item5={
  LinearLayout;
  gravity="center";
  layout_width="fill";
  layout_height="fill";
  {
    LinearLayout;
    gravity="center";
    orientation="vertical";
    layout_height="19.1%h";
    layout_width="fill";
    {
      CardView;
      radius=20;
      CardElevation=0;
      backgroundColor=颜色1;
      layout_height="120dp";
      layout_width="340dp";
      {
        LinearLayout;
        orientation="vertical";
        layout_height="fill";
        layout_width="fill";
        {
          TextView;
          id="标题1";
          layout_marginTop="5dp";
          layout_marginLeft="10dp";
          text="中文函数";
          textColor=颜色3;
          textSize="18sp";
        };
        {
          TextView;
          id="内容1";
          layout_marginLeft="10dp";
          textSize="14sp";
          textColor=颜色4,
          layout_marginTop="5dp";
          text=[==[需导入中文函数包
import "ThomeLua"]==];
        };
      };
    };
  };
};


内容="【中文函数使用说明】使用中文函数库需要导入包\nimport \"ThomeLua\"【MD提示】--MD提示(\"内容\",\"背景颜色\",\"字体颜色\",\"阴影\",\"圆角\")\nMD提示(\"ThomeLua\",0xFF2196F3,\"#ffffffff\",\"4\",\"10\")【圆角提示】--圆角提示(\"内容\")\n圆角提示(\"ThomeLua\")【窗口标题】窗口标题(\"ThomeLua\")【载入页面】载入页面(\"layout\")【隐藏标题栏】隐藏标题栏()【打印】a=\"ThomeLua\"\n打印(a)【提示】a=\"ThomeLua\"\n提示(a)【截取字符串】--截取(字符串,前字符,后字符)\na=\"123456\"\nb=截取(a,\"3\",\"6\")\n提示(b)\n--结果45【替换字符串】--替换(字符串,要替换的字符,替换成的字符)\na=\"123456789\"\nb=替换(a,\"123\",\"321\")\n提示(b)\n--结果321456789【字符串长度】--字符串长度(\"字符串\")\na=\"ThomeLua\"\nb=字符串长度(a)\n打印(b)\n结果4【字符转ASCII码】--字符转ASCII码(\"内容\",\"要转换的位数\")\n字符转ASCII码(\"abc\",2)\n--将abc中的b转换为ASCII码【ASCII码转字符】--ASCII码转字符(ASCII码)\n字符转ASCII码(98)\n--将98转换字符后为b【ASCII码判断字符】--ASCII码判断字符(\"内容\",要判断的位数,ASCII码)\nASCII码判断字符(\"abc\",2,98)\n--判断abc中的第2位转换为ASCII码是否为98【状态栏颜色】状态栏颜色(\"0xff4285f4\")【沉浸状态栏】沉浸状态栏()【设置文本】设置文本(id,\"内容\")【跳转页面】跳转页面(\"main\")【关闭页面】关闭页面()【获取文本】--获取文本(id)\na=获取文本(id)\n提示(a)【结束程序】结束程序()【控件圆角】控件圆角(id,0xb0000000,20)\n--id是控件id，0xb0000000是颜色，20是角度\n可以使用字符串型的\"0xb0000000\"颜色\n可以使用字符串型的\"#b0000000\"颜色代码【获取设备标识码】--获取设备标识码()\na=获取设备标识码()\n打印(a)【获取IMEI】--获取IMEI()\na=获取IMEI()\n打印(a)【控件背景渐变动画】--控件背景渐变动画(控件ID,颜色1,颜色2,颜色3,颜色4)\n控件背景渐变动画(a,\"0xffFF8080\",\"0xff8080FF\",\"0xff80ff80\",\"0xff80ffff\")【获取屏幕尺寸】--获取屏幕尺寸()\na=获取屏幕尺寸\n打印(a)【判断是否安装指定软件】--安装判断(包名)\na=安装判断(\"com.ThomeLua.IDE\")\n打印(a)【设置文本中划线风格】设置中划线(ID)【设置文本下划线风格】设置下划线(ID)【设置字体加粗】设置字体加粗(ID)【设置字体斜体】设置字体斜体(ID)【分享内容】分享(\"内容\")【跳转到QQ群】加群(\"群号码\")【跳转到QQ聊天】QQ聊天(\"QQ号\")【发送短信】发送短信(号码,内容)【获取剪切板】--获取剪切板()\na=获取剪切板()\n打印(a)【写入剪切板】写入剪切板(\"内容\")【开启wifi】开启WIFI()【关闭wifi】关闭WIFI()【断开网络】断开网络()【创建文件】创建文件(\"路径\")\n--创建文件(\"/sdcard/ThomeLua.txt\")【创建文件夹】创建文件夹(\"路径\")\n--创建文件夹(\"/sdcard/ThomeLua/\")【创建多级文件夹】创建多级文件夹(\"路径\")\n--创建多级文件夹(\"/sdcard/a/b/c/\")【移动文件】移动文件(\"旧文件路径\",\"新文件路径\")【写入文件】写入文件(\"路径\",\"内容\")【删除文件】删除文件(\"路径\")【按钮颜色】按钮颜色(id,0xb00000000)【编辑框颜色】编辑框颜色(id,0xb00000000)【进度条颜色】进度条颜色(id,0xb00000000)【控件颜色】控件颜色(id,0xb00000000)【获取手机存储路径】a=获取手机存储路径()\n打印(a)【获取屏幕高】a=获取屏幕高()\n打印(a)\n--获取之后赋值到a【获取屏幕宽】a=获取屏幕宽()\n打印(a)\n--获取之后赋值到a【文件是否存在】a=\"文件是否存在(\"路径\")\na=true则为存在\nfalse为不存在\"【打开侧滑】打开侧滑()【关闭侧滑】关闭侧滑()【显示控件】显示(id)\n--显示控件【隐藏控件】隐藏(id)\n--隐藏控件【播放本地音乐】播放本地音乐(\"/sdcard/1.mp3\")【在线播放音乐】在线播放音乐(\"http://adc.cn/1.mp3\")【播放本地视频】播放本地视频(\"/sdcard/1.mp4\")【在线播放视频】在线播放视频(\"http://adc.cn/1.mp4\")【打开APP】打开app(\"包名\")【卸载APP】卸载app(\"包名\")【安装APP】安装app(\"/sdcard/1.apk\")【调用系统下载文件】系统下载文件(\"文件直链\",\"文件下载目录\",\"文件名\")【设置波纹效果】波纹(id,颜色)【删除控件】删除控件(父控件id,控件id)【状态栏高色】状态栏亮色()【重构页面】重构页面()【窗口全屏】窗口全屏()【取消全屏】取消全屏()【返回桌面】返回桌面()【获取悬浮窗权限】获取悬浮窗权限()【"
data={}
adp=LuaAdapter(activity,data,item5)
中文列表.Adapter=adp
标题=内容:gmatch("】(.-)【")
for i in 内容:gmatch("【(.-)】") do
  sss=标题()
  adp.add{标题1=i,内容1=sss}
end
中文列表.onItemClick=function(l,v,p,i)
  activity.newActivity("main4",android.R.anim.fade_in,android.R.anim.fade_out,{v.tag.标题1.text,v.tag.内容1.text})
  return true
end
波纹(mImageView2,0xFFD9D9D9)
function mImageView2.onClick()
  activity.finish()
end