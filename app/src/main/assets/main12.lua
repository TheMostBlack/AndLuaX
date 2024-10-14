require "import"
import "android.app.*"
import "android.os.*"
import "android.widget.*"
import "android.view.*"
import "AndLua"
import "other"
import "Dialog"
import "toast"
import "main6"
compile "libs/android-support-v4"
import "android.support.v4.widget.*"
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


layout12={
  LinearLayout;
  layout_height="fill";
  layout_width="fill";
  backgroundColor=颜色2,
  orientation="vertical";
  {
    SwipeRefreshLayout;
    id="sxdb",
    {
      RelativeLayout;
      layout_height="fill";
      layout_width="fill";
      {
        LinearLayout;
        layout_height="56dp";
        backgroundColor=颜色1;
        layout_width="fill";
        {
          ImageView;
          onClick=function()
            activity.finish()
          end;
          layout_gravity="center";
          src="res/ThomeLua8.png";
          ColorFilter="0xFF03A9F4";
          layout_width="25dp";
          id="dbq";
          layout_marginLeft="20dp";
        };
        {
          TextView;
          layout_gravity="center";
          text="打包管理";
          textSize="18sp";
          layout_marginLeft="30dp";
          textColor="0xFF03A9F4";
        };
      };
      {

        LinearLayout;
        gravity="center";
        layout_height="88.2%h";
        orientation="vertical";
        layout_alignParentBottom="true";
        layout_width="fill";
        {
          ListView;
          layout_gravity="end";
          layout_width="fill";
          layout_height="fill";
          id="打包列表";
          DividerHeight=0;
          layout_marginTop="5dp";
        };
      };
    };
  },
}
activity.setContentView(loadlayout(layout12))
--隐藏标题栏()
activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(颜色1);
if tonumber(Build.VERSION.SDK) >= 23 then
  activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
end

local item12={
  LinearLayout;
  gravity="center";
  layout_width="fill";
  layout_height="fill";
  {
    LinearLayout;
    orientation="vertical";
    gravity="center";
    layout_width="fill";
    layout_height="70dp";
    {
      CardView;
      radius=20;
      backgroundColor=颜色1;
      CardElevation=0;
      layout_width="340dp";
      layout_height="60dp";
      {
        LinearLayout;
        layout_width="match_parent";
        layout_height="match_parent";
        {
          LinearLayout;
          gravity="center";
          layout_width="8%h";
          layout_height="match_parent";
          {
            ImageView;
            src="res/bft.png";
            layout_width="35dp";
            layout_height="35dp";
          };
        };
        {
          LinearLayout;
          orientation="vertical";
          layout_width="match_parent";
          layout_height="match_parent";
          {
            TextView;
            textColor=颜色3;
            layout_marginTop="10dp";
            id="打包文件名";
            text="文件名";
          };
          {
            TextView;
            textColor=颜色3,
            text="时间";
            id="打包时间";
            layout_marginTop="5dp";
          };
        };
      };
    };
  };
};
function GetFilelastTime(path)
  f = File(path);
  cal = Calendar.getInstance();
  time = f.lastModified()
  cal.setTimeInMillis(time);
  return cal.getTime().toLocaleString()
end
local function 刷新打包()
  sdata={}
  asdp=LuaAdapter(activity,sdata,item12)
  打包列表.Adapter=asdp
  local a=luajava.astable(File(打包文件夹).listFiles())
  if a[1]==nil then
   else
    for i=1,#a do
      if File(打包文件夹.."/"..a[i].name).isFile() then
        asdp.add{打包文件名=a[i].name,打包时间=GetFilelastTime(打包文件夹.."/"..a[i].name)}
      end
    end
  end
end
刷新打包()
sxdb.setColorSchemeColors({0xFF03A9F4});
sxdb.setProgressBackgroundColorSchemeColor(颜色1);
sxdb.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener{onRefresh=function()
    刷新打包()
    sxdb.setRefreshing(false);
  end})
波纹(dbq,0xFFD9D9D9)
function 打包列表.onItemClick(l,v,p,i)
  activity.installApk(打包文件夹.."/"..v.tag.打包文件名.text)
end
function 打包列表.onItemLongClick(l,v,p,i)
  local bt=v.tag.打包文件名.text
  local sc={
    LinearLayout;
    layout_width="fill";
    gravity="center";
    layout_height="fill";
    {
      LinearLayout;
      backgroundColor=颜色1,
      orientation="vertical";
      layout_width="match_parent";
      layout_height="160dp";
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
        textColor=颜色3,
        layout_marginLeft="25dp";
        text="您确定要删除此文件吗？删除后将无法恢复！";
        layout_marginTop="10dp";
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
          id="取消删除打包";
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
            text="删除";
            layout_height="40dp";
            id="确定删除打包";
          };
        };
      };
    };
  };

  Dialog1=MyBottomSheetDialog()
  .设置布局(sc)
  .设置弹窗背景(颜色4)
  .设置弹窗圆角("10dp")
  .显示()
  美化按钮(取消删除打包,10,0x7E000000,0x7E000000)
  美化按钮(确定删除打包,10,0x7a00bfff,0x7a00bfff)
  function 确定删除打包.onClick()
    dialog1.dismiss()
    os.remove(打包文件夹.."/"..bt)
    刷新打包()
    print"删除成功"
  end
  function 取消删除打包.onClick()
    dialog1.dismiss()
  end
  return true
end