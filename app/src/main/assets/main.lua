require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
--import "AndLua"
import "other"
import "java.io.File"
import "com.androlua.LuaUtil"
compile "libs/android-support-v4"
import "android.support.v4.widget.*"
import "bmob"
import "android.graphics.drawable.ColorDrawable"
import "android.graphics.drawable.StateListDrawable"
import "android.graphics.PorterDuffColorFilter"
import "android.graphics.PorterDuff"
import "Dialog"
import "java.io.File"
import "android.graphics.Color"
import "android.graphics.drawable.GradientDrawable"
import "android.graphics.drawable.RippleDrawable"
import "android.content.res.ColorStateList"
import "android.graphics.drawable.ShapeDrawable"
import "android.graphics.drawable.shapes.RectShape"
import "android.graphics.Path"
import "android.graphics.Canvas"
import "com.baoyz.widget.PullRefreshLayout"
--activity.setTheme(R.AndLua5)
activity.setTitle("ThomeLua")
local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xCFFFFFFF
  颜色5=0xff303030
  颜色7=0xEBFFFFFF
  activity.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
 else
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色4=0xff757575
  颜色2=0xFFF2F1F6
  颜色5=0x5FFFFFFF
  颜色7=0xff303030
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)
end


import "android.graphics.Typeface"
import "java.io.File"

import "project"







import "Community"
import "com.applua.RippleView"

--import "My"






import "layout"



activity.setContentView(loadlayout(layout))
--隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

波纹(其他,0xFFD9D9D9)
local wl=activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE).getActiveNetworkInfo();

import "projectitem"

local plugindir=("/sdcard/ThomeLua/project/")

local function getinfo(dir)
  local app={}
  loadfile(plugindir.."/"..dir.."/init.lua","bt",app)()
  return app
end



local function sort(a,b)
  return string.lower(a.appname) < string.lower(b.appname)
end

local pds=File(plugindir).list()
local pls={}
for n=0,#pds-1 do
  local s,i=pcall(getinfo,pds[n])
  switch s
   case true
    i.path="/"..pds[n]
    table.insert(pls,i)
  end
end
table.sort(pls,sort)

local function checkicon(i)
  --i=plugindir.."/"..pps[i].."/icon.png"
  local f=io.open(i)
  if f then
    f:close()
    return i
   else
    return R.drawable.icon
  end
end


function 刷新项目()
  local adp=LuaAdapter(activity,projectitem)
  adp.clear()
  local pds=File(plugindir).list()
  local pls={}
  for n=0,#pds-1 do
    local s,i=pcall(getinfo,pds[n])
    switch s
     case true
      i.path="/"..pds[n]
      table.insert(pls,i)
    end
  end
  table.sort(pls,sort)
  for k,v in ipairs(pls) do
    local i=plugindir..v.path.."/icon.png"
    switch v.appname
     case nil
      appname="nil"
     default
      appname=v.appname
    end
    switch v.packagename
     case nil
      packagename="nil"
     default
      packagename=v.packagename
    end
    switch v.appcode
     case nil
      appcode="nil"
     default
      appcode=v.appcode
    end
    switch v.path
     case nil
      pat="nil"
     default
      pat=v.path
    end
    adp.add{图标=checkicon(i),软件名=appname,包名="包名："..packagename,版本="版本："..appcode,文件夹名称=pat}
  end
  项目列表.Adapter=adp
end
项目列表.onItemClick=function(l,v,p,i)
  --activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
  activity.newActivity("main2",android.R.anim.fade_in,android.R.anim.fade_out,{项目文件夹..v.tag.文件夹名称.text,true,v.tag.文件夹名称.text,"/storage/emulated/0/ThomeLua+/project/"..v.tag.软件名.text})
  return true
end
--[[function 刷新项目(pls)
  local adp=LuaAdapter(activity,projectitem)
  for k,v in ipairs(pls) do
    local i=plugindir..v.path.."/icon.png"
    adp.add{图标=checkicon(i),软件名=appname,包名="包名："..packagename,版本="版本："..appcode}
  end
  项目列表.Adapter=adp
end]]
项目列表.onItemLongClick=function(l,v,p,i)
  标题=v.tag.文件夹名称.text
  local ca={
    CardView;
    radius=30;
    layout_width="match_parent";
    --orientation="vertical";
    layout_height="match_parent";
    {
      CardView;
      layout_gravity="center";
      layout_height="match_parent";
      layout_width="match_parent";
      backgroundColor=颜色1,
      radius=20;
      {
        TextView;
        text="项目操作";
        layout_marginTop="10dp";
        textSize="20sp";
        layout_marginLeft="25dp";
        textColor="0xFF03A9F4";
      };

      {
        ListView;
        id="项目操作";
        items={
          "";
          " 删除";
          " 打包";
          " 属性";
          " 分享";
          --  " 详情";
        };
        layout_height="fill";
        backgroundColor="0xffffff";
        DividerHeight=0;
        layout_width="fill";
      };
    };
  };


  dialog= AlertDialog.Builder(this)
  dialog1=dialog.show()
  dialog1.getWindow().setContentView(loadlayout(ca));
  dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
  local dialogWindow = dialog1.getWindow();
  dialogWindow.setGravity(Gravity.BOTTOM);
  dialog1.getWindow().getAttributes().width=(activity.Width);
  dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
  项目操作.onItemClick=function(l,v,p,i)
    if p==1 then
      a=LuaUtil.rmDir(File(项目文件夹.."/"..标题))
      if a==true then
        print"删除成功"
        刷新项目()
       else
        print"删除失败"
        刷新项目()
      end
      dialog1.dismiss()
     elseif p==2 then
      import "java.util.zip.ZipOutputStream"
      import "android.net.Uri"
      import "java.io.File"
      import "android.widget.Toast"
      import "java.util.zip.CheckedInputStream"
      import "java.io.FileInputStream"
      import "android.content.Intent"
      import "com.androlua.LuaUtil"
      import "java.security.Signer"
      import "java.util.ArrayList"
      import "java.io.FileOutputStream"
      import "java.io.BufferedOutputStream"
      import "java.util.zip.ZipInputStream"
      import "java.io.BufferedInputStream"
      import "java.util.zip.ZipEntry"
      import "android.app.ProgressDialog"
      import "java.util.zip.CheckedOutputStream"
      import "java.util.zip.Adler32"
      import "android.graphics.drawable.ColorDrawable"
      local bin_dlg, error_dlg
      local function update(s)
        bin_dlg.setMessage(s)
      end

      local function callback(s)
        LuaUtil.rmDir(File(activity.getLuaExtDir("bin/.temp")))
        bin_dlg.dismiss()
        bin_dlg.Message = ""
        if not s:find("成功") then
          error_dlg.Message = s
          error_dlg.show()
        end
      end

      local function create_bin_dlg()
        if bin_dlg then
          return
        end
        bin_dlg = ProgressDialog(activity);
        bin_dlg.setTitle("正在打包");
        bin_dlg.setMax(100);
      end

      local function create_error_dlg2()
        if error_dlg then
          return
        end
        error_dlg = AlertDialogBuilder(activity)
        error_dlg.Title = "出错"
        error_dlg.setPositiveButton("确定", nil)
      end
      local function binapk(luapath, apkpath)
        require "import"
        --  module(...,package.seeall)
        --by nirenr

        local function ps(str)
          str = str:gsub("%b\"\"",""):gsub("%b\'\'","")
          local _,f= str:gsub ('%f[%w]function%f[%W]',"")
          local _,t= str:gsub ('%f[%w]then%f[%W]',"")
          local _,i= str:gsub ('%f[%w]elseif%f[%W]',"")
          local _,d= str:gsub ('%f[%w]do%f[%W]',"")
          local _,e= str:gsub ('%f[%w]end%f[%W]',"")
          local _,r= str:gsub ('%f[%w]repeat%f[%W]',"")
          local _,u= str:gsub ('%f[%w]until%f[%W]',"")
          local _,a= str:gsub ("{","")
          local _,b= str:gsub ("}","")
          return (f+t+d+r+a)*4-(i+e+u+b)*4
        end


        local function _format()
          local p=0
          return function(str)
            str=str:gsub("[ \t]+$","")
            str=string.format('%s%s',string.rep(' ',p),str)
            p=p+ps(str)
            return str
          end
        end


        function format(Text)
          local t=os.clock()
          local Format=_format()
          Text=Text:gsub('[ \t]*([^\r\n]+)',function(str)return Format(str)end)
          print('操作完成,耗时:'..os.clock()-t)
          return Text
        end


        function build(path)
          if path then
            local str,st=loadfile(path)
            if st then
              return nil,st
            end
            local path=path..'c'

            local st,str=pcall(string.dumpThomeLua,str,true)
            if st then
              f=io.open(path,'wb')
              f:write(str)
              f:close()
              return path
             else
              os.remove(path)
              return nil,str
            end
          end
        end

        function build_aly(path2)
          if path2 then
            local f,st=io.open(path2)
            if st then
              return nil,st
            end
            local str=f:read("*a")
            f:close()
            str=string.format("local layout=%s\nreturn layout",str)
            local path=path2..'c'
            str,st=loadstring(str,path2:match("[^/]+/[^/]+$"),"bt")
            if st then
              return nil,st:gsub("%b[]",path2,1)
            end

            local st,str=pcall(string.dumpThomeLua,str,true)
            if st then
              f=io.open(path,'wb')
              f:write(str)
              f:close()
              return path
             else
              os.remove(path)
              return nil,str
            end
          end
        end


        compile "mao"
        compile "sign"
        import "java.util.zip.*"
        import "java.io.*"
        import "mao.res.*"
        import "apksigner.*"
        local b = byte[2 ^ 16]
        local function copy(input, output)
          LuaUtil.copyFile(input, output)
          input.close()
          --[[local l=input.read(b)
      while l>1 do
        output.write(b,0,l)
        l=input.read(b)
      end]]
        end
        local function copy2(input, output)
          LuaUtil.copyFile(input, output)
        end
        local temp = File(apkpath).getParentFile();
        if (not temp.exists()) then

          if (not temp.mkdirs()) then

            error("create file " .. temp.getName() .. " fail");
          end
        end

        local tmp = luajava.luadir.. "/tmp.apk"
        local info = activity.getApplicationInfo()
        local ver = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName
        local code = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode

        --local zip=ZipFile(info.publicSourceDir)
        local zipFile = File(info.publicSourceDir)
        local fis = FileInputStream(zipFile);
        --local checksum = CheckedInputStream(fis, Adler32());
        local zis = ZipInputStream(BufferedInputStream(fis));

        local fot = FileOutputStream(tmp)
        --local checksum2 = CheckedOutputStream(fot, Adler32());
        local out = ZipOutputStream(BufferedOutputStream(fot))
        local f = File(luapath)
        local errbuffer = {}
        local replace = {}
        local checked = {}
        local lualib = {}
        local md5s = {}
        local libs = File(activity.ApplicationInfo.nativeLibraryDir).list()
        libs = luajava.astable(libs)
        for k, v in ipairs(libs) do
          --libs[k]="lib/armeabi/"..libs[k]
          replace[v] = true
        end

        local mdp = activity.Application.MdDir
        local function getmodule(dir)
          local mds = File(activity.Application.MdDir .. dir).listFiles()
          mds = luajava.astable(mds)
          for k, v in ipairs(mds) do
            if mds[k].isDirectory() then
              getmodule(dir .. mds[k].Name .. "/")
             else
              mds[k] = "lua" .. dir .. mds[k].Name
              replace[mds[k]] = true
            end
          end
        end

        getmodule("/")

        local function checklib(path)
          if checked[path] then
            return
          end
          local cp, lp
          checked[path] = true
          local f = io.open(path)
          local s = f:read("*a")
          f:close()
          for m, n in s:gmatch("require *%(? *\"([%w_]+)%.?([%w_]*)") do
            cp = string.format("lib%s.so", m)
            if n ~= "" then
              lp = string.format("lua/%s/%s.lua", m, n)
              m = m .. '/' .. n
             else
              lp = string.format("lua/%s.lua", m)
            end
            if replace[cp] then
              replace[cp] = false
            end
            if replace[lp] then
              checklib(mdp .. "/" .. m .. ".lua")
              replace[lp] = false
              lualib[lp] = mdp .. "/" .. m .. ".lua"
            end
          end
          for m, n in s:gmatch("import *%(? *\"([%w_]+)%.?([%w_]*)") do
            cp = string.format("lib%s.so", m)
            if n ~= "" then
              lp = string.format("lua/%s/%s.lua", m, n)
              m = m .. '/' .. n
             else
              lp = string.format("lua/%s.lua", m)
            end
            if replace[cp] then
              replace[cp] = false
            end
            if replace[lp] then
              checklib(mdp .. "/" .. m .. ".lua")
              replace[lp] = false
              lualib[lp] = mdp .. "/" .. m .. ".lua"
            end
          end
        end

        replace["libluajava.so"] = false
        function isintable(value,tb)
          for k,v in pairs(tb) do
            switch v
             case value
              return true
            end
          end
          return false
        end

        function strippath(filename)
          return string.match(filename, ".+/([^/]*%.%w+)$")
        end
        function stripextension(filename)
          local idx = filename:match(".+()%.%w+$")
          if(idx) then
            return filename:sub(1, idx-1)
           else
            return filename
          end
        end
        function getExtension(str)
          return str:match(".+%.(%w+)$")
        end
        local function addDir(out, dir, f)
          local entry = ZipEntry("assets/" .. dir)
          out.putNextEntry(entry)
          local ls = f.listFiles()
          for n = 0, #ls - 1 do
            local name = ls[n].getName()
            if name==(".using") then
              checklib(luapath .. dir .. name)
             elseif name:find("%.apk$") or name:find("%.luac$") or name:find("^%.") then
             elseif name:find("%.lua$") then
              checklib(luapath .. dir .. name)
              switch skip_compilation
               case nil
                path=build(luapath..dir..name)
               default
                switch isintable(dir..name,skip_compilation)
                 case true
                  local readlua=io.open(luapath..dir..name):read("*a")
                  io.open(luapath..dir..name.."c","w"):write(readlua):close()
                  path=luapath..dir..name.."c"
                 default
                  path=build(luapath..dir..name)
                end
              end
              if path then
                if replace["assets/" .. dir .. name] then
                  table.insert(errbuffer, dir .. name .. "/.aly")
                end
                local entry = ZipEntry("assets/" .. dir .. name)
                out.putNextEntry(entry)

                replace["assets/" .. dir .. name] = true
                copy(FileInputStream(File(path)), out)
                table.insert(md5s, LuaUtil.getFileMD5(path))
                os.remove(path)
               else
                table.insert(errbuffer, err)
              end
             elseif name:find("%.aly$") then
              switch skip_compilation
               case nil
                path=build_aly(luapath..dir..name)
               default
                switch isintable(dir..name,skip_compilation)
                 case true
                  local readlua=io.open(luapath..dir..name):read("*a")
                  io.open(luapath..dir..name.."c","w"):write(stripextension(strippath(luapath..dir..name)).."="..readlua):close()
                  path=luapath..dir..name.."c"
                 default
                  path=build_aly(luapath..dir..name)
                end
              end
              if path then
                name = name:gsub("aly$", "lua")
                if replace["assets/" .. dir .. name] then
                  table.insert(errbuffer, dir .. name .. "/.aly")
                end
                local entry = ZipEntry("assets/" .. dir .. name)
                out.putNextEntry(entry)

                replace["assets/" .. dir .. name] = true
                copy(FileInputStream(File(path)), out)
                table.insert(md5s, LuaUtil.getFileMD5(path))
                os.remove(path)
               else
                table.insert(errbuffer, err)
              end
             elseif ls[n].isDirectory() then
              addDir(out, dir .. name .. "/", ls[n])
             else
              local entry = ZipEntry("assets/" .. dir .. name)
              out.putNextEntry(entry)
              replace["assets/" .. dir .. name] = true
              copy(FileInputStream(ls[n]), out)
              table.insert(md5s, LuaUtil.getFileMD5(ls[n]))
            end
          end
        end


        this.update("正在编译...");
        if f.isDirectory() then
          require "permission"
          dofile(luapath .. "init.lua")
          if user_permission then
            for k, v in ipairs(user_permission) do
              user_permission[v] = true
            end
          end


          local ss, ee = pcall(addDir, out, "", f)
          if not ss then
            table.insert(errbuffer, ee)
          end
          --print(ee,dump(errbuffer),dump(replace))


          local wel = File(luapath .. "icon.png")
          if wel.exists() then
            local entry = ZipEntry("res/drawable/icon.png")
            out.putNextEntry(entry)
            replace["res/drawable/icon.png"] = true
            copy(FileInputStream(wel), out)
          end
          local wel = File(luapath .. "welcome.png")
          if wel.exists() then
            local entry = ZipEntry("res/drawable/welcome.png")
            out.putNextEntry(entry)
            replace["res/drawable/welcome.png"] = true

            copy(FileInputStream(wel), out)
          end
         else
          return "error"
        end

        --print(dump(lualib))
        for name, v in pairs(lualib) do
          local path, err = build(v)
          if path then
            local entry = ZipEntry(name)
            out.putNextEntry(entry)
            copy(FileInputStream(File(path)), out)
            table.insert(md5s, LuaUtil.getFileMD5(path))
            os.remove(path)
           else
            table.insert(errbuffer, err)
          end
        end

        apkpath="/storage/emulated/0/ThomeLua/bin/"..appname.."_"..appver..".apk"
        function touint32(i)
          local code = string.format("%08x", i)
          local uint = {}
          for n in code:gmatch("..") do
            table.insert(uint, 1, string.char(tonumber(n, 16)))
          end
          return table.concat(uint)
        end

        this.update("正在打包...");
        local entry = zis.getNextEntry();
        while entry do
          local name = entry.getName()
          local lib = name:match("([^/]+%.so)$")
          if replace[name] then
           elseif lib and replace[lib] then
           elseif name:find("^assets/") then
           elseif name:find("^lua/") then
           elseif name:find("META%-INF") then
           else
            local entry = ZipEntry(name)
            out.putNextEntry(entry)
            if entry.getName() == "AndroidManifest.xml" then
              if path_pattern and #path_pattern > 1 then
                path_pattern = ".*\\\\." .. path_pattern:match("%w+$")
              end
              local list = ArrayList()
              local xml = AXmlDecoder.read(list, zis)
              local req = {
                [activity.getPackageName()] = packagename,
                [info.nonLocalizedLabel] = appname,
                [ver] = appver,
                [".*\\\\.alp"] = path_pattern or "",
                [".*\\\\.lua"] = "",
                [".*\\\\.luac"] = "",
              }
              for n = 0, list.size() - 1 do
                local v = list.get(n)
                if req[v] then
                  list.set(n, req[v])
                 elseif user_permission then
                  local p = v:match("%.permission%.([%w_]+)$")
                  if p and (not user_permission[p]) then
                    list.set(n, "android.permission.UNKNOWN")
                  end
                end
              end
              local pt = activity.getLuaPath(".tmp")
              local fo = FileOutputStream(pt)
              xml.write(list, fo)
              local code = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode
              fo.close()
              local f = io.open(pt)
              local s = f:read("a")
              f:close()
              s = string.gsub(s, touint32(code), touint32(tointeger(appcode) or 1),1)
              s = string.gsub(s, touint32(18), touint32(tointeger(appsdk) or 18),1)

              local f = io.open(pt, "w")
              f:write(s)
              f:close()
              local fi = FileInputStream(pt)
              copy(fi, out)
              os.remove(pt)
             elseif not entry.isDirectory() then
              copy2(zis, out)
            end
          end
          entry = zis.getNextEntry()
        end
        out.setComment(table.concat(md5s))
        --print(table.concat(md5s,"/n"))
        zis.close();
        out.closeEntry()
        out.close()

        if #errbuffer == 0 then
          this.update("正在签名...");
          os.remove(apkpath)
          Signer.sign(tmp, apkpath)
          os.remove(tmp)
          activity.installApk(apkpath)
          --[[import "android.net.*"
        import "android.content.*"
        i = Intent(Intent.ACTION_VIEW);
        i.setDataAndType(activity.getUriForFile(File(apkpath)), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.update("正在打开...");
        activity.startActivityForResult(i, 0);]]
          return "打包成功:" .. apkpath
         else
          os.remove(tmp)
          this.update("打包出错:\n " .. table.concat(errbuffer, "\n"));
          return "打包出错:\n " .. table.concat(errbuffer, "\n")
        end
      end
      luabindir=activity.getLuaExtDir("bin")
      local function bin(path)
        local p = {}
        local e, s = pcall(function()dofile(path .. "init.lua")end)
        if e then
          create_error_dlg2()
          create_bin_dlg()
          bin_dlg.show()
          activity.newTask(binapk, update, callback).execute { path, activity.getLuaExtPath("bin", appname .. "_" .. appver .. ".apk") }
         else
          Toast.makeText(activity, "工程配置文件错误." .. s, Toast.LENGTH_SHORT).show()
        end
      end
      bin(项目文件夹.."/"..标题.."/")
      dialog1.dismiss()
     elseif p==4 then
      function Sharing(path)
        import "android.webkit.MimeTypeMap"
        import "android.content.Intent"
        import "android.net.Uri"
        import "java.io.File"
        import "android.content.FileProvider"
        intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.setType("*/*")
        uri=FileProvider.getUriForFile(activity,activity.getPackageName(),File(path));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        activity.startActivity(Intent.createChooser(intent, "分享到:"))
      end
      LuaUtil.zip(项目文件夹.."/"..标题,"/sdcard/ThomeLua/backup/")
      os.execute("mv ".."/sdcard/ThomeLua/backup/"..标题..".zip".." ".."/sdcard/ThomeLua/backup/"..标题..".alp")
      Sharing("/sdcard/ThomeLua/backup/"..标题..".alp")
      --os.remove("/sdcard/ThomeLua/"..标题..".alp")
     elseif p==1 then
      import "java.util.zip.ZipOutputStream"
      import "android.net.Uri"
      import "java.io.File"
      import "android.widget.Toast"
      import "java.util.zip.CheckedInputStream"
      import "java.io.FileInputStream"
      import "android.content.Intent"
      import "com.androlua.LuaUtil"
      import "java.security.Signer"
      import "java.util.ArrayList"
      import "java.io.FileOutputStream"
      import "java.io.BufferedOutputStream"
      import "java.util.zip.ZipInputStream"
      import "java.io.BufferedInputStream"
      import "java.util.zip.ZipEntry"
      import "android.app.ProgressDialog"
      import "java.util.zip.CheckedOutputStream"
      import "java.util.zip.Adler32"
      import "android.graphics.drawable.ColorDrawable"
      local bin_dlg, error_dlg
      local function update(s)
        bin_dlg.setMessage(s)
      end

      local function callback(s)
        LuaUtil.rmDir(File(activity.getLuaExtDir("bin/.temp")))
        bin_dlg.dismiss()
        bin_dlg.Message = ""
        if not s:find("成功") then
          error_dlg.Message = s
          error_dlg.show()
        end
      end

      local function create_bin_dlg()
        if bin_dlg then
          return
        end
        bin_dlg = ProgressDialog(activity);
        bin_dlg.setTitle("正在打包");
        bin_dlg.setMax(100);
      end

      local function create_error_dlg2()
        if error_dlg then
          return
        end
        error_dlg = AlertDialogBuilder(activity)
        error_dlg.Title = "出错"
        error_dlg.setPositiveButton("确定", nil)
      end
      local function binapk(luapath, apkpath)
        require "import"
        --  module(...,package.seeall)
        --by nirenr

        local function ps(str)
          str = str:gsub("%b\"\"",""):gsub("%b\'\'","")
          local _,f= str:gsub ('%f[%w]function%f[%W]',"")
          local _,t= str:gsub ('%f[%w]then%f[%W]',"")
          local _,i= str:gsub ('%f[%w]elseif%f[%W]',"")
          local _,d= str:gsub ('%f[%w]do%f[%W]',"")
          local _,e= str:gsub ('%f[%w]end%f[%W]',"")
          local _,r= str:gsub ('%f[%w]repeat%f[%W]',"")
          local _,u= str:gsub ('%f[%w]until%f[%W]',"")
          local _,a= str:gsub ("{","")
          local _,b= str:gsub ("}","")
          return (f+t+d+r+a)*4-(i+e+u+b)*4
        end


        local function _format()
          local p=0
          return function(str)
            str=str:gsub("[ \t]+$","")
            str=string.format('%s%s',string.rep(' ',p),str)
            p=p+ps(str)
            return str
          end
        end


        function format(Text)
          local t=os.clock()
          local Format=_format()
          Text=Text:gsub('[ \t]*([^\r\n]+)',function(str)return Format(str)end)
          print('操作完成,耗时:'..os.clock()-t)
          return Text
        end


        function build(path)
          if path then
            local str,st=loadfile(path)
            if st then
              return nil,st
            end
            local path=path..'c'

            local st,str=pcall(string.dumpThomeLua,str,true)
            if st then
              f=io.open(path,'wb')
              f:write(str)
              f:close()
              return path
             else
              os.remove(path)
              return nil,str
            end
          end
        end

        function build_aly(path2)
          if path2 then
            local f,st=io.open(path2)
            if st then
              return nil,st
            end
            local str=f:read("*a")
            f:close()
            str=string.format("local layout=%s\nreturn layout",str)
            local path=path2..'c'
            str,st=loadstring(str,path2:match("[^/]+/[^/]+$"),"bt")
            if st then
              return nil,st:gsub("%b[]",path2,1)
            end

            local st,str=pcall(string.dumpThomeLua,str,true)
            if st then
              f=io.open(path,'wb')
              f:write(str)
              f:close()
              return path
             else
              os.remove(path)
              return nil,str
            end
          end
        end


        compile "mao"
        compile "sign"
        import "java.util.zip.*"
        import "java.io.*"
        import "mao.res.*"
        import "apksigner.*"
        local b = byte[2 ^ 16]
        local function copy(input, output)
          LuaUtil.copyFile(input, output)
          input.close()
          --[[local l=input.read(b)
      while l>1 do
        output.write(b,0,l)
        l=input.read(b)
      end]]
        end
        local function copy2(input, output)
          LuaUtil.copyFile(input, output)
        end
        local temp = File(apkpath).getParentFile();
        if (not temp.exists()) then

          if (not temp.mkdirs()) then

            error("create file " .. temp.getName() .. " fail");
          end
        end

        local tmp = luajava.luadir.. "/tmp.apk"
        local info = activity.getApplicationInfo()
        local ver = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName
        local code = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode

        --local zip=ZipFile(info.publicSourceDir)
        local zipFile = File(info.publicSourceDir)
        local fis = FileInputStream(zipFile);
        --local checksum = CheckedInputStream(fis, Adler32());
        local zis = ZipInputStream(BufferedInputStream(fis));

        local fot = FileOutputStream(tmp)
        --local checksum2 = CheckedOutputStream(fot, Adler32());
        local out = ZipOutputStream(BufferedOutputStream(fot))
        local f = File(luapath)
        local errbuffer = {}
        local replace = {}
        local checked = {}
        local lualib = {}
        local md5s = {}
        local libs = File(activity.ApplicationInfo.nativeLibraryDir).list()
        libs = luajava.astable(libs)
        for k, v in ipairs(libs) do
          --libs[k]="lib/armeabi/"..libs[k]
          replace[v] = true
        end

        local mdp = activity.Application.MdDir
        local function getmodule(dir)
          local mds = File(activity.Application.MdDir .. dir).listFiles()
          mds = luajava.astable(mds)
          for k, v in ipairs(mds) do
            if mds[k].isDirectory() then
              getmodule(dir .. mds[k].Name .. "/")
             else
              mds[k] = "lua" .. dir .. mds[k].Name
              replace[mds[k]] = true
            end
          end
        end

        getmodule("/")

        local function checklib(path)
          if checked[path] then
            return
          end
          local cp, lp
          checked[path] = true
          local f = io.open(path)
          local s = f:read("*a")
          f:close()
          for m, n in s:gmatch("require *%(? *\"([%w_]+)%.?([%w_]*)") do
            cp = string.format("lib%s.so", m)
            if n ~= "" then
              lp = string.format("lua/%s/%s.lua", m, n)
              m = m .. '/' .. n
             else
              lp = string.format("lua/%s.lua", m)
            end
            if replace[cp] then
              replace[cp] = false
            end
            if replace[lp] then
              checklib(mdp .. "/" .. m .. ".lua")
              replace[lp] = false
              lualib[lp] = mdp .. "/" .. m .. ".lua"
            end
          end
          for m, n in s:gmatch("import *%(? *\"([%w_]+)%.?([%w_]*)") do
            cp = string.format("lib%s.so", m)
            if n ~= "" then
              lp = string.format("lua/%s/%s.lua", m, n)
              m = m .. '/' .. n
             else
              lp = string.format("lua/%s.lua", m)
            end
            if replace[cp] then
              replace[cp] = false
            end
            if replace[lp] then
              checklib(mdp .. "/" .. m .. ".lua")
              replace[lp] = false
              lualib[lp] = mdp .. "/" .. m .. ".lua"
            end
          end
        end

        replace["libluajava.so"] = false

        local function addDir(out, dir, f)
          local entry = ZipEntry("assets/" .. dir)
          out.putNextEntry(entry)
          local ls = f.listFiles()
          for n = 0, #ls - 1 do
            local name = ls[n].getName()
            if name==(".using") then
              checklib(luapath .. dir .. name)
             elseif name:find("%.apk$") or name:find("%.luac$") or name:find("^%.") then
             elseif name:find("%.lua$") then
              checklib(luapath .. dir .. name)
              local path, err = build(luapath .. dir .. name)
              if path then
                if replace["assets/" .. dir .. name] then
                  table.insert(errbuffer, dir .. name .. "/.aly")
                end
                local entry = ZipEntry("assets/" .. dir .. name)
                out.putNextEntry(entry)

                replace["assets/" .. dir .. name] = true
                copy(FileInputStream(File(path)), out)
                table.insert(md5s, LuaUtil.getFileMD5(path))
                os.remove(path)
               else
                table.insert(errbuffer, err)
              end
             elseif name:find("%.aly$") then
              local path, err = build_aly(luapath .. dir .. name)
              if path then
                name = name:gsub("aly$", "lua")
                if replace["assets/" .. dir .. name] then
                  table.insert(errbuffer, dir .. name .. "/.aly")
                end
                local entry = ZipEntry("assets/" .. dir .. name)
                out.putNextEntry(entry)

                replace["assets/" .. dir .. name] = true
                copy(FileInputStream(File(path)), out)
                table.insert(md5s, LuaUtil.getFileMD5(path))
                os.remove(path)
               else
                table.insert(errbuffer, err)
              end
             elseif ls[n].isDirectory() then
              addDir(out, dir .. name .. "/", ls[n])
             else
              local entry = ZipEntry("assets/" .. dir .. name)
              out.putNextEntry(entry)
              replace["assets/" .. dir .. name] = true
              copy(FileInputStream(ls[n]), out)
              table.insert(md5s, LuaUtil.getFileMD5(ls[n]))
            end
          end
        end


        this.update("正在编译...");
        if f.isDirectory() then
          require "permission"
          dofile(luapath .. "init.lua")
          if user_permission then
            for k, v in ipairs(user_permission) do
              user_permission[v] = true
            end
          end


          local ss, ee = pcall(addDir, out, "", f)
          if not ss then
            table.insert(errbuffer, ee)
          end
          --print(ee,dump(errbuffer),dump(replace))


          local wel = File(luapath .. "icon.png")
          if wel.exists() then
            local entry = ZipEntry("res/drawable/icon.png")
            out.putNextEntry(entry)
            replace["res/drawable/icon.png"] = true
            copy(FileInputStream(wel), out)
          end
          local wel = File(luapath .. "welcome.png")
          if wel.exists() then
            local entry = ZipEntry("res/drawable/welcome.png")
            out.putNextEntry(entry)
            replace["res/drawable/welcome.png"] = true

            copy(FileInputStream(wel), out)
          end
         else
          return "error"
        end

        --print(dump(lualib))
        for name, v in pairs(lualib) do
          local path, err = build(v)
          if path then
            local entry = ZipEntry(name)
            out.putNextEntry(entry)
            copy(FileInputStream(File(path)), out)
            table.insert(md5s, LuaUtil.getFileMD5(path))
            os.remove(path)
           else
            table.insert(errbuffer, err)
          end
        end

        apkpath="/storage/emulated/0/ThomeLua/bin/"..appname.."_"..appver..".apk"
        function touint32(i)
          local code = string.format("%08x", i)
          local uint = {}
          for n in code:gmatch("..") do
            table.insert(uint, 1, string.char(tonumber(n, 16)))
          end
          return table.concat(uint)
        end

        this.update("正在打包...");
        local entry = zis.getNextEntry();
        while entry do
          local name = entry.getName()
          local lib = name:match("([^/]+%.so)$")
          if replace[name] then
           elseif lib and replace[lib] then
           elseif name:find("^assets/") then
           elseif name:find("^lua/") then
           elseif name:find("META%-INF") then
           else
            local entry = ZipEntry(name)
            out.putNextEntry(entry)
            if entry.getName() == "AndroidManifest.xml" then
              if path_pattern and #path_pattern > 1 then
                path_pattern = ".*\\\\." .. path_pattern:match("%w+$")
              end
              local list = ArrayList()
              local xml = AXmlDecoder.read(list, zis)
              local req = {
                [activity.getPackageName()] = packagename,
                [info.nonLocalizedLabel] = appname,
                [ver] = appver,
                [".*\\\\.alp"] = path_pattern or "",
                [".*\\\\.lua"] = "",
                [".*\\\\.luac"] = "",
              }
              for n = 0, list.size() - 1 do
                local v = list.get(n)
                if req[v] then
                  list.set(n, req[v])
                 elseif user_permission then
                  local p = v:match("%.permission%.([%w_]+)$")
                  if p and (not user_permission[p]) then
                    list.set(n, "android.permission.UNKNOWN")
                  end
                end
              end
              local pt = activity.getLuaPath(".tmp")
              local fo = FileOutputStream(pt)
              xml.write(list, fo)
              local code = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode
              fo.close()
              local f = io.open(pt)
              local s = f:read("a")
              f:close()
              s = string.gsub(s, touint32(code), touint32(tointeger(appcode) or 1),1)
              s = string.gsub(s, touint32(18), touint32(tointeger(appsdk) or 18),1)

              local f = io.open(pt, "w")
              f:write(s)
              f:close()
              local fi = FileInputStream(pt)
              copy(fi, out)
              os.remove(pt)
             elseif not entry.isDirectory() then
              copy2(zis, out)
            end
          end
          entry = zis.getNextEntry()
        end
        out.setComment(table.concat(md5s))
        --print(table.concat(md5s,"/n"))
        zis.close();
        out.closeEntry()
        out.close()

        if #errbuffer == 0 then
          this.update("正在签名...");
          os.remove(apkpath)
          Signer.sign(tmp, apkpath)
          os.remove(tmp)
          activity.installApk(apkpath)
          --[[import "android.net.*"
        import "android.content.*"
        i = Intent(Intent.ACTION_VIEW);
        i.setDataAndType(activity.getUriForFile(File(apkpath)), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.update("正在打开...");
        activity.startActivityForResult(i, 0);]]
          return "打包成功:" .. apkpath
         else
          os.remove(tmp)
          this.update("打包出错:\n " .. table.concat(errbuffer, "\n"));
          return "打包出错:\n " .. table.concat(errbuffer, "\n")
        end
      end
      luabindir=activity.getLuaExtDir("bin")
      local function bin(path)
        local p = {}
        local e, s = pcall(function()dofile(path .. "init.lua")end)
        if e then
          create_error_dlg2()
          create_bin_dlg()
          bin_dlg.show()
          activity.newTask(binapk, update, callback).execute { path, activity.getLuaExtPath("bin", appname .. "_" .. appver .. ".apk") }
         else
          Toast.makeText(activity, "工程配置文件错误." .. s, Toast.LENGTH_SHORT).show()
        end
      end
      bin("/storage/emulated/0/ThomeLua/project/"..标题.."/")
      dialog1.dismiss()
     elseif p==3 then
      dialog1.dismiss()
      b=io.open(项目文件夹.."/"..标题.."/init.lua"):read("*a")
      if b:find("appcode") then
        版本=b:match('appcode="(.-)"')
       else
        版本="nil"
      end
      if b:find("packagename") then
        包名=b:match('packagename="(.-)"')
       else
        包名="nil"
      end
      if b:find("appver") then
        版本号=b:match('appver="(.-)"')
       else
        版本号="nil"
      end
      if b:find("appsdk") then
        SDK=b:match('appsdk="(.-)"')
       else
        SDK="nil"
      end
      local xq={
        CardView;
        radius=30;
        layout_width="match_parent";
        --orientation="vertical";
        layout_height="match_parent";
        {
          CardView;
          layout_gravity="center";
          layout_height="match_parent";
          layout_width="match_parent";
          backgroundColor=颜色1,
          radius=20;
          {
            TextView;
            text="文件详情";
            layout_marginTop="10dp";
            textSize="20sp";
            layout_marginLeft="25dp";
            textColor="0xFF03A9F4";
          };
          {
            ListView;
            id="文件详情";
            items={
              "";
              "名称："..标题;
              "包名："..包名;
              "版本："..版本;
              "版本号："..版本号;
              "SDK："..SDK;
            };
            layout_height="fill";
            backgroundColor="0xffffff";
            DividerHeight=0;
            layout_width="fill";
          };
        };
      };

      dialog= AlertDialog.Builder(this)
      dialog1=dialog.show()
      dialog1.getWindow().setContentView(loadlayout(xq));
      dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
      local dialogWindow = dialog1.getWindow();
      dialogWindow.setGravity(Gravity.BOTTOM);
      dialog1.getWindow().getAttributes().width=(activity.Width);
      dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
      文件详情.onItemClick=function(l,v,p,i)
        if p==1 then
          写入剪切板(标题)
          print"已复制到剪切板"
         elseif p==2 then
          写入剪切板(包名)
          print"已复制到剪切板"
         elseif p==3 then
          写入剪切板(版本)
          print"已复制到剪切板"
         elseif p==4 then
          写入剪切板(版本号)
          print"已复制到剪切板"
         elseif p==5 then
          写入剪切板(SDK)
          print"已复制到剪切板"
        end

        return true
      end
    end
    return true
  end
  return true


end

local function shortcut(path)
  import "android.content.Intent"
  import "android.net.Uri"
  intent = Intent();
  intent.setClass(activity, activity.getClass());
  intent.setData(Uri.parse("file://"..path))
  addShortcut = Intent("com.android.launcher.action.INSTALL_SHORTCUT");
  icon = Intent.ShortcutIconResource.fromContext(activity,
  R.drawable.icon);
  addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "工程列表");
  addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
  addShortcut.putExtra("duplicate", 0);
  addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
  activity.sendBroadcast(addShortcut);
end
--shortcut(activity.getLuaPath())
local ppls={}
for n=0,#pds-1 do
  local s,i=pcall(getinfo,pds[n])
  switch s
   case true
    i.path="/"..pds[n]
    table.insert(ppls,i)
  end
end
table.sort(ppls,sort)
function update(ppls)
  local adp=LuaAdapter(activity,projectitem)
  for k,v in ipairs(ppls) do
    local i=plugindir..v.path.."/icon.png"
    switch v.appname
     case nil
      appname="nil"
     default
      appname=v.appname
    end
    switch v.packagename
     case nil
      packagename="nil"
     default
      packagename=v.packagename
    end
    switch v.appcode
     case nil
      appcode="nil"
     default
      appcode=v.appcode
    end
    switch v.path
     case nil
      pat="nil"
     default
      pat=v.path
    end
    adp.add{图标=checkicon(i),软件名=appname,包名="包名："..packagename,版本="版本："..appcode,文件夹名称=pat}
  end
  项目列表.Adapter=adp
end


--[[function onCreateOptionsMenu(menu)
  local item=menu.add("搜索")
  item.setShowAsAction(1)
  item.setActionView(edit)
end

edit=EditText()
edit.Hint="输入关键字"
edit.Width=activity.Width/2
edit.SingleLine=true]]

actionbar.setVisibility(0)
--标题栏.setVisibility(8)
actp = actionbar.getLayoutParams()

alphain=AlphaAnimation(0,1)
alphain.setDuration(100)
alphaout=AlphaAnimation(1,0)
alphaout.setDuration(100)
local function dp2px(dpValue)
  local scale = activity.getResources().getDisplayMetrics().scaledDensity
  return dpValue * scale + 0.5
end

actheight=dp2px(90)
local function scaleup(dur)

  finding=true

  if actp.height<dp2px(50)
   else
    actp.height =actp.height-(actheight-dp2px(56))/dur
    actionbar.setLayoutParams(actp)




  end


  --启动Ticker定时器
  --scale.start()

  Translateright=TranslateAnimation(0, -dp2px(30), 0, 0)
  Translateright.setDuration(dur*3)
  Translateright.setFillAfter(false)
  findimag.startAnimation(Translateright)
end

local function scaledown(dur)
  --[[scale=Ticker()
    scale.Period=1
    scale.onTick=function()]]

  if actp.height>actheight
    stop=true

    --scale.stop()
   else
    actp.height =actp.height+(actheight-dp2px(56))/dur
    actionbar.setLayoutParams(actp)

  end


  --end
  --启动Ticker定时器
  --scale.start()
end

findbar.onClick=function()
  scaleup(100)
  task(50,function()
    edit.setVisibility(0)
    inbar.startAnimation(alphain)
    findbar.startAnimation(alphaout)
    findbar.setVisibility(8)
    inbar.setVisibility(0)
    dism.startAnimation(alphain)
    dism.setVisibility(0)
  end)
  edit.addTextChangedListener{
    onTextChanged=function(c)
      local s=tostring(c)
      if #s==0 then
        项目列表.Adapter=adp
        return
      end
      local t={}
      s=s:lower()
      for k,v in ipairs(ppls) do
        if v.appname:lower():find(s,1,true) then
          table.insert(t,v)
        end
      end
      update(t)
    end
  }
end

dism.onClick=function()
  scaledown(100)
  task(50,function()
    inbar.startAnimation(alphaout)
    findbar.startAnimation(alphain)
    findbar.setVisibility(0)
    inbar.setVisibility(8)
    dism.startAnimation(alphaout)
    dism.setVisibility(8)


  end)
end




--[[function jrlt.onClick()
  activity.newActivity("forum/main39")
end]]
sx.setDurations(2000,200)
sx.onRefresh=function(a)
  刷新项目()
  task(1150,function()
    sx.setRefreshing(false);
  end)
end
sx.setColor(0xFF03A9F4)
sx.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
sx.setRefreshing(true);
task(1000,function()
  sx.setRefreshing(false);
  task(155,function()
    刷新项目()
  end)
end)
--刷新项目()
--[[pagev.onPageChange=function(v,i)
  switch i
   case 0
    写入文件(activity.getLuaDir().."/Verify/set1.XY","0")
    --[[社区图片.setColorFilter(0xFF383A3D)
    p_c1.setColorFilter(0xFF383A3D)
    我的图片.setColorFilter(0xFF383A3D)
    项目图片.setColorFilter(0xFF2196F3)
   case 1
    写入文件(activity.getLuaDir().."/Verify/set1.XY","1")
    社区图片.setColorFilter(0xFF2196F3)
    p_c1.setColorFilter(0xFF383A3D)
    项目图片.setColorFilter(0xFF2196F3)
    我的图片.setColorFilter(0xFF383A3D)
  
case 2
    写入文件(activity.getLuaDir().."/Verify/set1.XY","2")
    社区图片.setColorFilter(0xFF383A3D)
    p_c1.setColorFilter(0xFF2196F3)
    项目图片.setColorFilter(0xFF383A3D)
    我的图片.setColorFilter(0xFF383A3D)
case 3
    写入文件(activity.getLuaDir().."/Verify/set1.XY","3")
    社区图片.setColorFilter(0xFF383A3D)
    p_c1.setColorFilter(0xFF383A3D)
    项目图片.setColorFilter(0xFF383A3D)
    我的图片.setColorFilter(0xFF2196F3)
  end

end]]
--[[function 项目.onClick()
  pagev.showPage(0)
end
function 社区.onClick()
  pagev.showPage(1)
end
function p_c1.onClick()
  pagev.showPage(2)
end
function 我的.onClick()
  pagev.showPage(3)
end]]

local function 创建工程()
  local xz={
    CardView;
    radius=30;
    layout_width="match_parent";
    --orientation="vertical";
    layout_height="match_parent";
    {
      CardView;
      layout_gravity="center";
      layout_height="match_parent";
      layout_width="match_parent";
      backgroundColor=颜色1,
      radius=20;
      {
        LinearLayout;
        layout_height="match_parent";
        layout_width="match_parent";
        orientation="vertical";
        {
          TextView;
          text="新建工程";
          textColor="0xFF03A9F4";
          textSize="19sp";
          layout_marginLeft="20dp";
          layout_marginTop="15dp";
        };
        {
          FrameLayout;
          layout_marginTop="5dp",
          layout_width="match_parent";
          layout_height="fill";
          {
            ListView;
            id="创建选择",
            items={
              " 参考模板";
              " 默认工程";
            };
            layout_height="match_parent";
            DividerHeight=0;
            layout_width="match_parent";
          };
        };
      };
    };
  };
  dialog= AlertDialog.Builder(this)
  dialog1=dialog.show()
  dialog1.getWindow().setContentView(loadlayout(xz));
  dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
  local dialogWindow = dialog1.getWindow();
  dialogWindow.setGravity(Gravity.BOTTOM);
  创建选择.onItemClick=function(l,v,p,i)
    if i==1 then
      dialog1.dismiss()
      mb={
        CardView;
        radius=30;
        layout_width="match_parent";
        --orientation="vertical";
        layout_height="match_parent";
        {
          CardView;
          layout_gravity="center";
          layout_height="match_parent";
          layout_width="match_parent";
          backgroundColor=颜色1,
          radius=20;
          {
            LinearLayout;
            layout_height="match_parent";
            layout_width="match_parent";
            orientation="vertical";
            {
              TextView;
              text="参考模板";
              textColor=0xFF03A9F4;
              textSize="18sp";
              layout_marginLeft="20dp";
              layout_marginTop="15dp";
            };
            {
              FrameLayout;
              layout_width="match_parent";
              layout_height="fill";
              {
                ListView;
                id="模板",
                items=
                {
                  " 密码进入软件";
                  " 美化路径模块";
                  " DrawerLayout";
                  " TabBar";
                  " TitleBar",
                };

                layout_height="match_parent";
                DividerHeight=0;
                layout_width="match_parent";
              };
            };
          };
        };
      };
      dialog= AlertDialog.Builder(this)
      dialog1=dialog.show()
      dialog1.getWindow().setContentView(loadlayout(mb));
      dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
      local dialogWindow = dialog1.getWindow();
      dialogWindow.setGravity(Gravity.BOTTOM);
      --模板.TextColor=颜色3
      模板.onItemClick=function(l,v,p,i);
        if i==1 then
          mc="密码进入软件"
          lj="xy1"
         elseif i==2 then
          mc="美化路径模块"
          lj="xy2"
         elseif i==3 then
          mc="DrawerLayout"
          lj="xy3"
         elseif i==4 then
          mc="TabBar"
          lj="xy4"
         elseif i==5 then
          mc="TitleBar"
          lj="xy5"
        end
        ZipUtil.unzip(activity.getLuaDir().."/project/"..lj..".zip",项目文件夹.."/"..mc)
        刷新项目()
        dialog1.dismiss()
        activity.newActivity("main2",android.R.anim.fade_in,android.R.anim.fade_out,{项目文件夹.."/"..mc,true,mc})
      end
     else
      dialog1.dismiss()
      tj=


      {
        CardView;
        radius=30;
        layout_width="match_parent";
        --orientation="vertical";
        backgroundColor=颜色2,
        layout_height="match_parent";
        {
          CardView;
          layout_gravity="center";
          layout_height="300dp";
          layout_width="match_parent";
          backgroundColor=颜色2,
          radius=20;
          {
            TextView;
            layout_marginTop="15dp";
            layout_marginLeft="20dp";
            textSize="20sp";
            textColor="0xFF03A9F4";
            text="新建工程";
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
              hint="请输入工程的名称";
              text="Myapp";
              id="输入名称",
              textColor=颜色4,
              --HintTextColor=颜色4,
            };
          };
          {

            LinearLayout;
            orientation="horizontal";
            layout_width="match_parent";
            layout_height="match_parent";
            gravity="center";
            {
              EditText;
              --layout_marginTop="10dp",
              layout_width="320dp";
              gravity="center";
              textSize="15sp";
              id="输入包名",
              --hintTextColor=颜色4,
              textColor=颜色4,
              hint="请输入工程的包名";
              text="com.Myapp.demo";
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
                text="创建";
              };
            };
          };
        };
      };


      dialog= AlertDialog.Builder(this)
      dialog1=dialog.show()
      dialog1.getWindow().setContentView(loadlayout(tj));
      dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
      local dialogWindow = dialog1.getWindow();
      dialogWindow.setGravity(Gravity.BOTTOM);
      dialog1.getWindow().getAttributes().width=(activity.Width);
      dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
      美化按钮1(取消创建工程,10,0x7E000000,颜色7)
      美化按钮1(确定创建工程,10,0x7a00bfff,0xFF03A9F4)
      控件圆角(输入名称,颜色5,30)
      控件圆角(输入包名,颜色5,30)
      function 确定创建工程.onClick()
        if #输入名称.text~=0 then
          if #输入包名.text~=0 then
            if File(项目文件夹.."/"..输入名称.text).isDirectory() then
              print"工程已存在"
             else
              main=[[
require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "layout"
activity.setTitle("Myapp")
activity.setTheme(android.R.style.Theme_DeviceDefault_Light)
activity.setContentView(loadlayout(layout))
]]
              init=[[
appname="Myapp"
packagename="com.Myapp.demo"
appcode="1"
appver="1.0"
appsdk="15"
debugmode=true
user_permission={
  "INTERNET",
  "WRITE_EXTERNAL_STORAGE",
}
skip_compilation={
}
]]
              layout1=[[
{
LinearLayout;
layout_width="fill";
orientation="vertical";
layout_height="fill";
};
]]
              创建文件夹(项目文件夹.."/"..输入名称.text)
              main1=string.gsub(main,"Myapp",输入名称.text)
              写入文件(项目文件夹.."/"..输入名称.text.."/main.lua",main1)
              init1=string.gsub(init,'appname="Myapp"','appname="'..输入名称.text..'"')
              init2=string.gsub(init1,"com.Myapp.demo",输入包名.text)
              写入文件(项目文件夹.."/"..输入名称.text.."/init.lua",init2)
              写入文件(项目文件夹.."/"..输入名称.text.."/layout.aly",layout1)
              sx.setRefreshing(true);
              task(10,function()
                sx.setRefreshing(false);
                刷新项目()
                if File(项目文件夹.."/"..输入名称.text).isDirectory() then
                  print"创建成功"
                  activity.newActivity("main2",android.R.anim.fade_in,android.R.anim.fade_out,{项目文件夹.."/"..输入名称.text,true,输入名称.text})
                  dialog1.dismiss()
                 else print"创建失败"
                end
              end)
            end
           else
            print"请输入工程包名"
          end
         else
          print"请输入工程名称"
        end
      end
      function 取消创建工程.onClick()
        dialog1.dismiss()
      end
    end
  end
end
--[[function 添加.onClick()
  a=io.open(activity.getLuaDir().."/Verify/set1.XY"):read("*a")
  if a=="0" then
    创建工程()
   else
    if a=="1" then

    end
  end
end]]
lastclick = os.time() - 2
function onKeyDown(e)
  local now = os.time()
  if e == 4 then
    if now - lastclick > 2 then
      --print("再按一次退出程序")
      Toast.makeText(activity, "再按一次退出程序.", Toast.LENGTH_SHORT ).show()
      lastclick = now
      return true
    end
  end
end
function 其他.onClick()
  pop=PopupMenu(activity,其他)
  menu=pop.Menu
  menu.add("创建工程").onMenuItemClick=function(a)
    创建工程()
  end
  menu.add("导入工程").onMenuItemClick=function(a)
    activity.newActivity("file",android.R.anim.fade_in,android.R.anim.fade_out,{"选择源码(*.alp)",Environment.getExternalStorageDirectory().toString(),{".alp"}})
    function onResult(name,arg)
      if name=="file" then
        sx.setRefreshing(true);
        task(10,function()
          sx.setRefreshing(false);
          刷新项目()
          print("导入成功")
        end)
       else

      end

    end
  end
  menu.add("中文手册").onMenuItemClick=function(a)
    activity.newActivity("main3",android.R.anim.fade_in,android.R.anim.fade_out)
  end
  menu.add("编辑器设置").onMenuItemClick=function(a)
    activity.newActivity("main13",android.R.anim.fade_in,android.R.anim.fade_out)
  end
  pop.show()
end
--[[项目列表.onItemClick=function(l,v,p,i)
  activity.newActivity("main2",android.R.anim.fade_in,android.R.anim.fade_out,{项目文件夹.."/"..v.tag.软件名.text,true,v.tag.软件名.text,"/storage/emulated/0/ThomeLua+/project/"..v.tag.软件名.text})
  return true
end]]

import "android.view.animation.Animation$AnimationListener"
import "android.view.animation.ScaleAnimation"
import "android.view.animation.ScaleAnimation"
function CircleButton (InsideColor,radiu,...)
  import "android.graphics.drawable.GradientDrawable"
  drawable = GradientDrawable()
  drawable.setShape(GradientDrawable.RECTANGLE)
  drawable.setColor(InsideColor)
  drawable.setCornerRadii({radiu,radiu,radiu,radiu,radiu,radiu,radiu,radiu});
  for k,v in ipairs({...}) do
    v.setBackgroundDrawable(drawable)
  end
end
CircleButton(0xff20d0d0,100,bt,bt1,bt2)
bt.onClick=function(v)
  if bt1.getVisibility()==0 then
    bt2.startAnimation(ScaleAnimation(1.0, 0.0, 1.0, 0.0,1, 0.5, 1, 0.5).setDuration(100))
    bt2.setVisibility(View.INVISIBLE)
    bt1.startAnimation(ScaleAnimation(1.0, 0.0, 1.0, 0.0,1, 0.5, 1, 0.5).setDuration(200))
    bt1.setVisibility(View.INVISIBLE)
    bt.text="+"

   else
    bt1.setVisibility(View.VISIBLE)
    bt2.setVisibility(View.VISIBLE)
    bt1.startAnimation(ScaleAnimation(0.0, 1.0, 0.0, 1.0,1, 0.5, 1, 0.5).setDuration(100))
    bt2.startAnimation(ScaleAnimation(0.0, 1.0, 0.0, 1.0,1, 0.5, 1, 0.5).setDuration(200))
    bt.text="-"
    bt1.Text="导入"
    bt2.Text="新建"
  end
end
bt1.onClick=function(v)
  bt1.startAnimation(ScaleAnimation(1.0, 0.0, 1.0, 0.0,1, 0.5, 1, 0.5).setDuration(100))
  bt1.setVisibility(View.INVISIBLE)
  bt2.startAnimation(ScaleAnimation(1.0, 0.0, 1.0, 0.0,1, 0.5, 1, 0.5).setDuration(200))
  bt2.setVisibility(View.INVISIBLE)
  bt.text="+"
  activity.newActivity("file",android.R.anim.fade_in,android.R.anim.fade_out,{"选择源码(*.alp)",Environment.getExternalStorageDirectory().toString(),{".alp"}})
  function onResult(name,arg)
    if name=="file" then
      sx.setRefreshing(true);
      task(10,function()
        sx.setRefreshing(false);
        刷新项目()
        print("导入成功")
      end)
     else
    end
  end
end
bt2.onClick=function(v)
  bt1.startAnimation(ScaleAnimation(1.0, 0.0, 1.0, 0.0,1, 0.5, 1, 0.5).setDuration(100))
  bt1.setVisibility(View.INVISIBLE)
  bt2.startAnimation(ScaleAnimation(1.0, 0.0, 1.0, 0.0,1, 0.5, 1, 0.5).setDuration(200))
  bt2.setVisibility(View.INVISIBLE)
  bt.text="+"
  创建工程()
end


