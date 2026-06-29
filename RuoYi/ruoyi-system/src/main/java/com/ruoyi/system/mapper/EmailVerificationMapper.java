package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.EmailVerification;
import org.apache.ibatis.annotations.*;

@Mapper
public interface EmailVerificationMapper {

    // 保存验证码，允许多条记录
    @Insert("INSERT INTO xray_email_verification(email, code, expire_at, created_at, updated_at) " +
            "VALUES(#{email}, #{code}, #{expireAt}, NOW(), NOW())")
    void insert(EmailVerification emailVerification);

    // 获取该邮箱最新的有效验证码
    @Select("SELECT id, `email`, `code`, expire_at as expireAt, created_at as createdAt, updated_at as updatedAt FROM xray_email_verification " +
            "WHERE email = #{email} AND expire_at > NOW() " +
            "ORDER BY created_at DESC LIMIT 1")
    EmailVerification getLatestValidCodeByEmail(@Param("email") String email);

    // 删除指定邮箱的验证码（可加条件只删除成功验证的）
    @Delete("DELETE FROM email_verification WHERE email = #{email} AND code = #{code}")
    void deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);
}

