require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "AndLua"
import "toast"
import "android"
--activity.setTheme(R.AndLua5)
local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  activity.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
 else
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色2=0xFFF2F1F6
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)
end
layout10={
  LinearLayout;
  layout_width="fill";
  layout_height="fill";
  backgroundColor=颜色2,
  orientation="vertical";
  {
    LinearLayout;
    orientation="vertical";
    layout_height="fill";
    layout_width="fill";
    {
      LinearLayout;
      backgroundColor=颜色1;
      layout_height="56dp";
      layout_width="fill";
      {
        ImageView;
        onClick=function()
          activity.finish()
        end;
        src="res/ThomeLua8.png";
        layout_marginLeft="20dp";
        ColorFilter="0xFF03A9F4";
        layout_width="25dp";
        layout_gravity="center";
      };
      {
        TextView;
        textSize="18sp";
        textColor="0xFF03A9F4";
        layout_marginLeft="30dp";
        text="Java  Api";
        layout_gravity="center";
      };
    };
    {
      EditText;
      hint="搜索",
      layout_marginTop="10dp",
      id="搜索api";
      --hintTextColor=颜色3,
      textColor=颜色3,
      layout_width="320dp";
      layout_gravity="center";
    };
    {
      LinearLayout;
      layout_height="match_parent";
      layout_width="match_parent";
      {
        ListView;
        fastScrollEnabled=true,
        layout_marginTop="10dp",
        DividerHeight=0;
        id="api";
        layout_height="fill";
        layout_width="fill";
      };
    };
  };
};


activity.setContentView(loadlayout(layout10))
--隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
控件圆角(搜索api,颜色2,25)
apiitem={
  LinearLayout;
  layout_height="fill";
  layout_width="fill";
  gravity="center";
  backgroundColor=颜色2,
  {
    LinearLayout;
    layout_width="match_parent";
    {
      TextView;
      textSize="17sp";
      id="nrapi";
      textColor=颜色3;
      layout_marginLeft="25dp";
      layout_marginTop="10dp";
      layout_marginBottom="10dp";
      text="com.androlua.Http";
    };
  };
};
data={}
local adp=LuaAdapter(activity,data,apiitem)
api.setAdapter(adp)
function 加载(内容)
  adp.clear()
  for k in apinr:gmatch("【(.-)】")
    if k:match(tostring(内容)) then
      adp.add{nrapi=k}
     else
      adp.clear()
    end
  end
end
加载("")

搜索api.addTextChangedListener({
  onTextChanged=function(a)
    加载(a)
  end})
function api.onItemClick(l,v,p,i)
  写入剪切板(v.tag.nrapi.text)
  print"复制成功"
end