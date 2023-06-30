package com.example.tgbot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "task")
public class Task {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "task_id")
    private Long id;
    @Column (name = "chat_id")
    private Long chatId;
    @Column (name = "date_time")
    private LocalDateTime dateTime;
    private String text;

    @Override
    public String toString() {
        return "[" + dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "] " + text;
    }
}
