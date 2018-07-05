package com.convrt.repository;

import com.convrt.entity.Context;
import com.convrt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContextRepository extends JpaRepository<Context, String> {

    Context findByToken(String token);

    Context findByTokenAndValidIsTrue(String token);

    Context findByTokenAndUserAgentAndValidIsTrue(String token, String userAgent);

    List<Context> findByUserUuidAndValidIsTrue(String userUuid);

    Context findByUserUuid(String userUuid);

    Context findByUser(User user);

    Context findByUserAndValidIsTrue(User user);

    boolean existsByTokenAndValidIsTrue(String token);

    void deleteByToken(String token);

}
