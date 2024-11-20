# remote-client-sdk
## 目录

- [概述](#概述)
- [特性](#特性)
- [快速开始](#快速开始)
  - [环境要求](#环境要求)
  - [配置](#配置)
- [提供的第三方API服务(持续更新中)](#提供的第三方api服务持续更新中)
  - [chatgpt增量模型接口调用](#chatgpt增量模型接口调用)
  - [快速生成二维码接口](#快速生成二维码接口)
  - [英汉互译接口调用](#英汉互译接口调用)
  - [天气查询接口调用](#天气查询接口调用)
  - [身份证实名认证api](#身份证实名认证api)
  - [短信验证码服务api](#短信验证码服务api)
  - [图形验证码api](#图形验证码api)
  - [发送邮箱验证码api](#发送邮箱验证码api)
  - [敏感词过滤api](#敏感词过滤api)
- [更多api将持续更新中](#更多api将持续更新中)
## 概述
为了开发需求，在某些项目中可能需要调用许多第三方api接口，由于部分api接口文档特别复杂麻烦，且不易于被检索调用，
remote-client-sdk是一个高性能、可扩展、专门用来提供第三方远程调用服务接口的项目，
该项目旨在开发方便，快速开发调用，无需处理复杂响应，为开发者提供一个便捷的sdk
## 特性
* 高性能：基于Spring Boot，提供快速响应和高并发处理能力。
* 易于集成：提供简单易用的API，支持快速集成到现有项目中。
* 扩展性强：支持自定义消息处理逻辑和模型配置，满足复杂业务需求。
* 简单易用：第三方api接口均已封装好，直接调用即可，简单易上手。
## 快速开始
### 环境要求
* JDK 8或更高版本
* Maven 3.6.0或更高版本
* SpringBoot 2.X 版本
### 配置
1. 在pom.xml文件中添加以下依赖：
```xml
<dependency>
    <groupId>io.github.lonelykkk</groupId>
    <artifactId>remote-client-sdk</artifactId>
    <version>0.0.7</version>
</dependency>
```

## 提供的第三方api服务(持续更新中)
### chatgpt增量模型接口调用
> 接口描述
>>
>> 一行代码调用你gpt接口，只需要输入问题即可返回回答，可方便快速集成在你的项目中。

> 入门案例
``` java
public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient();
        String chat = remoteClient.getAiChat("输入你的问题");  //以字符串类型，返回ai回答
        System.out.println(chat); //输出答案
    }
```
### 快速生成二维码接口
> 入门案例
* 第一个参数msg为String类型，输入你要存入即将要生成的二维码里面的数据
* 第二行参数size为Integer输入你需要生成二维码的大小
* 生成后的二维码图片默认保存在你当前springboot项目中的resources/imgCode目录下
* 该方法的返回值为你生成的二维码的路径，以String类型返回
```java
public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient();
        //1.第一个参数msg为String类型，输入你要存入即将要生成的二维码里面的数据，如我输入 https://www.baidu.com/,扫描生成后的二维码将跳转至百度链接
        //2.第二行参数size为Integer输入你需要生成二维码的大小,如输入150则二维码大小为 150*150px
        //3.生成后的二维码图片默认保存在你当前springboot项目中的resources/imgCode目录下
        //4.该方法的返回值为你生成的二维码的路径，以String类型返回
        String url = remoteClient.getImgCode("https://www.baidu.com/", 150);
        System.out.println(url);
    }
```
### 英汉互译接口调用
> 入门案例
```java
 public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient();
        String translation = remoteClient.getTranslation("输入你需要翻译的内容，中英文均可");
        System.out.println(translation);  //输出翻译后的结果,为String类型
    }
```
### 天气查询接口调用
> 接口描述
> 该api可用于查询当前地区24小时内的天气信息，只需要输入你需要查询的天气的地址，便可以返回对应地址的天气24小时的实体对象

> 入门案例
```java
public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient();
        try {
            HourWeatherList hourWeatherList = remoteClient.getWeather("上海"); //在此处输入你的需要查询的天气的地点
            // 定义日期格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            for (HourForecast forecast : hourWeatherList.getHourList()) {
                // 解析字符串为 Date 对象
                Date date = dateFormat.parse(forecast.getTime());
                System.out.println("Time: " + date);
                System.out.println("Weather: " + forecast.getWeather());
                System.out.println("Temperature: " + forecast.getTemperature());
                System.out.println("Wind Direction: " + forecast.getWindDirection());
                System.out.println("Wind Power: " + forecast.getWindPower());
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
}
```
> 对应的实体类
```java
public class HourWeatherList {
    @JsonProperty("remark")
    private String remark;

    @JsonProperty("ret_code")
    private int retCode;

    @JsonProperty("areaid")
    private String areaId;

    @JsonProperty("area")
    private String area;

    @JsonProperty("areaCode")
    private String areaCode;

    private List<HourForecast> hourList;
}
```
```java
public class HourForecast {
    @JsonProperty("time")
    private String time;

    @JsonProperty("wind_direction")
    private String windDirection;

    @JsonProperty("wind_power")
    private String windPower;

    @JsonProperty("areaid")
    private String areaId;

    @JsonProperty("weather_code")
    private String weatherCode;

    @JsonProperty("temperature")
    private String temperature;

    @JsonProperty("area")
    private String area;

    @JsonProperty("weather")
    private String weather;

}
```
### 身份证实名认证api
> 接口描述
> 该api提供了身份证实名认证服务，用户只需要传入姓名和身份证号便可以返回对应的身份信息

>入门案例

```java
public static void main(String[] args) throws Exception{
        RemoteClient remoteClient = new RemoteClient();
        IdentityCard identityCard = remoteClient.getAuthenticationCard("传入姓名", "传入身份证号");
        //打印输出结果
        System.out.println("生日：" + identityCard.getBirthday());
        System.out.println("响应结果(1.不一致 0.一致)：" + identityCard.getResult());
        System.out.println("地址：" + identityCard.getAddress());
        System.out.println("订单编号：" + identityCard.getOrderNo());
        System.out.println("性别：" + identityCard.getSex());
        System.out.println("desc：" + identityCard.getDesc());
    }
```
> 对应的实体类
```java
public class IdentityCard {
    private String birthday;
    private int result; //1.不一致 0.一致
    private String address; //地址
    private String orderNo;  //订单编号
    private String sex; //性别
    private String desc; //描述
}
```
### 短信验证码服务api
> 接口描述
> 该短信服务api为用户提供了发送短信验证码服务，用户只需要传入需要发送短信验证的手机号，
> 便可发送一个随机6位数验证码并返回

>入门案例
```java
public static void main(String[] args) throws Exception{
        RemoteClient remoteClient = new RemoteClient();
        final Integer sms = remoteClient.sendSms("输入你的手机号"); //返回这个随机生成的验证码，以便开发人员做后续验证码校验
        System.out.println("验证码为：" + sms); 
    }
```

### 图形验证码api
> 功能描述
> 该图形验证码api可用于生成一个验证码图片，用户可以在如登录校验等类似功能时使用该api生成一个图形验证码
> 该api返回图形验证码的内容以及图片的绝对路径，用户可根据返回的内容进行校验，可以根据返回的图片路径进行相应处理，如存储在数据库或者存储在云空间等等响应给前端

> 入门案例
```java
public static void main(String[] args) throws Exception{
        RemoteClient remoteClient = new RemoteClient();
        
        ImgCaptcha imgCaptcha = remoteClient.getImgCaptcha(5); //参数为int类型，用于指定你需要生成的图形验证码的数据量个数
        System.out.println("验证码为：" + imgCaptcha.getCaptchaText());
        System.out.println("路径为：" + imgCaptcha.getCaptchaUrl());
    }
```

### 发送邮箱验证码api
> 功能描述
> 该邮箱验证码主要用于用户在进行登录注册时通过邮箱验证码进行验证，用户需要输入你的邮箱，之后便可对你指定的邮箱发送随机生成的验证码
> 随后返回一个String类型的字符串，你可以根据该验证码进行校验

> 入门案例
```java
public static void main(String[] args) throws Exception{
        RemoteClient remoteClient = new RemoteClient();
        String code = remoteClient.sendSmtp("111@qq.com"); //此处输入你需要发送的邮箱，随后返回你发送的验证码方便后续校验
        System.out.println("收到的邮箱验证码为：" + code);
    }
```

### 敏感词过滤api
> 功能描述
> 该功能主要用于对文本合法性进行校验，查看是否有敏感词，如对用户发送的评论进行校验，如果有违禁词则会返回对应的结果方便开发人员进一步处理
> 返回类型为int类型，对应结果为 1：合规，2：不合规，3：疑似，4：审核失败，-1：api调用失败

>入门案例
```java
public static void main(String[] args) throws Exception{
        RemoteClient remoteClient = new RemoteClient();
        //result：1：合规，2：不合规，3：疑似，4：审核失败，-1：api调用失败
        int result = remoteClient.SensitiveFilter("你好");
        //后续可对result进行判断以便做进一步处理
    }
```
## 更多api将持续更新中

