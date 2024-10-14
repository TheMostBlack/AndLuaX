require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "toast"
import "bmob"
import "AndLua"
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
layout14={
  LinearLayout;
  orientation="vertical";
  backgroundColor=颜色2;
  layout_height="fill";
  layout_width="fill";
  {
    LinearLayout;
    backgroundColor=颜色1;
    layout_height="56dp";
    layout_width="fill";
    {
      ImageView;
      layout_gravity="center";
      layout_width="25dp";
      layout_marginLeft="20dp";
      id="tclts",
      src="res/ThomeLua8.png";
      ColorFilter="0xFF03A9F4";
    };
    {
      TextView;
      layout_gravity="center";
      textColor="0xFF03A9F4";
      layout_marginLeft="30dp";
      textSize="18sp";
      text="聊天大厅";
    };
    {
      ImageView;
      layout_gravity="center";
      layout_width="30dp";
      layout_marginLeft="25%h";
      id="ltsqt";
      src="imgs/Other.png";
      ColorFilter="0xFF03A9F4";
    };
  };
  {
    RelativeLayout;
    layout_width="fill";
    layout_height="fill";
    {
      LinearLayout;
      layout_centerVertical="true";
      layout_width="match_parent";
      orientation="vertical";
      layout_alignParentBottom="true";
      layout_height="89%h";
      layout_centerHorizontal="true";
      {
        RelativeLayout;
        layout_width="match_parent";
        layout_height="match_parent";
        {
          ListView;
          id="聊天室",
          layout_width="fill";
          layout_height="80%h";
          DividerHeight=0;
          layout_marginTop="1%h",
        };
        {
          LinearLayout;
          backgroundColor=颜色1;
          layout_alignParentBottom="true";
          layout_width="match_parent";
          gravity="center";
          layout_height="7%h";
          {
            EditText;
            layout_marginRight="3%w";
            hint="说点什么吧...";
            textSize="11sp";
            layout_height="5%h",
            textColor=颜色3,
            hintTextColor=颜色3,
            id="lxx",
            layout_width="75%w";
          };
          {
            Button;
            style="?android:attr/buttonBarButtonStyle";
            layout_width="9%h";
            textSize="10sp";
            id="l发送",
            layout_height="5%h";
            text="发送";
          };
        };
      };
    };
  };
};


activity.setContentView(loadlayout(layout14))
隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
xp,xq=...
id="f4583c64387a36e08a2d7f2181f4ca76"
key="e3b5994c466cb86b0450011c57bdf152"
local bmob=bmob(id,key)
lt={
  {
    LinearLayout;
    layout_width="fill";
    layout_height="fill";
    orientation="vertical";
    {
      LinearLayout;
      layout_marginLeft="1%h",
      layout_width="40%h";
      {
        CircleImageView;
        layout_width="7%h";
        layout_height="7%h";
        id="l用户图",
        src="imgs/User.png";
      };
      {
        LinearLayout;
        layout_width="match_parent";
        orientation="vertical";
        {
          TextView;
          id="l用户名",
          textColor=颜色4,
          layout_marginLeft="1%h";
          text="用户名";
          layout_marginTop="1%w";
        };
        {
          CardView;
          radius=15;
          CardElevation=0;
          backgroundColor=颜色1,
          layout_marginLeft="1%h";
          layout_marginBottom="10dp";
          layout_marginTop="1%h";
          {
            TextView;
            id="l信息",
            textColor=颜色3;
            layout_marginRight="1.3%h";
            layout_marginTop="1%h";
            layout_marginBottom="1%h";
            layout_marginLeft="1.3%h";
            layout_gravity="center";
            text="消息";
          };
        };
      };
    };
  };
  {
    LinearLayout;
    layout_width="fill";
    layout_height="fill";
    orientation="vertical";
    {
      LinearLayout;
      layout_gravity="right",
      layout_marginRight="1%h",
      layout_width="40%h";
      {
        LinearLayout;
        layout_width="33%h";
        gravity="right";
        orientation="vertical";
        {
          TextView;
          id="l用户名",
          textColor=颜色4,
          layout_marginTop="1%w";
          text="用户名";
          layout_marginRight="1%h";
        };
        {
          CardView;
          CardElevation=0;
          backgroundColor=颜色1,
          radius=15;
          layout_marginBottom="10dp";
          layout_marginRight="1%h";
          layout_marginTop="1%h";
          layout_marginLeft="1%h";
          {
            TextView;
            id="l信息",
            layout_marginTop="1%h";
            layout_marginBottom="1%h";
            layout_marginRight="1.3%h";
            layout_gravity="center";
            textColor=颜色3;
            text="消息";
            layout_marginLeft="1.3%h";
          };
        };
      };
      {
        CircleImageView;
        id="l用户图",
        layout_width="7%h";
        layout_height="7%h";
        src="imgs/User.png";
      };
    };
  };
}
local lgs=nil
控件圆角(lxx,颜色2,25)
控件圆角(l发送,颜色2,25)
local adp=LuaMultiAdapter(activity,lt)
function sxlt()
  if xp==true then
    adp.clear()
    bmob:query("lts",nil,function(a,b)
      if #b.results~=0 then
        for i=1,#b.results do
          local c=b.results[i].zh
          local d=b.results[i].xx
          local e=b.results[i].mc
          if e:find(xq) then
            adp.add{__type=2,l用户名=e,l用户图="http://q1.qlogo.cn/g?b=qq&nk="..c.."&s=640",l信息=d}
           else
            adp.add{__type=1,l用户名=e,l用户图="http://q1.qlogo.cn/g?b=qq&nk="..c.."&s=640",l信息=d}
          end
          local s=聊天室.getCount()
          聊天室.setSelection(s)
          lgs=s
        end
      end
    end)
  end
end
sxlt()
聊天室.Adapter=adp
if not File("/sdcard/.cookie.dat").isFile() then
  创建文件("/sdcard/.cookie.dat")
 else
  local lzh=io.open("/sdcard/.cookie.dat"):read("*a")
end
function l发送.onClick()
  if #lxx.text~=0 then
    local d={}
    d.mc=xq
    d.zh=lzh
    d.xx=lxx.text
    bmob:insert("lts",d,function(a,b)
      if a==-1 then
        print"发送失败"
       else
        sxlt()
      end
    end)
    lxx.setText("")
   else
    print"请输入内容"
  end
end
ti2=Ticker()
ti2.Period=42000
ti2.onTick=function()
  bmob:query("lts",nil,function(a,b)
    if #b.results~=0 then
      local llgs=#b.results
      if llgs>lgs then
        sxlt()
      end
    end
  end)
end
if xp==true then
  ti2.start()
end
function tclts.onClick()
  activity.finish()
  ti2.stop()
end
function ltsqt.onClick()
  pop=PopupMenu(activity,ltsqt)
  menu=pop.Menu
  menu.add("聊天公告").onMenuItemClick=function()
    print"禁止讨论与外挂相关的话题，政治敏感的话题"
  end
  pop.show()
end
波纹(tclts,0xFFD9D9D9)
波纹(ltsqt,0xFFD9D9D9)
function onKeyDown(a,b)
  if a==4 then
    ti2.stop()
  end
end