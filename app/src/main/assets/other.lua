import "java.io.File"
import "AndLua"
软件文件夹="/sdcard/ThomeLua"
项目文件夹=软件文件夹.."/project"
备份文件夹=软件文件夹.."/backup"
打包文件夹=软件文件夹.."/bin"
解压文件夹=软件文件夹.."/Decompression"
a=File(软件文件夹).isDirectory()
if a==false then
  创建文件夹(软件文件夹)
end
a=File(项目文件夹).isDirectory()
if a==false then
  创建文件夹(项目文件夹)
end
a=File(备份文件夹).isDirectory()
if a==false then
  创建文件夹(备份文件夹)
end
a=File(打包文件夹).isDirectory()
if a==false then
  创建文件夹(打包文件夹)
end
a=File(解压文件夹).isDirectory()
if a==false then
  创建文件夹(解压文件夹)
end
a=File("/sdcard/.cookie.dat").isFile()
if a==false then
  创建文件("/sdcard/.cookie.dat")
end
