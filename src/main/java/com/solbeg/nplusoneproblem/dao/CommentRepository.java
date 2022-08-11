package com.solbeg.nplusoneproblem.dao;

import com.solbeg.nplusoneproblem.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
