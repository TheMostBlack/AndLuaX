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
