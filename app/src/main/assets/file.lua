require "import"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "android.app.*"
import "android.graphics.Typeface"
import "android.content.Intent"
import "android.content.Context"
import "android.content.pm.PackageManager"
import "android.graphics.drawable.ColorDrawable"
import "android.content.Intent"
import "java.io.File"
import "other"
import "Dialog"
import "toast"
import "main6"
import "com.androlua.LuaAdapter"
import "android.graphics.drawable.StateListDrawable"
import "android.graphics.drawable.GradientDrawable"
import "com.androlua.LuaUtil"

title,StartPath,filterTypes=...
local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xffffffff
  颜色5=0xff303030
  颜色6=0xEBFFFFFF
  颜色7=0xFF03A9F4
  activity.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
 else
  颜色1=0x5FFFFFFF
  颜色3=0xff303030
  颜色4=0xffffffff
  颜色5=0x5FFFFFFF
  颜色2=0xFFF2F1F6
  颜色6=0xff303030
  颜色7=0xFF03A9F4
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)
end
item={
  LinearLayout;
  layout_height="fill";
  layout_width="fill";
  {
    LinearLayout;
    layout_width="match_parent";
    layout_height="70dp";
    gravity="center";
    {
      CardView;
      radius=25;
      layout_height="50dp";
      layout_width="340dp";
      CardElevation="0dp";
      backgroundColor=颜色1;
      {
        LinearLayout;
        orientation="horizontal";
        layout_width="match_parent";
        layout_height="match_parent";
        {
          ImageView;
          src="file.png";
          ColorFilter=颜色6,
          layout_marginLeft="10dp";
          layout_gravity="center";
          id="img";
        };
        {
          TextView;
          layout_height="20dp";
          id="file";
          textColor=颜色6;
          layout_marginLeft="10dp";
          layout_width="250dp";
          layout_gravity="center";
        };
      };
    };
  };
};
layout={
  LinearLayout;
  backgroundColor=颜色2,
  layout_height="fill";
  orientation="vertical";
  layout_width="fill";
  {
    LinearLayout;
    backgroundColor=颜色1;
    layout_height="106dp";
    orientation="vertical";
    layout_width="match_parent";
    {
      TextView;
      layout_marginTop="5dp";
      text="选择源码(*.alp)";
      layout_marginLeft="10dp";
      textSize="16sp";
      textColor="0xFF03A9F4";
    };
    {
      TextView;
      layout_marginTop="5dp";
      layout_marginLeft="10dp";
      singleLine=true;
      textColor="0xFF03A9F4";
      text="路径";
      focusable=true;
      ellipsize="marquee";
      layout_width="300dp";
      focusableInTouchMode=true;
      id="cp";
    };
    {
      LinearLayout;
      layout_height="fill";
      orientation="horizontal";
      layout_width="fill";
      gravity="center",
      {
        Button,
        layout_marginRight="2dp",
        text="ThomeLua",
        AllCaps=false,
        id="XX",
      },
      {
        Button,
        layout_marginRight="2dp",
        text="系统下载",
        id="系统",
      },
      {
        Button,
        layout_marginRight="2dp",
        text="内部存储",
        textColor=颜色3,
        id="内部",
      },
    },
  };
  {
    LinearLayout;
    layout_height="match_parent";
    layout_width="match_parent";
    {
      ListView;
      id="lv",
      layout_height="match_parent";
      DividerHeight=0;--设置无隔断线
      layout_width="match_parent";
    };
  };
};
activity.setContentView(layout)
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end
--导入类
local context=activity or service

local LuaBitmap=luajava.bindClass "com.androlua.LuaBitmap"
local function loadbitmap(path)
  if not path:find("^https*://") and not path:find("%.%a%a%a%a?$") then
    path=path..".png"
  end
  if path:find("^https*://") then
    return LuaBitmap.getHttpBitmap(context,path)
   elseif not path:find("^/") then
    return LuaBitmap.getLocalBitmap(context,string.format("%s/%s",luajava.luadir,path))
   else
    return LuaBitmap.getLocalBitmap(context,path)
  end
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
--美化按钮1(QQ,10,0x7a00bfff,0x7a00bfff)
美化按钮1(XX,10,0x7E000000,颜色6)
美化按钮1(系统,10,0x7E000000,颜色6)
美化按钮1(内部,10,0x7E000000,颜色6)
function getExtension(str)
  return str:match(".+%.(%w+)$")
end
import "android.widget.ArrayAdapter"
import "android.widget.LinearLayout"
import "android.widget.TextView"
import "java.io.File"
import "android.widget.ListView"
import "android.app.AlertDialog"
data={}
adp=LuaAdapter(activity,data,item)
lv.setAdapter(adp)
function attrdir(path)
  for file in lfs.dir(path) do
    if file ~= "." and file ~= ".." then
      local f = path.."/"..file
      local attr = lfs.attributes(f)
      switch attr.mode
       case "directory"
        adp.add{file=file,img="folder.png"}
       default
        task(1,function()
          adp.add{file=file,img="file.png"}
        end)
      end
    end
  end
end
attrdir("/sdcard")
cp.Text="/sdcard"
require "import"
import "android.widget.*"
import "android.view.*"
import "android.content.*"
import "android.net.*"
import "android.provider.*"
import "java.io.File"
--[[import "android.support.v4.provider.*"
import "android.net.Uri"
import "android.provider.DocumentsContract"
local data={}

data.requestPermission=function()
  local parse = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");

  local intent = Intent("android.intent.action.OPEN_DOCUMENT_TREE");

  intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
  | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
  | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
  | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);


  intent.putExtra("android.provider.extra.INITIAL_URI",DocumentsContract.buildDocumentUriUsingTree(parse, DocumentsContract.getTreeDocumentId(parse)));

  activity.startActivityForResult(intent, 11);

end


data.savePermission=function(requestCode, resultCode, data)
  if (data == nil) then
    return;
  end
  local uri = data.getData()
  local uri1 = "^content://com.android.externalstorage.documents/tree/.+%%3AAndroid%%2Fdata%%?2?F?$"
  if (tostring(uri):find(uri1) && requestCode == 11 && uri ~= nil) then
    activity.getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));--关键是这里，这个就是保存这个目录的访问权限
    return true
   else
    return false
  end
end


data.getSingleDoucmentFile=function(path)
  local path=String(path)
  if path.endsWith("/") then
    path = path.substring(0, path.length() - 1);
  end
  local path2 = path.replace("/storage/emulated/0/", ""):gsub("/sdcard/",""):gsub("/", "%%2F");
  return DocumentFile.fromSingleUri(activity, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" .. path2));

end

data.getDoucmentFile=function(path)
  local path=path:gsub("/sdcard/Android/data",""):gsub("/storage/emulated/0/Android/data", "")
  if path:sub(1,1)=="/" then
    path=utf8.sub(path,2,utf8.len(path))
  end
  local pathTab=luajava.astable(String(path).split("/"))

  local doucmentfile=DocumentFile.fromTreeUri(activity, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata"));
  if utf8.len(path)==0 then
    return doucmentfile
  end

  for i=1,#pathTab do
    local doucmentfile2=doucmentfile.findFile(pathTab[i])

    if doucmentfile2==nil then
      if pathTab[i]:find("%.(.+)") then
        --    doucmentfile2=doucmentfile.createFile("*/*",pathTab[i])
       else
        --    doucmentfile2=doucmentfile.createDirectory(pathTab[i])
      end
    end

    doucmentfile=doucmentfile2

  end
  return doucmentfile
end

data.getFileLastModified=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name

  return data.getDoucmentFile(parentPath).findFile(name).lastModified()
end


data.getFileLastModified=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name

  return data.getDoucmentFile(parentPath).findFile(name).lastModified()
end


data.getFileList=function(path)
  local list=luajava.astable(data.getDoucmentFile(path).listFiles())
  local s=utf8.sub(path,-1)

  table.foreach(list,function(k,v)
    list[k]=path..(s=="/" and "" or "/")..v.name
  end)
  return list
end

data.exists=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name

  return data.getDoucmentFile(parentPath).findFile(name).exists()

end

data.isDirectory=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name
  return data.getDoucmentFile(parentPath).findFile(name).isDirectory()

end


data.isFile=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name
  return data.getDoucmentFile(parentPath).findFile(name).isFile()
end


data.createFile=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name
  return data.getDoucmentFile(parentPath).createFile("*/*",name)
end

data.isGrant=function()
  for k,uriPermission in pairs(luajava.astable(activity.getContentResolver().getPersistedUriPermissions())) do
    if (uriPermission.isReadPermission() && uriPermission.getUri().toString():find("content://com.android.externalstorage.documents/tree/primary%%3AAndroid%%2Fdata")) then
      return true;
    end
  end
  return false;
end


data.createDirectory=function(path)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name
  return data.getDoucmentFile(parentPath).createDirectory(name)
end

data.renameTo=function(path,nameTo)
  local file=File(path)
  local parentPath=file.parentFile.path
  local name=file.name
  return data.getDoucmentFile(parentPath).findFile(name).renameTo(File(nameTo).name)
end

data.getFileLength=function(path)
  local singleDoucmentFile=data.getSingleDoucmentFile(path)
  return singleDoucmentFile.length()
end

data.deleteFile=function(path)
  local singleDoucmentFile=data.getSingleDoucmentFile(path)
  return singleDoucmentFile.delete()
end

data.getFileOutputStream=function(path)
  local singleDoucmentFile=data.getSingleDoucmentFile(path)
  return activity.getContentResolver().openOutputStream(singleDoucmentFile.uri)
end

data.getFileInputStream=function(path)
  local singleDoucmentFile=data.getSingleDoucmentFile(path)
  return activity.getContentResolver().openInputStream(singleDoucmentFile.uri)
end
function QQ.onClick()
  if not data.isGrant() then
    data.requestPermission()
   else
    adp.clear()
    ls=data.getFileList("/sdcard/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/")
    for index,c in ipairs(ls) do
      adp.add{file=c:match("/sdcard/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/(.+)"),img="file.png"}
    end
    lv.onItemClick=function(l,v,p,s)
      io.open("/sdcard/ThomeLua/dq.alp","w"):write(tostring(String(LuaUtil.readAll(data.getFileInputStream("/sdcard/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/"..v.tag.file.text))))):close()
    end
  end

  function onActivityResult(a,b,c)
    if a==11
      if not data.savePermission(a,b,c) then
        print("获取权限失败")
        --activity.finish()
       else
        adp.clear()
        ls=data.getFileList("/sdcard/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/")
        for index,c in ipairs(ls) do
          adp.add{file=c:match("/sdcard/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/(.+)"),img="file.png"}
        end
        lv.onItemClick=function(l,v,p,s)
          print(tostring(String(LuaUtil.readAll(
          data.getFileInputStream(path)))))
        end
      end
    end
  end
end]]
function XX.onClick()
  adp.clear()
  attrdir("/sdcard/ThomeLua")
  cp.Text="/sdcard/ThomeLua"
end
function 系统.onClick()
  adp.clear()
  attrdir("/sdcard/Download")
  cp.Text="/sdcard/Download"
end
function 内部.onClick()
  adp.clear()
  attrdir("/sdcard")
  cp.Text="/sdcard"
end
lv.onItemClick=function(l,v,p,s)
  local 路径=tostring(cp.Text)
  local 文件名=tostring(v.tag.file.Text)
  switch 文件名
   case "返回上级目录"
    adp.clear()
    local up=tostring(File(cp.Text).getParentFile())
    switch up
     case "/sdcard"
      attrdir(up)
      cp.Text=up
     default
      adp.add{file="返回上级目录",img="folder.png"}
      attrdir(up)
      cp.Text=up
    end
   default
    switch lfs.attributes(路径.."/"..文件名)["mode"]
     case "directory"
      adp.clear()
      adp.add{file="返回上级目录",img="folder.png"}
      attrdir(路径.."/"..文件名)
      cp.Text=路径.."/"..文件名
     default
      switch getExtension(tostring(路径.."/"..文件名))
       case "alp"

        local sc={
          CardView;
          radius=30;
          layout_width="match_parent";
          --orientation="vertical";
          layout_height="match_parent";
          {
            CardView;
            layout_gravity="center";
            layout_height="280dp";
            layout_width="match_parent";
            backgroundColor=颜色2,
            radius=20;
            {
              TextView;
              layout_marginLeft="25dp";
              layout_marginTop="10dp";
              textSize="20sp";
              textColor="0xFF03A9F4";
              text="提示";
            };
            {
              TextView;
              textSize="15sp";
              textColor=颜色6,
              layout_marginLeft="25dp";
              text="是否导入此工程？";
              layout_marginTop="45dp";
            };
            {
              LinearLayout;
              layout_marginBottom="50dp";
              layout_gravity="bottom";
              layout_marginTop="20dp";
              layout_width="match_parent";
              layout_height="50dp";
              {
                Button;
                text="取消";
                layout_gravity="center";
                layout_marginLeft="20dp";
                id="取消导入工程";
                layout_height="40dp";
              };
              {
                LinearLayout;
                layout_width="match_parent";
                gravity="right";
                layout_height="match_parent";
                {
                  Button;
                  layout_marginRight="20dp";
                  layout_gravity="center";
                  text="导入";
                  layout_height="40dp";
                  id="确定导入工程";
                };
              };
            };
          };
        };

        dialog2= AlertDialog.Builder(this)
        dialog3=dialog2.show()
        dialog3.getWindow().setContentView(loadlayout(sc));
        dialog3.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
        local dialogWindow = dialog3.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialog3.getWindow().getAttributes().width=(activity.Width);
        dialog3.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        美化按钮1(取消导入工程,10,0x7E000000,颜色6)
        美化按钮1(确定导入工程,10,0x7a00bfff,颜色7)
        function 取消导入工程.onClick()
          dialog3.dismiss()
        end
        function 确定导入工程.onClick()
          import "java.io.File"
          local a=File(tostring(路径.."/"..文件名)).getName()
          local b=a:match("(.-).alp")
          local c=项目文件夹.."/"..a
          local d=项目文件夹.."/"..b..".zip"
          local e=项目文件夹.."/"..b
          LuaUtil.copyDir(tostring(路径.."/"..文件名),c)
          File(c).renameTo(File(d))
          LuaUtil.unZip(d,e)
          os.remove(d)
          dialog3.dismiss()
          activity.result{true}
        end
       default
        print"暂不支持导入此工程"
      end
    end
  end
end