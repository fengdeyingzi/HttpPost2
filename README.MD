## HttpPost2

这个post工具采用**JavaFx**框架编写，用于开发调试

## 普通get提交
在第一个输入框中输入url即为普通的get请求

![scr_get](screen\scr_get.png)

## 普通post提交
在第二个输入框中输入post数据，则以post方式提交，若留空为get方式

![scr_post](screen\scr_post.png)

## 增强版post提交
第一个输入框留空，在post内容里直接输入以下格式的文本，然后点击获取数据按钮，这种方式主要用于配合开发时的log输出实现快速调试，如果要用get方式提交同理

![scr_postx](screen\scr_postx.png)

## 返回json数据自动格式化
为了调试数据方便，返回的数据自动格式化，去除了一个json格式化的按钮
