package com.example.demo.repo;

import com.example.demo.entity.Team;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    @Query("select distinct t from Team t join t.members m where lower(m.email) = lower(:email)")
    List<Team> findTeamsForMemberEmail(@Param("email") String email);

    @Query(value = "select count(*) from user_supporting_teams where team_id = :teamId", nativeQuery = true)
    long countSupporters(@Param("teamId") UUID teamId);
}
