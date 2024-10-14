require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "toast"
import "AndLua"
import "android.net.Uri"
import "android.content.Intent"
import "android.graphics.PorterDuffColorFilter"
import "android.graphics.PorterDuff"
--activity.setTheme(R.AndLua5)

local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色a=0xff303030
  颜色b=0xff212121
  颜色c=0xFFFFFFFF
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xffffffff
  颜色6=0xffffffff
  颜色5=0xff212121
  颜色7=0xEBFFFFFF
  activity.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
 else
  颜色a=0xffffffff
  颜色b=0xFFF2F1F6
  颜色c=0xff303030
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色4=0xff757575
  颜色2=0xFFF2F1F6
  颜色6=0xff757575
  颜色5=0x5FFFFFFF
  颜色7=0xff303030
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)
end
local ab=io.open(activity.getLuaDir().."/Verify/set5.XY"):read("*a")
local abc=io.open(activity.getLuaDir().."/Verify/set6.XY"):read("*a")
local abcd=io.open(activity.getLuaDir().."/Verify/set7.XY"):read("*a")
local bj=io.open(activity.getLuaDir().."/Verify/setb.XY"):read("*a")
local bbl=io.open(activity.getLuaDir().."/Verify/setbb.XY"):read("*a")
local abcde=io.open(activity.getLuaDir().."/Verify/set8.XY"):read("*a")
local abcdef=io.open(activity.getLuaDir().."/Verify/set9.XY"):read("*a")
local abcdefg=io.open(activity.getLuaDir().."/Verify/set10.XY"):read("*a")
local abcdefgh=io.open(activity.getLuaDir().."/Verify/set11.XY"):read("*a")
local abcdefghi=io.open(activity.getLuaDir().."/Verify/set12.XY"):read("*a")
switch abc
 case "颜色1"
  abc=颜色1
end
switch abcdef
 case "颜色3"
  abcdef=颜色3
end
switch bj
 case "颜色5"
  bj=颜色5
end
switch bbl
 case "颜色1"
  bbl=颜色1
end
layout13={
  ScrollView;
  {
    LinearLayout;
    layout_width="fill";
    orientation="vertical";
    backgroundColor=颜色b;
    layout_height="fill";
    {
      LinearLayout;
      orientation="vertical";
      layout_width="fill";
      layout_height="fill";
      {
        LinearLayout;
        layout_width="fill";
        backgroundColor=颜色a;
        layout_height="8.2%h";
        {
          ImageView;
          layout_gravity="center";
          onClick=function()
            activity.finish()
          end;
          layout_width="6.9%w";
          src="res/ThomeLua8.png";
          ColorFilter="0xFF03A9F4";
          layout_marginLeft="5.5%w";
        };
        {
          TextView;
          layout_gravity="center";
          text="软件设置";
          textColor="0xFF03A9F4";
          layout_marginLeft="8.3%w";
          textSize="18sp";
        };
        {
          LinearLayout;
          layout_height="match_parent";
          layout_width="match_parent";
          paddingRight="5.5%w";
          gravity="right";
          {
            ImageView;
            layout_gravity="center";
            id="保存设置",
            layout_width="6.9%w";
            src="res/ThomeLua9.png";
            ColorFilter="0xFF03A9F4";
          };
        };
      };
      {
        LinearLayout;
        orientation="vertical";
        layout_width="match_parent";
        layout_height="match_parent";
        {
          CardView;
          backgroundColor=颜色a,
          layout_gravity="center";
          layout_width="94.4%w";
          layout_height="19%h";
          radius=25;
          layout_marginTop="3%h";
          CardElevation="0dp";
          {
            LinearLayout;
            orientation="vertical";
            layout_width="match_parent";
            layout_height="match_parent";
            {
              TextView;
              text="通用";
              textColor="0xFF03A9F4";
              layout_marginTop="1%h";
              layout_marginLeft="3%w";
              textSize="16sp";
            };
            {
              LinearLayout;
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="检测更新";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";
                {
                  Switch;
                  id="检测更新",
                  layout_gravity="center";
                };
              };
            };
            {
              LinearLayout;
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="深色模式";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  Switch;
                  id="夜间模式",
                  layout_gravity="center";
                };
              };
            };
          };
        };
        {
          CardView;
          layout_gravity="center";
          layout_width="94.4%w";
          layout_height="111%h";
          radius=25;
          backgroundColor=颜色a,
          layout_marginTop="3%h";
          CardElevation="0dp";
          {
            LinearLayout;
            orientation="vertical";
            layout_width="match_parent";
            layout_height="match_parent";

            {
              TextView;
              text="编辑器设置";
              textColor="0xFF03A9F4";
              layout_marginTop="1%h";
              layout_marginLeft="3%w";
              textSize="16sp";
            };
            {
              LinearLayout;
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="自动换行";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  Switch;
                  id="自动换行",
                  layout_gravity="center";
                };
              };
            };
            {
              LinearLayout;
              id="自定义符号栏",
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {

                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="自定义符号栏";
                textColor=颜色c;
              };
            };
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(ab),"确定",1)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="基词颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=ab;
                  id="q";
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="42.8%w";
                };
              },
            };
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abc),"确定",2)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="卡片背景颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abc;
                  id="qq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="35%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abcd),"确定",3)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="卡片字体颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abcd;
                  id="qqq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="35%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                switch bj
                 case 颜色5
                  local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                  local ip=a:match("2(.-)"..'"')
                  if ip=="开" then
                    yss1("调色板","0xFF212121","确定",33)
                   else
                    yss1("调色板","0x5FFFFFFF","确定",33)
                  end
                 default
                  yss1("调色板",tostring(bj),"确定",33)
                end
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="编辑器背景颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=bj;
                  id="b",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="30.8%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                switch bbl
                 case 颜色1
                  local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                  local ip=a:match("2(.-)"..'"')
                  if ip=="开" then
                    yss1("调色板","0xFF303030","确定",333)
                   else
                    yss1("调色板","0xFFFFFFFF","确定",333)
                  end
                 default
                  yss1("调色板",tostring(bbl),"确定",333)
                end
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="工具栏颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=bbl;
                  id="bb",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="38.8%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abcde),"确定",4)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="字符串颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abcde;
                  id="qqqq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="38.8%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abcdef),"确定",5)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="文本颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abcdef;
                  id="qqqqq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="42.8%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abcdefg),"确定",6)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="数字颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abcdefg;
                  id="qqqqqq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="42.8%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abcdefgh),"确定",7)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="注释颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abcdefgh;
                  id="qqqqqqq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="42.8%w";
                };
              };

            },
            {
              LinearLayout;
              onClick=function()
                yss1("调色板",tostring(abcdefghi),"确定",8)
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="关键字颜色";
                textColor=颜色c;
              };
              {
                LinearLayout;
                layout_height="match_parent";
                layout_width="match_parent";
                paddingRight="10%w";
                gravity="right";{
                  CardView;
                  layout_gravity="center";
                  layout_width="3%h";
                  backgroundColor=abcdefghi;
                  id="qqqqqqqq",
                  layout_height="3%h";
                  radius="10dp";
                  layout_marginLeft="38.8%w";
                };
              },
            };
            {
              LinearLayout;
              onClick=function()
                io.open(activity.getLuaDir().."/Verify/set5.XY","w"):write("0xff8dbdc9"):close()
                io.open(activity.getLuaDir().."/Verify/set6.XY","w"):write("颜色1"):close()
                io.open(activity.getLuaDir().."/Verify/set7.XY","w"):write("0xFF03A9F4"):close()
                io.open(activity.getLuaDir().."/Verify/set8.XY","w"):write("0xFF03A9F4"):close()
                io.open(activity.getLuaDir().."/Verify/set9.XY","w"):write("颜色3"):close()
                io.open(activity.getLuaDir().."/Verify/set10.XY","w"):write("0xFF03A9F4"):close()
                io.open(activity.getLuaDir().."/Verify/set11.XY","w"):write("0xffa0a0a0"):close()
                io.open(activity.getLuaDir().."/Verify/set12.XY","w"):write("0xFF03A9F4"):close()
                io.open(activity.getLuaDir().."/Verify/setb.XY","w"):write("颜色5"):close()
                io.open(activity.getLuaDir().."/Verify/setbb.XY","w"):write("颜色1"):close()
                q.backgroundColor=0xff8dbdc9
                qq.backgroundColor=颜色1
                qqq.backgroundColor=0xFF03A9F4
                qqqq.backgroundColor=0xFF03A9F4
                qqqqq.backgroundColor=颜色3
                qqqqqq.backgroundColor=0xFF03A9F4
                qqqqqqq.backgroundColor=0xffa0a0a0
                qqqqqqqq.backgroundColor=0xFF03A9F4
                b.backgroundColor=颜色5
                bb.backgroundColor=颜色1
                print("已恢复颜色")
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {

                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="恢复颜色";
                textColor=颜色c;
              };



            },
            {
              LinearLayout;
              onClick=function()
                LuaUtil.copyDir(activity.getLuaDir().."/Verify","/sdcard/ThomeLua/backup/Verify")
                os.execute("rm -r ".."/sdcard/ThomeLua/backup/Verify/set1.XY")
                os.execute("rm -r ".."/sdcard/ThomeLua/backup/Verify/set2.XY")
                os.execute("rm -r ".."/sdcard/ThomeLua/backup/Verify/set3.XY")
                os.execute("rm -r ".."/sdcard/ThomeLua/backup/Verify/set4.XY")
                os.execute("rm -r ".."/sdcard/ThomeLua/backup/Verify/set13.XY")
                local date=os.date("%Y%m%d%H%M%S")
                LuaUtil.zip("/sdcard/ThomeLua/backup/Verify","/sdcard/ThomeLua/backup/XColor/","XColor_"..date..".XColor")
                os.execute("rm -r ".."/sdcard/ThomeLua/backup/Verify")
                local jm=require "crypt"
                local jmh=jm.desencode(io.open("/sdcard/ThomeLua/backup/XColor/XColor_"..date..".XColor"):read("*a"))
                io.open("/sdcard/ThomeLua/backup/XColor/XColor_"..date..".XColor","w"):write(jmh):close()
                print("已导出颜色")
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="导出颜色";
                textColor=颜色c;
              };



            },
            {
              LinearLayout;
              onClick=function()
                import "android.graphics.Typeface"
                local Text_Type=Typeface.defaultFromStyle(Typeface.BOLD)
                local sd = StateListDrawable()
                import "android.graphics.Color"
                import "android.content.res.ColorStateList"
                import "android.graphics.drawable.RippleDrawable"
                import "android.content.Context"
                appt={C_Bacgg=function(mBinding,radiu,InsideColor,S,S2,T1)
                    local drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,{});
                    drawable.setCornerRadius(radiu);
                    drawable.setColor(颜色5)
                    drawable.setStroke(3, 0xCFB0B0B0)
                    drawable.setGradientType(GradientDrawable.RECTANGLE);
                    mBinding.setTextColor(T1)
                    mBinding.setTypeface(Text_Type)
                    return drawable
                  end}
                美化按钮1=function(mBinding,radiu,InsideColor,T1)
                  stateList = {
                    {android.R.attr.state_pressed},
                    {android.R.attr.state_focused},
                    {android.R.attr.state_activated},
                    {android.R.attr.selectableItemBackground},
                  };
                  sd.addState({ android.R.attr.state_enabled}, appt.C_Bacgg(mBinding,radiu,InsideColor,S,S2,T1))
                  pressedColor =InsideColor --Color.parseColor("#7ab946ff");
                  stateColorList ={
                    pressedColor,
                    pressedColor,
                    pressedColor,
                    normalColor
                  };
                  colorStateList = ColorStateList(stateList, stateColorList);
                  rippleDrawable = RippleDrawable(colorStateList,sd,nil);
                  mBinding.setBackground(rippleDrawable);
                end
                cm=activity.getSystemService(Context.CLIPBOARD_SERVICE)
                function 控件圆角(view,InsideColor,radiu)
                  import "android.graphics.drawable.GradientDrawable"
                  drawable = GradientDrawable()
                  drawable.setShape(GradientDrawable.RECTANGLE)
                  drawable.setColor(InsideColor)
                  drawable.setCornerRadii({radiu,radiu,radiu,radiu,radiu,radiu,radiu,radiu});
                  view.setBackgroundDrawable(drawable)
                end
                tj=
                {
                  CardView;
                  radius=30;
                  layout_width="match_parent";
                  --orientation="vertical";
                  layout_height="match_parent";
                  {
                    CardView;
                    layout_gravity="center";
                    layout_height="300dp";
                    layout_width="match_parent";
                    backgroundColor=颜色1,
                    radius=20;
                    {
                      TextView;
                      layout_marginTop="15dp";
                      layout_marginLeft="20dp";
                      textSize="20sp";
                      textColor="0xFF03A9F4";
                      text="导入颜色(*.XColor)";
                    };
                    {
                      LinearLayout;
                      orientation="horizontal";
                      layout_width="match_parent";
                      layout_height="150dp";
                      gravity="center";
                      {
                        EditText;
                        layout_marginTop="10dp",
                        layout_width="320dp";
                        gravity="center";
                        textSize="15sp";
                        hint="请输入XColor路径";
                        id="路径",
                        textColor=颜色4,
                        --HintTextColor=颜色4,
                      };
                    };

                    {
                      LinearLayout;
                      layout_height="match_parent";
                      layout_width="match_parent";
                      {
                        Button;
                        id="取消创建工程",
                        layout_gravity="center";
                        layout_marginLeft="20dp";
                        textColor="0x7E000000";
                        text="取消";
                        layout_marginTop="50dp",
                        layout_height="40dp";
                      };
                      {
                        LinearLayout;
                        gravity="right";
                        layout_width="match_parent";
                        layout_height="match_parent";
                        {
                          Button;
                          layout_height="40dp";
                          id="确定创建工程",
                          layout_marginTop="50dp",
                          layout_gravity="center";
                          layout_marginRight="20dp";
                          textColor="0xFF03A9F4";
                          text="导入";
                        };
                      };
                    };
                  };
                };



                dialog= AlertDialog.Builder(this)
                dialog1=dialog.show()
                dialog1.getWindow().setContentView(loadlayout(tj));
                import "android.content.res.ColorStateList"
                import "android.graphics.drawable.ColorDrawable"
                import "android.graphics.drawable.GradientDrawable"
                import "android.graphics.drawable.RippleDrawable"
                import "android.content.res.ColorStateList"
                import "android.graphics.drawable.ColorDrawable"
                dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
                local dialogWindow = dialog1.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialog1.getWindow().getAttributes().width=(activity.Width);
                dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                美化按钮1(取消创建工程,10,0x7E000000,颜色7)
                美化按钮1(确定创建工程,10,0x7a00bfff,0xFF03A9F4)
                控件圆角(路径,颜色2,30)
                function file_exists(path)
                  local f=io.open(path,'r')
                  if f~=nil then io.close(f) return true else return false end
                end
                function 取消创建工程.onClick()
                  dialog1.dismiss()
                end
                function 确定创建工程.onClick()
                  local jm=require "crypt"
                  local emh=jm.desdecode(io.open(路径.Text):read("*a"))
                  io.open(路径.Text.."J","w"):write(emh):close()
                  LuaUtil.unZip(路径.Text.."J","/sdcard/ThomeLua/backup/XColor/")
                  os.execute("rm -r "..路径.Text.."J")
                  local ab=io.open("/sdcard/ThomeLua/backup/XColor/set5.XY"):read("*a")
                  local abc=io.open("/sdcard/ThomeLua/backup/XColor/set6.XY"):read("*a")
                  local abcd=io.open("/sdcard/ThomeLua/backup/XColor/set7.XY"):read("*a")
                  local abcde=io.open("/sdcard/ThomeLua/backup/XColor/set8.XY"):read("*a")
                  local abcdef=io.open("/sdcard/ThomeLua/backup/XColor/set9.XY"):read("*a")
                  local abcdefg=io.open("/sdcard/ThomeLua/backup/XColor/set10.XY"):read("*a")
                  local abcdefgh=io.open("/sdcard/ThomeLua/backup/XColor/set11.XY"):read("*a")
                  local abcdefghi=io.open("/sdcard/ThomeLua/backup/XColor/set12.XY"):read("*a")
                  switch file_exists("/sdcard/ThomeLua/backup/XColor/setb.XY")
                   case true
                    local bj=io.open("/sdcard/ThomeLua/backup/XColor/setb.XY"):read("*a")
                    io.open(activity.getLuaDir().."/Verify/setb.XY","w"):write(bj):close()
                   default io.open(activity.getLuaDir().."/Verify/setb.XY","w"):write("颜色5"):close()
                  end
                  switch file_exists("/sdcard/ThomeLua/backup/XColor/setbb.XY")
                   case true
                    local bbl=io.open("/sdcard/ThomeLua/backup/XColor/setbb.XY"):read("*a")
                    io.open(activity.getLuaDir().."/Verify/setbb.XY","w"):write(bbl):close()
                   default
                    io.open(activity.getLuaDir().."/Verify/setbb.XY","w"):write("颜色1"):close()
                  end

                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set5.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set6.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set7.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set8.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set9.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set10.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set11.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/set12.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/setb.XY")
                  os.execute("rm -r ".."/sdcard/ThomeLua/backup/XColor/setbb.XY")
                  io.open(activity.getLuaDir().."/Verify/set5.XY","w"):write(ab):close()
                  io.open(activity.getLuaDir().."/Verify/set6.XY","w"):write(abc):close()
                  io.open(activity.getLuaDir().."/Verify/set7.XY","w"):write(abcd):close()
                  io.open(activity.getLuaDir().."/Verify/set8.XY","w"):write(abcde):close()
                  io.open(activity.getLuaDir().."/Verify/set9.XY","w"):write(abcdef):close()
                  io.open(activity.getLuaDir().."/Verify/set10.XY","w"):write(abcdefg):close()
                  io.open(activity.getLuaDir().."/Verify/set11.XY","w"):write(abcdefgh):close()
                  io.open(activity.getLuaDir().."/Verify/set12.XY","w"):write(abcdefghi):close()
                  switch type(ab)
                   case "number"
                    ab=ab
                   case "string"
                    ab=tonumber(ab)
                  end
                  switch type(abc)
                   case "number"
                    abc=abc
                   case "string"
                    abc=tonumber(abc)
                  end
                  switch type(abcd)
                   case "number"
                    abcd=abcd
                   case "string"
                    abcd=tonumber(abcd)
                  end
                  switch type(abcde)
                   case "number"
                    abcde=abcde
                   case "string"
                    abcde=tonumber(abcde)
                  end
                  switch type(abcdef)
                   case "number"
                    abcdef=abcdef
                   case "string"
                    abcdef=tonumber(abcdef)
                  end
                  switch type(abcdefg)
                   case "number"
                    abcdefg=abcdefg
                   case "string"
                    abcdefg=tonumber(abcdefg)
                  end
                  switch type(abcdefgh)
                   case "number"
                    abcdefgh=abcdefgh
                   case "string"
                    abcdefgh=tonumber(abcdefgh)
                  end
                  switch type(abcdefghi)
                   case "number"
                    abcdefghi=abcdefghi
                   case "string"
                    abcdefghi=tonumber(abcdefghi)
                  end
                  switch abc
                   case "颜色1"
                    abc=颜色1
                   case nil
                    abc=颜色1
                   case ""
                    abc=颜色1
                  end
                  switch abcdef
                   case "颜色3"
                    abcdef=颜色3
                   case nil
                    abcdef=颜色3
                   case ""
                    abcdef=颜色3
                  end
                  switch bj
                   case "颜色5"
                    local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                    local ip=a:match("2(.-)"..'"')
                    if ip=="开" then
                      b.backgroundColor="0xFF212121"
                     else
                      b.backgroundColor="0x5FFFFFFF"
                    end
                   case nil
                    local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                    local ip=a:match("2(.-)"..'"')
                    if ip=="开" then
                      b.backgroundColor="0xFF212121"
                     else
                      b.backgroundColor="0x5FFFFFFF"
                    end
                   case ""
                    local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                    local ip=a:match("2(.-)"..'"')
                    if ip=="开" then
                      b.backgroundColor="0xFF212121"
                     else
                      b.backgroundColor="0x5FFFFFFF"
                    end
                   default
                    b.backgroundColor=tonumber(bj)
                  end
                  switch bbl
                   case "颜色1"
                    local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                    local ip=a:match("2(.-)"..'"')
                    if ip=="开" then
                      bb.backgroundColor="0xFF303030"
                     else
                      bb.backgroundColor="0xFFFFFFFF"
                    end
                   case nil
                    local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                    local ip=a:match("2(.-)"..'"')
                    if ip=="开" then
                      bb.backgroundColor="0xFF303030"
                     else
                      bb.backgroundColor="0xFFFFFFFF"
                    end
                   case ""
                    local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
                    local ip=a:match("2(.-)"..'"')
                    if ip=="开" then
                      bb.backgroundColor="0xFF303030"
                     else
                      bb.backgroundColor="0xFFFFFFFF"
                    end
                   default
                    bb.backgroundColor=tonumber(bbl)
                  end
                  q.backgroundColor=ab
                  qq.backgroundColor=abc
                  qqq.backgroundColor=abcd
                  qqqq.backgroundColor=abcde
                  qqqqq.backgroundColor=abcdef
                  qqqqqq.backgroundColor=abcdefg
                  qqqqqqq.backgroundColor=abcdefgh
                  qqqqqqqq.backgroundColor=abcdefghi


                  print("已导入颜色")
                  dialog1.dismiss()
                end
              end,
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="导入颜色";
                textColor=颜色c;
              };



            },
          },
        };
        {
          CardView;
          backgroundColor=颜色a,
          layout_gravity="center";
          layout_width="94.4%w";
          layout_height="wrap_content";
          radius=25;
          layout_marginTop="3%h";
          layout_marginBottom="3%h";
          CardElevation="0dp";
          {
            LinearLayout;
            orientation="vertical";
            layout_width="match_parent";
            layout_height="match_parent";
            {
              TextView;
              text="其他";
              textColor="0xFF03A9F4";
              layout_marginTop="1%h";
              layout_marginLeft="3%w";
              textSize="16sp";
            };
            {
              LinearLayout;
              id="加群",
              layout_width="match_parent";
              layout_marginTop="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="加入官方群聊：704423243";
                textColor=颜色c;
              };
            };
            {
              LinearLayout;
              id="联系作者",
              layout_width="match_parent";
              layout_marginTop="1%h";
              layout_marginBottom="1%h";
              orientation="horizontal";
              layout_height="6%h";
              {
                ImageView;
                layout_gravity="center";
                layout_width="4%h";
                src="res/ThomeLua11.png";
                layout_height="4%h";
                ColorFilter=颜色c;
                layout_marginLeft="3%w";
              };
              {
                TextView;
                layout_gravity="center";
                layout_marginLeft="5%w";
                text="作者QQ：176971467";
                textColor=颜色c;
              };
            };
          };
        };
      };
    };
  };
};

activity.setContentView(loadlayout(layout13))

--隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色a);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
import "color1"
检测更新.ThumbDrawable.setColorFilter(PorterDuffColorFilter(0xFF03A9F4,PorterDuff.Mode.SRC_ATOP));
夜间模式.ThumbDrawable.setColorFilter(PorterDuffColorFilter(0xFF03A9F4,PorterDuff.Mode.SRC_ATOP));
自动换行.ThumbDrawable.setColorFilter(PorterDuffColorFilter(0xFF03A9F4,PorterDuff.Mode.SRC_ATOP));
检测更新.TrackDrawable.setColorFilter(PorterDuffColorFilter(0xFF03A9F4,PorterDuff.Mode.SRC_ATOP))
夜间模式.TrackDrawable.setColorFilter(PorterDuffColorFilter(0xFF03A9F4,PorterDuff.Mode.SRC_ATOP))
自动换行.TrackDrawable.setColorFilter(PorterDuffColorFilter(0xFF03A9F4,PorterDuff.Mode.SRC_ATOP))
function 联系作者.onClick()
  import "android.net.Uri"
  import "android.content.Intent"
  activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=176971467")))
end
function 加群.onClick()
  activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=704423243&card_type=group&source=qrcode")))
end
local c=a:match("2(.-)"..'"')
local b=a:match("1(.-)"..'"')
local d=io.open(activity.getLuaDir().."/Verify/set13.XY"):read("*a")

if b=="开" then 检测更新.Checked=true else 检测更新.Checked=false end
if c=="开" then 夜间模式.Checked=true else 夜间模式.Checked=false end
if d=="true" then 自动换行.Checked=true elseif d==true then 自动换行.Checked=true else 自动换行.Checked=false end
function 保存设置.onClick()
  local c=[["1%s"
"2%s"
"3%s"]]
  if 检测更新.Checked==true then e="开" else e="关" end
  if 夜间模式.Checked==true then f="开" else f="关" end
  if 自动换行.Checked==true then
    io.open(activity.getLuaDir().."/Verify/set13.XY","w"):write("true"):close()
   else
    io.open(activity.getLuaDir().."/Verify/set13.XY","w"):write(""):close()
  end
  local d=c:format(e,f,g)
  写入文件(activity.getLuaDir().."/Verify/set4.XY",d)
  activity.finish()
  print"保存成功"
end
function 自定义符号栏.onClick()
  InputLayout={
    LinearLayout;
    orientation="vertical";
    Focusable=true,
    FocusableInTouchMode=true,
    {
      TextView;
      textSize="15sp",
      layout_marginTop="1%h";
      layout_marginLeft="3.5%w",
      layout_marginRight="10dp",
      layout_width="match_parent";
      layout_gravity="center",
      text="请输入自定义符号，用换行符隔开";
      textColor="0xFF03A9F4",
    };
    {
      EditText;
      layout_marginTop="5dp";
      layout_marginLeft="10dp",
      layout_marginRight="10dp",
      layout_width="match_parent";
      layout_gravity="center",
      id="edit";
    };
  };

  AlertDialog.Builder(this)
  .setTitle("自定义符号栏")
  .setView(loadlayout(InputLayout))
  .setPositiveButton("保存",{onClick=function()
      写入文件(activity.getLuaDir().."/Verify/set2.XY",edit.text.." @XY")
      print"已保存"
    end})
  .setNegativeButton("取消",nil)
  .show()
  local a=io.open(activity.getLuaDir().."/Verify/set2.XY"):read("*a")
  local b=a:find("@")
  local c=a:sub(0,b-2)
  edit.text=c

end
