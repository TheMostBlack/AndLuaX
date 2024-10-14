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


