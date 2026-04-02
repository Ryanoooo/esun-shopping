package com.esun.shopping.repository;

import com.esun.shopping.entity.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Member findByEmail(String email) {
        String sql = "SELECT * FROM sp_find_member_by_email(?)";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Member m = new Member();
                m.setMemberId(rs.getString("member_id"));
                m.setMemberName(rs.getString("member_name"));
                m.setEmail(rs.getString("email"));
                m.setPasswordHash(rs.getString("password_hash"));
                m.setPhone(rs.getString("phone"));
                m.setRole(rs.getString("role"));
                m.setCreatedAt(rs.getString("created_at"));
                return m;
            }
            return null;
        }, email);
    }

    public Member findById(String memberId) {
        String sql = "SELECT member_id, member_name, email, phone, role, " +
                     "TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at " +
                     "FROM member WHERE member_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Member m = new Member();
                m.setMemberId(rs.getString("member_id"));
                m.setMemberName(rs.getString("member_name"));
                m.setEmail(rs.getString("email"));
                m.setPhone(rs.getString("phone"));
                m.setRole(rs.getString("role"));
                m.setCreatedAt(rs.getString("created_at"));
                return m;
            }
            return null;
        }, memberId);
    }

    public void insertMember(String memberId, String memberName,
                             String email, String passwordHash, String phone) {
        String sql = "CALL sp_register_member(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, memberId, memberName, email, passwordHash, phone);
    }
}
