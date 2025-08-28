package com.team9.fitness.repository;

import com.team9.fitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名和密码查询用户记录数量
     * 
     * @param username 用户名
     * @param password 密码
     * @return 匹配的记录数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.username = :username AND u.password = :password")
    Long judge(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户名查询用户记录数量
     * 
     * @param username 用户名
     * @return 匹配的记录数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.username = :username")
    Long countByUsername(@Param("username") String username);

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    User findByUsername(String username);

}
