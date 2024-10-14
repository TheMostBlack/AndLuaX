import "android.net.Uri"
import "android.content.Intent"
import "com.kn.okhtttp.*"
import "okhttp3.*"
import "java.io.*"
import "cjson"
import "android.support.v4.widget.*"
import "com.bm.library.PhotoView"
function print(内容) toasts={ CardView; CardElevation="0"; radius=10; backgroundColor="0xff575757"; { TextView; layout_marginLeft="5%w", layout_marginRight="5%w", textSize="17sp"; layout_marginTop="1.5%h", layout_marginBottom="1.5%h", TextColor="0xffffffff", text=内容; layout_gravity="center"; }; }; local toast=Toast.makeText(activity,nil,Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, 0).show() toast.setView(loadlayout(toasts)) toast.show() end
function 边(id,Size,Color,Color1,radiu)
  local drawable = GradientDrawable()
  drawable.setStroke(Size,Color)
  drawable.setColor(Color1)
  drawable.setCornerRadii({radiu,radiu,radiu,radiu,radiu,radiu,radiu,radiu});
  id.setBackgroundDrawable(drawable)
end
function 圆(Color,radius)
  drawable = GradientDrawable()
  drawable.setShape(GradientDrawable.RECTANGLE)
  drawable.setColor(Color)
  drawable.setCornerRadii({radius,radius,0,0,0,0,0,0});
  return drawable
end
function 边1(Size,Color,Color1,radiu)
  local drawable = GradientDrawable()
  drawable.setStroke(Size,Color)
  drawable.setColor(Color1)
  drawable.setCornerRadii({radiu,radiu,radiu,radiu,radiu,radiu,radiu,radiu});
  return drawable
end
function 水珠动画(view,time)
  import "android.animation.ObjectAnimator"
  ObjectAnimator().ofFloat(view,"scaleX",{1.2,.8,1.1,.9,1}).setDuration(time).start()
  ObjectAnimator().ofFloat(view,"scaleY",{1.2,.8,1.1,.9,1}).setDuration(time).start()
end
function 上移动画(id)
  translate=TranslateAnimation(0,0, 500, 0)
  translate.setDuration(300);
  translate.setRepeatCount(0);
  translate.setFillAfter(true)
  id.startAnimation(translate)
  水珠动画(id,1000)
end
function 下移动画(id)
  translate=TranslateAnimation(0,0, 0, 500)
  translate.setDuration(300);
  translate.setRepeatCount(0);
  translate.setFillAfter(true)
  id.startAnimation(translate)
end
import "android.graphics.PorterDuffColorFilter"
import "android.graphics.PorterDuff"
import "java.io.File"
import "android.graphics.Typeface"
function MD提示(str,color,color2,ele,rad)
  if time then toasttime=Toast.LENGTH_SHORT else toasttime= Toast.LENGTH_SHORT end
  toasts={
    CardView;
    id="toastb",
    CardElevation=ele;
    radius=rad;
    backgroundColor=color;
    {
      TextView;
      layout_margin="7dp";
      textSize="13sp";
      TextColor=color2,
      text=str;
      layout_gravity="center";
      id="mess",
    };
  };
  local toast=Toast.makeText(activity,nil,toasttime);
  toast.setView(loadlayout(toasts))
  toast.show()
end
import "android.graphics.drawable.GradientDrawable"
import "android.graphics.drawable.ColorDrawable"
import "AndLua"
if not File("/sdcard/Android/data/com.yidian.forum").isDirectory() then
  创建文件夹("/sdcard/Android/data/com.yidian.forum")
end
if not File("/sdcard/Android/data/com.yidian.forum/files").isDirectory() then
  创建文件夹("/sdcard/Android/data/com.yidian.forum/files")
end
if not File("/sdcard/Android/data/com.yidian.forum/cache").isDirectory() then
  创建文件夹("/sdcard/Android/data/com.yidian.forum/cache")
end

import "bmob"
id="720a1216bfaae6b6b37fe4da44a06749"
key="f7b61518946e572eddced0049e70c5c0"
bmoba=bmob(id,key)

id1="2e26b6c1ff69fe889959119acdedacbf"
key1="d487cbdcde77b01df4e6483348097c28"
bmobb=bmob(id1,key1)

id2="36c6e9d6e893664935234d9f267d6327"
key2="ccc183d1afe07286253ed084cda368c8"
bmobc=bmob(id2,key2)

key3="09fc1f888ccd457c013aba36391c992d"
id3="06334ff176ae71bc7cfff9586453f86e"
bmobc=bmob(id3,key3)

function 窗口标题(text)
  activity.setTitle(text)
end
function 普通弹窗(a,b,c,d,e,f)
  local tc={
    LinearLayout;
    layout_height="fill";
    layout_width="fill";
    orientation="vertical";
    gravity="center";
    {
      CardView;
      CardElevation="0dp";
      layout_width="85%w";
      radius="10dp";
      backgroundColor="0xffffffff";
      {
        LinearLayout;
        layout_width="match_parent";
        orientation="vertical";
        layout_height="match_parent";
        {
          LinearLayout;
          layout_width="match_parent";
          gravity="center";
          layout_height="9%h";
          {
            TextView;
            textSize="17sp";
            Typeface=Typeface.createFromFile(File(activity.getLuaDir().."/font/a.ttf"));
            textColor="0xff3333333";
            text=a;
          };
        };
        {
          LinearLayout;
          layout_marginBottom="3%h";
          layout_width="match_parent";
          gravity="center";
          {
            TextView;
            layout_height="match_parent";
            layout_width="75%w";
            textColor="0xff363636";
            text=b;
            textSize="15sp";
            Typeface=Typeface.createFromFile(File(activity.getLuaDir().."/font/b.ttf"));
          };
        };
        {
          LinearLayout;
          backgroundColor="0xfff7f7f7";
          layout_width="match_parent";
          orientation="horizontal";
          layout_height="7%h";
          {
            LinearLayout;
            id="t_a";
            onClick=e;
            layout_width="38%w";
            gravity="center";
            layout_height="match_parent";
            {
              TextView;
              textSize="16sp";
              textColor="0xff49dadb";
              text=c;
            };
          };
          {
            TextView;
            backgroundColor="0xffadadad";
            layout_gravity="center";
            layout_marginRight="1%w";
            layout_marginLeft="1%w";
            layout_width="0.1%w";
            layout_height="4%h";
          };
          {
            LinearLayout;
            onClick=f;
            id="t_b";
            layout_width="match_parent";
            gravity="center";
            layout_height="match_parent";
            {
              TextView;
              textSize="15sp";
              textColor="0xff49dadb";
              text=d;
              Typeface=Typeface.createFromFile(File(activity.getLuaDir().."/font/a.ttf"));
            };
          };
        };
      };
    };
  };
  dialog=AlertDialog.Builder(activity)
  .setView(loadlayout(tc))
  dialog1=dialog.show()
  dialog1.setCancelable(false)
  dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
  波纹(t_b,0xffd1d1d1)
  波纹(t_a,0xffd1d1d1)
end
function 控件旋转(id,时长)
  import "android.view.animation.LinearInterpolator"
  import "android.animation.ValueAnimator"
  import "android.animation.ObjectAnimator"
  动画 = ObjectAnimator()
  动画.setTarget(id);
  动画.setDuration(时长);
  动画.setRepeatCount(ValueAnimator.INFINITE)
  动画.setPropertyName("rotation");
  动画.setFloatValues({0,720});
  动画.setRepeatMode(ValueAnimator.INFINITE)
  动画.setInterpolator(LinearInterpolator() )
  动画.start();
end
function 光标颜色(id,颜色)
  local v = Build.VERSION.RELEASE
  local v1=utf8.sub(v,1,1)
  if tonumber(v1) > 8 then
   else
    import "android.graphics.drawable.Drawable"
    import "android.graphics.PorterDuff"
    local fCursorDrawableRes=TextView.getDeclaredField("mCursorDrawableRes").setAccessible(true);
    local mCursorDrawableRes=fCursorDrawableRes.getInt(id);
    local editor=TextView.getDeclaredField("mEditor").setAccessible(true).get(id);
    local fCursorDrawable=editor.getClass().getDeclaredField("mCursorDrawable").setAccessible(true);
    local drawables=Drawable[1];
    drawables[0]=id.getContext().getResources().getDrawable(mCursorDrawableRes);
    drawables[0].setColorFilter(颜色, PorterDuff.Mode.SRC_IN);
    fCursorDrawable.set(editor,drawables);
  end
end
function 渐变色背景(id,color1,color2)
  import "android.view.Window"
  import "android.view.WindowManager"
  import "android.graphics.Color"
  import "android.graphics.drawable.GradientDrawable"
  local color = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,{color1,color2});
  id.setBackground(color)
end
function Http_upload(ur,name,f,zhacai)
  client = OkTest.newok()
  f=File(f)
  requestBody = MultipartBody.Builder()
  .setType(MultipartBody.FORM)
  .addFormDataPart(name,tostring(f.Name),RequestBody.create(MediaType.parse("multipart/form-data"), f))
  .build()

  request = Request.Builder()
  .header("User-Agent","Dalvik/2.1.0 (Linux; U; Android 5.1.1; Nexus 4 Build/LMY48T)")
  .url(ur)
  .post(requestBody)
  .build();

  client.newCall(request).enqueue(Callback{
    onFailure=function( call, e)
      zhacai(String(e.body().bytes()).toString())
    end,
    onResponse=function(call, response)
      code=response.code()
      header=response.headers()
      data=String(response.body().bytes()).toString()
      zhacai(code,data,header)
    end
  });
end

function 加载弹窗(text)
  local jz={
    LinearLayout;
    layout_height="fill";
    gravity="center";
    layout_width="fill";
    orientation="vertical";
    {
      CardView;
      layout_height="25%w";
      layout_width="25%w";
      backgroundColor="0xffffffff";
      CardElevation="0dp";
      radius="15dp";
      {
        LinearLayout;
        layout_height="match_parent";
        orientation="vertical";
        layout_width="match_parent";
        gravity="center";
        {
          ProgressBar;
          id="jz_tp";
          layout_height="8%w";
          layout_width="10%w";
        };
        {
          TextView;
          textSize="14sp";
          text=text,
          Typeface=Typeface.createFromFile(File(activity.getLuaDir().."/font/a.ttf"));
          layout_marginTop="2%w";
        };
      };
    };
  };
  dialog=AlertDialog.Builder(activity)
  .setView(loadlayout(jz))
  dialog1=dialog.show()
  dialog1.setCancelable(false)
  dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
  jz_tp.IndeterminateDrawable.setColorFilter(PorterDuffColorFilter(0xff49dadb,PorterDuff.Mode.SRC_ATOP))
end

function 载入界面(id)
  activity.setContentView(loadlayout(id))
end

function 隐藏标题栏()
  activity.ActionBar.hide()
end

function 设置主题(id)
  activity.setTheme(id)
end

function 打印(text)
  print(text)
end

function 窗口全屏()
  activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
end

function 取消全屏()
  activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
end

function 返回桌面()
  activity.moveTaskToBack(true)
end

function 提示(text)
  Toast.makeText(activity,text,Toast.LENGTH_SHORT).show()
end

function 截取文本(str,str1,str2)
  str1=str1:gsub("%p",function(s) return("%"..s) end)
  return str:match(str1 .. "(.-)"..str2)
end

function 替换文本(str,str1,str2)
  str1=str1:gsub("%p",function(s) return("%"..s) end)
  str2=str2:gsub("%%","%%%%")
  return str:gsub(str1,str2)
end

function 字符串长度(str)
  return utf8.len(str)
end

function 状态栏颜色(color)
  if Build.VERSION.SDK_INT >= 21 then
    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).setStatusBarColor(color);
  end
end

function 沉浸状态栏()
  if Build.VERSION.SDK_INT >= 19 then
    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
  end
end

function 设置文本(id,text)
  id.Text=text
end

function 跳转页面(name)
  activity.newActivity(name)
end

function 跳转界面(name)
  activity.newActivity(name)
end

function 关闭页面()
  activity.finish()
end

function 关闭界面()
  activity.finish()
end

function 获取文本(id)
  return id.Text
end

function 结束程序()
  activity.finish()
end

function 重构页面()
  activity.recreate()
end

function 重构界面()
  activity.recreate()
end

function 控件圆角(view,InsideColor,radiu)
  import "android.graphics.drawable.GradientDrawable"
  drawable = GradientDrawable()
  drawable.setShape(GradientDrawable.RECTANGLE)
  drawable.setColor(InsideColor)
  drawable.setCornerRadii({radiu,radiu,radiu,radiu,radiu,radiu,radiu,radiu});
  view.setBackgroundDrawable(drawable)
end

function 获取设备标识码()
  import "android.provider.Settings$Secure"
  return Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID)
end

function 获取IMEI()
  import "android.content.Context"
  return activity.getSystemService(Context.TELEPHONY_SERVICE).getDeviceId()
end

function 控件背景渐变动画(view,color1,color2,color3,color4)
  import "android.animation.ObjectAnimator"
  import "android.animation.ArgbEvaluator"
  import "android.animation.ValueAnimator"
  import "android.graphics.Color"
  colorAnim = ObjectAnimator.ofInt(view,"backgroundColor",{color1, color2, color3,color4})
  colorAnim.setDuration(3000)
  colorAnim.setEvaluator(ArgbEvaluator())
  colorAnim.setRepeatCount(ValueAnimator.INFINITE)
  colorAnim.setRepeatMode(ValueAnimator.REVERSE)
  colorAnim.start()
end

function 获取屏幕尺寸(ctx)
  import "android.util.DisplayMetrics"
  dm = DisplayMetrics();
  ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
  diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
  return diagonalPixels / (160 * dm.density);
end

function 是否安装APP(packageName)
  if pcall(function() activity.getPackageManager().getPackageInfo(packageName,0) end) then
    return true
   else
    return false
  end
end

function 设置中划线(id)
  import "android.graphics.Paint"
  id.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG)
end

function 设置下划线(id)
  import "android.graphics.Paint"
  id.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG)
end

function 设置字体加粗(id)
  import "android.graphics.Paint"
  id.getPaint().setFakeBoldText(true)
end

function 设置斜体(id)
  import "android.graphics.Paint"
  id.getPaint().setTextSkewX(0.2)
end

function 分享内容(text)
  intent=Intent(Intent.ACTION_SEND);
  intent.setType("text/plain");
  intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
  intent.putExtra(Intent.EXTRA_TEXT, text);
  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  activity.startActivity(Intent.createChooser(intent,"分享到:"));
end

function 加QQ群(qq)
  import "android.net.Uri"
  import "android.content.Intent"
  activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin="..qq.."&card_type=group&source=qrcode")))
end

function 跳转QQ群(qq)
  import "android.net.Uri"
  import "android.content.Intent"
  activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin="..qq.."&card_type=group&source=qrcode")))
end

function QQ聊天(qq)
  import "android.net.Uri"
  import "android.content.Intent"
  activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="..qq)))
end

function 跳转QQ聊天(qq)
  import "android.net.Uri"
  import "android.content.Intent"
  activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="..qq)))
end

function 发送短信(phone,text)
  require "import"
  import "android.telephony.*"
  SmsManager.getDefault().sendTextMessage(tostring(phone), nil, tostring(text), nil, nil)
end

function 获取剪切板()
  import "android.content.Context"
  return activity.getSystemService(Context.CLIPBOARD_SERVICE).getText()
end

function 写入剪切板(text)
  import "android.content.Context"
  activity.getSystemService(Context.CLIPBOARD_SERVICE).setText(text)
end

function 开启WIFI()
  import "android.content.Context"
  wifi = activity.Context.getSystemService(Context.WIFI_SERVICE)
  wifi.setWifiEnabled(true)
end

function 关闭WIFI()
  import "android.content.Context"
  wifi = activity.Context.getSystemService(Context.WIFI_SERVICE)
  wifi.setWifiEnabled(false)
end

function 断开网络()
  import "android.content.Context"
  wifi = activity.Context.getSystemService(Context.WIFI_SERVICE)
  wifi.disconnect()
end

function 创建文件(file)
  import "java.io.File"
  return File(file).createNewFile()
end

function 创建文件夹(file)
  import "java.io.File"
  return File(file).mkdir()
end

function 创建多级文件夹(file)
  import "java.io.File"
  return File(file).mkdirs()
end

function 移动文件(file,file2)
  import "java.io.File"
  return File(file).renameTo(File(file2))
end

function 写入文件(file,text)
  return io.open(file,"w"):write(text):close()
end

function 设置按钮颜色(id,color)
  id.getBackground().setColorFilter(PorterDuffColorFilter(color,PorterDuff.Mode.SRC_ATOP))
end

function 设置编辑框颜色(id,color)
  id.getBackground().setColorFilter(PorterDuffColorFilter(color,PorterDuff.Mode.SRC_ATOP));
end

function 设置进度条颜色(id,color)
  id.IndeterminateDrawable.setColorFilter(PorterDuffColorFilter(color,PorterDuff.Mode.SRC_ATOP))
end

function 设置控件颜色(id,color)
  id.setBackgroundColor(color)
end

function 获取手机存储路径()
  return Environment.getExternalStorageDirectory().toString()
end

function 获取屏幕宽()
  return activity.getWidth()
end

function 获取屏幕高()
  return activity.getHeight()
end

function 文件是否存在(file)
  return File(file).exists()
end

function 关闭左侧滑(id)
  id.closeDrawer(3)
end

function 打开左侧滑()
  id.openDrawer(3)
end

function 显示控件(id)
  id.setVisibility(0)
end

function 隐藏控件(id)
  id.setVisibility(8)
end

function 打开APP(packageName)
  import "android.content.Intent"
  import "android.content.pm.PackageManager"
  manager = activity.getPackageManager()
  open = manager.getLaunchIntentForPackage(packageName)
  this.startActivity(open)
end

function 卸载APP(file)
  import "android.net.Uri"
  import "android.content.Intent"
  uri = Uri.parse("package:"..file)
  intent = Intent(Intent.ACTION_DELETE,uri)
  activity.startActivity(intent)
end

function 安装APP(file)
  import "android.content.Intent"
  import "android.net.Uri"
  intent = Intent(Intent.ACTION_VIEW)
  intent.setDataAndType(Uri.parse("file://"..file), "application/vnd.android.package-archive")
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  activity.startActivity(intent)
end

function 系统下载文件(url,directory,name)
  import "android.content.Context"
  import "android.net.Uri"
  downloadManager=activity.getSystemService(Context.DOWNLOAD_SERVICE);
  url=Uri.parse(url);
  request=DownloadManager.Request(url);
  request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
  request.setDestinationInExternalPublicDir(directory,name);
  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
  downloadManager.enqueue(request);
end

function 波纹(id,color)
  import "android.content.res.ColorStateList"
  local attrsArray = {android.R.attr.selectableItemBackgroundBorderless}
  local typedArray =activity.obtainStyledAttributes(attrsArray)
  ripple=typedArray.getResourceId(0,0)
  aoos=activity.Resources.getDrawable(ripple)
  aoos.setColor(ColorStateList(int[0].class{int{}},int{color}))
  id.setBackground(aoos.setColor(ColorStateList(int[0].class{int{}},int{color})))
end


function 随机数(min,max)
  return math.random(min,max)
end

function 删除控件(id,id2)
  return (id).removeView(id2)
end

function 状态栏亮色()
  if Build.VERSION.SDK_INT >= 23 then
    activity.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
  end
end

import"android.view.animation.*"
import"android.view.animation.Animation$AnimationListener"
import"android.graphics.drawable.GradientDrawable"
import "android.widget.LinearLayout"
import "android.view.View"
import "android.view.animation.TranslateAnimation"
import "android.widget.CardView"
import "android.view.WindowManager"
import "android.graphics.drawable.GradientDrawable"
import "android.view.Gravity"
import "android.widget.PageView"
import "android.view.animation.Animation$AnimationListener"
import "android.app.AlertDialog"
import "android.widget.PageLayout$OnPageChangeListener"
import "android.widget.PageView$OnPageChangeListener"
import "android.R$id"
import "android.graphics.drawable.ColorDrawable"
import "com.androlua.R$drawable"
import "android.R$drawable"
import "android.view.animation.Animation$AnimationListener"
import "android.view.animation.AlphaAnimation"
import "android.widget.AbsoluteLayout"
MyDialog={}
MyDialog.设置布局=function(v) MyDialog.layout=v or MyDialog.layout return MyDialog end

MyDialog.弹窗高度="fill"
MyDialog.弹窗圆角="5dp"
MyDialog.弹窗外部颜色="#00000000"
MyDialog.弹窗阴影="0dp"
MyDialog.弹窗背景="#FFFFFFFF"
MyDialog.弹窗宽度="100%w"
MyDialog.弹窗上边距="0dp"
MyDialog.弹窗下边距="0dp"
MyDialog.设置弹窗下边距=function(v) MyDialog.弹窗下边距=v or MyDialog.弹窗下边距 return MyDialog end
MyDialog.设置弹窗外部点击事件=function(v) MyDialog.弹窗外部点击事件=v or MyDialog.弹窗外部点击事件 return MyDialog end
MyDialog.设置弹窗上边距=function(v) MyDialog.弹窗上边距=v or MyDialog.弹窗上边距 return MyDialog end
MyDialog.设置弹窗宽度=function(v) MyDialog.弹窗宽度= v or MyDialog.弹窗宽度 return MyDialog end
MyDialog.设置弹窗背景=function(v) MyDialog.弹窗背景=v or MyDialog.弹窗背景 return MyDialog end
MyDialog.设置弹窗阴影=function(v) MyDialog.弹窗阴影=v or MyDialog.弹窗阴影 return MyDialog end
MyDialog.设置弹窗外部颜色=function(v) MyDialog.弹窗外部颜色=v or MyDialog.弹窗外部颜色 return MyDialog end
MyDialog.设置弹窗圆角=function(v) MyDialog.弹窗圆角=v or MyDialog.弹窗圆角 return MyDialog end
MyDialog.设置弹窗高度=function(v) MyDialog.弹窗高度=v or MyDialog.弹窗高度 return MyDialog end



MyDialog.弹窗获取高宽 = function(A0_100)
  A0_100.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
  MyDialog.弹窗高= A0_100.getMeasuredHeight()
  MyDialog.弹窗宽= A0_100.getMeasuredWidth()
  return MyDialog.弹窗高, MyDialog.弹窗宽
end

MyDialog.layout={}
MyDialog.弹窗高=50
MyDialog.弹窗外部点击事件=function()MyDialog.弹窗关闭()end
MyDialog.弹窗快速消失动画 = AlphaAnimation(1, 0.2).setDuration(0).setFillAfter(true)
MyDialog.弹窗消失动画= AlphaAnimation(1, 0).setDuration(250).setFillAfter(true)
MyDialog.弹窗显示动画= AlphaAnimation(0, 1).setDuration(250).setFillAfter(true)
MyDialog.弹窗快速消失动画 = AlphaAnimation(1, 0).setDuration(0).setFillAfter(true)
MyDialog.弹窗上移动画 = TranslateAnimation(0, 0, 2000, 0).setDuration(300).setFillAfter(true)
MyDialog.弹窗下移动画 = TranslateAnimation(0, 0, 0, 2000).setDuration(300).setFillAfter(true)
MyDialog.弹窗设置圆角 = function(控件,颜色,上圆角, 下圆角)
  drawable = GradientDrawable()
  drawable.setShape(GradientDrawable.RECTANGLE)
  drawable.setColor(颜色)
  drawable.setCornerRadii({
    上圆角,
    上圆角,
    上圆角,
    上圆角,
    下圆角,
    下圆角,
    下圆角,
    下圆角
  })
  控件.setBackgroundDrawable(rawable)
  return MyDialog
end

MyDialog.弹窗状态改变事件=function()end
MyDialog.弹窗位置改变事件 = function(A0_115)
  if A0_115 == 1 then
    MyDialog.弹窗关闭()
  end
end

MyDialog.弹窗滑动事件=function()end
MyDialog.弹窗关闭=function()
  _弹窗区域.startAnimation(MyDialog.弹窗下移动画)
  _弹窗阴影区域.startAnimation(MyDialog.弹窗消失动画)
  MyDialog.弹窗下移动画.setAnimationListener(AnimationListener({
    onAnimationEnd = function()
      _弹窗主布局.startAnimation(MyDialog.弹窗快速消失动画)
      _弹窗主布局.setVisibility(View.GONE)
      MyDialog.dialog.dismiss()
    end
  }))
end
MyDialog.显示=function()
  import "android.graphics.Typeface"
  import "android.graphics.drawable.ColorDrawable"
  local aly2= {
    AbsoluteLayout,
    id = "_弹窗主布局",
    layout_height = "100%h",
    layout_width = "100%w",
    {
      LinearLayout,
      layout_height = "100%h",
      layout_width = "100%w",
      {
        LinearLayout,
        id = "_弹窗圆角布局",
        layout_width = "0dp",
        background = MyDialog.弹窗背景,
        {
          LinearLayout,
          layout_height = MyDialog.弹窗圆角,
        }
      },

      {
        LinearLayout,
        id = "_弹窗阴影区域",
        background = MyDialog.弹窗外部颜色,
        layout_height = "100%h",
        layout_width = "100%w",
        {
          LinearLayout,
          layout_height = "100%h",
          layout_width = "100%w",
          layout_gravity = "bottom",

          {
            LinearLayout,
            rotation = "270",
            layout_width = "100%h",
            layout_height = "fill_parent",
            gravity = "bottom",
            layout_gravity = "bottom",

            {
              PageView,
              background = "#00000000",
              id = "_弹窗页面布局",

              OnPageChangeListener=(PageView.OnPageChangeListener({
                onPageSelected = function(A0_105)
                  MyDialog.弹窗位置改变事件(A0_105)
                end,
                onPageScrolled = function(A0_106, A1_107, A2_108)
                end,
                onPageScrollStateChanged = function(A0_109)
                  MyDialog.弹窗状态改变事件(A0_109)
                end
              })),
              pages = {
                {
                  LinearLayout,
                  layout_height = "100%h",
                  layout_width = "100%w",

                  rotation = "90",
                  background = "#00000000",
                  {
                    LinearLayout,
                    id = "_弹窗父布局",
                    layout_height = "100%h",
                    layout_width = "100%w",
                    gravity = "bottom|center",
                    onClick=function()MyDialog.弹窗外部点击事件()end,
                    {
                      CardView,
                      id = "_弹窗区域",
                      elevation = MyDialog.弹窗阴影,
                      layout_gravity = "bottom|center",
                      layout_marginTop = MyDialog.弹窗上边距,
                      layout_marginBottom = MyDialog.弹窗下边距,
                      layout_width = MyDialog.弹窗宽度,
                      Radius=MyDialog.弹窗圆角,
                      onClick=function()end,
                      {
                        LinearLayout,
                        id = "_弹窗区域布局",
                        layout_width = MyDialog.弹窗宽度,
                        layout_height = MyDialog.弹窗高度,
                        gravity = "top|center",
                        onClick=function()end,
                        MyDialog.layout,
                      }
                    }
                  }
                },
                {
                  LinearLayout,
                  layout_height = "100%h",
                  layout_width = "100%w",

                }
              }
            }
          }
        }
      }
    }
  }
  dialog= AlertDialog.Builder(this)
  dialog1=dialog.show()
  MyDialog.dialog=dialog1
  dialog1.getWindow().setContentView(loadlayout(aly2));

  dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));

  local dialogWindow = dialog1.getWindow();

  dialogWindow.setGravity(Gravity.BOTTOM);
  dialog1.setCanceledOnTouchOutside(false);
  dialog1.getWindow().getAttributes().width=(activity.Width);
  dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);



  _弹窗页面布局.addOnPageChangeListener(PageView.OnPageChangeListener({
    onPageSelected = function(A0_105)
      MyDialog.弹窗位置改变事件(A0_105)
    end,
    onPageScrolled = function(A0_106, A1_107, A2_108)
    end,
    onPageScrollStateChanged = function(A0_109)
      MyDialog.弹窗状态改变事件(A0_109)
    end
  }))
  _弹窗主布局.setVisibility(0)
  return MyDialog
end


function MyBottomSheetDialog() return MyDialog end
