package com.example.tgbot.Repository;

import com.example.tgbot.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByDateTime (LocalDateTime dateTime);
    List<Task> findAllByChatId (Long chatId);
    void deleteByChatIdAndId (Long chatId, Long taskId);

}
