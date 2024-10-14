require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "java.io.File"
import "layout"
import "item"
import "autotheme"

activity.setTitle('插件')
function getFilesDir()
  return "/data/user/0/"..activity.getPackageName().."/files/"
end
local a=io.open(getFilesDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xffffffff
  颜色6=0xffffffff
  颜色5=0xff212121
  颜色7=0xEBFFFFFF
  activity.setTheme(android.R.style.Theme_DeviceDefault)
 else
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色4=0xff757575
  颜色2=0xFFF2F1F6
  颜色6=0xff757575
  颜色5=0x5FFFFFFF
  颜色7=0xff303030
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light)
end
activity.setContentView(loadlayout(layout))

local luadir,luapath=...
local plugindir=activity.getLuaExtDir("plugin")

local function getinfo(dir)
  local app={}
  loadfile(plugindir.."/"..dir.."/init.lua","bt",app)()
  return app
end

local pds=File(plugindir).list()
Arrays.sort(pds)
local pls={}
local pps={}
for n=0,#pds-1 do
  local s,i=pcall(getinfo,pds[n])
  if s then
    table.insert(pls,i)
    table.insert(pps,pds[n])
  end
end

function checkicon(i)
  i=plugindir.."/"..pps[i].."/icon.png"
  local f=io.open(i)
  if f then
    f:close()
    return i
   else
    return R.drawable.icon
  end
end

adp=LuaAdapter(activity,item)
for k,v in ipairs(pls) do
  adp.add{icon=checkicon(k),title=v.appname.." "..v.appver,description=v.description or ""}
end
plist.Adapter=adp
plist.onItemClick=function(l,v,p,i)
  activity.newActivity(plugindir.."/"..pps[p+1].."/main.lua",{luadir,luapath})
end
import "android.content.*"
import "android.graphics.drawable.StateListDrawable"
function onCreateOptionsMenu(menu)
  menu.add("导入插件").setShowAsAction(1)
end
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
function onOptionsItemSelected(item)
  if item.Title=="导入插件" then
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
          text="导入插件(*.XX)";
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
            hint="请输入插件路径";
            id="输入插件",
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
    控件圆角(输入插件,颜色2,30)
    function 取消创建工程.onClick()
      dialog1.dismiss()
    end
    function exec(cmd,sh,su)
      cmd=tostring(cmd)
      if sh==true then
        cmd=io.open(cmd):read("*a")
      end
      if su==0 then
        p=io.popen(string.format('%s',cmd))
       else
        p=io.popen(string.format('%s',"su -c "..cmd))
      end
      local s=p:read("*a")
      p:close()
      return s
    end


    function 确定创建工程.onClick()
      插件路径="/storage/emulated/0/ThomeLua/plugin/"
      插件名称=File(输入插件.Text).getName():match("(.-).XX")
      exec("cp -r "..输入插件.Text.." "..插件路径)
      os.execute('mkdir '..插件路径..插件名称)
      os.execute("mv "..插件路径..插件名称..".XX".." "..插件路径..插件名称..".zip")
      ZipUtil.unzip(插件路径..插件名称..".zip",插件路径..插件名称)
      os.execute("rm -r "..插件路径..插件名称..".zip")
      print("导入成功")
      dialog1.dismiss()
    end
  end
end

