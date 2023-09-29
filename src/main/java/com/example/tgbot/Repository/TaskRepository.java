package com.example.tgbot.Repository;

import com.example.tgbot.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.lang.annotation.Native;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByDateTime (LocalDateTime dateTime);


    @Query(value = "select * from task where date(date_time) = CURRENT_DATE;", nativeQuery = true)
    List<Task> findAllForToday ();
    List<Task> findAllByChatId (Long chatId);
    void deleteByChatIdAndId (Long chatId, Long taskId);

}
