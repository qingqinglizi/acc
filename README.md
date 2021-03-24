# acc
Authentication Checkout Central
说明：鉴权中心是通过一级票据（firstTicket）和二级票据（secondTicket）来认证的
建议：
1.票据存储方式待改善
    目前存在两个Map中：firstTicketMap，secondTicketMap
    格式：<ticket，Map> -->其中Map中存的是：loginId, xxx
                                          loginDate, xxx
2.票据生成方式有待改善
    目前一、二级票据都是由pc端ip地址、当前时间戳以及5位随机数（英文字母和数字）通过简单哈希不可逆方式生成

3.如果用户以清除cookie的方式来退出登录，那么在票据存储工具：firstTicketMap，secondTicketMap中，
已退出登录用户仍然占据着空间
