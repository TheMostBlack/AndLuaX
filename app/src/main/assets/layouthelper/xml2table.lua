require "import"
import "console"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "android.content.*"
import "com.androlua.*"
import "loadlayout3"
function getFilesDir()
  return "/data/user/0/"..activity.getPackageName().."/files/"
end
function autotheme()
local a=io.open(getFilesDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  return (android.R.style.Theme_DeviceDefault)
 else
  return (android.R.style.Theme_DeviceDefault_Light)
end
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
--activity.setTitle('XML转换器')
--activity.setTheme(android.R.style.Theme_Holo_Light)
cm=activity.getSystemService(Context.CLIPBOARD_SERVICE)
t={
  LinearLayout,
  id="l",
  orientation="vertical" ,
  --backgroundColor="#eeeeff",
  {
    LuaEditor,
    id="edit",
    --hint= "XML布局代码转换AndroLua布局表",
    layout_width="fill",
    layout_height="fill",
    layout_weight=1,
    --gravity="top"
  },
  {
    LinearLayout,
    layout_width="fill",
    backgroundColor=颜色1,
    {
      Button,
      id="open",
      text="转换",
      layout_width="fill",
      layout_weight=1,
      onClick ="click",
    } ,
    {
      Button,
      id="open",
      text="预览",
      layout_width="fill",
      layout_weight=1,
      onClick ="click2",
    } ,
    {
      Button,
      id="open",
      text="复制",
      layout_width="fill",
      layout_weight=1,
      onClick ="click3",
    } ,
    {
      Button,
      id="open",
      text="确定",
      layout_width="fill",
      layout_weight=1,
      onClick ="click4",
    } ,
  }
}

function xml2table(xml)
  local xml,s=xml:gsub("</%w+>","}")
  if s==0 then
    return xml
    end
  xml=xml:gsub("<%?[^<>]+%?>","")
  xml=xml:gsub("xmlns:android=%b\"\"","")
  xml=xml:gsub("%w+:","")
  xml=xml:gsub("\"([^\"]+)\"",function(s)return (string.format("\"%s\"",s:match("([^/]+)$")))end)
  xml=xml:gsub("[\t ]+","")
  xml=xml:gsub("\n+","\n")
  xml=xml:gsub("^\n",""):gsub("\n$","")
  xml=xml:gsub("<","{"):gsub("/>","}"):gsub(">",""):gsub("\n",",\n")
  return (xml)
end

dlg=Dialog(activity,autotheme())
dlg.setTitle("布局表预览")
function show(s)
  dlg.setContentView(loadlayout3(loadstring("return "..s)(),{}))
  dlg.show()
end

function click()
  local str=edit.getText().toString()
  str=xml2table(str)
  str=console.format(str)
  edit.setText(str)
end

function click2()
  local str=edit.getText().toString()
  show(str)
end


function click3(s)
  local cd = ClipData.newPlainText("label", edit.getText().toString())
  cm.setPrimaryClip(cd)
  Toast.makeText(activity,"已复制的剪切板",1000).show()
end

function click4()
  local str=edit.getText().toString()
  layout.main=loadstring("return "..str)()
  activity.setContentView(loadlayout2(layout.main,{}))
  dlg2.hide()

end


loadlayout(t)
dlg2=Dialog(activity,autotheme())
dlg2.setTitle("编辑代码")
dlg2.getWindow().setSoftInputMode(0x10)

dlg2.setContentView(l)



function editlayout(txt)
  edit.Text=txt
  edit.format()
  dlg2.show()
  local ab=io.open(getFilesDir().."/Verify/set5.XY"):read("*a")
local abc=io.open(getFilesDir().."/Verify/set6.XY"):read("*a")
local abcd=io.open(getFilesDir().."/Verify/set7.XY"):read("*a")
local abcde=io.open(getFilesDir().."/Verify/set8.XY"):read("*a")
local abcdef=io.open(getFilesDir().."/Verify/set9.XY"):read("*a")
local abcdefg=io.open(getFilesDir().."/Verify/set10.XY"):read("*a")
local abcdefgh=io.open(getFilesDir().."/Verify/set11.XY"):read("*a")
local abcdefghi=io.open(getFilesDir().."/Verify/set12.XY"):read("*a")
switch abc
 case "颜色1"
  abc=颜色1
  default
  abc=tonumber(abc)
end
switch abcdef
 case "颜色3"
  abcdef=颜色3
  default
  abcdef=tonumber(abcdef)
end
edit.setBasewordColor(tonumber(ab))--基词
edit.setPanelBackgroundColor(tonumber(abc))--卡片颜色
edit.setPanelTextColor(tonumber(abcd))--卡片字体颜色
edit.setStringColor(tonumber(abcde))--字符串颜色
edit.setTextColor(tonumber(abcdef))--文本颜色
edit.setUserwordColor(tonumber(abcdefg))--数字
edit.setCommentColor(tonumber(abcdefgh))--注释颜色
edit.setKeywordColor(tonumber(abcdefghi))--if then等
end

function onResume2()
  local cd=cm.getPrimaryClip();
  local msg=cd.getItemAt(0).getText()--.toString();
  edit.setText(msg)
end
