require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "AndLua"
import "main6"
import "toast"
activity.setTheme(R.AndLua5)

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
layout7={
  LinearLayout;
  layout_width="fill";
  layout_height="fill";
  orientation="vertical";
  backgroundColor=颜色2;
  {
    LinearLayout;
    orientation="vertical";
    layout_height="-1";
    layout_width="match_parent";
    {
      CardView;
      layout_width="-1";
      layout_marginTop="10dp";
      layout_marginRight="10dp";
      layout_height="-2";
      radius=25;
      layout_marginLeft="10dp";
      cardElevation=0;
      {
        LinearLayout;
        layout_width="-1";
        layout_height="-1";
        {
          CardView;
          layout_width="-1";
          backgroundColor=颜色1;
          radius=0;
          layout_height="70dp";
          cardElevation=0;
          {
            TextView;
            text="标题";
            layout_marginTop="5dp";
            layout_marginLeft="10dp";
            textColor="0xFF03A9F4";
          };
          {
            EditText;
            id="tzbt",
            textColor=颜色3,
            layout_width="320dp";
            layout_marginTop="10dp";
            singleLine="true";
            background="0";
            textSize="15sp";
            layout_gravity="center";
          };
          {
            TextView;
            id="tzbq";
            text="#其他内容#";
            layout_marginTop="5dp";
            layout_marginLeft="250dp";
            textColor="0xFF03A9F4";
          };
        };
      };
    };
    {
      CardView;
      layout_width="-1";
      layout_marginTop="10dp";
      layout_marginBottom="10dp";
      layout_marginRight="10dp";
      cardElevation=0;
      radius=25;
      backgroundColor=颜色1;
      layout_marginLeft="10dp";
      layout_height="100dp";
      {
        TextView;
        text="内容";
        layout_marginTop="5dp";
        layout_marginLeft="10dp";
        textColor="0xFF03A9F4";
      };
      {
        EditText;
        id="tznr";
        layout_width="320dp";
        layout_marginTop="20dp";
        layout_gravity="left";
        textSize="15sp";
        textColor=颜色3,
        layout_marginBottom="20dp";
        layout_marginLeft="10dp";
        background="0";
      };
    };
    {
      LinearLayout;
      orientation="horizontal";
      gravity="center";
      layout_height="450dp";
      layout_width="match_parent";
      {
        Button;
        id="取消发布";
        layout_marginBottom="200dp";
        text="取消";
        layout_width="150dp";
      };
      {
        Button;
        layout_width="150dp";
        layout_marginBottom="200dp";
        id="发布帖子";
        layout_marginLeft="20dp";
        text="发布";
      };
    };
  };
};

import "bmob"
id="1f6759cbbac7c4ef109e1c51ce9f7072"
key="7313b823d9b466408e11932c8060f7d2"
bm=bmob(id,key)
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色2);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
activity.setContentView(loadlayout(layout7))
隐藏标题栏()
美化按钮(取消发布,100,0x7a00bfff,0x7a00bfff)
美化按钮(发布帖子,100,0x7a00bfff,0x7a00bfff)
function 取消发布.onClick()
  activity.finish()
end
function tzbq.onClick()
  pop=PopupMenu(activity,tzbq)
  menu=pop.Menu
  menu.add("其他内容").onMenuItemClick=function()
    tzbq.text="#其他内容#"
  end
  menu.add("BUG反馈").onMenuItemClick=function()
    tzbq.text="#BUG反馈#"
  end
  menu.add("大佬求教").onMenuItemClick=function()
    tzbq.text="#大佬求教#"
  end
  menu.add("开发教程").onMenuItemClick=function()
    tzbq.text="#开发教程#"
  end
  pop.show()
end
function 发布帖子.onClick()
  if #tzbt.text~=0 then
    if #tznr.text~=0 then
      activity.result{true,tzbt.text,tzbq.text,tznr.text}
     else
      print"请输入帖子内容"
    end
   else
    print"请输入帖子标题"
  end
end