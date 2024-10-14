local a=io.open(activity.getLuaDir().."/Verify/set4.XY"):read("*a")
local ip=a:match("2(.-)"..'"')
if ip=="开" then
  颜色1=0xff303030
  颜色2=0xff212121
  颜色3=0xffffffff
  颜色4=0xffffffff
  颜色5=0xff303030
  activity.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar)
 else
  颜色1=0xffffffff
  颜色3=0xff303030
  颜色4=0xff757575
  颜色2=0xFFF2F1F6
  颜色5=0x5FFFFFFF
  activity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)
end
local L0_656, L1_657
function L0_656(A0_658, A1_659, A2_660, A3_661)
  import("android.graphics.PorterDuff")
  import("android.graphics.PorterDuffColorFilter")
  function CircleButton(A0_662, A1_663)
    import("android.graphics.drawable.GradientDrawable")
    drawable = GradientDrawable()
    drawable.setShape(GradientDrawable.RECTANGLE)
    drawable.setColor(A1_663)
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
    A0_662.setBackgroundDrawable(drawable)
  end
  
  yuxuan = {
    LinearLayout,
    orientation = "vertical",
    layout_height = "fill",
    layout_width = "fill",
    gravity = "center",
    {
      CardView,
      layout_height = "100dp",
      layout_width = "100dp",
      layout_marginTop = "20dp",
      backgroundColor=颜色1,
      id = "mmp5"
    },
    {
      EditText,
      text = "",
      id = "mmp4",
      background = "0",
      layout_marginLeft = "10dp",
      layout_marginRight = "10dp",
      layout_width = "match_parent",
      layout_height = "50dp",
      gravity = "center"
    },
    {
      LinearLayout,
      orientation = "horizontal",
      layout_height = "50dp",
      layout_width = "fill",
      gravity = "center",
      {
        TextView,
        text = "A",
        textColor=颜色7,
        layout_width = "10%w",
        layout_height = "50dp",
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
        textColor=颜色7,
        id = "mmp6",
        layout_width = "10%w",
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
      {
        TextView,
        text = "R",
        textColor=颜色7,
        layout_width = "10%w",
        layout_height = "50dp",
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
        textColor=颜色7,
        id = "mmp1",
        layout_width = "10%w",
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
      {
        TextView,
        text = "G",
        textColor=颜色7,
        layout_width = "10%w",
        layout_height = "50dp",
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
        textColor=颜色7,
        layout_width = "10%w",
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
      {
        TextView,
        text = "B",
        textColor=颜色7,
        layout_width = "10%w",
        layout_height = "50dp",
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
        textColor=颜色7,
        layout_width = "10%w",
        layout_height = "50dp",
        gravity = "center"
      }
    }
  }
  AlertDialog.Builder(this).setTitle(A0_658).setView(loadlayout(yuxuan)).setPositiveButton(getLS("L_Determine"), {onClick = A2_660}).setNeutralButton(getLS("L_The_default"), {
    onClick = function(A0_664)
      if A3_661 == "1" then
        gxlua("BackgroundColor", "0xffffffff")
        sxys()
      elseif A3_661 == "2" then
        gxlua("TextColor", "0xff333333")
        sxys()
      elseif A3_661 == "3" then
        gxlua("KeywordColor", "0xff3f7fb5")
        sxys()
      elseif A3_661 == "4" then
        gxlua("UserwordColor", "0xff6e81d9")
        sxys()
      elseif A3_661 == "5" then
        gxlua("BasewordColor", "0xff6e81d9")
        sxys()
      elseif A3_661 == "6" then
        gxlua("StringColor", bjzt())
        sxys()
      elseif A3_661 == "7" then
        gxlua("CommentColor", "0xffa0a0a0")
        sxys()
      elseif A3_661 == "8" then
        gxlua("PanelBackgroundColor", "0xffFFFFFF")
        sxys()
      elseif A3_661 == "9" then
        gxlua("PanelTextColor", bjzt())
        sxys()
      elseif A3_661 == "10" then
        array = activity.getTheme().obtainStyledAttributes({
          android.R.attr.colorBackground,
          android.R.attr.textColorPrimary,
          android.R.attr.colorPrimary,
          android.R.attr.colorPrimaryDark,
          android.R.attr.colorAccent
        })
        colorBackground = array.getColor(0, 16711935)
        textColorPrimary = array.getColor(1, 16711935)
        colorPrimary = array.getColor(2, 16711935)
        colorPrimaryDark = array.getColor(3, 16711935)
        colorAccent = array.getColor(4, 16711935)
        bjzt2 = tostring("0x" .. tostring(string.upper(Integer.toHexString(colorPrimary))))
        io.open(activity.getLuaDir() .. "/res/set205.LY", "w"):write(bjzt2):close()
        sxys()
      end
    end
    
  }).setNegativeButton(getLS("L_Cancel"), nil).show()
  mmp4.setText(A1_659)
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
  drawable.setColor(tointeger(A1_659))
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
    onProgressChanged = function(A0_665, A1_666)
      A1_666 = A1_666 + 1
      e = Integer.toHexString(A1_666 - 1)
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
      local L0_667, L1_668
    end
    ,
    onStopTrackingTouch = function()
      local L0_669, L1_670
    end
    ,
    onProgressChanged = function(A0_671, A1_672)
      A1_672 = A1_672 + 1
      a = Integer.toHexString(A1_672 - 1)
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
      local L0_673, L1_674
    end
    ,
    onStopTrackingTouch = function()
      local L0_675, L1_676
    end
    ,
    onProgressChanged = function(A0_677, A1_678)
      A1_678 = A1_678 + 1
      A0_658 = Integer.toHexString(A1_678 - 1)
      A0_658 = string.upper(A0_658)
      if #A0_658 == 1 then
        A0_658 = "0" .. A0_658
        mmp2.setText(A0_658)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      else
        mmp2.setText(A0_658)
        d = mmp6.getText() .. mmp1.getText() .. mmp2.getText() .. mmp3.getText()
        mmp4.setText("0x" .. d)
        ys = int("0x" .. d)
        CircleButton(mmp5, ys)
      end
    end
    
  })
  seek_blue.setOnSeekBarChangeListener({
    onStartTrackingTouch = function()
      local L0_679, L1_680
    end
    ,
    onStopTrackingTouch = function()
      local L0_681, L1_682
    end
    ,
    onProgressChanged = function(A0_683, A1_684)
      A1_684 = A1_684 + 1
      c = Integer.toHexString(A1_684 - 1)
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
  ak = string.sub(A1_659, 3, 4)
  seek_Ap.setProgress(tonumber(ak, 16))
  a2 = string.sub(A1_659, 5, 6)
  seek_red.setProgress(tonumber(a2, 16))
  a3 = string.sub(A1_659, 7, 8)
  seek_green.setProgress(tonumber(a3, 16))
  a4 = string.sub(A1_659, 9, 10)
  seek_blue.setProgress(tonumber(a4, 16))
end

yss = L0_656
function L0_656()
  gxlua("BackgroundColor", mmp4.Text)
  sxys()
end

sj = L0_656
function L0_656()
  gxlua("TextColor", mmp4.Text)
  sxys()
end

sj1 = L0_656
function L0_656()
  gxlua("KeywordColor", mmp4.Text)
  sxys()
end

sj2 = L0_656
function L0_656()
  gxlua("UserwordColor", mmp4.Text)
  sxys()
end

sj3 = L0_656
function L0_656()
  gxlua("BasewordColor", mmp4.Text)
  sxys()
end

sj4 = L0_656
function L0_656()
  gxlua("StringColor", mmp4.Text)
  sxys()
end

sj5 = L0_656
function L0_656()
  gxlua("CommentColor", mmp4.Text)
  sxys()
end

sj6 = L0_656
function L0_656()
  gxlua("PanelBackgroundColor", mmp4.Text)
  sxys()
end

sj7 = L0_656
function L0_656()
  gxlua("PanelTextColor", mmp4.Text)
  sxys()
end

sj8 = L0_656
function L0_656()
  io.open(activity.getLuaDir() .. "/res/set205.LY", "w"):write(mmp4.Text):close()
  sxys()
end

sj10 = L0_656