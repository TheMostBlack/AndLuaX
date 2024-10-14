import "android.app.AlertDialog"
import "android.view.Gravity"
import "android.view.WindowManager"
import "java.lang.Integer"
import "android.graphics.drawable.GradientDrawable"
import "android.graphics.drawable.RippleDrawable"
import "android.content.res.ColorStateList"
import "android.graphics.drawable.StateListDrawable"
import "ThomeLua"
local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xffffffff
  颜色6=0xffffffff
  颜色5=0xff212121
  颜色7=0xEBFFFFFF
  颜色8=0xFF000000
  activity.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
 else
  颜色1=0xFFFFFFFF
  颜色3=0xff303030
  颜色4=0xff757575
  颜色2=0xFFF2F1F6
  颜色6=0xff757575
  颜色5=0x5FFFFFFF
  颜色7=0xff303030
  颜色8=0xFF000000
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)
end
function yss1(A0_687,A1_688,按钮,位数)
  import("android.graphics.PorterDuff")
  import("android.graphics.PorterDuffColorFilter")
  function CircleButton(A0_689, A1_690)
    import("android.graphics.drawable.GradientDrawable")
    drawable = GradientDrawable()
    drawable.setShape(GradientDrawable.RECTANGLE)
    drawable.setColor(A1_690)
    drawable.setCornerRadii({
      360,
      360,
      360,
      360,
      360,
      360,
      360,
      360
    })
    A0_689.setBackgroundDrawable(drawable)
  end

  import "android.graphics.drawable.ColorDrawable"
  import "android.widget.*";

  yuxuan =

  {
    LinearLayout,
    orientation = "vertical",
    layout_height = "fill",
    layout_width = "fill",
    gravity = "center",
    backgroundColor=颜色1;
    id="圆不溜秋",
    {
      TextView;
      layout_gravity="center";
      layout_marginTop = "10dp",
      text="调色板";
      textColor=颜色7;
      textSize="20sp";
    };
    {
      CardView,
      layout_height = "100dp",
      layout_width = "100dp",
      layout_marginTop = "20dp",
      id = "mmp5",
      --backgroundColor=颜色1;
    },

    {
      TextView,
      text = "",
      id = "mmp4",
      layout_marginLeft = "10dp",
      layout_marginRight = "10dp",
      layout_width = "match_parent",
      layout_height = "50dp",
      gravity = "center",
      textColor=颜色7,
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "A",
        layout_width = "10%w",
        layout_height = "50dp",
        gravity = "center",
        textColor=颜色7,
      },
      {
        SeekBar,
        id = "seek_Ap",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "FF",
        id = "mmp6",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      }
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "R",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_red",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "00",
        id = "mmp1",
        layout_width = "10%w",
        textColor=颜色7,
        layout_height = "50dp",
        gravity = "center"
      }
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "G",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_green",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "00",
        id = "mmp2",
        layout_width = "10%w",
        textColor=颜色7,
        layout_height = "50dp",
        gravity = "center"
      }
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "B",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_blue",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "00",
        id = "mmp3",
        layout_width = "10%w",
        textColor=颜色7,
        layout_height = "50dp",
        gravity = "center"
      };
    };
    {
      LinearLayout;
      layout_width="fill";
      gravity="right";
      layout_height="90dp";
      backgroundColor=颜色1;
      {
        Button;
        id="关闭调色板";
        textColor="0xFF383A3D",
        --backgroundColor=颜色1;
        text="取消";
        layout_marginRight="10dp",
        layout_height="40dp",
      };
      {
        Button;
        textColor="0xFF03A9F4",
        id="复制";
        text=按钮;
        layout_height="40dp",
        --backgroundColor=颜色1;
        layout_marginRight="5dp";
      };
    };
  };

  dialog= AlertDialog.Builder(this)
  dialog1=dialog.show()
  dialog1.getWindow().setContentView(loadlayout(yuxuan));
  dialog1.getWindow().setBackgroundDrawable(ColorDrawable(0x00000000));
  local dialogWindow = dialog1.getWindow();
  dialogWindow.setGravity(Gravity.BOTTOM);
  dialog1.getWindow().getAttributes().width=(activity.Width);
  dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);



  --颜色1=0x5FFFFFFF
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
      drawable.setStroke(3,0xCFB0B0B0)
      drawable.setGradientType(GradientDrawable.RECTANGLE);
      mBinding.setTextColor(T1)
      mBinding.setTypeface(Text_Type)
      return drawable
    end}
  美化按钮2=function(mBinding,radiu,InsideColor,T1)
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


  --  }).setNegativeButton(("取消"), nil).show()
  美化按钮2(关闭调色板,10,0x7E000000,颜色7)
  美化按钮2(复制,10,0x7a00bfff,0xFF03A9F4)
  复制.onClick=function()
    switch 复制.Text
      case "复制"
    import("android.content.*")
    a = mmp4.getText()
    activity.getSystemService(Context.CLIPBOARD_SERVICE).setText(a)
    print("颜色已复制")
    dialog1.dismiss()
default
a=mmp4.getText()
switch 位数
case 1
io.open(activity.getLuaDir().."/Verify/set5.XY","w"):write(a):close()
q.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 2
io.open(activity.getLuaDir().."/Verify/set6.XY","w"):write(a):close()
qq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 3
io.open(activity.getLuaDir().."/Verify/set7.XY","w"):write(a):close()
qqq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 4
io.open(activity.getLuaDir().."/Verify/set8.XY","w"):write(a):close()
qqqq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 5
io.open(activity.getLuaDir().."/Verify/set9.XY","w"):write(a):close()
qqqqq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 6
io.open(activity.getLuaDir().."/Verify/set10.XY","w"):write(a):close()
qqqqqq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 7
io.open(activity.getLuaDir().."/Verify/set11.XY","w"):write(a):close()
qqqqqqq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 8
io.open(activity.getLuaDir().."/Verify/set12.XY","w"):write(a):close()
qqqqqqqq.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 33
io.open(activity.getLuaDir().."/Verify/setb.XY","w"):write(a):close()
b.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
case 333
io.open(activity.getLuaDir().."/Verify/setbb.XY","w"):write(a):close()
bb.backgroundColor=tonumber(a)
print("颜色已更换")
dialog1.dismiss()
end
end
end


  关闭调色板.onClick=function()
    dialog1.dismiss()
  end

  控件圆角(圆不溜秋,颜色1,30)

  mmp4.setText(A1_688)
  seek_Ap.setMax(255)
  seek_Ap.setProgress(255)
  seek_red.setMax(255)
  seek_red.setProgress(1)
  seek_green.setMax(255)
  seek_green.setProgress(1)
  seek_blue.setMax(255)
  seek_blue.setProgress(1)
  seek_red.ProgressDrawable.setColorFilter(PorterDuffColorFilter(4294901760, PorterDuff.Mode.SRC_ATOP))
  seek_red.Thumb.setColorFilter(PorterDuffColorFilter(4294901760, PorterDuff.Mode.SRC_ATOP))
  seek_green.ProgressDrawable.setColorFilter(PorterDuffColorFilter(4278255360, PorterDuff.Mode.SRC_ATOP))
  seek_green.Thumb.setColorFilter(PorterDuffColorFilter(4278255360, PorterDuff.Mode.SRC_ATOP))
  seek_blue.ProgressDrawable.setColorFilter(PorterDuffColorFilter(4278190335, PorterDuff.Mode.SRC_ATOP))
  seek_blue.Thumb.setColorFilter(PorterDuffColorFilter(4278190335, PorterDuff.Mode.SRC_ATOP))
  import("android.graphics.drawable.GradientDrawable")
  drawable = GradientDrawable()
  drawable.setShape(GradientDrawable.RECTANGLE)
  drawable.setColor(tointeger(A1_688))
  drawable.setCornerRadii({
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360
  })
  mmp5.setBackgroundDrawable(drawable)
  function CircleButto(A0_689, A1_690)
    import("android.graphics.drawable.GradientDrawable")
    drawable = GradientDrawable()
    drawable.setShape(GradientDrawable.RECTANGLE)
    drawable.setColor(0x5FFFFFFF)
    drawable.setCornerRadii({
      30,
      30,
      30,
      30,
      30,
      30,
      30,
      30
    })
    圆不溜秋.setBackgroundDrawable(drawable)
  end
  --CircleButto()

  seek_Ap.setOnSeekBarChangeListener({
    onProgressChanged = function(A0_696, A1_697)
      A1_697 = A1_697 + 1
      e = Integer.toHexString(A1_697 - 1)
      e = string.upper(e)
      if #e == 1 then
        e = "0" .. e
        mmp6.setText(e)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp6.setText(e)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  seek_red.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_698, L1_699
    end
    ,
    onStopTrackingTouch = function()
      local L0_700, L1_701
    end
    ,
    onProgressChanged = function(A0_702, A1_703)
      A1_703 = A1_703 + 1
      a = Integer.toHexString(A1_703 - 1)
      a = string.upper(a)
      if #a == 1 then
        a = "0" .. a
        mmp1.setText(a)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp1.setText(a)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  seek_green.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_704, L1_705
    end
    ,
    onStopTrackingTouch = function()
      local L0_706, L1_707
    end
    ,
    onProgressChanged = function(A0_708, A1_709)
      A1_709 = A1_709 + 1
      A0_687 = Integer.toHexString(A1_709 - 1)
      A0_687 = string.upper(A0_687)
      if #A0_687 == 1 then
        A0_687 = "0" .. A0_687
        mmp2.setText(A0_687)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp2.setText(A0_687)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  seek_blue.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_710, L1_711
    end
    ,
    onStopTrackingTouch = function()
      local L0_712, L1_713
    end
    ,
    onProgressChanged = function(A0_714, A1_715)
      A1_715 = A1_715 + 1
      c = Integer.toHexString(A1_715 - 1)
      c = string.upper(c)
      if #c == 1 then
        c = "0" .. c
        mmp3.setText(c)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp3.setText(c)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  ak = string.sub(A1_688, 3, 4)
  seek_Ap.setProgress(tonumber(ak, 16))
  a2 = string.sub(A1_688, 5, 6)
  seek_red.setProgress(tonumber(a2, 16))
  a3 = string.sub(A1_688, 7, 8)
  seek_green.setProgress(tonumber(a3, 16))
  a4 = string.sub(A1_688, 9, 10)
  seek_blue.setProgress(tonumber(a4, 16))
end

function yss(A0_716, A1_717, A2_718)
  import("android.graphics.PorterDuff")
  import("android.graphics.PorterDuffColorFilter")
  function CircleButton(A0_719, A1_720)
    import("android.graphics.drawable.GradientDrawable")
    drawable = GradientDrawable()
    drawable.setShape(GradientDrawable.RECTANGLE)
    drawable.setColor(A1_720)
    drawable.setCornerRadii({
      360,
      360,
      360,
      360,
      360,
      360,
      360,
      360
    })
    A0_719.setBackgroundDrawable(drawable)
  end

  yuxuan = {
    LinearLayout,
    orientation = "vertical",
    layout_height = "fill",
    layout_width = "fill",
    gravity = "center",
    backgroundColor=颜色1;
    {
      CardView,
      layout_height = "100dp",
      layout_width = "100dp",
      layout_marginTop = "20dp",
      backgroundColor=颜色1;
      id = "mmp5"
    },
    {
      TextView,
      text = "",
      id = "mmp4",
      layout_marginLeft = "10dp",
      layout_marginRight = "10dp",
      layout_width = "match_parent",
      layout_height = "50dp",
      textColor=颜色7,
      gravity = "center"
    },

    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      textColor=颜色7,
      {
        TextView,
        text = "A",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_Ap",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "FF",
        id = "mmp6",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      }
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "R",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_red",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "00",
        id = "mmp1",
        layout_width = "10%w",
        textColor=颜色7,
        layout_height = "50dp",
        gravity = "center"
      }
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "G",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_green",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "00",
        id = "mmp2",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      }
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "fill",
      layout_width = "fill",
      gravity = "center",
      backgroundColor=颜色1;
      {
        TextView,
        text = "B",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      },
      {
        SeekBar,
        id = "seek_blue",
        layout_width = "65%w",
        layout_height = "50dp"
      },
      {
        TextView,
        text = "00",
        id = "mmp3",
        layout_width = "10%w",
        layout_height = "50dp",
        textColor=颜色7,
        gravity = "center"
      }
    }
  }
  AlertDialog.Builder(this).setTitle(A0_716).setView(loadlayout(yuxuan)).setPositiveButton(getLS("L_Determine"), {
    onClick = function(A0_721)
      if A2_718 == true then
        LuaEditor.paste("#" .. string.sub(mmp4.getText(), 5, -1))
       else
        LuaEditor.paste(mmp4.getText())
      end
    end

  }).setNegativeButton(getLS("L_Cancel"), nil).show()
  mmp4.setText(A1_717)
  seek_Ap.setMax(255)
  seek_Ap.setProgress(255)
  seek_red.setMax(255)
  seek_red.setProgress(1)
  seek_green.setMax(255)
  seek_green.setProgress(1)
  seek_blue.setMax(255)
  seek_blue.setProgress(1)
  seek_red.ProgressDrawable.setColorFilter(PorterDuffColorFilter(4294901760, PorterDuff.Mode.SRC_ATOP))
  seek_red.Thumb.setColorFilter(PorterDuffColorFilter(4294901760, PorterDuff.Mode.SRC_ATOP))
  seek_green.ProgressDrawable.setColorFilter(PorterDuffColorFilter(4278255360, PorterDuff.Mode.SRC_ATOP))
  seek_green.Thumb.setColorFilter(PorterDuffColorFilter(4278255360, PorterDuff.Mode.SRC_ATOP))
  seek_blue.ProgressDrawable.setColorFilter(PorterDuffColorFilter(4278190335, PorterDuff.Mode.SRC_ATOP))
  seek_blue.Thumb.setColorFilter(PorterDuffColorFilter(4278190335, PorterDuff.Mode.SRC_ATOP))
  import("android.graphics.drawable.GradientDrawable")
  drawable = GradientDrawable()
  drawable.setShape(GradientDrawable.RECTANGLE)
  drawable.setColor(tointeger(A1_717))
  drawable.setCornerRadii({
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360
  })
  mmp5.setBackgroundDrawable(drawable)
  seek_Ap.setOnSeekBarChangeListener({
    onProgressChanged = function(A0_722, A1_723)
      A1_723 = A1_723 + 1
      e = Integer.toHexString(A1_723 - 1)
      e = string.upper(e)
      if #e == 1 then
        e = "0" .. e
        mmp6.setText(e)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp6.setText(e)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  seek_red.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_724, L1_725
    end
    ,
    onStopTrackingTouch = function()
      local L0_726, L1_727
    end
    ,
    onProgressChanged = function(A0_728, A1_729)
      A1_729 = A1_729 + 1
      a = Integer.toHexString(A1_729 - 1)
      a = string.upper(a)
      if #a == 1 then
        a = "0" .. a
        mmp1.setText(a)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp1.setText(a)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  seek_green.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_730, L1_731
    end
    ,
    onStopTrackingTouch = function()
      local L0_732, L1_733
    end
    ,
    onProgressChanged = function(A0_734, A1_735)
      A1_735 = A1_735 + 1
      A0_716 = Integer.toHexString(A1_735 - 1)
      A0_716 = string.upper(A0_716)
      if #A0_716 == 1 then
        A0_716 = "0" .. A0_716
        mmp2.setText(A0_716)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp2.setText(A0_716)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  seek_blue.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_736, L1_737
    end
    ,
    onStopTrackingTouch = function()
      local L0_738, L1_739
    end
    ,
    onProgressChanged = function(A0_740, A1_741)
      A1_741 = A1_741 + 1
      c = Integer.toHexString(A1_741 - 1)
      c = string.upper(c)
      if #c == 1 then
        c = "0" .. c
        mmp3.setText(c)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
       else
        mmp3.setText(c)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end

  })
  ak = string.sub(A1_717, 3, 4)
  seek_Ap.setProgress(tonumber(ak, 16))
  a2 = string.sub(A1_717, 5, 6)
  seek_red.setProgress(tonumber(a2, 16))
  a3 = string.sub(A1_717, 7, 8)
  seek_green.setProgress(tonumber(a3, 16))
  a4 = string.sub(A1_717, 9, 10)
  seek_blue.setProgress(tonumber(a4, 16))
end