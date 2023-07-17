package com.aloys.nowcoder.dao;

import com.aloys.nowcoder.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@Deprecated
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

//    int deleteLoginTicket(String ticket);

    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);

    LoginTicket selectByTicket(String ticket);
}
