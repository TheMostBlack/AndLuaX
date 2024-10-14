require "import"
import "android.text.Html"
import "android.graphics.Paint"
import "android.app.*"
import "android.os.*"
import "java.io.*"
import "android.widget.*"
import "android.view.*"
import "android.content.*"
import "com.androlua.*"

import "android.view.WindowManager"
import "android.view.inputmethod.InputMethodManager"
layout={
  main={
    RelativeLayout,
    layout_width = "fill";
    layout_height = "fill";
  },

  ck={
    LinearLayout;
    {
      RadioGroup;
      layout_weight="1";
      id="ck_rg";
    };
    {
      Button;
      Text="确定";
      layout_gravity="right";
      id="ck_bt";
    };
    orientation="vertical";
  };
}

luapath,luadir=...
luadir=luadir or luapath:gsub("/[^/]+$","")
package.path=package.path..";"..luadir.."/?.lua;"

imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE)

import "loadlayout2"
require "xml2table"
import "autotheme"
activity.setTheme(autotheme())
if luapath:find("%.aly$") then
  local f=io.open(luapath)
  local s=f:read("*a")
  f:close()
  xpcall(function()
    layout.main=assert(loadstring("return "..s))()
  end,
  function()
    Toast.makeText(activity,"不支持编辑该布局",1000).show()
    activity.finish()
  end)
  showsave=true
end

if luapath:find("%.lua$") and content then
  xpcall(function()
    layout.main=assert(loadstring("return "..content))()
  end,
  function()
    Toast.makeText(activity,"加载布局失败布局",1000).show()
    activity.finish()
  end)
  showsave=true
end

--判断是否在点击该控件
is_clicking = false

--点击事件信息


function click_info_init()
  click_info = {
    --起始坐标
    start = {
      x = 0;y = 0;
    };
    start_time = 0;
    now_click = nil;
    mode = 1;
    now_view = nil;
  }
end

--保持View属性窗口开启
holding_view = false

local function clicking_background(c,p)
  p.color =
  ({0x554A148C,0x55004D40,0x55BF360C,0x55FFFF00})
  [click_info.mode]

  if click_info.now_click then
    c.drawRect(0,0,c.width,c.height,p)
    if click_info.mode == 1 and (System.currentTimeMillis() - click_info.start_time) < 750 then
      p.color = 0x4437474F
      c.drawRect(0,0,(c.width/750)*(System.currentTimeMillis() - click_info.start_time),c.height,p)
      if click_info.now_view then
        click_info.now_view.invalidate()
      end
    elseif click_info.mode == 1 then
      p.color = 0x550288D1
      c.drawRect(0,0,c.width,c.height,p)
    end
    p.style = Paint.Style.STROKE
    p.setStrokeWidth(20)
    p.setStrokeJoin(Paint.Join.ROUND)
    p.color =
    ({0x994A148C,0x99004D40,0x99BF360C,0x99FFFF00})
    [click_info.mode]
    c.drawRect(0,0,c.width,c.height,p)

    p.setTextSize(40)
    p.color =
    ({0xFF4A148C,0xFF004D40,0xFFBF360C,0xFFF57F17})
    [click_info.mode]
    p.setStyle(Paint.Style.FILL)
    c.drawText(click_info.now_click.class.getSimpleName(),20,50,p)

    p.setTextSize(20)

    c.drawText(
    ({(((System.currentTimeMillis() - click_info.start_time) > 750) and "固定属性面板") or "打开属性面板","选择子控件/父控件","添加/ID","删除"})
    [click_info.mode],20,70,p)
  end
end

clicking_background_drawable = LuaDrawable(clicking_background)

function onTouch(v,e)
  if is_clicking and e.getAction() == MotionEvent.ACTION_UP then
    getCurr(v)
    fd_dlg.hide()
    v.foreground = nil
    is_clicking = false
    if (System.currentTimeMillis() - click_info.start_time) > 750 then
      holding_view = true
    end
    if click_info.mode == 2 then
      if luajava.instanceof(v,ViewGroup) then
        func["子控件"](v)
       else
        func["父控件"](v)
      end
      return

     elseif click_info.mode == 3 then
      if luajava.instanceof(v,ViewGroup) then
        func["添加"](v)
       else
        func["id"](v)
      end
      return
     elseif click_info.mode == 4 then
      func["删除"](v)
      return
    end

    getCurr(v)
   elseif (not is_clicking) and e.getAction() == MotionEvent.ACTION_DOWN then
    holding_view = false
    v.foreground = clicking_background_drawable
    click_info_init()
    click_info.now_view = v
    click_info.start = {
      x = e.rawX;
      y = e.rawY;
    }
    click_info.start_time = System.currentTimeMillis()
    click_info.now_click = v
    is_clicking = true
    return true
   elseif is_clicking and e.getAction() == MotionEvent.ACTION_MOVE then

    local offset = activity.getWidth()/3
    --点击的偏移

    if
      math.abs(e.rawX - click_info.start.x) > offset or
      math.abs(e.rawY - click_info.start.y) > offset then

      is_clicking = false
      v.foreground = nil
     else
      if e.rawY - click_info.start.y > offset*0.3 then
        click_info.mode = 3
        v.invalidate()
       elseif e.rawY - click_info.start.y < -(offset*0.3) then
        click_info.mode = 2
        v.invalidate()
       elseif e.rawX - click_info.start.x < -(offset*0.3) then
        click_info.mode = 4
        v.invalidate()
       else
        if click_info.mode ~= 1 then
          click_info.start_time = System.currentTimeMillis()
        end
        click_info.mode = 1
        v.invalidate()
      end
    end
    return true
  end

end

local TypedValue=luajava.bindClass("android.util.TypedValue")
local dm=activity.getResources().getDisplayMetrics()
function dp(n)
  return TypedValue.applyDimension(1,n,dm)
end

function to(n)
  return string.format("%ddp",n//dn)
end

dn=dp(1)
lastX=0
lastY=0
vx=0
vy=0
vw=0
vh=0
zoomX=false
zoomY=false
function move(v,e)
  curr=v.Tag
  currView=v
  ry=e.getRawY()--获取触摸绝对Y位置
  rx=e.getRawX()--获取触摸绝对X位置
  if e.getAction() == MotionEvent.ACTION_DOWN then
    lp=v.getLayoutParams()
    vy=v.getY()--获取视图的Y位置
    vx=v.getX()--获取视图的X位置
    lastY=ry--记录按下的Y位置
    lastX=rx--记录按下的X位置
    vw=v.getWidth()--记录控件宽度
    vh=v.getHeight()--记录控件高度
    if vw-e.getX()<20 then
      zoomX=true--如果触摸右边缘启动缩放宽度模式
     elseif vh-e.getY()<20 then
      zoomY=true--如果触摸下边缘启动缩放高度模式
    end

   elseif e.getAction() == MotionEvent.ACTION_MOVE then
    --lp.gravity=Gravity.LEFT|Gravity.TOP --调整控件至左上角
    if zoomX then
      lp.width=(vw+(rx-lastX))--调整控件宽度
     elseif zoomY then
      lp.height=(vh+(ry-lastY))--调整控件高度
     else
      lp.x=(vx+(rx-lastX))--移动的相对位置
      lp.y=(vy+(ry-lastY))--移动的相对位置
    end
    v.setLayoutParams(lp)--调整控件到指定的位置
    --v.Parent.invalidate()
   elseif e.getAction() == MotionEvent.ACTION_UP then
    if (rx-lastX)^2<100 and (ry-lastY)^2<100 then
      getCurr(v)
     else
      curr.layout_x=to(v.getX())
      curr.layout_y=to(v.getY())
      if zoomX then
        curr.layout_width=to(v.getWidth())
       elseif zoomY then
        curr.layout_height=to(v.getHeight())
      end
    end
    zoomX=false--初始化状态
    zoomY=false--初始化状态
  end
  return true
end

function getCurr(v)
  curr=v.Tag
  currView=v
  fd_dlg.setView(View(activity))
  fd_dlg.Title=tostring(v.Class.getSimpleName())
  if luajava.instanceof(v,GridLayout) then
    fd_dlg.setItems(fds_grid)
   elseif luajava.instanceof(v,LinearLayout) then
    fd_dlg.setItems(fds_linear)
   elseif luajava.instanceof(v,CardView) then
    fd_dlg.setItems(fds_card)
   elseif luajava.instanceof(v,ViewGroup) then
    fd_dlg.setItems(fds_group)
   elseif luajava.instanceof(v,EditText) then
    fd_dlg.setItems(fds_edit)
   elseif luajava.instanceof(v,TextView) then
    fd_dlg.setItems(fds_text)
   elseif luajava.instanceof(v,ImageView) then
    fd_dlg.setItems(fds_image)
   else
    fd_dlg.setItems(fds_view)
  end



  if luajava.instanceof(v.Parent,LinearLayout) then
    fd_list.getAdapter().add("layout_weight")
   elseif luajava.instanceof(v.Parent,AbsoluteLayout) then
    fd_list.getAdapter().insert(5,"layout_x")
    fd_list.getAdapter().insert(6,"layout_y")
   elseif luajava.instanceof(v.Parent,RelativeLayout) then
    local adp=fd_list.getAdapter()
    for k,v in ipairs(relative) do
      adp.add(v)
    end
  end
  local adapter = fd_list.adapter
  local put_position = 4
  local unknow_fd = table.clone(curr)


  --移除数字索引
  for k,v in pairs(unknow_fd) do
    if tonumber(k) then
      unknow_fd[k] = 0
    end
  end

  for i = 0,adapter.count-1 do
    local key = adapter.getItem(i)

    if key == "id" then
      put_position = i
    end

    if curr[key] then
      adapter.remove(i)
      adapter.insert(put_position,Html.fromHtml("<b>"..key.."</b>:<i><font color=\"#546E7A\">"..tostring(curr[key]).."</font></i>"))

      unknow_fd[key] = nil
    end
  end

  for k,v in pairs(unknow_fd) do
    if not tonumber(k) then
      adapter.insert(put_position,Html.fromHtml("<b><font color=\"#1A237E\">"..k.."</font></b>:<i><font color=\"#546E7A\">"..tostring(v).."</font></i>"))
    end
  end

  fd_dlg.show()
end

function adapter(t)
  local ls=ArrayList()
  for k,v in ipairs(t) do
    ls.add(v)
  end
  return ArrayAdapter(activity,android.R.layout.simple_list_item_1, ls)
end

import "android.graphics.drawable.*"


curr=nil
activity.setTitle('布局助手')
activity.setTheme(autotheme())
--activity.Theme=android.R.style.Theme_Material_Light
xpcall(function()
  activity.setContentView(loadlayout2(layout.main,{}))
end,
function()
  Toast.makeText(activity,"不支持编辑该布局\n(请检查布局文件是否出错)",1000).show()
  activity.finish()
end)

relative={
  "layout_above","layout_alignBaseline","layout_alignBottom","layout_alignEnd","layout_alignLeft","layout_alignParentBottom","layout_alignParentEnd","layout_alignParentLeft","layout_alignParentRight","layout_alignParentStart","layout_alignParentTop","layout_alignRight","layout_alignStart","layout_alignTop","layout_alignWithParentIfMissing","layout_below","layout_centerHorizontal","layout_centerInParent","layout_centerVertical","layout_toEndOf","layout_toLeftOf","layout_toRightOf","layout_toStartOf"
}

--属性列表对话框
fd_dlg=AlertDialogBuilder(activity)
fd_list=fd_dlg.getListView()
fds_grid={
  "添加","删除","父控件","子控件",
  "id","orientation",
  "columnCount","rowCount",
  "layout_width","layout_height","layout_gravity",
  "background","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_linear={
  "添加","删除","父控件","子控件",
  "id","orientation","layout_width","layout_height","layout_gravity",
  "background","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_group={
  "添加","删除","父控件","子控件",
  "id","layout_width","layout_height","layout_gravity",
  "background","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_edit={
  "删除","父控件",
  "id","layout_width","layout_height","layout_gravity",
  "background","text","hint","textColorHint","inputType","digits","maxEms","textColor","textSize","singleLine","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_text={
  "删除","父控件",
  "id","layout_width","layout_height","layout_gravity",
  "background","text","textColor","textSize","textStyle","singleLine","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_image={
  "删除","父控件",
  "id","layout_width","layout_height","layout_gravity",
  "background","src","adjustViewBounds","scaleType","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_view={
  "删除","父控件",
  "id","layout_width","layout_height","layout_gravity",
  "background","gravity",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

fds_card={
  "添加","删除","父控件","子控件",
  "id","layout_width","layout_height","layout_gravity",
  "background","gravity","radius","elevation","backgroundColor",
  "layout_margin","layout_marginLeft","layout_marginTop","layout_marginRight","layout_marginBottom",
  "padding","paddingLeft","paddingTop","paddingRight","paddingBottom",
}

--全部View操作
local view_actions = {
  fds_grid;fds_linear;fds_card;fds_group;
  fds_edit;fds_text;fds_image;fds_view;
}

for k,v in ipairs(view_actions) do
  table.insert(v,3,"复制")
  table.insert(v,4,"编辑")
end

--属性选择列表
checks={}
checks.textStyle = {"normal","bold","italic"}
checks.inputType = {"none","phone","text","datetime","date","textAutoComplete","textAutoCorrect","textCapCharacters","textCapSentences","textCapWords","textEmailAddress","textEmailSubject","textFilter","textImeMultiLine","textLongMessage","textMultiLine","textNoSuggestions","textPassword","textPersonName","textPhonetic","textPostalAddress","textShortMessage","textUri","textVisiblePassword","textWebEditText","textWebEmailAddress","textWebPassword","numberPassword","numberSigned","number","numberDecimal"}
checks.adjustViewBounds={"true","false","none"}
checks.layout_width={"fill","wrap","other"}
checks.layout_height={"fill","wrap","other"}
checks.singleLine={"true","false"}
checks.orientation={"vertical","horizontal"}
checks.gravity={"left","top","right","bottom","start","center","end"}
checks.layout_gravity={"left","top","right","bottom","start","center","end"}
checks.scaleType={
  "matrix",
  "fitXY",
  "fitStart",
  "fitCenter",
  "fitEnd",
  "center",
  "centerCrop",
  "centerInside"}


function addDir(out,dir,f)
  local ls=f.listFiles()
  for n=0,#ls-1 do
    local name=ls[n].getName()
    if ls[n].isDirectory() then
      addDir(out,dir..name.."/",ls[n])
     elseif name:find("%.j?pn?g$") then
      table.insert(out,dir..name)
    end
  end
end

function checkid()
  local cs={}
  local parent=currView.Parent.Tag
  for k,v in ipairs(parent) do
    if v==curr then
      break
    end
    if type(v)=="table" and v.id then
      table.insert(cs,v.id)
    end
  end
  return cs
end

rbs={"layout_alignParentBottom","layout_alignParentEnd","layout_alignParentLeft","layout_alignParentRight","layout_alignParentStart","layout_alignParentTop","layout_centerHorizontal","layout_centerInParent","layout_centerVertical"}
ris={"layout_above","layout_alignBaseline","layout_alignBottom","layout_alignEnd","layout_alignLeft","layout_alignRight","layout_alignStart","layout_alignTop","layout_alignWithParentIfMissing","layout_below","layout_toEndOf","layout_toLeftOf","layout_toRightOf","layout_toStartOf"}
for k,v in ipairs(rbs) do
  checks[v]={"true","false","none"}
end

for k,v in ipairs(ris) do
  checks[v]=checkid
end

if luadir then
  checks.src=function()
    local src={}
    addDir(src,"",File(luadir))
    return src
  end
end

fd_list.onItemClick=function(l,v,p,i)

  fd_dlg.hide()

  local fd=tostring(v.Text)
  if string.find(fd,":") then
    fd = fd:gsub("%:.*","")
  end
  if checks[fd] then
    if type(checks[fd])=="table" then
      check_dlg.Title=fd
      check_dlg.setItems(checks[fd])
      check_dlg.show()
     else
      check_dlg.Title=fd
      check_dlg.setItems(checks[fd](fd))
      check_dlg.show()
    end
   else
    func[fd]()
  end
end

--子视图列表对话框
cd_dlg=AlertDialogBuilder(activity)
cd_list=cd_dlg.getListView()
cd_list.onItemClick=function(l,v,p,i)
  getCurr(chids[p])
  cd_dlg.hide()
end

--可选属性对话框
check_dlg=AlertDialogBuilder(activity)
check_list=check_dlg.getListView()
check_list.onItemClick=function(l,v,p,i)

  local v=tostring(v.Text)
  local fld=check_dlg.Title
  if v == "other" then
    check_dlg.hide()
    func[fld]()
    return
  end
  if #v==0 or v=="none" then
    v=nil
  end
  local fld=check_dlg.Title
  local old=curr[tostring(fld)]
  curr[tostring(fld)]=v
  check_dlg.hide()
  local s,l=pcall(loadlayout2,layout.main,{})
  if s then
    activity.setContentView(l)
   else
    curr[tostring(fld)]=old
    print(l)
  end

  if holding_view then
    getCurr(currView)
  end
end

func={}
setmetatable(func,{__index=function(t,k)
    return function()
      sfd_dlg.Title=k--tostring(currView.Class.getSimpleName())
      --sfd_dlg.Message=k
      fld.Text= (curr[k] and tostring(curr[k])) or ""
      fld.selectAll()
      sfd_dlg.show()
      fld.requestFocus()
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS);
    end
  end
})
func["添加"]=function()
  add_dlg.Title=tostring(currView.Class.getSimpleName())
  for n=0,#ns-1 do
    if n~=i then
      el.collapseGroup(n)
    end
  end
  add_dlg.show()
end

func["删除"]=function()
  local gp=currView.Parent.Tag
  if gp==nil then
    Toast.makeText(activity,"不可以删除顶部控件",1000).show()
    return
  end
  for k,v in ipairs(gp) do
    if v==curr then
      table.remove(gp,k)
      break
    end
  end
  activity.setContentView(loadlayout2(layout.main,{}))
end

func["复制"]=function()
  local clone = table.clone(curr)
  getCurr(currView.Parent)
  table.insert(curr,clone)

  local s,l=pcall(loadlayout2,layout.main,{})
  if s then
    activity.setContentView(l)
   else
    curr[tostring(fld)]=old
    print(l)
  end

  fd_dlg.hide()
end

func["编辑"]=function()
  local _layout ={
    LinearLayout;
    orientation="vertical";
    layout_height="fill";
    layout_width="fill";
    {
      LuaEditor;
      id="editor";
      layout_weight="1.0";
      layout_height="wrap";
      layout_width="fill";
    };
    {
      LinearLayout;
      layout_gravity="center";
      layout_width="fill";
      {
        Button;
        layout_weight="1";
        text="取消";
        id="cancel";
      };
      {
        Button;
        layout_weight="1";
        text="确定";
        id="ok";
      };
    };
  };
  local _ids = {}
  local dialog = Dialog()
  dialog.setContentView(loadlayout(_layout,_ids))
  dialog.setTitle("编辑控件布局代码")
  dialog.cancelable = true
  dialog.show()
  _ids.editor.text = dumplayout2(curr)
  Handler().postDelayed(function()
    _ids.editor.format()
  end,100)
  _ids.cancel.onClick = function()
    dialog.cancel()
  end

  _ids.ok.onClick = function()
    local code = _ids.editor.text
    e,i = pcall(loadstring("return "..code))
    if e then
      for k,v in pairs(i) do
        curr[k] = v
      end
      dialog.cancel()
     else
      print("代码出错，请检查拼写！\n"..i)
    end
    local s,l=pcall(loadlayout2,layout.main,{})
    if s then
      activity.setContentView(l)
     else
      curr[tostring(fld)]=old
      print(l)
    end
  end
end

func["父控件"]=function()
  local p=currView.Parent
  if p.Tag==nil then
    Toast.makeText(activity,"已是顶部控件",1000).show()
   else
    getCurr(p)
  end
end

chids={}
func["子控件"]=function()
  chids={}
  local arr={}
  for n=0,currView.ChildCount-1 do
    local chid=currView.getChildAt(n)
    chids[n]=chid
    table.insert(arr,chid.Class.getSimpleName())
  end
  cd_dlg.Title=tostring(currView.Class.getSimpleName())
  cd_dlg.setItems(arr)
  cd_dlg.show()
end

--添加视图对话框
add_dlg=Dialog(activity)
add_dlg.Title="添加"
wdt_list=ListView(activity)

ns={
  "小部件","检查视图","适配器视图","高级控件","布局","高级布局","附加控件"
}


wdt={
  {"Button -按钮控件","EditText -编辑框控件","TextView -文本控件",
    "ImageButton -图片按钮控件","ImageView -图片控件","CircleImageView -圆形图片控件","SearchView -搜索框"},
  {"CheckBox -复选框","RadioButton -单选框","ToggleButton -按钮开关控件","Switch -开关控件"},
  {"ListView -列表视图","GridView -网格视图","PageView -滑动视图","ExpandableListView -折叠列表","Spinner -下拉框"},
  {"SeekBar -拖动条","ProgressBar -进度条","RatingBar -评分栏",
    "DatePicker -日期选择器","TimePicker -时间选择器","NumberPicker -数字选择器","Chronometer -计时器"},
  {"LinearLayout -线性布局","AbsoluteLayout -绝对布局","FrameLayout -帧布局","RelativeLayout -相对布局","TableLayout -表布局","RippleLayout -水波纹布局"},
  {"CardView -卡片控件","RadioGroup -单选视图","GridLayout -网格布局",
    "ScrollView -纵向滚动布局","HorizontalScrollView -横向滚动布局"},
  {"LuaEditor -Lua代码编辑框","LuaWebView -Lua浏览器控件","PullingLayout -下拉刷新"}
}

wds={
  {"Button","EditText","TextView",
    "ImageButton","ImageView","CircleImageView","SearchView"},
  {"CheckBox","RadioButton","ToggleButton","Switch"},
  {"ListView","GridView","PageView","ExpandableListView","Spinner"},
  {"SeekBar","ProgressBar","RatingBar",
    "DatePicker","TimePicker","NumberPicker","Chronometer"},
  {"LinearLayout","AbsoluteLayout","FrameLayout","RelativeLayout","TableLayout","RippleLayout"},
  {"CardView","RadioGroup","GridLayout",
    "ScrollView","HorizontalScrollView"},
  {"LuaEditor","LuaWebView","PullingLayout"}
}


mAdapter=ArrayExpandableListAdapter(activity)
for k,v in ipairs(ns) do
  mAdapter.add(v,wdt[k])
end

el=ExpandableListView(activity)
el.setAdapter(mAdapter)
add_dlg.setContentView(el)

el.onChildClick=function(l,v,g,c)
  local w={_G[wds[g+1][c+1]]}
  table.insert(curr,w)
  local s,l=pcall(loadlayout2,layout.main,{})
  if s then
    activity.setContentView(l)
   else
    table.remove(curr)
    print(l)
  end
  add_dlg.hide()
end



function ok()
  imm.hideSoftInputFromWindow(fld.getWindowToken(), 0)
  local v=tostring(fld.Text)
  if #v==0 then
    v=nil
  end
  local fld=sfd_dlg.Title
  local old=curr[tostring(fld)]
  curr[tostring(fld)]=v
  --sfd_dlg.hide()
  local s,l=pcall(loadlayout2,layout.main,{})
  if s then
    activity.setContentView(l)


    if holding_view then
      getCurr(currView)
    end
   else
    curr[tostring(fld)]=old
    print(l)
  end

end

function none()
  local old=curr[tostring(sfd_dlg.Title)]
  curr[tostring(sfd_dlg.Title)]=nil
  --sfd_dlg.hide()
  local s,l=pcall(loadlayout2,layout.main,{})
  if s then
    activity.setContentView(l)
   else
    curr[tostring(sfd_dlg.Title)]=old
    print(l)
  end
  imm.hideSoftInputFromWindow(fld.getWindowToken(), 0)

  if holding_view then
    getCurr(currView)
  end
end


--输入属性对话框
sfd_dlg=AlertDialogBuilder(activity)
fld=EditText(activity)
fld.setFocusable(true);
fld.setFocusableInTouchMode(true);
sfd_dlg.setView(fld)
sfd_dlg.setPositiveButton("确定",{onClick=ok})
sfd_dlg.setNegativeButton("取消",{onClick=function()
    --隐藏键盘
    imm.hideSoftInputFromWindow(fld.getWindowToken(), 0)
    if holding_view then
      getCurr(currView)
    end
end})
sfd_dlg.setNeutralButton("无",{onClick=none})
function dumparray(arr)
  local ret={}
  table.insert(ret,"{\n")
  for k,v in ipairs(arr) do
    table.insert(ret,string.format("\"%s\";\n",v))
  end
  table.insert(ret,"};\n")
  return table.concat(ret)
end
function dumplayout(t)
  table.insert(ret,"{\n")
  table.insert(ret,tostring(t[1].getSimpleName()..";\n"))
  for k,v in pairs(t) do
    if type(k)=="number" then
      --do nothing
     elseif type(v)=="table" then
      table.insert(ret,k.."="..dumparray(v))
     elseif type(v)=="string" then
      if v:find("[\"\'\r\n]") then
        table.insert(ret,string.format("%s=[==[%s]==];\n",k,v))
       else
        table.insert(ret,string.format("%s=\"%s\";\n",k,v))
      end
     else
      table.insert(ret,string.format("%s=%s;\n",k,tostring(v)))
    end
  end
  for k,v in ipairs(t) do
    if type(v)=="table" then
      dumplayout(v)
    end
  end
  table.insert(ret,"};\n")
end

function dumplayout2(t)
  ret={}
  dumplayout(t)
  return table.concat(ret)
end

function onCreateOptionsMenu(menu)
  menu.add("复制")
  menu.add("编辑")
  menu.add("预览")
  if showsave then
    menu.add("保存")
  end
end

function save(s)
  local f=io.open(luapath,"w")
  f:write(s)
  f:close()
end

import "android.content.*"
cm=activity.getSystemService(activity.CLIPBOARD_SERVICE)

function onMenuItemSelected(id,item)
  local t=item.getTitle()
  if t=="复制" then
    local cd = ClipData.newPlainText("label",dumplayout2(layout.main))
    cm.setPrimaryClip(cd)
    Toast.makeText(activity,"已复制到剪切板",1000).show()
   elseif t=="编辑" then
    editlayout(dumplayout2(layout.main))
   elseif t=="预览" then
    show(dumplayout2(layout.main))
   elseif t=="保存" then
    if luapath:find("%.lua$") then
      activity.result({dumplayout2(layout.main)})
      return
    end
    save(dumplayout2(layout.main))
    Toast.makeText(activity,"已保存",1000).show()
    activity.setResult(10000,Intent());
    activity.finish()
  end
end

function onStart()
  activity.setContentView(loadlayout2(layout.main,{}))
end

lastclick=os.time()-2
function onKeyDown(e)
  local now=os.time()
  if e==4 then
    if now-lastclick>2 then
      Toast.makeText(activity, "再按一次返回.", Toast.LENGTH_SHORT ).show()
      lastclick=now
      return true
    end
  end
end


