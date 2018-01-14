# 12306-taker

此小应用主要使用 Selenium WebDriver自动填入，查询火车票信息，提交订单

1. 配置Webdriver, 浏览器相关路径信息（Config.java）, 和用户需要订票的相关信息 （resources/travelInformation.example）
2. 运行main程序， SeleniumRunner.java
3. 应用会自动打开浏览器至登陆页面，然后用户需要手动登陆
4. 登陆完后，应用自动查询，直至查票成功并提交订单
5. 订票成功后，用户只需要在半小时内完成手动支付即可。
