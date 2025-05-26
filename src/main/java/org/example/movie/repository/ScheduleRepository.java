package org.example.movie.repository;

import org.example.movie.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Thêm các phương thức tùy chỉnh nếu cần (ví dụ: tìm kiếm theo thời gian)
}