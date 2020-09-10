# `OkHttp`手写实现

[TOC]

本文手写实现了`OkHttp`网络访问请求框架，只是学习的`demo`，仅供学习使用。

## 一、实现效果

实现效果如下图所示：

![image](https://github.com/tianyalu/NeOkHttpManualImpl/raw/master/show/show.gif)

## 二、实现步骤



## 三、`OkHttp`连接池

`Socket`的连接和断开需要经历三次握手和四次挥手，消耗较大，如果访问量大的话消耗会更大，所以考虑使用连接池对访问相同域名服务器的`Socket`复用。

![image](https://github.com/tianyalu/NeOkHttpManualImpl/raw/master/show/okhttp_connection_pool.png)



