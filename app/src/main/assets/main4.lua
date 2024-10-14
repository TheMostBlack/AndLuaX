require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "AndLua"
import "toast"
--activity.setTheme(R.AndLua5)
local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
 else
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色2=0xFFF2F1F6
end

layout4={
  LinearLayout;
  orientation="vertical";
  layout_width="fill";
  layout_height="fill";
  {
    RelativeLayout;
    layout_height="fill";
    layout_width="fill";
    {
      LinearLayout;
      layout_width="fill";
      layout_height="50dp";
      backgroundColor=颜色1;
      {
        ImageView;
        layout_width="25dp";
        onClick=function()
          activity.finish()
        end;
        layout_gravity="center";
        ColorFilter="0xFF03A9F4";
        src="res/ThomeLua8.png";
        id="mImageView1";
        layout_marginLeft="20dp";
      };
      {
        TextView;
        textColor="0xFF03A9F4";
        layout_gravity="center";
        textSize="18sp";
        text="中文手册";
        id="oi";
        layout_marginLeft="30dp";
      };
    };
    {
      LinearLayout;
      layout_width="fill";
      visibility=8;
      layout_height="50dp";
      id="选择代码";
      backgroundColor=颜色1;
      {
        ImageView;
        layout_width="30dp";
        layout_marginLeft="10dp";
        layout_gravity="center";
        ColorFilter="0xFF03A9F4";
        layout_height="30dp";
        id="XY0";
        src="res3/1.png";
      };
      {
        TextView;
        layout_gravity="center";
        textSize="18sp";
        textColor="0xFF03A9F4";
        text="选择代码";
        layout_marginLeft="15dp";
      };
      {
        ImageView;
        layout_width="29dp";
        layout_marginLeft="30dp";
        layout_gravity="center";
        ColorFilter="0xFF03A9F4";
        layout_height="29dp";
        id="XY1";
        src="res3/2.png";
      };
      {
        ImageView;
        layout_width="25dp";
        layout_marginLeft="25dp";
        layout_gravity="center";
        ColorFilter="0xFF03A9F4";
        layout_height="25dp";
        id="XY2";
        src="res3/3.png";
      };
      {
        ImageView;
        layout_width="25dp";
        layout_marginLeft="25dp";
        layout_gravity="center";
        ColorFilter="0xFF03A9F4";
        layout_height="25dp";
        id="XY3";
        src="res3/4.png";
      };
      {
        ImageView;
        layout_width="25dp";
        layout_marginLeft="25dp";
        layout_gravity="center";
        ColorFilter="0xFF03A9F4";
        layout_height="25dp";
        id="XY4";
        src="res3/5.png";
      };
    };
    {
      LinearLayout;
      layout_width="fill";
      layout_height="88.2%h";
      layout_marginTop="50dp";
      {
        LuaEditor;
        TextSize="12sp";
        id="r";
        backgroundColor=颜色2,
      };
    };
    {
      LinearLayout;
      layout_alignParentBottom="true";
      layout_height="6%h";
      layout_width="fill";
      {
        HorizontalScrollView;
        horizontalScrollBarEnabled=false;
        layout_width="fill";
        layout_height="fill";
        backgroundColor=颜色1;
        {
          LinearLayout;
          id="bar1";
          layout_width="fill";
          layout_height="fill";
        };
      };
    };
  };
};

activity.setContentView(loadlayout(layout4))
--隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
参数,参数1=...
oi.text=参数
r.text=参数1
--波纹(mImageView1,0xFFD9D9D9)
activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
r.OnSelectionChangedListener=function(a,b,c,d)
  if a==true then
    选择代码.setVisibility(0)
   else
    选择代码.setVisibility(8)
  end
end
function XY1.onClick()
  r.selectAll()
end
function XY2.onClick()
  r.cut()
end
function XY3.onClick()
  r.copy()
end
function XY4.onClick()
  r.paste()
end
r.setBasewordColor(0xff8dbdc9)--基词
r.setPanelBackgroundColor(颜色1)--卡片颜色
r.setPanelTextColor(0xFF03A9F4)--卡片字体颜色
r.setStringColor(0xFF03A9F4)--字符串颜色
r.setTextColor(颜色3)--文本颜色
r.setUserwordColor(0xFF03A9F4)--数字
r.setCommentColor(0xffa0a0a0)--注释颜色
r.setKeywordColor(0xFF03A9F4)--if then等
a={"删除文件()","获取悬浮窗权限()"}
r.addNames(a)
p={
  LinearLayout;
  layout_width="40dp";
  gravity="center";
  layout_height="fill";
  {
    TextView;
    id="符号文本1";
    text="Fun";
    textSize="15sp";
    textColor="0xFF03A9F4";
  };
};
fh=io.open(activity.getLuaDir().."/Verify/set2.XY"):read("*a")
for t,c in fh:gmatch("(.-) ") do
  button={
    Button;
    text=tostring(t);
    layout_width="45dp";
    layout_height="fill";
    background="#ffffff";
    textColor=0xFF03A9F4;
    padding="5dp",
    id="sssss1",
  };
  m1=loadlayout(button)
  bar1.addView(m1)
  波纹(sssss1,0xE7EAEEEE)
  m1.onClick=function(v)
    if v.text=="Fun" or v.text=="fun" or v.text=="function" or v.text=="Function" then
      r.paste("function")
     else
      if v.text=="End" or v.text=="end" then
        r.paste("end")
       else
        r.paste(v.Text)
      end
    end
  end
  m1.onLongClick=function(v)
    if v.text=="Fun" or v.text=="fun" or v.text=="function" or v.text=="Function" then
      r.paste("function")
     else
      if v.text=="End" or v.text=="end" then
        r.paste("end")
       else
        r.paste(v.Text..v.text)
      end
    end
    return true
  end
end
